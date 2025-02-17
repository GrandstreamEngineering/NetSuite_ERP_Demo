// Copyright 2025 Grandstream
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     https://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.gs.app.netsuiteerp.ui.pages.inventory_transfers.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.annotation.Keep
import com.gs.app.netsuiteerp.beans.Currency
import com.gs.app.netsuiteerp.beans.InventoryTransfer
import com.gs.app.netsuiteerp.beans.InventoryTransferShow
import com.gs.app.netsuiteerp.beans.InventoryTransfersPageData
import com.gs.app.netsuiteerp.beans.ItemDataLink
import com.gs.app.netsuiteerp.beans.ResponseError
import com.gs.app.netsuiteerp.beans.OAuth2TokenErrorBody
import com.gs.app.netsuiteerp.beans.OAuth2Token
import com.gs.app.netsuiteerp.beans.ResponseErrorBody
import com.gs.app.netsuiteerp.beans.ResponseErrorHeader
import com.gs.app.netsuiteerp.beans.Subsidiary
import com.gs.app.netsuiteerp.common.cache.NetworkCache
import com.gs.app.netsuiteerp.network.NetworkConfig
import com.gs.app.netsuiteerp.ui.base.response.ResponseState
import com.gs.app.netsuiteerp.ui.base.response.ResponseUiEffect
import com.gs.app.netsuiteerp.ui.base.response.ResponseViewModel
import com.gs.app.netsuiteerp.ui.pages.inventory_transfers.intent.InventoryTransfersUIIntent
import com.gs.app.netsuiteerp.ui.pages.inventory_transfers.respository.InventoryTransfersNetRepository
import com.gs.app.netsuiteerp.ui.pages.inventory_transfers.state.InventoryTransfersUiState
import com.gs.app.netsuiteerp.utils.GsonUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import retrofit2.awaitResponse
import javax.inject.Inject

@Keep
@HiltViewModel
class InventoryTransfersViewModel @Inject constructor(
    private val repository: InventoryTransfersNetRepository,
    application: Application
) : ResponseViewModel<InventoryTransfersUIIntent, InventoryTransfersUiState>(application) {

    override fun initUiState(): InventoryTransfersUiState {
        return InventoryTransfersUiState(ResponseState.None)
    }

    override fun handleIntent(intent: InventoryTransfersUIIntent) {
        when (intent) {
            is InventoryTransfersUIIntent.GetInventoryTransfers -> getInventoryTransfers(intent.token, intent.startDate, intent.endDate, intent.limit, intent.offset)
            is InventoryTransfersUIIntent.RefreshToken -> refreshToken(intent.token)
        }
    }

    private fun getInventoryTransfers(token: OAuth2Token, startDate: String?, endDate: String?, limit: Int, offset: Int) {
        updateUiState {
            copy().onLoadingState()
        }
        request(
            request = {
                val dateLimit = StringBuilder()
                if (!TextUtils.isEmpty(startDate) || !TextUtils.isEmpty(endDate)) {
                    if (!TextUtils.isEmpty(endDate)) {
                        dateLimit.append("createdDate ON_OR_BEFORE \"$endDate\"")
                    }
                    if (!TextUtils.isEmpty(startDate)) {
                        if (!TextUtils.isEmpty(endDate)) {
                            dateLimit.append(" AND ")
                        }
                        dateLimit.append("createdDate ON_OR_AFTER \"$startDate\"")
                    }
                }
                repository.getInventoryTransfers(token, dateLimit.toString(), limit, offset)
            },
            successCallback = { body ->
                val pageData = InventoryTransfersPageData(body.count, body.hasMore, ArrayList(), body.links, body.offset, body.totalResults)
                if (!body.items.isNullOrEmpty()) {
                    loadInventoryTransferPageData(token, body.items, pageData)
                } else {
                    updateUiState {
                        copy().onSuccessInventoryTransfers(pageData)
                    }
                }
            },
            successEmptyCallback = {},
            failureCallback = { code, message ->
                updateUiState {
                    copy().onFailure(code, message)
                }
            },
            failureErrorBodyCallback = { code, errorHeader, errorBody: ResponseErrorBody? ->
                if (errorBody != null) {
                    updateUiState {
                        copy().onFailureInventoryTransfers(code, ResponseError(errorHeader, errorBody))
                    }
                } else {
                    updateUiState {
                        copy().onFailure(code, "")
                    }
                }
            },
            convertErrorBody = { errorBody ->
                try {
                    GsonUtil.gson.fromJson(errorBody, ResponseErrorBody::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                } as ResponseErrorBody?
            },
            allowEmptyBody = false,
            allowFailureErrorBody = true
        )
    }

    private suspend fun loadInventoryTransferPageData(token: OAuth2Token, items: List<ItemDataLink>, solveResult: InventoryTransfersPageData)  {
        launchOnIO {
            if (items.size > 3) {
                val splitSize = items.size / 3
                // 启动三个并发的异步任务
                val jobs = listOf(
                    async { loadInventoryTransferShows(token, items.subList(0, splitSize)) },
                    async { loadInventoryTransferShows(token, items.subList(splitSize, 2 * splitSize)) },
                    async { loadInventoryTransferShows(token, items.subList(2 * splitSize, items.size)) }
                )
                // 等待所有任务完成并合并结果
                val results = jobs.awaitAll()
                // 处理合并后的结果
                for (result in results) {
                    if (result.isEmpty()) {
                        return@launchOnIO
                    }
                    solveResult.items?.addAll(result)
                }
                updateUiState {
                    copy().onSuccessInventoryTransfers(solveResult)
                }
            } else {
                val inventoryTransferShows = loadInventoryTransferShows(token, items)
                if (inventoryTransferShows.isEmpty()) {
                    return@launchOnIO
                }
                solveResult.items?.addAll(inventoryTransferShows)
                updateUiState {
                    copy().onSuccessInventoryTransfers(solveResult)
                }
            }
        }
    }

    private suspend fun loadInventoryTransferShows(token: OAuth2Token, items: List<ItemDataLink>): List<InventoryTransferShow> {
        val result = ArrayList<InventoryTransferShow>()
        for (index in items.indices) {
            val inventoryTransferShow = loadInventoryTransferShow(token, items[index]) ?: return ArrayList()
            result.add(inventoryTransferShow)
        }
        return result
    }

    private suspend fun loadInventoryTransferShow(token: OAuth2Token, itemDataLink: ItemDataLink): InventoryTransferShow? {
        val inventoryTransfer = getInventoryTransfer(token, itemDataLink.id) ?: return null
        var subsidiary: Subsidiary? = null
        if (inventoryTransfer.subsidiary.links.isNullOrEmpty()) {
            subsidiary = getSubsidiary(token, inventoryTransfer.subsidiary.id!!)
            subsidiary?.let {
                NetworkCache.setCache(NetworkConfig.BASE_REST_URL + NetworkConfig.REST_PATH_SUBSIDIARY + "/" + inventoryTransfer.subsidiary.id, GsonUtil.gson.toJson(it))
            }
        } else {
            val subsidiaryCache = NetworkCache.getCache(inventoryTransfer.subsidiary.links[0].href!!)
            if (TextUtils.isEmpty(subsidiaryCache)) {
                subsidiary = getSubsidiary(token, inventoryTransfer.subsidiary.id!!)
                subsidiary?.let {
                    NetworkCache.setCache(inventoryTransfer.subsidiary.links[0].href!!, GsonUtil.gson.toJson(it))
                }
            } else {
                try {
                    subsidiary = GsonUtil.gson.fromJson(subsidiaryCache, Subsidiary::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                    subsidiary = getSubsidiary(token, inventoryTransfer.subsidiary.id!!)
                    subsidiary?.let {
                        NetworkCache.setCache(inventoryTransfer.subsidiary.links[0].href!!, GsonUtil.gson.toJson(it))
                    }
                }
            }
        }
        if (subsidiary == null) {
            return null
        }
        var currency: Currency? = null
        if (!subsidiary.currency.links.isNullOrEmpty()) {
            val currencyCache = NetworkCache.getCache(subsidiary.currency.links!![0].href!!)
            if (TextUtils.isEmpty(currencyCache)) {
                currency = getCurrency(token, subsidiary.currency.id)
                currency?.let {
                    NetworkCache.setCache(subsidiary.currency.links!![0].href!!, GsonUtil.gson.toJson(it))
                }
            } else {
                try {
                    currency = GsonUtil.gson.fromJson(currencyCache, Currency::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                    currency = getCurrency(token, subsidiary.currency.id)
                    currency?.let {
                        NetworkCache.setCache(subsidiary.currency.links!![0].href!!, GsonUtil.gson.toJson(it))
                    }
                }
            }
        } else {
            currency = getCurrency(token, subsidiary.currency.id)
            currency?.let {
                NetworkCache.setCache(NetworkConfig.BASE_REST_URL + NetworkConfig.REST_PATH_CURRENCY + "/" + subsidiary.currency.id, GsonUtil.gson.toJson(it))
            }
        }
        if (currency == null) {
            return null
        }
        return InventoryTransferShow(itemDataLink, inventoryTransfer, subsidiary, currency)
    }

    private suspend fun getInventoryTransfer(token: OAuth2Token, id: String): InventoryTransfer? {
        val response = repository.getInventoryTransfer(token, id).awaitResponse()
        if (response.isSuccessful) {
            return response.body()
        }
        val errorHeader = response.headers().get("www-authenticate")
            ?: response.headers().get("WWW-Authenticate")
        var errorResponseHeader: ResponseErrorHeader? = null
        if (!TextUtils.isEmpty(errorHeader)) {
            val split = errorHeader!!.split(",")
            if (split.isNotEmpty()) {
                var error = ""
                var errorDescription = ""
                for (param in split) {
                    if (param.contains("error=")) {
                        error = param.substring(param.indexOf("error=") + "error=".length)
                            .trim().replace("\"", "")
                    } else if (param.contains("error_description=")) {
                        errorDescription =
                            param.substring(param.indexOf("error_description=") + "error_description=".length)
                                .trim().replace("\"", "")
                    }
                }
                if (!TextUtils.isEmpty(error) && !TextUtils.isEmpty(errorDescription)) {
                    errorResponseHeader = ResponseErrorHeader(error, errorDescription)
                }
            }
        }
        if (errorResponseHeader != null && !TextUtils.isEmpty(errorResponseHeader.error)) {
            if (errorResponseHeader.error.lowercase() == NetworkConfig.ERROR_INVALID_TOKEN) {
                sendEffect(ResponseUiEffect.TokenExpired(errorResponseHeader.error_description))
            }
        }
        val errorBody = response.errorBody()?.string()
        if (!TextUtils.isEmpty(errorBody)) {
            val errorBodyObject = try {
                GsonUtil.gson.fromJson(errorBody, ResponseErrorBody::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            } as ResponseErrorBody?
            if (errorBodyObject != null) {
                updateUiState {
                    copy().onFailureInventoryTransfers(response.code(), ResponseError(errorResponseHeader, errorBodyObject))
                }
            } else {
                updateUiState {
                    copy().onFailure(response.code(), errorBody)
                }
            }
        } else {
            updateUiState {
                copy().onFailure(response.code(), errorBody)
            }
        }
        return null
    }

    private suspend fun getSubsidiary(token: OAuth2Token, id: String): Subsidiary? {
        val response = repository.getSubsidiary(token, id).awaitResponse()
        if (response.isSuccessful) {
            return response.body()
        }
        val errorHeader = response.headers().get("www-authenticate")
            ?: response.headers().get("WWW-Authenticate")
        var errorResponseHeader: ResponseErrorHeader? = null
        if (!TextUtils.isEmpty(errorHeader)) {
            val split = errorHeader!!.split(",")
            if (split.isNotEmpty()) {
                var error = ""
                var errorDescription = ""
                for (param in split) {
                    if (param.contains("error=")) {
                        error = param.substring(param.indexOf("error=") + "error=".length)
                            .trim().replace("\"", "")
                    } else if (param.contains("error_description=")) {
                        errorDescription =
                            param.substring(param.indexOf("error_description=") + "error_description=".length)
                                .trim().replace("\"", "")
                    }
                }
                if (!TextUtils.isEmpty(error) && !TextUtils.isEmpty(errorDescription)) {
                    errorResponseHeader = ResponseErrorHeader(error, errorDescription)
                }
            }
        }
        if (errorResponseHeader != null && !TextUtils.isEmpty(errorResponseHeader.error)) {
            if (errorResponseHeader.error.lowercase() == NetworkConfig.ERROR_INVALID_TOKEN) {
                sendEffect(ResponseUiEffect.TokenExpired(errorResponseHeader.error_description))
            }
        }
        val errorBody = response.errorBody()?.string()
        if (!TextUtils.isEmpty(errorBody)) {
            val errorBodyObject = try {
                GsonUtil.gson.fromJson(errorBody, ResponseErrorBody::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            } as ResponseErrorBody?
            if (errorBodyObject != null) {
                updateUiState {
                    copy().onFailureInventoryTransfers(response.code(), ResponseError(errorResponseHeader, errorBodyObject))
                }
            } else {
                updateUiState {
                    copy().onFailure(response.code(), errorBody)
                }
            }
        } else {
            updateUiState {
                copy().onFailure(response.code(), errorBody)
            }
        }
        return null
    }

    private suspend fun getCurrency(token: OAuth2Token, id: String): Currency? {
        val response = repository.getCurrency(token, id).awaitResponse()
        if (response.isSuccessful) {
            return response.body()
        }
        val errorHeader = response.headers().get("www-authenticate")
            ?: response.headers().get("WWW-Authenticate")
        var errorResponseHeader: ResponseErrorHeader? = null
        if (!TextUtils.isEmpty(errorHeader)) {
            val split = errorHeader!!.split(",")
            if (split.isNotEmpty()) {
                var error = ""
                var errorDescription = ""
                for (param in split) {
                    if (param.contains("error=")) {
                        error = param.substring(param.indexOf("error=") + "error=".length)
                            .trim().replace("\"", "")
                    } else if (param.contains("error_description=")) {
                        errorDescription =
                            param.substring(param.indexOf("error_description=") + "error_description=".length)
                                .trim().replace("\"", "")
                    }
                }
                if (!TextUtils.isEmpty(error) && !TextUtils.isEmpty(errorDescription)) {
                    errorResponseHeader = ResponseErrorHeader(error, errorDescription)
                }
            }
        }
        if (errorResponseHeader != null && !TextUtils.isEmpty(errorResponseHeader.error)) {
            if (errorResponseHeader.error.lowercase() == NetworkConfig.ERROR_INVALID_TOKEN) {
                sendEffect(ResponseUiEffect.TokenExpired(errorResponseHeader.error_description))
            }
        }
        val errorBody = response.errorBody()?.string()
        if (!TextUtils.isEmpty(errorBody)) {
            val errorBodyObject = try {
                GsonUtil.gson.fromJson(errorBody, ResponseErrorBody::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            } as ResponseErrorBody?
            if (errorBodyObject != null) {
                updateUiState {
                    copy().onFailureInventoryTransfers(response.code(), ResponseError(errorResponseHeader, errorBodyObject))
                }
            } else {
                updateUiState {
                    copy().onFailure(response.code(), errorBody)
                }
            }
        } else {
            updateUiState {
                copy().onFailure(response.code(), errorBody)
            }
        }
        return null
    }

    override fun refreshToken(token: OAuth2Token) {
        updateUiState {
            copy().onLoadingState()
        }
        request(
            request = { repository.refreshToken(token) },
            successCallback = { body ->
                updateUiState {
                    copy().onSuccessToken(body)
                }
            },
            successEmptyCallback = {},
            failureCallback = { code, message ->
                updateUiState {
                    copy().onFailure(code, message)
                }
            },
            failureErrorBodyCallback = { code, _, errorBody ->
                if (errorBody != null) {
                    updateUiState {
                        copy().onFailureToken(code, errorBody)
                    }
                } else {
                    updateUiState {
                        copy().onFailure(code, "")
                    }
                }
            },
            convertErrorBody = { errorBody ->
                try {
                    GsonUtil.gson.fromJson(errorBody, OAuth2TokenErrorBody::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                } as OAuth2TokenErrorBody?
            },
            allowEmptyBody = false,
            allowFailureErrorBody = true
        )
    }
}
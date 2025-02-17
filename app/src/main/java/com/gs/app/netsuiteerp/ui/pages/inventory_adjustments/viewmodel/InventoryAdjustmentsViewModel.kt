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

package com.gs.app.netsuiteerp.ui.pages.inventory_adjustments.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.annotation.Keep
import com.gs.app.netsuiteerp.beans.Currency
import com.gs.app.netsuiteerp.beans.InventoryAdjustment
import com.gs.app.netsuiteerp.beans.InventoryAdjustmentsPageData
import com.gs.app.netsuiteerp.beans.InventoryAdjustmentShow
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
import com.gs.app.netsuiteerp.ui.pages.inventory_adjustments.intent.InventoryAdjustmentsUIIntent
import com.gs.app.netsuiteerp.ui.pages.inventory_adjustments.respository.InventoryAdjustmentsNetRepository
import com.gs.app.netsuiteerp.ui.pages.inventory_adjustments.state.InventoryAdjustmentsUiState
import com.gs.app.netsuiteerp.utils.GsonUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import retrofit2.awaitResponse
import javax.inject.Inject

@Keep
@HiltViewModel
class InventoryAdjustmentsViewModel @Inject constructor(
    private val repository: InventoryAdjustmentsNetRepository,
    application: Application
) : ResponseViewModel<InventoryAdjustmentsUIIntent, InventoryAdjustmentsUiState>(application) {

    override fun initUiState(): InventoryAdjustmentsUiState {
        return InventoryAdjustmentsUiState(ResponseState.None)
    }

    override fun handleIntent(intent: InventoryAdjustmentsUIIntent) {
        when (intent) {
            is InventoryAdjustmentsUIIntent.GetInventoryAdjustments -> getInventoryAdjustments(intent.token, intent.startDate, intent.endDate, intent.limit, intent.offset)
            is InventoryAdjustmentsUIIntent.RefreshToken -> refreshToken(intent.token)
        }
    }

    private fun getInventoryAdjustments(token: OAuth2Token, startDate: String?, endDate: String?, limit: Int, offset: Int) {
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
                repository.getInventoryAdjustments(token, dateLimit.toString(), limit, offset)
            },
            successCallback = { body ->
                val pageData = InventoryAdjustmentsPageData(body.count, body.hasMore, ArrayList(), body.links, body.offset, body.totalResults)
                if (!body.items.isNullOrEmpty()) {
                    loadInventoryAdjustmentPageData(token, body.items, pageData)
                } else {
                    updateUiState {
                        copy().onSuccessInventoryAdjustments(pageData)
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
                        copy().onFailureInventoryAdjustments(code, ResponseError(errorHeader, errorBody))
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

    private suspend fun loadInventoryAdjustmentPageData(token: OAuth2Token, items: List<ItemDataLink>, solveResult: InventoryAdjustmentsPageData)  {
        launchOnIO {
            if (items.size > 3) {
                val splitSize = items.size / 3
                // 启动三个并发的异步任务
                val jobs = listOf(
                    async { loadInventoryAdjustmentShows(token, items.subList(0, splitSize)) },
                    async { loadInventoryAdjustmentShows(token, items.subList(splitSize, 2 * splitSize)) },
                    async { loadInventoryAdjustmentShows(token, items.subList(2 * splitSize, items.size)) }
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
                    copy().onSuccessInventoryAdjustments(solveResult)
                }
            } else {
                val inventoryAdjustmentShows = loadInventoryAdjustmentShows(token, items)
                if (inventoryAdjustmentShows.isEmpty()) {
                    return@launchOnIO
                }
                solveResult.items?.addAll(inventoryAdjustmentShows)
                updateUiState {
                    copy().onSuccessInventoryAdjustments(solveResult)
                }
            }
        }
    }

    private suspend fun loadInventoryAdjustmentShows(token: OAuth2Token, items: List<ItemDataLink>): List<InventoryAdjustmentShow> {
        val result = ArrayList<InventoryAdjustmentShow>()
        for (index in items.indices) {
            val inventoryAdjustmentShow = loadInventoryAdjustmentShow(token, items[index]) ?: return ArrayList()
            result.add(inventoryAdjustmentShow)
        }
        return result
    }

    private suspend fun loadInventoryAdjustmentShow(token: OAuth2Token, itemDataLink: ItemDataLink): InventoryAdjustmentShow? {
        val inventoryAdjustment = getInventoryAdjustment(token, itemDataLink.id) ?: return null
        var subsidiary: Subsidiary? = null
        if (inventoryAdjustment.subsidiary.links.isNullOrEmpty()) {
            subsidiary = getSubsidiary(token, inventoryAdjustment.subsidiary.id!!)
            subsidiary?.let {
                NetworkCache.setCache(NetworkConfig.BASE_REST_URL + NetworkConfig.REST_PATH_SUBSIDIARY + "/" + inventoryAdjustment.subsidiary.id, GsonUtil.gson.toJson(it))
            }
        } else {
            val subsidiaryCache = NetworkCache.getCache(inventoryAdjustment.subsidiary.links[0].href!!)
            if (TextUtils.isEmpty(subsidiaryCache)) {
                subsidiary = getSubsidiary(token, inventoryAdjustment.subsidiary.id!!)
                subsidiary?.let {
                    NetworkCache.setCache(inventoryAdjustment.subsidiary.links[0].href!!, GsonUtil.gson.toJson(it))
                }
            } else {
                try {
                    subsidiary = GsonUtil.gson.fromJson(subsidiaryCache, Subsidiary::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                    subsidiary = getSubsidiary(token, inventoryAdjustment.subsidiary.id!!)
                    subsidiary?.let {
                        NetworkCache.setCache(inventoryAdjustment.subsidiary.links[0].href!!, GsonUtil.gson.toJson(it))
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
        return InventoryAdjustmentShow(itemDataLink, inventoryAdjustment, subsidiary, currency)
    }

    private suspend fun getInventoryAdjustment(token: OAuth2Token, id: String): InventoryAdjustment? {
        val response = repository.getInventoryAdjustment(token, id).awaitResponse()
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
                    copy().onFailureInventoryAdjustments(response.code(), ResponseError(errorResponseHeader, errorBodyObject))
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
                    copy().onFailureInventoryAdjustments(response.code(), ResponseError(errorResponseHeader, errorBodyObject))
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
                    copy().onFailureInventoryAdjustments(response.code(), ResponseError(errorResponseHeader, errorBodyObject))
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
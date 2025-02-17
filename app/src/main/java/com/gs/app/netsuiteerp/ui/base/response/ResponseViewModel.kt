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

package com.gs.app.netsuiteerp.ui.base.response

import android.app.Application
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.gs.app.netsuiteerp.NetsuiteErpApp
import com.gs.app.netsuiteerp.R
import com.gs.app.netsuiteerp.beans.OAuth2Token
import com.gs.app.netsuiteerp.beans.ResponseErrorHeader
import com.gs.app.netsuiteerp.network.NetworkConfig
import com.gs.library.mvi.kotlin.BaseViewModel
import com.gs.library.mvi.kotlin.IUiIntent
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.awaitResponse

abstract class ResponseViewModel<UiIntent : IUiIntent, UiState : ResponseUiState> (application: Application)
    : BaseViewModel<UiIntent, UiState, ResponseUiEffect>(application) {
    protected fun <Body : Any, ErrorBody :Any> request(
        request: () -> Call<Body>,
        successCallback: suspend (Body) -> Unit,
        successEmptyCallback: suspend () -> Unit,
        failureCallback: suspend (code: Int, message: String?) -> Unit,
        failureErrorBodyCallback: suspend (code: Int, errorHeader: ResponseErrorHeader?, errorBody: ErrorBody?) -> Unit,
        convertErrorBody: suspend (errorBody: String) -> ErrorBody?,
        allowEmptyBody: Boolean,// 允许网络请求成功的应答的body为空
        allowFailureErrorBody: Boolean // 允许解析网络请求失败的应答的body
    ) {
        viewModelScope.launch {
            try {
                val response = request().awaitResponse()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        successCallback(body)
                    } else if (allowEmptyBody) {
                        successEmptyCallback()
                    } else {
                        failureCallback(NetworkConfig.ERROR_CODE_EMPTY_BODY, NetsuiteErpApp.getApplication().getString(R.string.empty_response_error))
                    }
                } else {
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
                    if (allowFailureErrorBody) {
                        val errorBody = response.errorBody()?.string()
                        if (!TextUtils.isEmpty(errorBody)) {
                            val errorBodyObject = convertErrorBody(errorBody!!)
                            if (errorBodyObject != null) {
                                failureErrorBodyCallback(response.code(), errorResponseHeader, errorBodyObject)
                            } else {
                                failureCallback(response.code(), errorBody)
                            }
                        } else {
                            failureCallback(response.code(), errorBody)
                        }
                    } else {
                        failureCallback(response.code(), response.errorBody()?.string())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ResponseViewModel", "onFailure(): ${e.message}")
                failureCallback(NetworkConfig.ERROR_CODE_NETWORK, e.message)
            }
        }
    }

    protected abstract fun refreshToken(token: OAuth2Token)
}
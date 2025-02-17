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

package com.gs.app.netsuiteerp.ui.pages.home.viewmodel

import android.app.Application
import androidx.annotation.Keep
import com.gs.app.netsuiteerp.beans.ResponseError
import com.gs.app.netsuiteerp.beans.OAuth2Authorize
import com.gs.app.netsuiteerp.beans.OAuth2TokenErrorBody
import com.gs.app.netsuiteerp.beans.OAuth2Token
import com.gs.app.netsuiteerp.beans.ResponseErrorBody
import com.gs.app.netsuiteerp.network.response.EmptyResponse
import com.gs.app.netsuiteerp.ui.base.response.ResponseState
import com.gs.app.netsuiteerp.ui.base.response.ResponseViewModel
import com.gs.app.netsuiteerp.ui.pages.home.intent.HomeUIIntent
import com.gs.app.netsuiteerp.ui.pages.home.respository.HomeNetRepository
import com.gs.app.netsuiteerp.ui.pages.home.state.HomeUiState
import com.gs.app.netsuiteerp.utils.GsonUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Keep
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeNetRepository,
    application: Application
) : ResponseViewModel<HomeUIIntent, HomeUiState>(application) {

    override fun initUiState(): HomeUiState {
        return HomeUiState(ResponseState.None)
    }

    override fun handleIntent(intent: HomeUIIntent) {
        when (intent) {
            is HomeUIIntent.GetEmployee -> getEmployee(intent.authorize, intent.token)
            is HomeUIIntent.RefreshToken -> refreshToken(intent.token)
            is HomeUIIntent.RevokeToken -> revokeToken(intent.token)
        }
    }

    private fun getEmployee(authorize: OAuth2Authorize, token: OAuth2Token) {
        updateUiState {
            copy().onLoadingState()
        }
        request(
            request = { repository.getEmployee(authorize, token) },
            successCallback = { body ->
                updateUiState {
                    copy().onSuccessEmployee(body)
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
                        copy().onFailureEmployee(code, ResponseError(errorHeader, errorBody))
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

    private fun revokeToken(token: OAuth2Token) {
        updateUiState {
            copy().onLoadingState()
        }
        request(
            request = { repository.revokeToken(token) },
            successCallback = { body ->
                updateUiState {
                    copy().onSuccessRevokeToken(body)
                }
            },
            successEmptyCallback = {
                updateUiState {
                    copy().onSuccessRevokeToken(EmptyResponse())
                }
            },
            failureCallback = { code, message ->
                updateUiState {
                    copy().onFailure(code, message)
                }
            },
            failureErrorBodyCallback = { code, _, errorBody ->
                if (errorBody != null) {
                    updateUiState {
                        copy().onFailureRevokeToken(code, errorBody)
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
            allowEmptyBody = true,
            allowFailureErrorBody = true
        )
    }
}
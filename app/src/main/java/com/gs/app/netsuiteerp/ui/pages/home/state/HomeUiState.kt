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

package com.gs.app.netsuiteerp.ui.pages.home.state

import com.gs.app.netsuiteerp.beans.Employee
import com.gs.app.netsuiteerp.beans.ResponseError
import com.gs.app.netsuiteerp.beans.OAuth2TokenErrorBody
import com.gs.app.netsuiteerp.beans.OAuth2Token
import com.gs.app.netsuiteerp.network.response.EmptyResponse
import com.gs.app.netsuiteerp.ui.base.response.ResponseState
import com.gs.app.netsuiteerp.ui.base.response.ResponseUiState

data class HomeUiState(val state: ResponseState) : ResponseUiState {

    override fun onNoneState(): HomeUiState {
        return when (state) {
            is ResponseState.None -> this
            else -> copy(state = ResponseState.None)
        }
    }

    override fun onLoadingState() : HomeUiState {
        return when (state) {
            is ResponseState.Loading -> this
            else -> copy(state = ResponseState.Loading)
        }
    }

    override fun onFailure(code: Int, message: String?) : HomeUiState {
        return when (state) {
            is ResponseState.None -> this
            else -> copy(state = ResponseState.Failure(code, message))
        }
    }

    override fun onFailureWithError(code: Int, data: Any): HomeUiState {
        return when (state) {
            is ResponseState.None -> this
            else -> copy(state = ResponseState.FailureWithError(code, data))
        }
    }

    override fun onSuccess(data: Any): HomeUiState {
        return when (state) {
            is ResponseState.None -> this
            else -> copy(state = ResponseState.Success(data))
        }
    }

    fun onFailureToken(code: Int, error: OAuth2TokenErrorBody) : HomeUiState {
        return onFailureWithError(code, error)
    }

    fun onFailureEmployee(code: Int, error: ResponseError) : HomeUiState {
        return onFailureWithError(code, error)
    }

    fun onFailureRevokeToken(code: Int, error: OAuth2TokenErrorBody) : HomeUiState {
        return onFailureWithError(code, error)
    }

    fun onSuccessToken(data: OAuth2Token) : HomeUiState {
        return onSuccess(data)
    }

    fun onSuccessEmployee(data: Employee) : HomeUiState {
        return onSuccess(data)
    }

    fun onSuccessRevokeToken(data: EmptyResponse) : HomeUiState {
        return onSuccess(data)
    }
}
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

import com.gs.library.mvi.kotlin.IUiState

sealed class ResponseState {
    data object None : ResponseState()
    data object Loading : ResponseState()
    data class Failure(val code: Int, val message: String?) : ResponseState()
    data class FailureWithError<T>(val code: Int, val data: T) : ResponseState()
    data class Success<T>(val data: T) : ResponseState()
}

interface ResponseUiState : IUiState {
    fun onNoneState(): ResponseUiState
    fun onLoadingState(): ResponseUiState
    fun onFailure(code: Int, message: String?): ResponseUiState
    fun onFailureWithError(code: Int, data: Any): ResponseUiState
    fun onSuccess(data: Any): ResponseUiState
}
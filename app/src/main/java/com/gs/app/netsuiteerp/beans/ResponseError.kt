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

package com.gs.app.netsuiteerp.beans

data class ResponseError(val errorHeader: ResponseErrorHeader?, val errorBody: ResponseErrorBody?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ResponseError) return false
        if (errorHeader != other.errorHeader) return false
        if (errorBody != other.errorBody) return false
        return true
    }

    override fun hashCode(): Int {
        var result = errorHeader?.hashCode() ?: 0
        result = 31 * result + (errorBody?.hashCode() ?: 0)
        return result
    }
}
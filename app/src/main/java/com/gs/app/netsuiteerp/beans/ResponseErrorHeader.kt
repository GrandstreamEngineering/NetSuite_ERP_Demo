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

data class ResponseErrorHeader(
    val error: String,
    val error_description: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ResponseErrorHeader) return false
        if (error != other.error) return false
        if (error_description != other.error_description) return false
        return true
    }

    override fun hashCode(): Int {
        var result = error.hashCode()
        result = 31 * result + error_description.hashCode()
        return result
    }
}
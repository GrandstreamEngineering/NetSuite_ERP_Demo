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

data class OAuth2Authorize(
    val code: String,
    val company: String,
    val entity: String,
    val role: String,
    val state: String?,
    val error: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OAuth2Authorize) return false
        if (code != other.code) return false
        if (company != other.company) return false
        if (entity != other.entity) return false
        if (role != other.role) return false
        if (state != other.state) return false
        if (error != other.error) return false
        return true
    }

    override fun hashCode(): Int {
        var result = code.hashCode()
        result = 31 * result + company.hashCode()
        result = 31 * result + entity.hashCode()
        result = 31 * result + role.hashCode()
        result = 31 * result + (state?.hashCode() ?: 0)
        result = 31 * result + (error?.hashCode() ?: 0)
        return result
    }
}
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

import com.google.gson.annotations.SerializedName

data class ResponseErrorBody(
    @SerializedName("o:errorDetails")
    val errorDetails: List<OErrorDetail>?,
    val status: Int,
    val title: String?,
    val type: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ResponseErrorBody) return false
        if (status != other.status) return false
        if (title != other.title) return false
        if (type != other.type) return false
        if (errorDetails == null && other.errorDetails != null
            || errorDetails != null && other.errorDetails == null
            || (errorDetails != null && other.errorDetails != null && errorDetails != other.errorDetails)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = errorDetails?.hashCode() ?: 0
        result = 31 * result + status
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        return result
    }
}

data class OErrorDetail(
    val detail: String?,
    @SerializedName("o:errorCode")
    val errorCode: String?,
    @SerializedName("o:errorHeader")
    val errorHeader: String?,
    @SerializedName("o:errorPath")
    val errorPath: String?,
    @SerializedName("o:errorQueryParam")
    val errorQueryParam: String?,
    @SerializedName("o:errorUrl")
    val errorUrl: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OErrorDetail) return false
        if (detail != other.detail) return false
        if (errorCode != other.errorCode) return false
        if (errorHeader != other.errorHeader) return false
        if (errorPath != other.errorPath) return false
        if (errorQueryParam != other.errorQueryParam) return false
        if (errorUrl != other.errorUrl) return false
        return true
    }

    override fun hashCode(): Int {
        var result = detail?.hashCode() ?: 0
        result = 31 * result + (errorCode?.hashCode() ?: 0)
        result = 31 * result + (errorHeader?.hashCode() ?: 0)
        result = 31 * result + (errorPath?.hashCode() ?: 0)
        result = 31 * result + (errorQueryParam?.hashCode() ?: 0)
        result = 31 * result + (errorUrl?.hashCode() ?: 0)
        return result
    }
}
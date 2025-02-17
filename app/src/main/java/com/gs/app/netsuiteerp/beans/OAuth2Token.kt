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

data class OAuth2Token(
    // 采用JSON Web令牌（JWT）格式, 访问令牌的有效期为60分钟
    val access_token: String?,
    // 采用JSON JWT格式, 刷新令牌的有效期为七天
    // 重要提示: 如果您使用OAuth 2.0的公共客户端, 则刷新令牌仅在三个小时内有效，并且仅供一次性使用
    val refresh_token: String?,
    // 该值表示访问令牌有效的时间段, 单位为秒, 始终为3600。
    val expires_in: String?,
    // 始终是bearer
    val token_type: String?,
    // 此参数是OAuth 2.0的一部分, 但它仅在NetSuite中用作OIDC提供程序功能流
    // 您不需要将token_id参数配置为OAuth 2.0功能流的一部分
    val id_token: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OAuth2Token) return false
        if (access_token != other.access_token) return false
        if (refresh_token != other.refresh_token) return false
        if (expires_in != other.expires_in) return false
        if (token_type != other.token_type) return false
        if (id_token != other.id_token) return false
        return true
    }

    override fun hashCode(): Int {
        var result = access_token?.hashCode() ?: 0
        result = 31 * result + (refresh_token?.hashCode() ?: 0)
        result = 31 * result + (expires_in?.hashCode() ?: 0)
        result = 31 * result + (token_type?.hashCode() ?: 0)
        result = 31 * result + (id_token?.hashCode() ?: 0)
        return result
    }
}
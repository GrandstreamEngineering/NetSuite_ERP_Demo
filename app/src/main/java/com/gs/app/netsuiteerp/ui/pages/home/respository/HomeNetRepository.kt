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

package com.gs.app.netsuiteerp.ui.pages.home.respository

import com.gs.app.netsuiteerp.BuildConfig
import com.gs.app.netsuiteerp.beans.Employee
import com.gs.app.netsuiteerp.beans.OAuth2Authorize
import com.gs.app.netsuiteerp.beans.OAuth2Token
import com.gs.app.netsuiteerp.network.NetworkConfig
import com.gs.app.netsuiteerp.network.RetrofitClient
import com.gs.app.netsuiteerp.network.response.EmptyResponse
import okhttp3.Credentials
import retrofit2.Call

class HomeNetRepository {
    fun getEmployee(authorize: OAuth2Authorize, token: OAuth2Token): Call<Employee> {
        return RetrofitClient.api.getEmployee(authorize.entity, token.token_type + " " + token.access_token)
    }

    fun refreshToken(token: OAuth2Token): Call<OAuth2Token> {
        return RetrofitClient.api.refreshToken(
            Credentials.basic(BuildConfig.NETSUITE_CONSUMER_KEY, BuildConfig.NETSUITE_CONSUMER_SECRET),
            NetworkConfig.OAUTH2_REFRESH_TOKEN_GRANT_TYPE_VALUE,
            token.refresh_token!!
        )
    }

    fun revokeToken(token: OAuth2Token): Call<EmptyResponse> {
        return RetrofitClient.api.revokeToken(
            Credentials.basic(BuildConfig.NETSUITE_CONSUMER_KEY, BuildConfig.NETSUITE_CONSUMER_SECRET),
            token.refresh_token!!
        )
    }
}
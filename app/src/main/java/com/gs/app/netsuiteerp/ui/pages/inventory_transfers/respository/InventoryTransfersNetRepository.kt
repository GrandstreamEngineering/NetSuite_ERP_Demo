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

package com.gs.app.netsuiteerp.ui.pages.inventory_transfers.respository

import android.text.TextUtils
import com.gs.app.netsuiteerp.BuildConfig
import com.gs.app.netsuiteerp.beans.Currency
import com.gs.app.netsuiteerp.beans.InventoryTransfer
import com.gs.app.netsuiteerp.beans.OAuth2Token
import com.gs.app.netsuiteerp.beans.PageData
import com.gs.app.netsuiteerp.beans.Subsidiary
import com.gs.app.netsuiteerp.network.NetworkConfig
import com.gs.app.netsuiteerp.network.RetrofitClient
import okhttp3.Credentials
import retrofit2.Call

class InventoryTransfersNetRepository {
    fun getInventoryTransfers(token: OAuth2Token, q: String?, limit: Int, offset: Int): Call<PageData> {
        return if (TextUtils.isEmpty(q))
            if (limit > 0)
                RetrofitClient.api.getInventoryTransfers(token.token_type + " " + token.access_token, "$limit", "$offset")
            else
                RetrofitClient.api.getInventoryTransfers(token.token_type + " " + token.access_token)
        else
            if (limit > 0)
                RetrofitClient.api.getInventoryTransfers(token.token_type + " " + token.access_token, q!!, "$limit", "$offset")
            else
                RetrofitClient.api.getInventoryTransfers(token.token_type + " " + token.access_token, q!!)
    }

    fun getInventoryTransfer(token: OAuth2Token, id: String): Call<InventoryTransfer> {
        return RetrofitClient.api.getInventoryTransfer(id, token.token_type + " " + token.access_token)
    }

    fun getSubsidiary(token: OAuth2Token, id: String): Call<Subsidiary> {
        return RetrofitClient.api.getSubsidiary(id, token.token_type + " " + token.access_token)
    }

    fun getCurrency(token: OAuth2Token, id: String): Call<Currency> {
        return RetrofitClient.api.getCurrency(id, token.token_type + " " + token.access_token)
    }

    fun refreshToken(token: OAuth2Token): Call<OAuth2Token> {
        return RetrofitClient.api.refreshToken(
            Credentials.basic(BuildConfig.NETSUITE_CONSUMER_KEY, BuildConfig.NETSUITE_CONSUMER_SECRET),
            NetworkConfig.OAUTH2_REFRESH_TOKEN_GRANT_TYPE_VALUE,
            token.refresh_token!!
        )
    }
}
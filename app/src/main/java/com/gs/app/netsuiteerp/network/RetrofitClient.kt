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

package com.gs.app.netsuiteerp.network

import com.google.gson.Gson
import com.gs.app.netsuiteerp.BuildConfig
import com.gs.app.netsuiteerp.network.apis.NetsuiteServiceApi
import com.gs.app.netsuiteerp.network.interceptor.LoggingInterceptor
import com.gs.app.netsuiteerp.network.interceptor.TrackCallFactory
import com.gs.app.netsuiteerp.network.interceptor.TrackEventListenerFactory
import com.gs.app.netsuiteerp.network.interceptor.TrackInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    // 超时时间: 20s
    private const val TIME_OUT = 20

    // gson
    private val gson = Gson().newBuilder().setLenient().serializeNulls().create()

    // network api
    val api by lazy { getService(NetsuiteServiceApi::class.java, String.format(NetworkConfig.BASE_REST_URL, BuildConfig.NETSUITE_ACCOUNT_ID_IN_URL)) }

    private val client: OkHttpClient
        get() {
            val builder = OkHttpClient.Builder()
            builder
//                .addInterceptor(TokenCheckInterceptor())
                .addInterceptor(LoggingInterceptor())
                .addInterceptor(TrackInterceptor())
                .eventListenerFactory(TrackEventListenerFactory)
                .sslSocketFactory(SSLSocketManager.sslSocketFactory, SSLSocketManager.x509TrustManager!!)
                .hostnameVerifier(SSLSocketManager.hostnameVerifier)
                .connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
            return builder.build()
        }

    // 动态代理构建Retrofit的服务
    private fun <S> getService(serviceClass: Class<S>, baseUrl: String): S {
        return Retrofit.Builder()
            .callFactory(TrackCallFactory(client))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(baseUrl)
            .build()
            .create(serviceClass)
    }
}
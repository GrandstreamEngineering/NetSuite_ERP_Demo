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

import com.gs.app.netsuiteerp.network.interceptor.LoggingInterceptor
import com.gs.app.netsuiteerp.network.interceptor.TrackEventListenerFactory
import com.gs.app.netsuiteerp.network.interceptor.TrackInterceptor
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request.Builder
import okhttp3.RequestBody
import java.util.concurrent.TimeUnit


object OkhttpHelper {
    // 超时时间: 20s
    private const val TIME_OUT = 20

    val client: OkHttpClient
        get() = OkHttpClient.Builder()
            .addInterceptor(LoggingInterceptor())
            .addInterceptor(TrackInterceptor())
            .eventListenerFactory(TrackEventListenerFactory)
            .sslSocketFactory(
                SSLSocketManager.sslSocketFactory,
                SSLSocketManager.x509TrustManager!!
            )
            .hostnameVerifier(SSLSocketManager.hostnameVerifier)
            .connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
            .build()

    fun get(baseUrl: String, params: Map<String, String>?, headers: Map<String, String>?, callback: Callback): Call {
        val urlBuilder = StringBuilder(baseUrl)
        if (!params.isNullOrEmpty()) {
            if (!urlBuilder.contains("?")) {
                urlBuilder.append("?")
            }
            val containsParams = urlBuilder.toString().contains("?") && urlBuilder.toString().contains("&")
            var isFirst = true
            for (param in params.entries) {
                if (!isFirst || containsParams) {
                    urlBuilder.append("&")
                } else {
                    isFirst = false
                }
                urlBuilder.append(param.key).append("=").append(param.value)
            }
        }

        val requestBuilder = Builder()
            .url(urlBuilder.toString())
            .addHeader("Content-Type", "application/json; charset=UTF-8")
            .addHeader("Connection", "keep-alive")

        if (!headers.isNullOrEmpty()) {
            for (header in headers.entries) {
                if (header.key.equals("Content-Type", true)) {
                    requestBuilder.header("Content-Type", header.value)
                } else if (header.key.equals("Connection", true)) {
                    requestBuilder.header("Connection", header.value)
                } else {
                    requestBuilder.addHeader(header.key, header.value)
                }
            }
        }

        val call = client.newCall(requestBuilder.get().build())
        call.enqueue(callback)
        return call
    }

    fun postForm(baseUrl: String, params: Map<String, String>?, headers: Map<String, String>?, bodyParams: Map<String, String>?, callback: Callback): Call {
        val urlBuilder = StringBuilder(baseUrl)
        if (!params.isNullOrEmpty()) {
            if (!urlBuilder.contains("?")) {
                urlBuilder.append("?")
            }
            val containsParams = urlBuilder.toString().contains("?") && urlBuilder.toString().contains("&")
            var isFirst = true
            for (param in params.entries) {
                if (!isFirst || containsParams) {
                    urlBuilder.append("&")
                } else {
                    isFirst = false
                }
                urlBuilder.append(param.key).append("=").append(param.value)
            }
        }

        val requestBuilder = Builder()
            .url(urlBuilder.toString())
            .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            .addHeader("Connection", "keep-alive")

        if (!headers.isNullOrEmpty()) {
            for (header in headers.entries) {
                if (header.key.equals("Content-Type", true)) {
                    requestBuilder.header("Content-Type", header.value)
                } else if (header.key.equals("Connection", true)) {
                    requestBuilder.header("Connection", header.value)
                } else {
                    requestBuilder.addHeader(header.key, header.value)
                }
            }
        }

        val bodyBuilder = FormBody.Builder()
        if (!bodyParams.isNullOrEmpty()) {
            for (param in bodyParams.entries) {
                bodyBuilder.addEncoded(param.key, param.value)
            }
        }
        val call = client.newCall(requestBuilder.post(bodyBuilder.build()).build())
        call.enqueue(callback)
        return call
    }

    fun postJson(baseUrl: String, params: Map<String, String>?, headers: Map<String, String>?, body: String?, callback: Callback): Call {
        val urlBuilder = StringBuilder(baseUrl)
        if (!params.isNullOrEmpty()) {
            if (!urlBuilder.contains("?")) {
                urlBuilder.append("?")
            }
            val containsParams = urlBuilder.toString().contains("?") && urlBuilder.toString().contains("&")
            var isFirst = true
            for (param in params.entries) {
                if (!isFirst || containsParams) {
                    urlBuilder.append("&")
                } else {
                    isFirst = false
                }
                urlBuilder.append(param.key).append("=").append(param.value)
            }
        }

        val requestBuilder = Builder()
            .url(urlBuilder.toString())
            .addHeader("Content-Type", "application/json; charset=UTF-8")
            .addHeader("Connection", "keep-alive")

        if (!headers.isNullOrEmpty()) {
            for (header in headers.entries) {
                if (header.key.equals("Content-Type", true)) {
                    requestBuilder.header("Content-Type", header.value)
                } else if (header.key.equals("Connection", true)) {
                    requestBuilder.header("Connection", header.value)
                } else {
                    requestBuilder.addHeader(header.key, header.value)
                }
            }
        }

        val call = client.newCall(requestBuilder.post(RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), body ?: "{}")).build())
        call.enqueue(callback)
        return call
    }

    fun postText(baseUrl: String, params: Map<String, String>?, headers: Map<String, String>?, body: String?, callback: Callback): Call {
        val urlBuilder = StringBuilder(baseUrl)
        if (!params.isNullOrEmpty()) {
            if (!urlBuilder.contains("?")) {
                urlBuilder.append("?")
            }
            val containsParams = urlBuilder.toString().contains("?") && urlBuilder.toString().contains("&")
            var isFirst = true
            for (param in params.entries) {
                if (!isFirst || containsParams) {
                    urlBuilder.append("&")
                } else {
                    isFirst = false
                }
                urlBuilder.append(param.key).append("=").append(param.value)
            }
        }

        val requestBuilder = Builder()
            .url(urlBuilder.toString())
            .addHeader("Content-Type", "text/plain; charset=UTF-8")
            .addHeader("Connection", "keep-alive")

        if (!headers.isNullOrEmpty()) {
            for (header in headers.entries) {
                if (header.key.equals("Content-Type", true)) {
                    requestBuilder.header("Content-Type", header.value)
                } else if (header.key.equals("Connection", true)) {
                    requestBuilder.header("Connection", header.value)
                } else {
                    requestBuilder.addHeader(header.key, header.value)
                }
            }
        }

        val call = client.newCall(requestBuilder.post(RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), body ?: "")).build())
        call.enqueue(callback)
        return call
    }

    fun patchForm(baseUrl: String, params: Map<String, String>?, headers: Map<String, String>?, bodyParams: Map<String, String>?, callback: Callback): Call {
        val urlBuilder = StringBuilder(baseUrl)
        if (!params.isNullOrEmpty()) {
            if (!urlBuilder.contains("?")) {
                urlBuilder.append("?")
            }
            val containsParams = urlBuilder.toString().contains("?") && urlBuilder.toString().contains("&")
            var isFirst = true
            for (param in params.entries) {
                if (!isFirst || containsParams) {
                    urlBuilder.append("&")
                } else {
                    isFirst = false
                }
                urlBuilder.append(param.key).append("=").append(param.value)
            }
        }

        val requestBuilder = Builder()
            .url(urlBuilder.toString())
            .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            .addHeader("Connection", "keep-alive")

        if (!headers.isNullOrEmpty()) {
            for (header in headers.entries) {
                if (header.key.equals("Content-Type", true)) {
                    requestBuilder.header("Content-Type", header.value)
                } else if (header.key.equals("Connection", true)) {
                    requestBuilder.header("Connection", header.value)
                } else {
                    requestBuilder.addHeader(header.key, header.value)
                }
            }
        }

        val bodyBuilder = FormBody.Builder()
        if (!bodyParams.isNullOrEmpty()) {
            for (param in bodyParams.entries) {
                bodyBuilder.addEncoded(param.key, param.value)
            }
        }
        val call = client.newCall(requestBuilder.patch(bodyBuilder.build()).build())
        call.enqueue(callback)
        return call
    }

    fun patchJson(baseUrl: String, params: Map<String, String>?, headers: Map<String, String>?, body: String?, callback: Callback): Call {
        val urlBuilder = StringBuilder(baseUrl)
        if (!params.isNullOrEmpty()) {
            if (!urlBuilder.contains("?")) {
                urlBuilder.append("?")
            }
            val containsParams = urlBuilder.toString().contains("?") && urlBuilder.toString().contains("&")
            var isFirst = true
            for (param in params.entries) {
                if (!isFirst || containsParams) {
                    urlBuilder.append("&")
                } else {
                    isFirst = false
                }
                urlBuilder.append(param.key).append("=").append(param.value)
            }
        }

        val requestBuilder = Builder()
            .url(urlBuilder.toString())
            .addHeader("Content-Type", "application/json; charset=UTF-8")
            .addHeader("Connection", "keep-alive")

        if (!headers.isNullOrEmpty()) {
            for (header in headers.entries) {
                if (header.key.equals("Content-Type", true)) {
                    requestBuilder.header("Content-Type", header.value)
                } else if (header.key.equals("Connection", true)) {
                    requestBuilder.header("Connection", header.value)
                } else {
                    requestBuilder.addHeader(header.key, header.value)
                }
            }
        }

        val call = client.newCall(requestBuilder.patch(RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), body ?: "{}")).build())
        call.enqueue(callback)
        return call
    }

    fun patchText(baseUrl: String, params: Map<String, String>?, headers: Map<String, String>?, body: String?, callback: Callback): Call {
        val urlBuilder = StringBuilder(baseUrl)
        if (!params.isNullOrEmpty()) {
            if (!urlBuilder.contains("?")) {
                urlBuilder.append("?")
            }
            val containsParams = urlBuilder.toString().contains("?") && urlBuilder.toString().contains("&")
            var isFirst = true
            for (param in params.entries) {
                if (!isFirst || containsParams) {
                    urlBuilder.append("&")
                } else {
                    isFirst = false
                }
                urlBuilder.append(param.key).append("=").append(param.value)
            }
        }

        val requestBuilder = Builder()
            .url(urlBuilder.toString())
            .addHeader("Content-Type", "text/plain; charset=UTF-8")
            .addHeader("Connection", "keep-alive")

        if (!headers.isNullOrEmpty()) {
            for (header in headers.entries) {
                if (header.key.equals("Content-Type", true)) {
                    requestBuilder.header("Content-Type", header.value)
                } else if (header.key.equals("Connection", true)) {
                    requestBuilder.header("Connection", header.value)
                } else {
                    requestBuilder.addHeader(header.key, header.value)
                }
            }
        }

        val call = client.newCall(requestBuilder.patch(RequestBody.create(MediaType.parse("text/plain; charset=UTF-8"), body ?: "")).build())
        call.enqueue(callback)
        return call
    }

    fun putForm(baseUrl: String, params: Map<String, String>?, headers: Map<String, String>?, bodyParams: Map<String, String>?, callback: Callback): Call {
        val urlBuilder = StringBuilder(baseUrl)
        if (!params.isNullOrEmpty()) {
            if (!urlBuilder.contains("?")) {
                urlBuilder.append("?")
            }
            val containsParams = urlBuilder.toString().contains("?") && urlBuilder.toString().contains("&")
            var isFirst = true
            for (param in params.entries) {
                if (!isFirst || containsParams) {
                    urlBuilder.append("&")
                } else {
                    isFirst = false
                }
                urlBuilder.append(param.key).append("=").append(param.value)
            }
        }

        val requestBuilder = Builder()
            .url(urlBuilder.toString())
            .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            .addHeader("Connection", "keep-alive")

        if (!headers.isNullOrEmpty()) {
            for (header in headers.entries) {
                if (header.key.equals("Content-Type", true)) {
                    requestBuilder.header("Content-Type", header.value)
                } else if (header.key.equals("Connection", true)) {
                    requestBuilder.header("Connection", header.value)
                } else {
                    requestBuilder.addHeader(header.key, header.value)
                }
            }
        }

        val bodyBuilder = FormBody.Builder()
        if (!bodyParams.isNullOrEmpty()) {
            for (param in bodyParams.entries) {
                bodyBuilder.addEncoded(param.key, param.value)
            }
        }
        val call = client.newCall(requestBuilder.put(bodyBuilder.build()).build())
        call.enqueue(callback)
        return call
    }

    fun putJson(baseUrl: String, params: Map<String, String>?, headers: Map<String, String>?, body: String?, callback: Callback): Call {
        val urlBuilder = StringBuilder(baseUrl)
        if (!params.isNullOrEmpty()) {
            if (!urlBuilder.contains("?")) {
                urlBuilder.append("?")
            }
            val containsParams = urlBuilder.toString().contains("?") && urlBuilder.toString().contains("&")
            var isFirst = true
            for (param in params.entries) {
                if (!isFirst || containsParams) {
                    urlBuilder.append("&")
                } else {
                    isFirst = false
                }
                urlBuilder.append(param.key).append("=").append(param.value)
            }
        }

        val requestBuilder = Builder()
            .url(urlBuilder.toString())
            .addHeader("Content-Type", "application/json; charset=UTF-8")
            .addHeader("Connection", "keep-alive")

        if (!headers.isNullOrEmpty()) {
            for (header in headers.entries) {
                if (header.key.equals("Content-Type", true)) {
                    requestBuilder.header("Content-Type", header.value)
                } else if (header.key.equals("Connection", true)) {
                    requestBuilder.header("Connection", header.value)
                } else {
                    requestBuilder.addHeader(header.key, header.value)
                }
            }
        }

        val call = client.newCall(requestBuilder.put(RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), body ?: "{}")).build())
        call.enqueue(callback)
        return call
    }

    fun putText(baseUrl: String, params: Map<String, String>?, headers: Map<String, String>?, body: String?, callback: Callback): Call {
        val urlBuilder = StringBuilder(baseUrl)
        if (!params.isNullOrEmpty()) {
            if (!urlBuilder.contains("?")) {
                urlBuilder.append("?")
            }
            val containsParams = urlBuilder.toString().contains("?") && urlBuilder.toString().contains("&")
            var isFirst = true
            for (param in params.entries) {
                if (!isFirst || containsParams) {
                    urlBuilder.append("&")
                } else {
                    isFirst = false
                }
                urlBuilder.append(param.key).append("=").append(param.value)
            }
        }

        val requestBuilder = Builder()
            .url(urlBuilder.toString())
            .addHeader("Content-Type", "text/plain; charset=UTF-8")
            .addHeader("Connection", "keep-alive")

        if (!headers.isNullOrEmpty()) {
            for (header in headers.entries) {
                if (header.key.equals("Content-Type", true)) {
                    requestBuilder.header("Content-Type", header.value)
                } else if (header.key.equals("Connection", true)) {
                    requestBuilder.header("Connection", header.value)
                } else {
                    requestBuilder.addHeader(header.key, header.value)
                }
            }
        }

        val call = client.newCall(requestBuilder.put(RequestBody.create(MediaType.parse("text/plain; charset=UTF-8"), body ?: "")).build())
        call.enqueue(callback)
        return call
    }
}
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

package com.gs.app.netsuiteerp.network.interceptor

import android.util.Log
import com.gs.app.netsuiteerp.network.NetworkConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class LoggingInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.e("LoggingInterceptor", "intercept()")
        //这个chain里面包含了request和response
        val request: Request = chain.request()
        val t1 = System.nanoTime() //请求发起的时间
        if (NetworkConfig.DEBUG) Log.w(
            "Request,",
            String.format("发送请求 %s on %s%n%s", request.url(), request, request.headers())
        )
        val response: Response = chain.proceed(request)
        val t2 = System.nanoTime() //收到响应的时间
        val responseBody = response.peekBody((1024 * 1024).toLong())
        if (NetworkConfig.DEBUG) {
            Log.w(
                "Response,",
                String.format(
                    "接收响应: [%s]  %.1fms%n%s",
                    response.request().url(),
                    (t2 - t1) / 1e6,
                    response.headers()
                )
            )
            Log.w("body,", responseBody.string())
            // 服务器异常时会返回null字符串
//            val mediaType: MediaType = "text/plain; charset=utf-8".toMediaTypeOrNull()!!
//            val stringData = "null"
//            val responseBody: ResponseBody =  stringData.toResponseBody(mediaType)
//            val newResponse = response.newBuilder()
//                .body(responseBody)
//                .build()
//            return newResponse
        }
        return response
    }
}
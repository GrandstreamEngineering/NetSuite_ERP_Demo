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

import okhttp3.Interceptor
import okhttp3.Response


/**
 * OkHttp拦截器的方式获取网络请求信息，保存起来
 */
class TrackInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val callId = chain.request().tag() as? Long
        callId?.let {
            TrackEventListener.put(it, "code", response.code())
            TrackEventListener.put(it, "protocol", response.protocol())
            TrackEventListener.put(it, "url", response.request().url())
            TrackEventListener.put(it, "method", response.request().method())
        }

        return response
    }
}
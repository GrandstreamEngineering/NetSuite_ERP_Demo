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

import okhttp3.Call
import okhttp3.Request
import java.util.concurrent.atomic.AtomicLong

/**
 * 用于包装OkhttpClient实例，内部绑定tag-callid
 */
class TrackCallFactory(private val factory: Call.Factory) : Call.Factory {
    private val callId = AtomicLong(1L)  // 唯一标识一个请求
    override fun newCall(request: Request): Call {
        val id = callId.getAndIncrement()  // 获取新请求id
        // 重构 Request 实例，并通过tag方式带上请求id
        val newRequest = request.newBuilder().tag(id).build()
        // 将新请求传递给被装饰的 factory
        return factory.newCall(newRequest)
    }
}

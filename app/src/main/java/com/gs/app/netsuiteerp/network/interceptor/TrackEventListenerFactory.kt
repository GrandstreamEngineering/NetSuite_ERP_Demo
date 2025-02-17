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
import okhttp3.EventListener

/**
 * 每个请求申请独立的事件监听器
 */
object TrackEventListenerFactory : EventListener.Factory {
    override fun create(call: Call): EventListener {
        val callId = call.request().tag() as? Long // 获取请求id
        return TrackEventListener(callId) // 将请求id传递给事件监听器
    }
}
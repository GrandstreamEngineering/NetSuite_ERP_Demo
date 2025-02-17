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

package com.gs.app.netsuiteerp.common.login

import com.gs.app.netsuiteerp.beans.Employee
import com.gs.app.netsuiteerp.beans.OAuth2Authorize
import com.gs.app.netsuiteerp.beans.OAuth2Token

object LoginManager {
    const val SP_KEY_IS_LOGIN = "is_login"
    const val SP_KEY_AUTHORIZE = "key_authorize"
    const val SP_KEY_TOKEN = "key_token"
    const val SP_KEY_EMPLOYEE = "key_employee"

    @Volatile
    private var isLogin = false
    private var authorize: OAuth2Authorize? = null
    private var token: OAuth2Token? = null
    private var employee: Employee? = null

    @Synchronized
    fun login(authorize: OAuth2Authorize, token: OAuth2Token) {
        LoginManager.authorize = authorize
        LoginManager.token = token
        isLogin = true
    }

    @Synchronized
    fun setEmployee(employee: Employee) {
        this.employee = employee
    }

    @Synchronized
    fun logout() {
        authorize = null
        token = null
        isLogin = false
    }

    @Synchronized
    fun isLogin(): Boolean {
        return isLogin
    }

    @Synchronized
    fun getAuthorize(): OAuth2Authorize? {
        return authorize
    }

    @Synchronized
    fun getToken(): OAuth2Token? {
        return token
    }

    @Synchronized
    fun getEmployee(): Employee? {
        return employee
    }
}
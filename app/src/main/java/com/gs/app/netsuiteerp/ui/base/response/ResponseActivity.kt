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

package com.gs.app.netsuiteerp.ui.base.response

import androidx.appcompat.app.AlertDialog
import com.gs.app.erp.utils.SharedPreferenceUtil
import com.gs.app.netsuiteerp.R
import com.gs.app.netsuiteerp.beans.OAuth2Token
import com.gs.app.netsuiteerp.common.ActivityLifecycleManager
import com.gs.app.netsuiteerp.common.login.LoginManager
import com.gs.app.netsuiteerp.network.NetworkConfig
import com.gs.app.netsuiteerp.ui.base.BaseMviActivity
import com.gs.app.netsuiteerp.ui.pages.login.LoginActivity
import com.gs.app.netsuiteerp.utils.GlobalAlertDialogUtil
import com.gs.app.netsuiteerp.utils.GsonUtil
import com.gs.library.mvi.kotlin.IUiIntent

abstract class ResponseActivity<UiIntent : IUiIntent, UiState : ResponseUiState, VM : ResponseViewModel<UiIntent, UiState>> : BaseMviActivity<UiIntent, UiState, ResponseUiEffect, VM>() {

    abstract fun refreshToken()

    // 处理无效token
    protected fun handleInvalidToken(refreshToken: () -> Unit, gotoLoginForCancel: Boolean) {
        GlobalAlertDialogUtil.showDialog(AlertDialog.Builder(this)
            .setTitle(R.string.refresh_invalid_token_title)
            .setMessage(R.string.refresh_invalid_token_msg)
            .setNegativeButton(R.string.cancel) { dialog, which ->
                dialog.dismiss()
                SharedPreferenceUtil.put(this, LoginManager.SP_KEY_IS_LOGIN, false)
                SharedPreferenceUtil.put(this, LoginManager.SP_KEY_AUTHORIZE, "")
                SharedPreferenceUtil.put(this, LoginManager.SP_KEY_TOKEN, "")
                SharedPreferenceUtil.put(this, LoginManager.SP_KEY_EMPLOYEE, "")
                if (gotoLoginForCancel) {
                    LoginManager.logout()
                    ActivityLifecycleManager.instance.startActivityAndClearTask(LoginActivity::class.java)
                    finishAfterTransition()
                }
            }
            .setPositiveButton(R.string.refresh) { dialog, which ->
                dialog.dismiss()
                refreshToken()
            }
            .setCancelable(false)
        )
    }

    protected fun onRefreshTokenSuccess(newToken: OAuth2Token) {
        val oldToken = LoginManager.getToken()!!
        val token = OAuth2Token(newToken.access_token, oldToken.refresh_token, newToken.expires_in, newToken.token_type, oldToken.id_token)
        LoginManager.login(LoginManager.getAuthorize()!!, token)
        if (SharedPreferenceUtil.get(this, LoginManager.SP_KEY_IS_LOGIN, false) as Boolean) {
            SharedPreferenceUtil.put(this, LoginManager.SP_KEY_TOKEN, GsonUtil.gson.toJson(token))
        }
    }

    protected fun showTokenError(error: String) {
        when (error.lowercase()) {
            NetworkConfig.ERROR_INVALID_REQUEST -> {
                showErrorDialog(getString(R.string.oauth2_step2_error_invalid_request_msg)) {}
            }
            NetworkConfig.ERROR_INVALID_CLIENT -> {
                showErrorDialog(getString(R.string.oauth2_step2_error_invalid_client_msg)) {}
            }
            NetworkConfig.ERROR_INVALID_GRANT -> {
                showErrorDialog(getString(R.string.oauth2_step2_error_invalid_grant_msg)) {}
            }
            NetworkConfig.ERROR_UNAUTHORIZED_CLIENT -> {
                showErrorDialog(getString(R.string.oauth2_step2_error_unauthorized_client_msg)) {}
            }
            NetworkConfig.ERROR_UNSUPPORTED_GRANT_TYPE -> {
                showErrorDialog(getString(R.string.oauth2_step2_error_unsupported_grant_type_msg)) {}
            }
            NetworkConfig.ERROR_INVALID_SCOPE -> {
                showErrorDialog(getString(R.string.oauth2_step2_error_invalid_scope_msg)) {}
            }
            else -> {
                showErrorDialog(error) {}
            }
        }
    }

    protected fun showCommonErrorDialog(error: String) {
        when (error.lowercase()) {
            NetworkConfig.ERROR_INVALID_REQUEST -> {
                showErrorDialog(getString(R.string.oauth2_api_error_invalid_request_msg)) {}
            }
            NetworkConfig.ERROR_INVALID_TOKEN -> {
                refreshToken()
            }
            else -> {
                showErrorDialog(error) {}
            }
        }
    }
}
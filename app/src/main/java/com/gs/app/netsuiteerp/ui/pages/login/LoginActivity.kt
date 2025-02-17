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

package com.gs.app.netsuiteerp.ui.pages.login

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.gs.app.erp.utils.SharedPreferenceUtil
import com.gs.app.netsuiteerp.BuildConfig
import com.gs.app.netsuiteerp.R
import com.gs.app.netsuiteerp.beans.Employee
import com.gs.app.netsuiteerp.beans.ResponseError
import com.gs.app.netsuiteerp.beans.OAuth2Authorize
import com.gs.app.netsuiteerp.beans.OAuth2TokenErrorBody
import com.gs.app.netsuiteerp.beans.OAuth2Token
import com.gs.app.netsuiteerp.common.login.LoginManager
import com.gs.app.netsuiteerp.network.NetworkConfig
import com.gs.app.netsuiteerp.ui.base.response.ResponseActivity
import com.gs.app.netsuiteerp.ui.base.response.ResponseState
import com.gs.app.netsuiteerp.ui.pages.home.HomeActivity
import com.gs.app.netsuiteerp.ui.pages.login.intent.LoginUiIntent
import com.gs.app.netsuiteerp.ui.base.response.ResponseUiEffect
import com.gs.app.netsuiteerp.ui.pages.login.state.LoginUiState
import com.gs.app.netsuiteerp.ui.pages.login.viewmodel.LoginViewModel
import com.gs.app.netsuiteerp.utils.GlobalAlertDialogUtil
import com.gs.app.netsuiteerp.utils.GsonUtil
import com.gs.app.netsuiteerp.utils.ProgressDialogUtil
import com.gs.app.netsuiteerp.utils.StatusBarUtil
import com.gs.library.ui.toast.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

enum class Step {
    NONE,
    OAUTH_STEP1,
    OAUTH_STEP2,
    OAUTH_STEP3,
    OAUTH_STEP4,
    DONE
}

@AndroidEntryPoint
class LoginActivity : ResponseActivity<LoginUiIntent, LoginUiState, LoginViewModel>(), View.OnClickListener {
    private val TAG = "LoginActivity"

    private val viewModel: LoginViewModel by viewModels()

    private val toolbar: Toolbar by lazy { findViewById(R.id.toolbar) }
    private val tv_employee: TextView by lazy { findViewById(R.id.tv_employee) }
    private val btn_login: TextView by lazy { findViewById(R.id.btn_login) }
    private val cb_auto_login: AppCompatCheckBox by lazy { findViewById(R.id.cb_auto_login) }

    private var codeVerify: String? = null
    private var countDownTimer: CountDownTimer? = null

    private var step = Step.NONE

    override fun getLayoutResId(): Int {
        return R.layout.activity_login
    }

    override fun initView(savedInstanceState: Bundle?) {
        StatusBarUtil.setStatusBarColor(window, getColor(R.color.color_white))
        StatusBarUtil.setStatusBarLightMode(window, true)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)
        val titleTextView = toolbar.getChildAt(0) as TextView//主标题
        val layoutParams = titleTextView.layoutParams
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT//填充父类
        titleTextView.layoutParams = layoutParams
        titleTextView.setGravity(Gravity.CENTER_HORIZONTAL)//水平居中，CENTER，即水平也垂直，自选

        btn_login.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        val isLogin = SharedPreferenceUtil.get(this, LoginManager.SP_KEY_IS_LOGIN, false) as Boolean
        if (isLogin) {
            val authorizeValue = SharedPreferenceUtil.get(this, LoginManager.SP_KEY_AUTHORIZE, "") as String
            val tokenValue = SharedPreferenceUtil.get(this, LoginManager.SP_KEY_TOKEN, "") as String
            val employeeValue = SharedPreferenceUtil.get(this, LoginManager.SP_KEY_EMPLOYEE, "") as String
            if (!TextUtils.isEmpty(authorizeValue) && !TextUtils.isEmpty(tokenValue) && !TextUtils.isEmpty(employeeValue)) {
                try {
                    val authorize = GsonUtil.gson.fromJson(authorizeValue, OAuth2Authorize::class.java)
                    val token = GsonUtil.gson.fromJson(tokenValue, OAuth2Token::class.java)
                    val employee = GsonUtil.gson.fromJson(employeeValue, Employee::class.java)
                    viewModel.authorize = authorize
                    viewModel.token = token
                    viewModel.employee = employee
                    cb_auto_login.isChecked = true
                    tv_employee.text = if (!TextUtils.isEmpty(employee.entityId)) {
                        employee.entityId
                    } else if (!TextUtils.isEmpty(employee.firstName) || !TextUtils.isEmpty(employee.lastName)) {
                        if (!TextUtils.isEmpty(employee.firstName)) employee.firstName + " " + employee.lastName
                        else employee.lastName
                    } else {
                        employee.email ?: ""
                    }
                    tv_employee.visibility = View.VISIBLE
                    btn_login.text = getString(R.string.login_loading)
                    ProgressDialogUtil.instance.showDialog(this@LoginActivity, R.string.login_loading, true)
                    step = Step.OAUTH_STEP3
                    viewModel.sendUiIntent(LoginUiIntent.GetEmployee(authorize, token))
                } catch (e: Exception) {
                    e.printStackTrace()
                    SharedPreferenceUtil.put(this, LoginManager.SP_KEY_IS_LOGIN, false)
                    SharedPreferenceUtil.put(this, LoginManager.SP_KEY_AUTHORIZE, "")
                    SharedPreferenceUtil.put(this, LoginManager.SP_KEY_TOKEN, "")
                    SharedPreferenceUtil.put(this, LoginManager.SP_KEY_EMPLOYEE, "")
                }
            }
        }
    }

    override fun initUIEffect() {
        viewModel.uiEffectFlow.onEach { effect ->
            when (effect) {
                is ResponseUiEffect.ToastWithID -> ToastUtils.showShortToast(applicationContext, effect.id)
                is ResponseUiEffect.ToastWithStr -> ToastUtils.showShortToast(applicationContext, effect.str)
                else -> {}
            }
        }.launchIn(lifecycleScope)
    }

    override fun initUIState() {
        viewModel.uiStateFlow.onEach { uiState ->
            when (uiState.state) {
                is ResponseState.Loading -> {
                    btn_login.isEnabled = false
                    cb_auto_login.isEnabled = false
//                    ProgressDialogUtil.instance.dialog?.let {
//                        ProgressDialogUtil.instance.showDialog(this, R.string.login_loading, true)
//                    }
                }
                is ResponseState.Success<*> -> {
                    if (uiState.state.data is Employee) {
                        viewModel.employee = uiState.state.data
                        done()
                        goHome()
                    } else {
                        val token = uiState.state.data as OAuth2Token
                        if (step == Step.OAUTH_STEP4) {
                            if (viewModel.token != null) {
                                viewModel.token = OAuth2Token(token.access_token, viewModel.token!!.refresh_token, token.expires_in, token.token_type, viewModel.token!!.id_token)
                            } else {
                                viewModel.token = token
                            }
                        } else {
                            viewModel.token = token
                        }
                        step = Step.OAUTH_STEP3
                        viewModel.sendUiIntent(LoginUiIntent.GetEmployee(viewModel.authorize!!, viewModel.token!!))
                    }
                }
                is ResponseState.Failure -> {
                    if (uiState.state.code == NetworkConfig.ERROR_CODE_NETWORK) {
                        GlobalAlertDialogUtil.showDialog(AlertDialog.Builder(this)
                            .setTitle(R.string.customer_tips)
                            .setMessage(R.string.try_again_network_request)
                            .setNeutralButton(R.string.cancel) { dialog, which ->
                                dialog.dismiss()
                                viewModel.sendUiIntent(LoginUiIntent.Reset)
                            }
                            .setPositiveButton(R.string.sure) { dialog, which ->
                                dialog.dismiss()
                                btn_login.text = getString(R.string.login_loading)
                                ProgressDialogUtil.instance.showDialog(this@LoginActivity, R.string.login_loading, true)
                                when (step) {
                                    Step.OAUTH_STEP3 -> {
                                        viewModel.sendUiIntent(LoginUiIntent.GetEmployee(viewModel.authorize!!, viewModel.token!!))
                                    }
                                    Step.OAUTH_STEP4 -> {
                                        viewModel.sendUiIntent(LoginUiIntent.RefreshToken(viewModel.token!!))
                                    }
                                    else -> {
                                        viewModel.sendUiIntent(LoginUiIntent.GetToken(viewModel.authorize!!, codeVerify))
                                    }
                                }
                            }
                        )
                    } else {
                        viewModel.sendUiIntent(LoginUiIntent.Reset)
                        showErrorDialog(uiState.state.message ?: "") {}
                    }
                }
                is ResponseState.FailureWithError<*> -> {
                    val oldStep = step
                    viewModel.sendUiIntent(LoginUiIntent.Reset)
                    if (oldStep == Step.OAUTH_STEP2 || oldStep == Step.OAUTH_STEP4) {
                        if (oldStep == Step.OAUTH_STEP4) {
                            SharedPreferenceUtil.put(this, LoginManager.SP_KEY_IS_LOGIN, false)
                            SharedPreferenceUtil.put(this, LoginManager.SP_KEY_AUTHORIZE, "")
                            SharedPreferenceUtil.put(this, LoginManager.SP_KEY_TOKEN, "")
                            SharedPreferenceUtil.put(this, LoginManager.SP_KEY_EMPLOYEE, "")
                        }
                        val errorResponse = uiState.state.data as OAuth2TokenErrorBody
                        showTokenError(errorResponse.error)
                    } else {
                        val errorResponse = uiState.state.data as ResponseError
                        if (errorResponse.errorHeader != null) {
                            showCommonErrorDialog(errorResponse.errorHeader.error)
                        } else {
                            showErrorDialog(GsonUtil.gson.toJson(errorResponse.errorBody)) {}
                        }
                    }
                }
                else -> {
                    stop()
                }
            }
        }.launchIn(lifecycleScope)
    }

    override fun onClick(v: View?) {
        v?.let {
            when(it.id) {
                R.id.btn_login -> login()
                else -> {}
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let {
            Log.e(TAG, it.toString())
            parseRedirectUri(it)
        }
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        GlobalAlertDialogUtil.showDialog(AlertDialog.Builder(this)
            .setTitle(R.string.customer_tips)
            .setMessage(R.string.exit_app_message)
            .setNeutralButton(R.string.cancel) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.sure) { dialog, which ->
                dialog.dismiss()
                finishAfterTransition()
            }
        )
//        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.login_page, menu!!)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home || item.itemId == R.id.menu_exit) {
            GlobalAlertDialogUtil.showDialog(AlertDialog.Builder(this)
                .setTitle(R.string.customer_tips)
                .setMessage(R.string.exit_app_message)
                .setNeutralButton(R.string.cancel) { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.exit) { dialog, which ->
                    dialog.dismiss()
                    finishAfterTransition()
                }
           )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        stopCountDownTimer()
        super.onDestroy()
    }

    private fun login() {
        step = Step.OAUTH_STEP1
        btn_login.isEnabled = false
        cb_auto_login.isEnabled = false
        btn_login.text = String.format(getString(R.string.waiting_for_authorization), 120)
        ProgressDialogUtil.instance.showDialog(this, String.format(getString(R.string.waiting_for_authorization), 120), true)
        startCountDownTimer()
        viewModel.sendUiIntent(LoginUiIntent.Login)
//        codeVerify = LoginApi.signatureCodeChallenge(LoginApi.generateCodeChallenge(64))
        val stringBuild = StringBuilder(String.format(NetworkConfig.OAUTH2_STEP1_AUTHORIZE_TOKEN_URL, BuildConfig.NETSUITE_ACCOUNT_ID_IN_URL))
            .append("?")
            .append(NetworkConfig.OAUTH2_STEP1_RESPONSE_TYPE_KEY).append("=").append(NetworkConfig.OAUTH2_STEP1_RESPONSE_TYPE_VALUE)
            .append("&").append(NetworkConfig.OAUTH2_STEP1_CLIENT_ID_KEY).append("=").append(BuildConfig.NETSUITE_CONSUMER_KEY)
            .append("&").append(NetworkConfig.OAUTH2_STEP1_REDIRECT_URI_KEY).append("=").append(BuildConfig.NETSUITE_REDIRECT_URI)
            .append("&").append(NetworkConfig.OAUTH2_STEP1_SCOPE_KEY).append("=").append(NetworkConfig.OAUTH2_STEP1_SCOPE_VALUE)
            .append("&").append(NetworkConfig.OAUTH2_STEP1_STATE_KEY).append("=").append(NetworkConfig.OAUTH2_STEP1_STATE_VALUE)
//            .append("&").append(NetworkConfig.OAUTH2_STEP1_CODE_CHALLENGE_METHOD_KEY).append("=").append(NetworkConfig.OAUTH2_STEP1_CODE_CHALLENGE_METHOD_VALUE)
//            .append("&").append(NetworkConfig.OAUTH2_STEP1_CODE_CHALLENGE_KEY).append("=").append(codeVerify!!)
            .append("&").append(NetworkConfig.OAUTH2_STEP1_PROMPT_KEY).append("=").append(NetworkConfig.OAUTH2_STEP1_PROMPT_VALUE)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(stringBuild.toString()))
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
    }

    private fun startCountDownTimer() {
        // 创建倒计时对象
        countDownTimer = object : CountDownTimer(120000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                // 每秒调用，显示剩余时间
                val secondsRemaining = millisUntilFinished / 1000L
                val countDownTime = String.format(getString(R.string.waiting_for_authorization), secondsRemaining)
                btn_login.text = countDownTime
                if (ProgressDialogUtil.instance.isShowing) {
                    ProgressDialogUtil.instance.showDialog(this@LoginActivity, countDownTime, true)
                }
            }

            override fun onFinish() {
                // 倒计时结束时调用
                stopCountDownTimer()
                viewModel.sendUiIntent(LoginUiIntent.Reset)
            }
        }
        // 启动倒计时
        countDownTimer?.start()
    }

    private fun stopCountDownTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
    }

    private fun parseRedirectUri(uri: Uri) {
        val filterUri = Uri.parse(BuildConfig.NETSUITE_REDIRECT_URI)
        if (uri.scheme == filterUri.scheme
            && uri.host == filterUri.host
            && uri.path == filterUri.path) {
            // https://netsuite-addin.com/?
            // state=a0c34b68b7b0b7e145bf6ccaab97c169f67a1c7f631e00c18adef4da4f1d1bfd
            // &role=3
            // &entity=740
            // &company=3310559_SB1
            // &code=ac0d6f589680f1403fee036fe10e4c7c0f02f2e1f4fc5d07ca61e33f1c63cb03
            if (uri.query != null && uri.query!!.isNotEmpty()) {
                val queryParameters = getQueryParameters(uri)
                if (queryParameters.isNotEmpty()) {
                    val state = queryParameters["state"] ?: ""
                    val role = queryParameters["role"] ?: ""
                    val entity = queryParameters["entity"] ?: ""
                    val company = queryParameters["company"] ?: ""
                    val code = queryParameters["code"] ?: ""
                    val error = queryParameters["error"] ?: ""
                    if (state == NetworkConfig.OAUTH2_STEP1_STATE_VALUE) {
                        if (TextUtils.isEmpty(error)) {
                            step = Step.OAUTH_STEP2
                            stopCountDownTimer()
                            btn_login.text = getString(R.string.login_loading)
                            ProgressDialogUtil.instance.showDialog(this@LoginActivity, R.string.login_loading, true)
                            viewModel.authorize = OAuth2Authorize(code, company, entity, role, state, error)
                            viewModel.sendUiIntent(LoginUiIntent.GetToken(viewModel.authorize!!, codeVerify))
                        } else {
                            stopCountDownTimer()
                            viewModel.sendUiIntent(LoginUiIntent.Reset)
                            showStep1Error(error)
                        }
                    }
                }
            }
        }
    }

    private fun getQueryParameters(uri: Uri): Map<String, String> {
        val query: String = uri.encodedQuery ?: return HashMap()
        val parameters = HashMap<String, String>()
        var start = 0
        do {
            val next = query.indexOf('&', start)
            val end = if (next == -1) query.length else next
            var separator = query.indexOf('=', start)
            if (separator > end || separator == -1) {
                separator = end
            }
            if (separator + 1 < end) {
                val name = query.substring(start, separator)
                val value = query.substring(separator + 1, end)
                parameters[name] = value
            }
            // Move start to end of name.
            start = end + 1
        } while (start < query.length)
        return parameters
    }

    private fun showStep1Error(error: String) {
        when (error.toLowerCase()) {
            NetworkConfig.ERROR_INVALID_REQUEST -> {
                showErrorDialog(getString(R.string.oauth2_step1_error_invalid_request_msg)) {}
            }
            NetworkConfig.ERROR_UNAUTHORIZED_CLIENT -> {
                showErrorDialog(getString(R.string.oauth2_step1_error_unauthorized_client_msg)) {}
            }
            NetworkConfig.ERROR_ACCESS_DENIED -> {
                showErrorDialog(getString(R.string.oauth2_step1_error_access_denied_msg)) {}
            }
            NetworkConfig.ERROR_UNSUPPORTED_RESPONSE_TYPE -> {
                showErrorDialog(getString(R.string.oauth2_step1_error_unsupported_response_type_msg)) {}
            }
            NetworkConfig.ERROR_INVALID_SCOPE -> {
                showErrorDialog(getString(R.string.oauth2_step1_error_invalid_scope_msg)) {}
            }
            else -> {
                showErrorDialog(error) {}
            }
        }
    }

    override fun refreshToken() {
        handleInvalidToken({
            step = Step.OAUTH_STEP4
            btn_login.text = getString(R.string.login_loading)
            ProgressDialogUtil.instance.showDialog(this@LoginActivity, R.string.login_loading, true)
            viewModel.sendUiIntent(LoginUiIntent.RefreshToken(viewModel.token!!))
        }, false)
    }

    private fun stop() {
        step = Step.NONE
        stopCountDownTimer()
        btn_login.isEnabled = true
        cb_auto_login.isEnabled = true
        btn_login.text = getString(R.string.login)
        tv_employee.text = ""
        tv_employee.visibility = View.INVISIBLE
        ProgressDialogUtil.instance.dismissDialog()
    }

    private fun done() {
        step = Step.DONE
        stopCountDownTimer()
        btn_login.isEnabled = true
        cb_auto_login.isEnabled = true
        btn_login.text = getString(R.string.login)
        viewModel.employee?.let {
            tv_employee.text = if (TextUtils.isEmpty(it.firstName) && TextUtils.isEmpty(it.lastName)) {
                it.email ?: ""
            } else {
                it.firstName + " " + it.lastName
            }
            tv_employee.visibility = View.VISIBLE
        }
        ProgressDialogUtil.instance.dismissDialog()
    }

    private fun goHome() {
        LoginManager.login(viewModel.authorize!!, viewModel.token!!)
        SharedPreferenceUtil.put(this, LoginManager.SP_KEY_IS_LOGIN, cb_auto_login.isChecked)
        SharedPreferenceUtil.put(this, LoginManager.SP_KEY_AUTHORIZE, if (cb_auto_login.isChecked) GsonUtil.gson.toJson(viewModel.authorize!!) else "")
        SharedPreferenceUtil.put(this, LoginManager.SP_KEY_TOKEN, if (cb_auto_login.isChecked) GsonUtil.gson.toJson(viewModel.token!!) else "")
        viewModel.employee?.let {
            LoginManager.setEmployee(it)
            SharedPreferenceUtil.put(this, LoginManager.SP_KEY_EMPLOYEE, if (cb_auto_login.isChecked) GsonUtil.gson.toJson(it) else "")
        }
        startActivity(Intent(this, HomeActivity::class.java))
        finishAfterTransition()
    }
}
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

package com.gs.app.netsuiteerp.ui.pages.home

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gs.app.erp.utils.SharedPreferenceUtil
import com.gs.app.netsuiteerp.R
import com.gs.app.netsuiteerp.beans.Employee
import com.gs.app.netsuiteerp.beans.OAuth2TokenErrorBody
import com.gs.app.netsuiteerp.beans.OAuth2Token
import com.gs.app.netsuiteerp.beans.ResponseError
import com.gs.app.netsuiteerp.common.login.LoginManager
import com.gs.app.netsuiteerp.network.response.EmptyResponse
import com.gs.app.netsuiteerp.ui.base.response.ResponseActivity
import com.gs.app.netsuiteerp.ui.base.response.ResponseState
import com.gs.app.netsuiteerp.ui.base.response.ResponseUiEffect
import com.gs.app.netsuiteerp.ui.pages.home.adapter.ModuleListAdapter
import com.gs.app.netsuiteerp.ui.pages.home.intent.HomeUIIntent
import com.gs.app.netsuiteerp.ui.pages.home.state.HomeUiState
import com.gs.app.netsuiteerp.ui.pages.home.viewmodel.HomeViewModel
import com.gs.app.netsuiteerp.ui.pages.inventory_management.InventoryManagementActivity
import com.gs.app.netsuiteerp.ui.pages.login.LoginActivity
import com.gs.app.netsuiteerp.ui.widgets.recyclerview.GridItemDecoration
import com.gs.app.netsuiteerp.utils.GlobalAlertDialogUtil
import com.gs.app.netsuiteerp.utils.GsonUtil
import com.gs.app.netsuiteerp.utils.ProgressDialogUtil
import com.gs.library.ui.toast.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class HomeActivity : ResponseActivity<HomeUIIntent, HomeUiState, HomeViewModel>() {
    private val viewModel: HomeViewModel by viewModels()

    private val toolbar: Toolbar by lazy { findViewById(R.id.toolbar) }
    private val rv_module: RecyclerView by lazy { findViewById(R.id.rv_module) }

    private lateinit var moduleList: List<String>
    private lateinit var moduleListAdapter: ModuleListAdapter

    override fun getLayoutResId(): Int {
        return R.layout.activity_home
    }

    override fun initView(savedInstanceState: Bundle?) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)
        toolbar.setSubtitleTextColor(getColor(R.color.btn_default))

        val employee = LoginManager.getEmployee()
        employee?.let {
            updateTitle(employee)
        }

        moduleList = arrayListOf(getString(R.string.inventory_management))
        moduleListAdapter = ModuleListAdapter(this, moduleList)
        rv_module.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        rv_module.addItemDecoration(
            GridItemDecoration.Builder(this)
                .color(R.color.color_transparent)
                .size(resources.getDimensionPixelSize(R.dimen.preference_divider_l_r))
                .build()
        )
        rv_module.itemAnimator = DefaultItemAnimator()
        rv_module.adapter = moduleListAdapter
        moduleListAdapter.setListener { position ->
            if (moduleList[position] == getString(R.string.inventory_management)) {
                startActivity(Intent(this@HomeActivity, InventoryManagementActivity::class.java))
            }
        }
    }

    private fun updateTitle(employee: Employee) {
        if (!TextUtils.isEmpty(employee.entityId)) {
            toolbar.setTitle(employee.entityId)
            toolbar.setSubtitle(if (employee.subsidiary != null && !TextUtils.isEmpty(employee.subsidiary.refName))
                employee.subsidiary.refName
            else
                employee.email ?: ""
            )
        } else if (!TextUtils.isEmpty(employee.firstName) || !TextUtils.isEmpty(employee.lastName)) {
            toolbar.setTitle(if (!TextUtils.isEmpty(employee.firstName))
                employee.firstName + " " + employee.lastName
            else
                employee.lastName
            )
            toolbar.setSubtitle(if (employee.subsidiary != null && !TextUtils.isEmpty(employee.subsidiary.refName))
                employee.subsidiary.refName
            else
                employee.email ?: ""
            )
        } else {
            toolbar.setTitle(employee.email ?: "")
            toolbar.setSubtitle(if (employee.subsidiary != null && !TextUtils.isEmpty(employee.subsidiary.refName))
                employee.subsidiary.refName
            else
                ""
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_page, menu!!)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home || item.itemId == R.id.menu_exit) {
            GlobalAlertDialogUtil.showDialog(
                AlertDialog.Builder(this)
                    .setTitle(R.string.customer_tips)
                    .setMessage(R.string.exit_message)
                    .setNeutralButton(R.string.cancel) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.logout) { dialog, which ->
                        dialog.dismiss()
                        viewModel.sendUiIntent(HomeUIIntent.RevokeToken(LoginManager.getToken()!!))
                    }
                    .setPositiveButton(R.string.exit_app) { dialog, which ->
                        dialog.dismiss()
                        finish()
                    }
            )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun refreshToken() {
        handleInvalidToken({
            viewModel.sendUiIntent(HomeUIIntent.RefreshToken(LoginManager.getToken()!!))
        }, true)
    }

    override fun initUIState() {
        viewModel.uiStateFlow.onEach { uiState ->
            when (uiState.state) {
                is ResponseState.Loading -> {
                    ProgressDialogUtil.instance.showDialog(this)
                }
                is ResponseState.Success<*> -> {
                    when (val response = uiState.state.data) {
                        is Employee -> {
                            onGetEmployeeSuccess(response)
                        }
                        is OAuth2Token -> {
                            onRefreshTokenSuccess(response)
                        }
                        is EmptyResponse -> {
                            onRevokeTokenSuccess()
                        }
                    }
                    ProgressDialogUtil.instance.dismissDialog()
                }
                is ResponseState.Failure -> {
                    ProgressDialogUtil.instance.dismissDialog()
                    showErrorDialog(uiState.state.message ?: "") {}
                }
                is ResponseState.FailureWithError<*> -> {
                    ProgressDialogUtil.instance.dismissDialog()
                    val error = uiState.state.data
                    if (error is OAuth2TokenErrorBody) {
                        showTokenError(error.error)
                    } else if (error is ResponseError) {
                        if (error.errorHeader != null) {
                            showCommonErrorDialog(error.errorHeader.error)
                        } else {
                            showErrorDialog(GsonUtil.gson.toJson(error.errorBody)) {}
                        }
                    }
                }
                else -> {
                    ProgressDialogUtil.instance.dismissDialog()
                }
            }
        }.launchIn(lifecycleScope)
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

    override fun onResume() {
        super.onResume()
        if (viewModel.uiStateFlow.value.state !is ResponseState.Loading) {
            viewModel.sendUiIntent(HomeUIIntent.GetEmployee(LoginManager.getAuthorize()!!, LoginManager.getToken()!!))
        }
    }

    private fun onGetEmployeeSuccess(employee: Employee) {
        LoginManager.setEmployee(employee)
        if (SharedPreferenceUtil.get(this, LoginManager.SP_KEY_IS_LOGIN, false) as Boolean) {
            SharedPreferenceUtil.put(this, LoginManager.SP_KEY_EMPLOYEE, GsonUtil.gson.toJson(employee))
        }
        updateTitle(employee)
    }

    private fun onRevokeTokenSuccess() {
        LoginManager.logout()
        SharedPreferenceUtil.put(this, LoginManager.SP_KEY_IS_LOGIN, false)
        SharedPreferenceUtil.put(this, LoginManager.SP_KEY_AUTHORIZE, "")
        SharedPreferenceUtil.put(this, LoginManager.SP_KEY_TOKEN, "")
        SharedPreferenceUtil.put(this, LoginManager.SP_KEY_EMPLOYEE, "")
        startActivity(Intent(this, LoginActivity::class.java))
        finishAfterTransition()
    }
}
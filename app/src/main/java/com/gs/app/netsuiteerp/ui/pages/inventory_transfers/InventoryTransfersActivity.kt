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

package com.gs.app.netsuiteerp.ui.pages.inventory_transfers

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.gs.app.netsuiteerp.R
import com.gs.app.netsuiteerp.beans.InventoryTransferShow
import com.gs.app.netsuiteerp.beans.InventoryTransfersPageData
import com.gs.app.netsuiteerp.beans.OAuth2Token
import com.gs.app.netsuiteerp.beans.OAuth2TokenErrorBody
import com.gs.app.netsuiteerp.beans.ResponseError
import com.gs.app.netsuiteerp.common.login.LoginManager
import com.gs.app.netsuiteerp.ui.base.response.ResponseActivity
import com.gs.app.netsuiteerp.ui.base.response.ResponseState
import com.gs.app.netsuiteerp.ui.base.response.ResponseUiEffect
import com.gs.app.netsuiteerp.ui.pages.inventory_transfers.adapter.InventoryTransfersAdapter
import com.gs.app.netsuiteerp.ui.pages.inventory_transfers.intent.InventoryTransfersUIIntent
import com.gs.app.netsuiteerp.ui.pages.inventory_transfers.state.InventoryTransfersUiState
import com.gs.app.netsuiteerp.ui.pages.inventory_transfers.viewmodel.InventoryTransfersViewModel
import com.gs.app.netsuiteerp.ui.widgets.GsMaterialSpinner
import com.gs.app.netsuiteerp.ui.widgets.recyclerview.DividerItemDecoration
import com.gs.app.netsuiteerp.utils.DateUtil
import com.gs.app.netsuiteerp.utils.GsonUtil
import com.gs.app.netsuiteerp.utils.ProgressDialogUtil
import com.gs.library.ui.toast.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@AndroidEntryPoint
class InventoryTransfersActivity : ResponseActivity<InventoryTransfersUIIntent, InventoryTransfersUiState, InventoryTransfersViewModel>(),
    SwipeRefreshLayout.OnRefreshListener {
    private val viewModel: InventoryTransfersViewModel by viewModels()

    private val toolbar: Toolbar by lazy { findViewById(R.id.toolbar) }
    private val spinner_start_date: GsMaterialSpinner by lazy { findViewById(R.id.spinner_start_date) }
    private val spinner_end_date: GsMaterialSpinner by lazy { findViewById(R.id.spinner_end_date) }
    private val spinner_limit: GsMaterialSpinner by lazy { findViewById(R.id.spinner_limit) }
    private val btn_reset: Button by lazy { findViewById(R.id.btn_reset) }
    private val swipe_refresh_layout: SwipeRefreshLayout by lazy { findViewById(R.id.swipe_refresh_layout) }
    private val rv_inventory_transfer: RecyclerView by lazy { findViewById(R.id.rv_inventory_transfer) }

    private lateinit var limitLabels: Array<String>
    private val limitValues = arrayOf(-1, 10, 20, 50, 100)
    private var limitPosition = 1

    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null
    private val formatter = DateTimeFormatter.ofPattern("M/d/yyyy")

    private val inventoryTransfers = ArrayList<InventoryTransferShow>()
    private lateinit var inventoryTransfersAdapter: InventoryTransfersAdapter

    private var isLoading = false

    override fun getLayoutResId(): Int {
        return R.layout.activity_inventory_transfers
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView(savedInstanceState: Bundle?) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        limitLabels = resources.getStringArray(R.array.limit_count_labels)

        val calendar = Calendar.getInstance()
        val currentYear = calendar[Calendar.YEAR] // 获取当前年份
        val currentMonth = calendar[Calendar.MONTH] + 1// 获取当前月，月份从0开始
        val currentDay = calendar[Calendar.DAY_OF_MONTH]

        val startYear = if (currentMonth > 1) currentYear else (currentYear - 1)
        val startMonth = if (currentMonth > 1) (currentMonth - 1) else 12
        val startMaxDays = DateUtil.monthMaxDays(startYear, startMonth)
        startDate = LocalDate.of(startYear, startMonth, if (currentDay > startMaxDays) startMaxDays else currentDay)
        spinner_start_date.setText(startDate?.format(formatter))
        spinner_start_date.setOnClickListener {
            val date: LocalDate = startDate ?: LocalDate.now()
            val datePickerDialog = DatePickerDialog(
                this@InventoryTransfersActivity,
                { view, year, month, dayOfMonth -> // 注意：月份是从0开始的，所以显示时需要+1

                    val tempStartDate = LocalDate.of(year, month + 1, dayOfMonth)
                    if (endDate != null && tempStartDate?.isAfter(endDate) == true) {
                        Toast.makeText(
                            this@InventoryTransfersActivity,
                            R.string.tip_time_over,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        startDate = tempStartDate
                        spinner_start_date.setText(startDate?.format(formatter))
                        onRefresh()
                    }

                }, date.year, date.monthValue - 1, date.dayOfMonth
            )
            datePickerDialog.show()
            datePickerDialog.setOnDismissListener {
                spinner_start_date.editText?.focusable = View.NOT_FOCUSABLE
            }
        }

        endDate = LocalDate.of(currentYear, currentMonth, currentDay)
        spinner_end_date.setText(endDate?.format(formatter))
        spinner_end_date.setOnClickListener {
            val date: LocalDate = endDate ?: LocalDate.now()
            val datePickerDialog = DatePickerDialog(
                this@InventoryTransfersActivity,
                { view, year, month, dayOfMonth -> // 注意：月份是从0开始的，所以显示时需要+1
                    val tempEndDate = LocalDate.of(year, month + 1, dayOfMonth)
                    if (startDate != null && tempEndDate?.isBefore(startDate) == true) {
                        Toast.makeText(
                            this@InventoryTransfersActivity,
                            R.string.tip_time_over,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        endDate = tempEndDate
                        spinner_end_date.setText(endDate?.format(formatter))
                        inventoryTransfersAdapter.pageSize = limitValues[spinner_limit.checkedPos]
                        onRefresh()
                    }

                }, date.year, date.monthValue - 1, date.dayOfMonth
            )
            datePickerDialog.show()
            datePickerDialog.setOnDismissListener {
                spinner_end_date.editText?.focusable = View.NOT_FOCUSABLE
            }
        }

        spinner_limit.setDropDownItem(limitLabels, limitPosition)
        spinner_limit.setOnDismissListener {
            inventoryTransfersAdapter.pageSize = limitValues[spinner_limit.checkedPos]
            onRefresh()
        }
        spinner_limit.setOnItemSelectListener { position ->
            if (limitPosition != position) {
                limitPosition = position
                onRefresh()
            }
        }

        btn_reset.setOnClickListener {
            reset()
        }

        inventoryTransfersAdapter = InventoryTransfersAdapter(this, inventoryTransfers)
        rv_inventory_transfer.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_inventory_transfer.itemAnimator = DefaultItemAnimator()
        rv_inventory_transfer.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
            .setDrawable(getDrawable(R.color.color_line))
        )
        rv_inventory_transfer.adapter = inventoryTransfersAdapter
        inventoryTransfersAdapter.setListener { position ->

        }
        inventoryTransfersAdapter.setOnLoadMoreListener { currentPage ->
            loadMore()
        }

        swipe_refresh_layout.setOnRefreshListener(this)
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        onRefresh()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finishAfterTransition()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun overNavigationBarView(): View {
        return swipe_refresh_layout
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh() {
        if (isLoading) {
            swipe_refresh_layout.isRefreshing = false
            return
        }
        isLoading = true
        swipe_refresh_layout.isRefreshing = false
        inventoryTransfersAdapter.pageSize = limitValues[spinner_limit.checkedPos]
        inventoryTransfers.clear()
        inventoryTransfersAdapter.notifyDataSetChanged()
        viewModel.sendUiIntent(
            InventoryTransfersUIIntent.GetInventoryTransfers(
            LoginManager.getToken()!!,
            startDate?.format(formatter),
            endDate?.format(formatter),
            limitValues[spinner_limit.checkedPos],
            (inventoryTransfersAdapter.currentPage - 1) * limitValues[spinner_limit.checkedPos]
        ))
    }

    private fun reset() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar[Calendar.YEAR] // 获取当前年份
        val currentMonth = calendar[Calendar.MONTH] + 1// 获取当前月，月份从0开始
        val currentDay = calendar[Calendar.DAY_OF_MONTH]

        val startYear = if (currentMonth > 1) currentYear else (currentYear - 1)
        val startMonth = if (currentMonth > 1) (currentMonth - 1) else 12
        val startMaxDays = DateUtil.monthMaxDays(startYear, startMonth)
        startDate = LocalDate.of(startYear, startMonth, if (currentDay > startMaxDays) startMaxDays else currentDay)
        spinner_start_date.setText(startDate?.format(formatter))

        endDate = LocalDate.of(currentYear, currentMonth, currentDay)
        spinner_end_date.setText(endDate?.format(formatter))

        spinner_limit.checkedPos = 1

        onRefresh()
    }

    private fun loadMore() {
        if (isLoading) {
            return
        }
        isLoading = true
        // 更新列表
        viewModel.sendUiIntent(
            InventoryTransfersUIIntent.GetInventoryTransfers(
            LoginManager.getToken()!!,
            startDate?.format(formatter),
            endDate?.format(formatter),
            limitValues[spinner_limit.checkedPos],
            inventoryTransfersAdapter.currentPage * limitValues[spinner_limit.checkedPos]
        ))
    }

    override fun refreshToken() {
        handleInvalidToken({
            viewModel.sendUiIntent(InventoryTransfersUIIntent.RefreshToken(LoginManager.getToken()!!))
        }, true)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initUIState() {
        viewModel.uiStateFlow.onEach { uiState ->
            when (uiState.state) {
                is ResponseState.Loading -> {
                    ProgressDialogUtil.instance.showDialog(this)
                }
                is ResponseState.Success<*> -> {
                    when (val response = uiState.state.data) {
                        is InventoryTransfersPageData -> {
                            val pageSize = limitValues[limitPosition]
                            val pageData = uiState.state.data as InventoryTransfersPageData
                            val totalPage = if (pageSize > 0) ((pageData.totalResults / pageSize) + (pageData.totalResults % pageSize)) else 1
                            if (totalPage == 1) {
                                inventoryTransfers.clear()
                            }
                            if (!pageData.items.isNullOrEmpty()) {
                                inventoryTransfers.addAll(pageData.items)
                            }
                            val currentPage = if (pageSize > 0) ((inventoryTransfers.size / pageSize) + (inventoryTransfers.size % pageSize)) else 1
                            inventoryTransfersAdapter.setPage(currentPage, totalPage)
                            inventoryTransfersAdapter.notifyDataSetChanged()
                        }
                        is OAuth2Token -> {
                            onRefreshTokenSuccess(response)
                        }
                    }
                    ProgressDialogUtil.instance.dismissDialog()
                    isLoading = false
                }
                is ResponseState.Failure -> {
                    ProgressDialogUtil.instance.dismissDialog()
                    showErrorDialog(uiState.state.message ?: "") {}
                    isLoading = false
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
                    isLoading = false
                }
                else -> {
                    ProgressDialogUtil.instance.dismissDialog()
                    isLoading = false
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
}
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

package com.gs.app.netsuiteerp.ui.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Outline
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gs.app.netsuiteerp.R
import com.gs.app.netsuiteerp.common.scan.ActionType
import com.gs.app.netsuiteerp.common.scan.Constants
import com.gs.app.netsuiteerp.common.scan.CustomBroadcast
import com.gs.app.netsuiteerp.common.scan.OutputMode
import com.gs.app.netsuiteerp.common.scan.OutputModeSettings
import com.gs.app.netsuiteerp.common.scan.QueryType
import com.gs.app.netsuiteerp.common.scan.ScanSettings
import com.gs.app.netsuiteerp.utils.DensityUtil.getNavigationBarHeight
import com.gs.app.netsuiteerp.utils.GlobalAlertDialogUtil
import com.gs.app.netsuiteerp.utils.ProgressDialogUtil
import com.gs.library.mvi.base.AbsActivity

abstract class BaseActivity : AbsActivity(), View.OnKeyListener, TextView.OnEditorActionListener {
    private val TAG = "BaseActivity"
    private var isResumed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerScanResultCfg()
    }

    override fun onResume() {
        super.onResume()
        setOverNavigationBar()
        onResumeForScan()
        isResumed = true
    }

    override fun onPause() {
        super.onPause()
        isResumed = false
        onPauseForScan()
    }

    override fun onDestroy() {
        ProgressDialogUtil.instance.dismissDialog()
        GlobalAlertDialogUtil.dismissDialog()
        unregisterScanResultCfg()
        super.onDestroy()
    }

    protected open fun overNavigationBarView(): View? {
        return null
    }

    protected fun setOverNavigationBar() {
        val overNavigationBarView = overNavigationBarView()
        if (overNavigationBarView != null) {
            overNavigationBarView.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(
                        0,
                        0,
                        view.width,
                        view.height + getNavigationBarHeight(this@BaseActivity),
                        0f
                    )
                }
            }
        }
    }

    protected fun showErrorDialog(message: String, block: () -> Unit) {
        GlobalAlertDialogUtil.showDialog(AlertDialog.Builder(this)
            .setTitle(R.string.error_tips)
            .setMessage(message)
            .setPositiveButton(R.string.sure) {dialog, which ->
                dialog.dismiss()
                block()
            })
    }

    /**
     * 弹出软键盘
     * @param editView
     */
    protected fun showSoftInputMethod(editView: EditText?) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (!imm.showSoftInput(editView, 0)) {
            Log.w(TAG, "Failed to show soft input method.")
        }
    }

    /**
     * 隐藏软键盘
     */
    protected fun hideSoftInputWindow() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val focus = currentFocus
        if (imm.isActive && focus != null) {
            imm.hideSoftInputFromWindow(
                focus.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    /**
     * 隐藏软键盘和焦点
     */
    protected fun hideSoftInputWindowAndFocus() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val focus = currentFocus
        if (imm.isActive && focus != null) {
            focus.clearFocus()
            imm.hideSoftInputFromWindow(
                focus.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    /**
     * 判断软键盘是否已显示
     * @return
     */
    protected fun isSoftWindowShown(): Boolean {
        //获取当屏幕内容的高度
        val screenHeight = this.window.decorView.height
        //获取View可见区域的bottom
        val rect = Rect()
        //DecorView即为activity的顶级view
        this.window.decorView.getWindowVisibleDisplayFrame(rect)
        //考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
        //选取screenHeight*2/3进行判断
        return screenHeight * 2 / 3 > rect.bottom
    }

    /***********************************************************
     * 监听扫描结果
     ***********************************************************/
    open fun scanResultView(): EditText? {
        return null
    }

    open fun scanRefresh() {}

    private fun onResumeForScan() {
        scanResultView()?.setOnKeyListener(this)
        scanResultView()?.setOnEditorActionListener(this)
        enableScanOutputBroadcast()
        registerScanResult()
        getScanResultCfg()
    }

    private fun onPauseForScan() {
        scanResultView()?.setOnKeyListener(null)
        scanResultView()?.setOnEditorActionListener(null)
        unregisterScanResult()
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        val keyCodeName = when (keyCode) {
            KeyEvent.KEYCODE_0 -> {
                "KEYCODE_0"
            }

            KeyEvent.KEYCODE_1 -> {
                "KEYCODE_1"
            }

            KeyEvent.KEYCODE_2 -> {
                "KEYCODE_2"
            }

            KeyEvent.KEYCODE_3 -> {
                "KEYCODE_3"
            }

            KeyEvent.KEYCODE_4 -> {
                "KEYCODE_4"
            }

            KeyEvent.KEYCODE_5 -> {
                "KEYCODE_5"
            }

            KeyEvent.KEYCODE_6 -> {
                "KEYCODE_6"
            }

            KeyEvent.KEYCODE_7 -> {
                "KEYCODE_7"
            }

            KeyEvent.KEYCODE_8 -> {
                "KEYCODE_8"
            }

            KeyEvent.KEYCODE_9 -> {
                "KEYCODE_9"
            }

            KeyEvent.KEYCODE_A -> {
                "KEYCODE_A"
            }

            KeyEvent.KEYCODE_B -> {
                "KEYCODE_B"
            }

            KeyEvent.KEYCODE_C -> {
                "KEYCODE_C"
            }

            KeyEvent.KEYCODE_D -> {
                "KEYCODE_D"
            }

            KeyEvent.KEYCODE_E -> {
                "KEYCODE_E"
            }

            KeyEvent.KEYCODE_F -> {
                "KEYCODE_F"
            }

            KeyEvent.KEYCODE_G -> {
                "KEYCODE_G"
            }

            KeyEvent.KEYCODE_H -> {
                "KEYCODE_H"
            }

            KeyEvent.KEYCODE_I -> {
                "KEYCODE_I"
            }

            KeyEvent.KEYCODE_J -> {
                "KEYCODE_J"
            }

            KeyEvent.KEYCODE_K -> {
                "KEYCODE_K"
            }

            KeyEvent.KEYCODE_L -> {
                "KEYCODE_L"
            }

            KeyEvent.KEYCODE_M -> {
                "KEYCODE_M"
            }

            KeyEvent.KEYCODE_N -> {
                "KEYCODE_N"
            }

            KeyEvent.KEYCODE_O -> {
                "KEYCODE_O"
            }

            KeyEvent.KEYCODE_P -> {
                "KEYCODE_P"
            }

            KeyEvent.KEYCODE_Q -> {
                "KEYCODE_Q"
            }

            KeyEvent.KEYCODE_R -> {
                "KEYCODE_S"
            }

            KeyEvent.KEYCODE_T -> {
                "KEYCODE_T"
            }

            KeyEvent.KEYCODE_U -> {
                "KEYCODE_U"
            }

            KeyEvent.KEYCODE_V -> {
                "KEYCODE_V"
            }

            KeyEvent.KEYCODE_W -> {
                "KEYCODE_W"
            }

            KeyEvent.KEYCODE_X -> {
                "KEYCODE_X"
            }

            KeyEvent.KEYCODE_Y -> {
                "KEYCODE_Y"
            }

            KeyEvent.KEYCODE_Z -> {
                "KEYCODE_Z"
            }

            KeyEvent.KEYCODE_STAR -> {
                "KEYCODE_STAR"
            }

            KeyEvent.KEYCODE_ENTER -> {
                "KEYCODE_ENTER"
            }

            240 -> {
                "SCAN_KEY"
            }

            241 -> {
                "SCAN_KEY"
            }

            242 -> {
                "SCAN_KEY"
            }

            243 -> {
                "SCAN_KEY"
            }

            else -> {
                "UNKNOWN"
            }
        }
        val action = event?.let {
            when (it.action) {
                KeyEvent.ACTION_DOWN -> {
                    "DOWN"
                }

                KeyEvent.ACTION_UP -> {
                    "UP"
                }

                else -> {
                    "OTHER"
                }
            }
        }
        Log.e(TAG, "onKey(): $keyCode, $keyCodeName, $action")
        if (keyCode == 240 || keyCode == 241 || keyCode == 242 || keyCode == 243) {
            return false
        }
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_DEL) {
            return false
        }
        // 监听到Enter键时，说明存新增一个扫码结果
        if (keyCode == KeyEvent.KEYCODE_ENTER && event!!.action == KeyEvent.ACTION_DOWN) {
            scanRefresh()
        }
        return false
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEARCH) {
            scanRefresh()
        }
        return false
    }

    private fun getScanResultCfg() {
        try {
            val intent = Intent(Constants.ACTION_SCAN_GET_CFG)
            intent.putExtra(Constants.EXTRA_QUERY, QueryType.Profile.value)
            sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val scanResultReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.e(TAG, "ScanResultReceiver: ${intent.action}")
            val action =
                if (scanResultBroadcastCfg != null) scanResultBroadcastCfg!!.action else Constants.ACTION_SCAN_RESULT
            val extraBarcode1 =
                if (scanResultBroadcastCfg != null) scanResultBroadcastCfg!!.barcode1 else Constants.EXTRA_SCAN_BARCODE1
            val extraBarcode2 =
                if (scanResultBroadcastCfg != null) scanResultBroadcastCfg!!.barcode2 else Constants.EXTRA_SCAN_BARCODE2
            val extraBarcodeType =
                if (scanResultBroadcastCfg != null) scanResultBroadcastCfg!!.barcodeType else Constants.EXTRA_SCAN_BARCODE_TYPE
            val extraBarcodeTypeName =
                if (scanResultBroadcastCfg != null) scanResultBroadcastCfg!!.sBarcodeType else Constants.EXTRA_SCAN_BARCODE_TYPE_NAME
            if (action == intent.action) {
                val state = intent.getStringExtra(Constants.EXTRA_SCAN_STATE)
                val barcode1 = intent.getStringExtra(extraBarcode1)
                val barcode2 = intent.getStringExtra(extraBarcode2)
                val barcodeType = intent.getIntExtra(extraBarcodeType, -1)
                val barcodeTypeName = intent.getStringExtra(extraBarcodeTypeName)
                Log.e(TAG, "onReceive(): state = $state")
                Log.e(TAG, "onReceive(): barcode1 = $barcode1")
                Log.e(TAG, "onReceive(): barcode2 = $barcode2")
                Log.e(TAG, "onReceive(): barcodeType = $barcodeType")
                Log.e(TAG, "onReceive(): barcodeTypeName = $barcodeTypeName")
                if (state == Constants.SCAN_RESULT_OK && !TextUtils.isEmpty(barcode1)) {
                    var filter = scanResultView()?.text.toString()
                    if (TextUtils.isEmpty(filter)) {
                        filter = barcode1!!
                    } else {
                        filter = filter + "," + barcode1!!
                    }
                    scanResultView()?.setText(filter)
                    scanResultView()?.setSelection(filter.length)
                    scanRefresh()
                }
            }
        }
    }


    private fun registerScanResultCfg() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.ACTION_SCAN_CFG_INFO)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            registerReceiver(scanResultCfgReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(scanResultCfgReceiver, intentFilter)
        }
    }

    private val scanResultCfgReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.e(TAG, "ScanResultCfgReceiver: ${intent.action}")
            if (Constants.ACTION_SCAN_CFG_INFO == intent.action) {
                val query = intent.getStringExtra(Constants.EXTRA_QUERY)
                Log.e(TAG, "ScanResultCfgReceiver: $query")
                if (query == QueryType.Profile.value) {
                    val scanCfg = intent.getStringExtra(Constants.EXTRA_INFO)
                    scanCfg?.let {
                        val gson = Gson()
                        try {
                            val scanSettings = gson.fromJson<ScanSettings>(scanCfg, object : TypeToken<ScanSettings>(){}.type)
                            if (scanSettings?.outputModeSettings != null && scanSettings.outputModeSettings!!.customBroadcast != null) {
                                scanResultBroadcastCfg = scanSettings.outputModeSettings!!.customBroadcast
                                Log.e(TAG, "scan result cfg: ${gson.toJson(scanResultBroadcastCfg!!)}")
                                if (isResumed) {
                                    unregisterReceiver(scanResultReceiver)
                                    registerScanResult()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    /**
     * 监听扫描结果输出广播：当“输出模式”设置为“广播输出”，或其他模式设置了“附加输出广播”，接接收到扫描结果
     */
    private var scanResultBroadcastCfg: CustomBroadcast? = null
    private fun registerScanResult() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.ACTION_SCAN_RESULT)
        scanResultBroadcastCfg?.let {
            Log.e(TAG, "scan result cfg action: ${it.action}")
            intentFilter.addAction(it.action)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            registerReceiver(scanResultReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(scanResultReceiver, intentFilter)
        }
    }

    /**
     * 设置扫描输出模式为广播输出
     */
    private fun enableScanOutputBroadcast() {
        try {
            val intent = Intent(Constants.ACTION_SCAN_SET_CFG)
            intent.putExtra(Constants.EXTRA_ACTION, ActionType.Modify.value)
            intent.putExtra(
                Constants.EXTRA_REQUEST_INFO, Gson().toJson(
                    ScanSettings(
                        OutputModeSettings(OutputMode.Broadcast.value, null)
                    )
                )
            )
            sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun unregisterScanResult() {
        unregisterReceiver(scanResultReceiver)
    }

    private fun unregisterScanResultCfg() {
        unregisterReceiver(scanResultCfgReceiver)
    }
}
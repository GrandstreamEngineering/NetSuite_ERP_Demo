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

package com.gs.app.netsuiteerp.utils

import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt

object StatusBarUtil {
    /**
     * 设置状态栏图标和文字为深色风格
     */
    fun setStatusBarLightMode(window: Window, dark: Boolean): Boolean {
        return androidMSetStatusBarLightMode(window, dark)
    }

    /**
     * 设置状态栏文本颜色
     */
    fun androidMSetStatusBarLightMode(window: Window, dark: Boolean): Boolean {
        val decor = window.decorView
        var ui = decor.systemUiVisibility
        ui = if (dark) {
            ui or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            ui and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        decor.systemUiVisibility = ui
        return true
    }

    /**
     * 设置状态栏样式
     * @param isFullScreen 是否全屏
     * @param statusBarColor 状态栏颜色
     */
    fun setStatusBarStyle(window: Window, isFullScreen: Boolean, @ColorInt statusBarColor: Int): Boolean {
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        //设置状态栏颜色
        setStatusBarColor(window, statusBarColor)
        if (isFullScreen) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
        return true
    }

    /**
     * 设置状态栏颜色
     */
    fun setStatusBarColor(window: Window, @ColorInt statusBarColor: Int) {
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //设置状态栏颜色
        window.statusBarColor = statusBarColor
    }
}
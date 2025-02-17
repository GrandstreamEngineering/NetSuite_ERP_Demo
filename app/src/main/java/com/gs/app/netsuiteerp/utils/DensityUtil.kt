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

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowInsets
import android.view.WindowManager

object DensityUtil {
    fun dip2px(c: Context, dpValue: Float): Int {
        val scale = c.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun dip2sp(c: Context, dpValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue,
            c.resources.displayMetrics
        )
            .toInt()
    }

    fun px2dip(c: Context, pxValue: Float): Int {
        val scale = c.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun px2sp(c: Context, pxValue: Float): Int {
        val fontScale = c.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    fun sp2px(c: Context, spValue: Float): Int {
        val fontScale = c.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    fun sp2dip(c: Context, spValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            spValue,
            c.resources.displayMetrics
        )
            .toInt()
    }

    fun getScreenW(c: Context): Int {
        return c.resources.displayMetrics.widthPixels
    }

    fun getScreenH(c: Context): Int {
        return c.resources.displayMetrics.heightPixels
    }

    fun getScreenRealH(context: Context): Int {
        val h: Int
        val winMgr = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = winMgr.defaultDisplay
        val dm = DisplayMetrics()
        display.getRealMetrics(dm)
        h = dm.heightPixels
        return h
    }

    fun getScreenRealW(context: Context): Int {
        val h: Int
        val winMgr = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = winMgr.defaultDisplay
        val dm = DisplayMetrics()
        display.getRealMetrics(dm)
        h = dm.widthPixels
        return h
    }

    fun getStatusBarHeight(c: Context): Int {
        var result = 0
        @SuppressLint("InternalInsetResource") val resourceId =
            c.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = c.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun navigationBarHeight(c: Context): Int {
        var result = 0
        // 首先检查导航栏是否是固定的
        val resources = c.resources
        @SuppressLint("DiscouragedApi", "InternalInsetResource") val resourceId =
            resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        // 如果导航栏高度为0，则可能是固定的，我们还需要检查是否隐藏了导航栏
        if (result == 0) {
            val dm = DisplayMetrics()
            val windowManager = c.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            if (windowManager != null) {
                windowManager.defaultDisplay.getMetrics(dm)
                val width = dm.widthPixels
                val height = dm.heightPixels
                try {
                    val m = Class.forName("android.view.Display")
                        .getMethod("getRealSize", Point::class.java)
                    m.isAccessible = true
                    val realSize = Point()
                    m.invoke(windowManager.defaultDisplay, realSize)
                    result = realSize.y - height
                } catch (ignored: Exception) {
                    // 反射调用失败，可能是因为API版本不匹配或者设备问题
                    ignored.printStackTrace()
                }
            }
        }
        return result
    }

    fun getNavigationBarHeight(activity: Activity): Int {
        val rootWindowInsets = activity.window.decorView.rootWindowInsets
        return rootWindowInsets.getInsets(WindowInsets.Type.navigationBars()).bottom
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     */
    fun snapShotWithStatusBar(activity: Activity): Bitmap {
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bmp = view.drawingCache
        val width = getScreenW(activity)
        val height = getScreenH(activity)
        var bp: Bitmap? = null
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height)
        view.destroyDrawingCache()
        return bp
    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     */
    fun snapShotWithoutStatusBar(activity: Activity): Bitmap {
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bmp = view.drawingCache
        val frame = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(frame)
        val statusBarHeight = frame.top
        val width = getScreenW(activity)
        val height = getScreenH(activity)
        var bp: Bitmap? = null
        bp = Bitmap.createBitmap(
            bmp, 0, statusBarHeight, width, height
                    - statusBarHeight
        )
        view.destroyDrawingCache()
        return bp
    }
}
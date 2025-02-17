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

package com.gs.app.netsuiteerp.common

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Intent
import android.os.Bundle
import android.util.Log
import java.util.Stack

class ActivityLifecycleManager : ActivityLifecycleCallbacks {
    private object Singleton {
        val instance = ActivityLifecycleManager()
    }

    private val stack = Stack<Activity?>()
    val activityCount: Int
        get() = stack.size

    fun startActivity(activityClass: Class<Activity>) {
        try {
            val topActivity = stack.peek()
            topActivity?.startActivity(Intent(topActivity, activityClass))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startActivity(activityClass: Class<out Activity>, requestCode: Int) {
        try {
            val topActivity = stack.peek()
            topActivity?.startActivityForResult(Intent(topActivity, activityClass), requestCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startActivityAndClearTask(activityClass: Class<out Activity>) {
        val activities = Stack<Activity>()
        activities.addAll(stack)
        stack.clear()
        var started = false
        while (activities.isNotEmpty()) {
            try {
                val topActivity = activities.pop()
                if (!started) {
                    started = true
                    topActivity?.startActivity(Intent(topActivity, activityClass))
                }
                topActivity?.finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        stack.push(activity)
        Log.d("ActivityLifecycle", "onActivityCreated(): ${activity::class.java.simpleName}")
    }

    override fun onActivityStarted(activity: Activity) {
        Log.d("ActivityLifecycle", "onActivityStarted(): ${activity::class.java.simpleName}")
    }
    override fun onActivityResumed(activity: Activity) {
        Log.d("ActivityLifecycle", "onActivityResumed(): ${activity::class.java.simpleName}")
    }
    override fun onActivityPaused(activity: Activity) {
        Log.d("ActivityLifecycle", "onActivityPaused(): ${activity::class.java.simpleName}")
    }
    override fun onActivityStopped(activity: Activity) {
        Log.d("ActivityLifecycle", "onActivityStopped(): ${activity::class.java.simpleName}")
    }
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Log.d("ActivityLifecycle", "onActivitySaveInstanceState(): ${activity::class.java.simpleName}")
    }
    override fun onActivityDestroyed(activity: Activity) {
        stack.remove(activity)
        Log.d("ActivityLifecycle", "onActivityDestroyed(): ${activity::class.java.simpleName}")
    }

    companion object {
        val instance: ActivityLifecycleManager
            get() = Singleton.instance
    }
}

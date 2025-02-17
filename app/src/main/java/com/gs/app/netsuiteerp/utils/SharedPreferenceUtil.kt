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

package com.gs.app.erp.utils

import android.content.Context

object SharedPreferenceUtil {
    private const val SP_NAME = "gs_erp"
    fun <T> put(context: Context, key: String, value: T) {
        val sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()
        when (value) {
            is String -> {
                editor.putString(key, value)
            }
            is Int -> {
                editor.putInt(key, value)
            }
            is Long -> {
                editor.putLong(key, value)
            }
            is Float -> {
                editor.putFloat(key, value)
            }
            is Boolean -> {
                editor.putBoolean(key, value)
            }
            else -> throw IllegalArgumentException("没有对应的类型，请检查一下")
        }
        editor.apply()
    }

    fun <T> get(context: Context, key: String, defaultValue: T): Any{
        val sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        return when (defaultValue) {
            is String -> {
                sp.getString(key, defaultValue as String) ?: ""
            }
            is Int -> {
                sp.getInt(key, (defaultValue as Int))
            }
            is Long -> {
                sp.getLong(key, (defaultValue as Long))
            }
            is Float -> {
                sp.getFloat(key, (defaultValue as Float))
            }
            is Boolean -> {
                sp.getBoolean(key, (defaultValue as Boolean))
            }
            else -> throw IllegalArgumentException("没有对应的类型，请检查一下")
        }
    }

    fun remove(context: Context, key: String) {
        val sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.remove(key)
        editor.apply()
    }

    fun getAll(context: Context): Map<String, *> {
        val sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        return sp.all
    }

    fun cleanAll(context: Context) {
        val sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.clear()
        editor.apply()
    }

    fun contains(context: Context, key: String): Boolean {
        val sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        return sp.contains(key)
    }
}
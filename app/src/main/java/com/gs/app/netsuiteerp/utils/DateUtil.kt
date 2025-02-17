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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

object DateUtil {
    /**
     * 一天的毫秒数
     */
    private const val DAY_MILLIS = 86400000L

    @SuppressLint("SimpleDateFormat")
    fun timestamp2Date(timestamp: Long): String {
        try {
            return SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Date(timestamp))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @SuppressLint("SimpleDateFormat")
    fun date2Timestamp(date: String): Long {
        try {
            val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
            return simpleDateFormat.parse(date)!!.time
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0L
    }

    @SuppressLint("SimpleDateFormat")
    fun recentlyWeekDate(): String {
//        val calendar = Calendar.getInstance()
//        calendar.firstDayOfWeek = Calendar.SUNDAY // 设置周日为一周的开始
//        val dayOfWeek = calendar[Calendar.DAY_OF_WEEK] // 获取当前周几
//        val timestamp = calendar.timeInMillis - (dayOfWeek - 1) * DAY_MILLIS
        try {
            return SimpleDateFormat("M/d/yyyy").format(Date(System.currentTimeMillis() - 6 * DAY_MILLIS))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @SuppressLint("SimpleDateFormat")
    fun recentlyMonthDate(): String {
//        val calendar = Calendar.getInstance()
//        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH] // 获取一个月的几号
//        val timestamp = calendar.timeInMillis - (dayOfMonth - 1) * DAY_MILLIS
        try {
            return SimpleDateFormat("M/d/yyyy").format(Date(System.currentTimeMillis() - 29 * DAY_MILLIS))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @SuppressLint("SimpleDateFormat")
    fun recentlyHalfYearDate(): String {
        try {
            return SimpleDateFormat("M/d/yyyy").format(Date(System.currentTimeMillis() - 6 * 30 * DAY_MILLIS))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @SuppressLint("SimpleDateFormat")
    fun recentlyYearDate(): String {
//        val calendar = Calendar.getInstance()
//        val dayOfYear = calendar[Calendar.DAY_OF_YEAR] // 获取一年中第几天
//        val timestamp = calendar.timeInMillis - (dayOfYear - 1) * DAY_MILLIS
        try {
            return SimpleDateFormat("M/d/yyyy").format(Date(System.currentTimeMillis() - 12 * 30 * DAY_MILLIS))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun currentDayOfYear(): Int {
        val calendar = Calendar.getInstance()
        return calendar[Calendar.DAY_OF_YEAR] // 获取一年中第几天
    }

    fun currentDayOfMonth(): Int {
        val calendar = Calendar.getInstance()
        return calendar[Calendar.DAY_OF_MONTH] // 获取一个月的几号
    }

    fun currentDayOfWeek(): Int {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.SUNDAY // 设置周日为一周的开始
        return calendar[Calendar.DAY_OF_WEEK] // 获取当前周几
    }

    fun currentWeekOfYear(): Int {
        val calendar = Calendar.getInstance()
        return calendar[Calendar.WEEK_OF_YEAR] // 获取一年中第几周
    }

    fun currentWeekOfMonth(): Int {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.SUNDAY // 设置周日为一周的开始
        return calendar[Calendar.WEEK_OF_MONTH] // 获取一月中第几周
    }

    fun currentMonth(): Int {
        val calendar = Calendar.getInstance()
        return calendar[Calendar.MONTH] + 1 // 获取当前月，月份从0开始，所以+1
    }

    fun currentYear(): Int {
        val calendar = Calendar.getInstance()
        return calendar[Calendar.YEAR] // 获取当前年份
    }

    fun monthMaxDays(year: Int, month: Int): Int {
        // 判断year是否为闰年的条件：
        // 1. 如果年份能被4整除但不能被100整除，它是闰年。
        // 2. 如果年份能被400整除，它也是闰年。
        val isLeapYear = ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)
        return if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) 31
        else if (month == 2)
            if (isLeapYear) 29 else 28
        else 30
    }
}
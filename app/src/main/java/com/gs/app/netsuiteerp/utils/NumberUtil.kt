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

import java.text.NumberFormat
import java.util.Currency

object NumberUtil {
    fun formatWithCommas(value: Double): String {
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 2
        numberFormat.minimumFractionDigits = 2
        return numberFormat.format(value)
    }

    fun formatWithCurrency(value: Double, currencyCode: String): String {
        val format = NumberFormat.getCurrencyInstance()
        // 设置货币符号，使用指定的货币代码
        val currency = Currency.getInstance(currencyCode)
        format.currency = currency
        // 格式化数值
        return format.format(value)
    }
}
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

package com.gs.app.netsuiteerp.beans

data class Currency(
    val currencyPrecision: Int,
    val displaySymbol: String,
    val exchangeRate: Int,
    val formatSample: String,
    val fxRateUpdateTimezone: FxRateUpdateTimezone,
    val id: String,
    val includeInFxRateUpdates: Boolean,
    val isBaseCurrency: Boolean,
    val isInactive: Boolean,
    val lastModifiedDate: String,
    val links: List<WebLink>?,
    val locale: Locale,
    val name: String,
    val overrideCurrencyFormat: Boolean,
    val symbol: String,
    val symbolPlacement: SymbolPlacement
)

data class FxRateUpdateTimezone(
    val id: String,
    val refName: String
)

data class Locale(
    val id: String,
    val refName: String
)

data class SymbolPlacement(
    val id: String,
    val refName: String
)
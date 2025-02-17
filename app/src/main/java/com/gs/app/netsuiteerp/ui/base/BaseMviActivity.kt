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

import android.os.Bundle
import com.gs.library.mvi.kotlin.BaseViewModel
import com.gs.library.mvi.kotlin.IUiEffect
import com.gs.library.mvi.kotlin.IUiIntent
import com.gs.library.mvi.kotlin.IUiState


abstract class BaseMviActivity<UiIntent : IUiIntent, UiState : IUiState, UiEffect : IUiEffect, VM : BaseViewModel<UiIntent, UiState, UiEffect>>
    : BaseActivity() {

    override fun initData(savedInstanceState: Bundle?) {
        initUIState()
        initUIEffect()
    }

    /**
     * 根据UIState更新UI
     */
    protected abstract fun initUIState()

    /**
     * 根据UiEffect更新UI：如Toast、弹框、跳转页面、切换fragment等
     */
    protected abstract fun initUIEffect()
}
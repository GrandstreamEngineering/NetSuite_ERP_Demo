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

package com.gs.app.netsuiteerp.module

import android.app.Application
import com.gs.app.netsuiteerp.ui.pages.home.respository.HomeNetRepository
import com.gs.app.netsuiteerp.ui.pages.home.viewmodel.HomeViewModel
import com.gs.app.netsuiteerp.ui.pages.inventory_adjustments.respository.InventoryAdjustmentsNetRepository
import com.gs.app.netsuiteerp.ui.pages.inventory_adjustments.viewmodel.InventoryAdjustmentsViewModel
import com.gs.app.netsuiteerp.ui.pages.inventory_transfers.respository.InventoryTransfersNetRepository
import com.gs.app.netsuiteerp.ui.pages.inventory_transfers.viewmodel.InventoryTransfersViewModel
import com.gs.app.netsuiteerp.ui.pages.login.respository.LoginNetRepository
import com.gs.app.netsuiteerp.ui.pages.login.viewmodel.LoginViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {
    // 提供ViewModel的工厂方法
    companion object {
        @Provides
        @IntoMap
        @ViewModelKey(LoginViewModel::class)
        fun provideLoginViewModel(repository: LoginNetRepository, application: Application): LoginViewModel {
            return LoginViewModel(repository, application)
        }

        @Provides
        @IntoMap
        @ViewModelKey(HomeViewModel::class)
        fun provideHomeViewModel(repository: HomeNetRepository, application: Application): HomeViewModel {
            return HomeViewModel(repository, application)
        }

        @Provides
        @IntoMap
        @ViewModelKey(InventoryAdjustmentsViewModel::class)
        fun provideInventoryAdjustmentsViewModel(repository: InventoryAdjustmentsNetRepository, application: Application): InventoryAdjustmentsViewModel {
            return InventoryAdjustmentsViewModel(repository, application)
        }

        @Provides
        @IntoMap
        @ViewModelKey(InventoryTransfersViewModel::class)
        fun provideInventoryTransfersViewModel(repository: InventoryTransfersNetRepository, application: Application): InventoryTransfersViewModel {
            return InventoryTransfersViewModel(repository, application)
        }
    }
}
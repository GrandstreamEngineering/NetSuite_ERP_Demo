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

import com.gs.app.netsuiteerp.ui.pages.home.respository.HomeNetRepository
import com.gs.app.netsuiteerp.ui.pages.inventory_adjustments.respository.InventoryAdjustmentsNetRepository
import com.gs.app.netsuiteerp.ui.pages.inventory_transfers.respository.InventoryTransfersNetRepository
import com.gs.app.netsuiteerp.ui.pages.login.respository.LoginNetRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 实例化Repository
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideLoginNetRepository(): LoginNetRepository {
        return LoginNetRepository() // 直接实例化Repository
    }

    @Singleton
    @Provides
    fun provideHomeNetRepository(): HomeNetRepository {
        return HomeNetRepository() // 直接实例化Repository
    }

    @Singleton
    @Provides
    fun provideInventoryAdjustmentsNetRepository(): InventoryAdjustmentsNetRepository {
        return InventoryAdjustmentsNetRepository() // 直接实例化Repository
    }

    @Singleton
    @Provides
    fun provideInventoryTransfersNetRepository(): InventoryTransfersNetRepository{
        return InventoryTransfersNetRepository() // 直接实例化Repository
    }
}
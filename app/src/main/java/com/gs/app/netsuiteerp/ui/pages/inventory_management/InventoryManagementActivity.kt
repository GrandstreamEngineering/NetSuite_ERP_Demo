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

package com.gs.app.netsuiteerp.ui.pages.inventory_management

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gs.app.netsuiteerp.R
import com.gs.app.netsuiteerp.ui.base.BaseActivity
import com.gs.app.netsuiteerp.ui.pages.inventory_adjustments.InventoryAdjustmentsActivity
import com.gs.app.netsuiteerp.ui.pages.inventory_management.adapter.ModuleListAdapter
import com.gs.app.netsuiteerp.ui.pages.inventory_transfers.InventoryTransfersActivity

class InventoryManagementActivity : BaseActivity() {
    private val toolbar: Toolbar by lazy { findViewById(R.id.toolbar) }
    private val rv_module: RecyclerView by lazy { findViewById(R.id.rv_module) }

    private lateinit var moduleList: List<String>
    private lateinit var moduleListAdapter: ModuleListAdapter

    override fun getLayoutResId(): Int {
        return R.layout.activity_inventory_management
    }

    override fun initView(savedInstanceState: Bundle?) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        moduleList = arrayListOf(
            getString(R.string.inventory_adjustments),
            getString(R.string.inventory_transfer)
        )
        moduleListAdapter = ModuleListAdapter(this, moduleList)
        rv_module.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_module.itemAnimator = DefaultItemAnimator()
        rv_module.adapter = moduleListAdapter
        moduleListAdapter.setListener { position ->
            when(position) {
                0 -> startActivity(Intent(this@InventoryManagementActivity, InventoryAdjustmentsActivity::class.java))
                1 -> startActivity(Intent(this@InventoryManagementActivity, InventoryTransfersActivity::class.java))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finishAfterTransition()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun overNavigationBarView(): View {
        return rv_module
    }
}
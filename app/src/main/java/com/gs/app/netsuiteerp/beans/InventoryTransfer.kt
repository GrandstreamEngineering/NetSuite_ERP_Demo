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

data class InventoryTransfer(
    val createdDate: String,
    val custbody_15699_exclude_from_ep_process: Boolean,
    val custbody_3805_dunning_letters_toemail: Boolean,
    val custbody_3805_dunning_letters_toprint: Boolean,
    val custbody_3805_dunning_paused: Boolean,
    val custbody_cps_bdc_lastupdatedbyimport: Boolean,
    val custbody_cs_pymt_inv_paymentreference: CustbodyCsPymtInvPaymentreferenceLinks?,
    val custbody_custom_po_received_date: String?,
    val custbody_gs_chbx_excld_auto_close: Boolean,
    val custbody_gs_custom_is_material_sales: Boolean,
    val custbody_gs_loose_carton_check: Boolean,
    val custbody_gs_pre_release_email_sent: Boolean,
    val custbody_gs_so_converted_to_shipping: CustbodyGsSoConvertedToShippingLinks?,
    val custbody_gs_so_hold_pre_release: Boolean,
    val custbody_gs_so_hold_prepaid: Boolean,
    val custbody_gs_so_hold_vad: Boolean,
    val custbody_gs_vad_email_sent: Boolean,
    val customForm: CustomForm,
    val excludeFromGLNumbering: Boolean,
    val externalId: String,
    val id: String,
    val inventory: InventoryLinks,
    val lastModifiedDate: String,
    val links: List<WebLink>?,
    val location: LocationLinks,
    val memo: String,
    val postingPeriod: PostingPeriodLinks,
    val prevDate: String,
    val subsidiary: SubsidiaryLinks,
    val tranDate: String,
    val tranId: String,
    val transferLocation: TransferLocationLinks
)

data class CustbodyGsSoConvertedToShippingLinks(
    val id: String,
    val links: List<WebLink>,
    val refName: String
)

data class LocationLinks(
    val id: String,
    val links: List<WebLink>,
    val refName: String
)

data class TransferLocationLinks(
    val id: String,
    val links: List<WebLink>,
    val refName: String
)
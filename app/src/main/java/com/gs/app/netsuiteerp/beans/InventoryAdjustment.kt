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

data class InventoryAdjustment(
    val account: AccountLinks,
    val cleared: Boolean,
    val createdDate: String,
    val custbody_cs_pymt_inv_paymentreference: CustbodyCsPymtInvPaymentreferenceLinks?,
    val customForm: CustomForm,
    val estimatedTotalValue: Double,
    val excludeFromGLNumbering: Boolean,
    val externalId: String,
    val id: String,
    val inventory: InventoryLinks,
    val lastModifiedDate: String,
    val links: List<WebLink>?,
    val memo: String,
    val postingPeriod: PostingPeriodLinks,
    val prevDate: String,
    val subsidiary: SubsidiaryLinks,
    val tranDate: String,
    val tranId: String
)

data class AccountLinks(
    val id: String,
    val links: List<WebLink>?,
    val refName: String?
)

data class CustbodyCsPymtInvPaymentreferenceLinks(
    val links: List<WebLink>?
)

data class InventoryLinks(
    val links: List<WebLink>?
)

data class PostingPeriodLinks(
    val id: String,
    val links: List<WebLink>?,
    val refName: String?
)
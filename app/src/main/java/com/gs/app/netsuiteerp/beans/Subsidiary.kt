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

data class Subsidiary(
    val country: Country,
    val currency: CurrencyLinks,
    val custrecord_fispan_vbd_approvals_all: Boolean,
    val custrecord_fispan_vbd_approvals_import: Boolean,
    val custrecord_pt_sub_taxonomy_reference: CustrecordPtSubTaxonomyReferenceLinks,
    val custrecord_subnav_subsidiary_logo: String,
    val email: String,
    val fax: String,
    val id: String,
    val isElimination: Boolean,
    val isInactive: Boolean,
    val lastModifiedDate: String,
    val legalName: String,
    val links: List<WebLink>,
    val mainAddress: MainAddressLinks,
    val name: String,
    val returnAddress: ReturnAddressLinks,
    val shippingAddress: ShippingAddressLinks,
    val state: String,
    val url: String
)

data class Country(
    val id: String,
    val refName: String
)

data class CustrecordPtSubTaxonomyReferenceLinks(
    val id: String,
    val links: List<WebLink>?,
    val refName: String
)

data class MainAddressLinks(
    val links: List<WebLink>?
)

data class ReturnAddressLinks(
    val links: List<WebLink>?
)

data class ShippingAddressLinks(
    val links: List<WebLink>?
)
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

data class Employee(
    val addressBook: AddressBookLinks?,
    val autoName: Boolean,
    val btemplate: String?,
    val campaigns: CampaignsLinks?,
    val concurrentwebservicesuser: Boolean,
    val corporatecards: CorporatecardsLinks?,
    val cseg1: Cseg1Links?,
    val currency: CurrencyLinks?,
    val currencylist: CurrencylistLinks?,
    val custentity_2663_payment_method: Boolean,
    val custentity_esc_last_modified_date: String?,
    val custentity_fispan_pending_approval: Boolean,
    val custentity_restrictexpensify: Boolean,
    val customForm: CustomForm?,
    val dateCreated: String?,
    val defaultAddress: String?,
    val defaultexpensereportcurrency: DefaultexpensereportcurrencyLinks?,
    val department: DepartmentLinks?,
    val effectivedatemode: Effectivedatemode?,
    val email: String?,
    val empcenterqty: String?,
    val empcenterqtymax: String?,
    val empperms: EmppermsLinks?,
    val entityId: String?,
    val externalId: String?,
    val firstName: String?,
    val fulluserqty: String?,
    val fulluserqtymax: String?,
    val gender: Gender?,
    val giveAccess: Boolean,
    val globalSubscriptionStatus: GlobalSubscriptionStatus?,
    val hireDate: String?,
    val i9verified: Boolean,
    val id: String?,
    val initials: String?,
    val isInactive: Boolean,
    val isempcenterqtyenforced: String?,
    val isfulluserqtyenforced: String?,
    val isretailuserqtyenforced: String?,
    val issalesrep: Boolean,
    val lastModifiedDate: String?,
    val lastName: String?,
    val links: List<WebLink>?,
    val purchaseorderlimit: Double,
    val requirePwdChange: Boolean,
    val retailuserqty: String?,
    val retailuserqtymax: String?,
    val roles: RolesLinks?,
    val sendEmail: Boolean,
    val subsidiary: SubsidiaryLinks?,
    val supervisor: SupervisorLinks?,
    val terminationbydeath: Boolean,
    val wasempcenterhasaccess: String?,
    val wasfulluserhasaccess: String?,
    val wasinactive: String?,
    val wasretailuserhasaccess: String?,
    val workCalendar: WorkCalendarLinks?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Employee

        if (addressBook != other.addressBook) return false
        if (autoName != other.autoName) return false
        if (btemplate != other.btemplate) return false
        if (campaigns != other.campaigns) return false
        if (concurrentwebservicesuser != other.concurrentwebservicesuser) return false
        if (corporatecards != other.corporatecards) return false
        if (cseg1 != other.cseg1) return false
        if (currency != other.currency) return false
        if (currencylist != other.currencylist) return false
        if (custentity_2663_payment_method != other.custentity_2663_payment_method) return false
        if (custentity_esc_last_modified_date != other.custentity_esc_last_modified_date) return false
        if (custentity_fispan_pending_approval != other.custentity_fispan_pending_approval) return false
        if (custentity_restrictexpensify != other.custentity_restrictexpensify) return false
        if (customForm != other.customForm) return false
        if (dateCreated != other.dateCreated) return false
        if (defaultAddress != other.defaultAddress) return false
        if (defaultexpensereportcurrency != other.defaultexpensereportcurrency) return false
        if (department != other.department) return false
        if (effectivedatemode != other.effectivedatemode) return false
        if (email != other.email) return false
        if (empcenterqty != other.empcenterqty) return false
        if (empcenterqtymax != other.empcenterqtymax) return false
        if (empperms != other.empperms) return false
        if (entityId != other.entityId) return false
        if (externalId != other.externalId) return false
        if (firstName != other.firstName) return false
        if (fulluserqty != other.fulluserqty) return false
        if (fulluserqtymax != other.fulluserqtymax) return false
        if (gender != other.gender) return false
        if (giveAccess != other.giveAccess) return false
        if (globalSubscriptionStatus != other.globalSubscriptionStatus) return false
        if (hireDate != other.hireDate) return false
        if (i9verified != other.i9verified) return false
        if (id != other.id) return false
        if (initials != other.initials) return false
        if (isInactive != other.isInactive) return false
        if (isempcenterqtyenforced != other.isempcenterqtyenforced) return false
        if (isfulluserqtyenforced != other.isfulluserqtyenforced) return false
        if (isretailuserqtyenforced != other.isretailuserqtyenforced) return false
        if (issalesrep != other.issalesrep) return false
        if (lastModifiedDate != other.lastModifiedDate) return false
        if (lastName != other.lastName) return false
        if (links != other.links) return false
        if (purchaseorderlimit != other.purchaseorderlimit) return false
        if (requirePwdChange != other.requirePwdChange) return false
        if (retailuserqty != other.retailuserqty) return false
        if (retailuserqtymax != other.retailuserqtymax) return false
        if (roles != other.roles) return false
        if (sendEmail != other.sendEmail) return false
        if (subsidiary != other.subsidiary) return false
        if (supervisor != other.supervisor) return false
        if (terminationbydeath != other.terminationbydeath) return false
        if (wasempcenterhasaccess != other.wasempcenterhasaccess) return false
        if (wasfulluserhasaccess != other.wasfulluserhasaccess) return false
        if (wasinactive != other.wasinactive) return false
        if (wasretailuserhasaccess != other.wasretailuserhasaccess) return false
        if (workCalendar != other.workCalendar) return false

        return true
    }

    override fun hashCode(): Int {
        var result = addressBook?.hashCode() ?: 0
        result = 31 * result + autoName.hashCode()
        result = 31 * result + (btemplate?.hashCode() ?: 0)
        result = 31 * result + (campaigns?.hashCode() ?: 0)
        result = 31 * result + concurrentwebservicesuser.hashCode()
        result = 31 * result + (corporatecards?.hashCode() ?: 0)
        result = 31 * result + (cseg1?.hashCode() ?: 0)
        result = 31 * result + (currency?.hashCode() ?: 0)
        result = 31 * result + (currencylist?.hashCode() ?: 0)
        result = 31 * result + custentity_2663_payment_method.hashCode()
        result = 31 * result + (custentity_esc_last_modified_date?.hashCode() ?: 0)
        result = 31 * result + custentity_fispan_pending_approval.hashCode()
        result = 31 * result + custentity_restrictexpensify.hashCode()
        result = 31 * result + (customForm?.hashCode() ?: 0)
        result = 31 * result + (dateCreated?.hashCode() ?: 0)
        result = 31 * result + (defaultAddress?.hashCode() ?: 0)
        result = 31 * result + (defaultexpensereportcurrency?.hashCode() ?: 0)
        result = 31 * result + (department?.hashCode() ?: 0)
        result = 31 * result + (effectivedatemode?.hashCode() ?: 0)
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (empcenterqty?.hashCode() ?: 0)
        result = 31 * result + (empcenterqtymax?.hashCode() ?: 0)
        result = 31 * result + (empperms?.hashCode() ?: 0)
        result = 31 * result + (entityId?.hashCode() ?: 0)
        result = 31 * result + (externalId?.hashCode() ?: 0)
        result = 31 * result + (firstName?.hashCode() ?: 0)
        result = 31 * result + (fulluserqty?.hashCode() ?: 0)
        result = 31 * result + (fulluserqtymax?.hashCode() ?: 0)
        result = 31 * result + (gender?.hashCode() ?: 0)
        result = 31 * result + giveAccess.hashCode()
        result = 31 * result + (globalSubscriptionStatus?.hashCode() ?: 0)
        result = 31 * result + (hireDate?.hashCode() ?: 0)
        result = 31 * result + i9verified.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + (initials?.hashCode() ?: 0)
        result = 31 * result + isInactive.hashCode()
        result = 31 * result + (isempcenterqtyenforced?.hashCode() ?: 0)
        result = 31 * result + (isfulluserqtyenforced?.hashCode() ?: 0)
        result = 31 * result + (isretailuserqtyenforced?.hashCode() ?: 0)
        result = 31 * result + issalesrep.hashCode()
        result = 31 * result + (lastModifiedDate?.hashCode() ?: 0)
        result = 31 * result + (lastName?.hashCode() ?: 0)
        result = 31 * result + (links?.hashCode() ?: 0)
        result = 31 * result + purchaseorderlimit.hashCode()
        result = 31 * result + requirePwdChange.hashCode()
        result = 31 * result + (retailuserqty?.hashCode() ?: 0)
        result = 31 * result + (retailuserqtymax?.hashCode() ?: 0)
        result = 31 * result + (roles?.hashCode() ?: 0)
        result = 31 * result + sendEmail.hashCode()
        result = 31 * result + (subsidiary?.hashCode() ?: 0)
        result = 31 * result + (supervisor?.hashCode() ?: 0)
        result = 31 * result + terminationbydeath.hashCode()
        result = 31 * result + (wasempcenterhasaccess?.hashCode() ?: 0)
        result = 31 * result + (wasfulluserhasaccess?.hashCode() ?: 0)
        result = 31 * result + (wasinactive?.hashCode() ?: 0)
        result = 31 * result + (wasretailuserhasaccess?.hashCode() ?: 0)
        result = 31 * result + (workCalendar?.hashCode() ?: 0)
        return result
    }
}

data class AddressBookLinks(
    val links: List<WebLink>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AddressBookLinks) return false
        if (links != other.links) return false
        return true
    }

    override fun hashCode(): Int {
        return links?.hashCode() ?: 0
    }
}

data class CampaignsLinks(
    val links: List<WebLink>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CampaignsLinks) return false
        if (links != other.links) return false
        return true
    }

    override fun hashCode(): Int {
        return links?.hashCode() ?: 0
    }
}

data class CorporatecardsLinks(
    val links: List<WebLink>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CorporatecardsLinks) return false
        if (links != other.links) return false
        return true
    }

    override fun hashCode(): Int {
        return links?.hashCode() ?: 0
    }
}

data class Cseg1Links(
    val id: String?,
    val links: List<WebLink>?,
    val refName: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cseg1Links) return false
        if (id != other.id) return false
        if (refName != other.refName) return false
        if (links != other.links) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (links?.hashCode() ?: 0)
        result = 31 * result + (refName?.hashCode() ?: 0)
        return result
    }
}

data class CurrencyLinks(
    val id: String,
    val links: List<WebLink>?,
    val refName: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CurrencyLinks) return false
        if (id != other.id) return false
        if (refName != other.refName) return false
        if (links != other.links) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (links?.hashCode() ?: 0)
        result = 31 * result + (refName?.hashCode() ?: 0)
        return result
    }
}

data class CurrencylistLinks(
    val links: List<WebLink>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CurrencylistLinks) return false
        if (links != other.links) return false
        return true
    }

    override fun hashCode(): Int {
        return links?.hashCode() ?: 0
    }
}

data class CustomForm(
    val id: String?,
    val refName: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CustomForm) return false
        if (id != other.id) return false
        if (refName != other.refName) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (refName?.hashCode() ?: 0)
        return result
    }
}

data class DefaultexpensereportcurrencyLinks(
    val id: String?,
    val links: List<WebLink>?,
    val refName: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DefaultexpensereportcurrencyLinks) return false
        if (id != other.id) return false
        if (refName != other.refName) return false
        if (links != other.links) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (links?.hashCode() ?: 0)
        result = 31 * result + (refName?.hashCode() ?: 0)
        return result
    }
}

data class DepartmentLinks(
    val id: String?,
    val links: List<WebLink>?,
    val refName: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DepartmentLinks) return false
        if (id != other.id) return false
        if (refName != other.refName) return false
        if (links != other.links) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (links?.hashCode() ?: 0)
        result = 31 * result + (refName?.hashCode() ?: 0)
        return result
    }
}

data class Effectivedatemode(
    val id: String?,
    val refName: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Effectivedatemode) return false
        if (id != other.id) return false
        if (refName != other.refName) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (refName?.hashCode() ?: 0)
        return result
    }
}

data class EmppermsLinks(
    val links: List<WebLink>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmppermsLinks) return false
        if (links != other.links) return false
        return true
    }

    override fun hashCode(): Int {
        return links?.hashCode() ?: 0
    }
}

data class Gender(
    val id: String?,
    val refName: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Gender) return false
        if (id != other.id) return false
        if (refName != other.refName) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (refName?.hashCode() ?: 0)
        return result
    }
}

data class GlobalSubscriptionStatus(
    val id: String?,
    val refName: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GlobalSubscriptionStatus) return false
        if (id != other.id) return false
        if (refName != other.refName) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (refName?.hashCode() ?: 0)
        return result
    }
}

data class WebLink(
    val href: String?,
    val rel: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WebLink) return false
        if (href != other.href) return false
        if (rel != other.rel) return false
        return true
    }

    override fun hashCode(): Int {
        var result = href?.hashCode() ?: 0
        result = 31 * result + (rel?.hashCode() ?: 0)
        return result
    }
}

data class RolesLinks(
    val links: List<WebLink>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RolesLinks) return false
        if (links != other.links) return false
        return true
    }

    override fun hashCode(): Int {
        return links?.hashCode() ?: 0
    }
}

data class SubsidiaryLinks(
    val id: String?,
    val links: List<WebLink>?,
    val refName: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SubsidiaryLinks) return false
        if (id != other.id) return false
        if (refName != other.refName) return false
        if (links != other.links) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (links?.hashCode() ?: 0)
        result = 31 * result + (refName?.hashCode() ?: 0)
        return result
    }
}

data class SupervisorLinks(
    val id: String?,
    val links: List<WebLink>?,
    val refName: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SupervisorLinks) return false
        if (id != other.id) return false
        if (refName != other.refName) return false
        if (links != other.links) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (links?.hashCode() ?: 0)
        result = 31 * result + (refName?.hashCode() ?: 0)
        return result
    }
}

data class WorkCalendarLinks(
    val id: String?,
    val links: List<WebLink>?,
    val refName: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WorkCalendarLinks) return false
        if (id != other.id) return false
        if (refName != other.refName) return false
        if (links != other.links) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (links?.hashCode() ?: 0)
        result = 31 * result + (refName?.hashCode() ?: 0)
        return result
    }
}
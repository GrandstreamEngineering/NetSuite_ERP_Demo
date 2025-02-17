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

package com.gs.app.netsuiteerp.network.apis

import com.gs.app.netsuiteerp.beans.Currency
import com.gs.app.netsuiteerp.beans.Employee
import com.gs.app.netsuiteerp.beans.InventoryAdjustment
import com.gs.app.netsuiteerp.beans.InventoryTransfer
import com.gs.app.netsuiteerp.beans.OAuth2Token
import com.gs.app.netsuiteerp.beans.PageData
import com.gs.app.netsuiteerp.beans.Subsidiary
import com.gs.app.netsuiteerp.network.NetworkConfig
import com.gs.app.netsuiteerp.network.response.EmptyResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NetsuiteServiceApi {
    // 获取 token
//    @Headers("content-type:application/json; charset=UTF-8")
//    @Headers("content-type:application/x-www-form-urlencoded; charset=UTF-8")
    @FormUrlEncoded // 必须 @POST + @Field 或 @FieldMap 组合使用，否则，可能会报错
    @POST(NetworkConfig.OAUTH2_STEP2_TOKEN_PATH)
    fun getToken(
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String,
        @Field(NetworkConfig.OAUTH2_STEP2_CODE) code: String,
        @Field(NetworkConfig.OAUTH2_STEP2_REDIRECT_URI_KEY) redirectUri: String,
        @Field(NetworkConfig.OAUTH2_STEP2_GRANT_TYPE_KEY) grantType: String,
        @Field(NetworkConfig.OAUTH2_STEP2_CODE_VERIFIER_KEY) codeVerifier: String
    ): Call<OAuth2Token>

    @FormUrlEncoded // 必须 @POST + @Field 或 @FieldMap 组合使用，否则，可能会报错
    @POST(NetworkConfig.OAUTH2_STEP2_TOKEN_PATH)
    fun getToken(
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String,
        @Field(NetworkConfig.OAUTH2_STEP2_CODE) code: String,
        @Field(NetworkConfig.OAUTH2_STEP2_REDIRECT_URI_KEY) redirectUri: String,
        @Field(NetworkConfig.OAUTH2_STEP2_GRANT_TYPE_KEY) grantType: String
    ): Call<OAuth2Token>

    // 更新token
    @FormUrlEncoded // 必须 @POST + @Field 或 @FieldMap 组合使用，否则，可能会报错
    @POST(NetworkConfig.OAUTH2_REFRESH_TOKEN_PATH)
    fun refreshToken(
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String,
        @Field(NetworkConfig.OAUTH2_REFRESH_TOKEN_GRANT_TYPE_KEY) grantType: String,
        @Field(NetworkConfig.OAUTH2_REFRESH_TOKEN_KEY) refreshToken: String
    ): Call<OAuth2Token>

    // 撤销token
    @FormUrlEncoded // 必须 @POST + @Field 或 @FieldMap 组合使用，否则，可能会报错
    @POST(NetworkConfig.OAUTH2_REVOKE_TOKEN_PATH)
    fun revokeToken(
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String,
        @Field(NetworkConfig.OAUTH2_REVOKE_TOKEN_KEY) refreshToken: String
    ): Call<EmptyResponse>

    // 退出登录
    @Headers("content-type:application/x-www-form-urlencoded; charset=UTF-8")
    @GET(NetworkConfig.OAUTH2_LOGOUT_PATH)
    fun logout(
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String,
        @Field(NetworkConfig.OAUTH2_LOGOUT_ID_TOKEN_HINT_KEY) idTokenHint: String
    ): Call<EmptyResponse>

    // 查询员工信息
    @Headers("content-type:application/json; charset=UTF-8")
    @GET("${NetworkConfig.REST_PATH_EMPLOYEE}/{id}")
    fun getEmployee(
        @Path("id") id: String,
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String
    ): Call<Employee>

    // 查询客户列表
    @Headers("content-type:application/json; charset=UTF-8")
    @GET(NetworkConfig.REST_PATH_CUSTOMER)
    fun getCustomer(
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String
    ): Call<EmptyResponse>

    // 查询客户信息
    @Headers("content-type:application/json; charset=UTF-8")
    @GET("${NetworkConfig.REST_PATH_CUSTOMER}/{id}")
    fun getCustomer(
        @Path("id") id: String,
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String
    ): Call<EmptyResponse>

    // 查询库存调整列表
    @Headers("content-type:application/json; charset=UTF-8")
    @GET(NetworkConfig.REST_PATH_INVENTORY_ADJUSTMENT)
    fun getInventoryAdjustments(
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String,
        @Query(NetworkConfig.REST_QUERY_PARAM_PAGE_Q) q: String,
        @Query(NetworkConfig.REST_QUERY_PARAM_PAGE_LIMIT) limit: String,
        @Query(NetworkConfig.REST_QUERY_PARAM_PAGE_OFFSET) offset: String
    ): Call<PageData>

    // 查询库存调整列表
    @Headers("content-type:application/json; charset=UTF-8")
    @GET(NetworkConfig.REST_PATH_INVENTORY_ADJUSTMENT)
    fun getInventoryAdjustments(
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String,
        @Query(NetworkConfig.REST_QUERY_PARAM_PAGE_LIMIT) limit: String,
        @Query(NetworkConfig.REST_QUERY_PARAM_PAGE_OFFSET) offset: String
    ): Call<PageData>

    // 查询库存调整列表
    @Headers("content-type:application/json; charset=UTF-8")
    @GET(NetworkConfig.REST_PATH_INVENTORY_ADJUSTMENT)
    fun getInventoryAdjustments(
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String,
        @Query(NetworkConfig.REST_QUERY_PARAM_PAGE_Q) q: String,
    ): Call<PageData>

    // 查询库存调整列表
    @Headers("content-type:application/json; charset=UTF-8")
    @GET(NetworkConfig.REST_PATH_INVENTORY_ADJUSTMENT)
    fun getInventoryAdjustments(
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String
    ): Call<PageData>

    // 获取库存调整
    @Headers("content-type:application/json; charset=UTF-8")
    @GET("${NetworkConfig.REST_PATH_INVENTORY_ADJUSTMENT}/{id}")
    fun getInventoryAdjustment(
        @Path("id") id: String,
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String
    ): Call<InventoryAdjustment>

    // 获取附属子公司信息
    @Headers("content-type:application/json; charset=UTF-8")
    @GET("${NetworkConfig.REST_PATH_SUBSIDIARY}/{id}")
    fun getSubsidiary(
        @Path("id") id: String,
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String
    ): Call<Subsidiary>

    // 获取货币信息
    @Headers("content-type:application/json; charset=UTF-8")
    @GET("${NetworkConfig.REST_PATH_CURRENCY}/{id}")
    fun getCurrency(
        @Path("id") id: String,
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String
    ): Call<Currency>

    // 查询库存转移列表
    @Headers("content-type:application/json; charset=UTF-8")
    @GET(NetworkConfig.REST_PATH_INVENTORY_TRANSFER)
    fun getInventoryTransfers(
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String,
        @Query(NetworkConfig.REST_QUERY_PARAM_PAGE_Q) q: String,
        @Query(NetworkConfig.REST_QUERY_PARAM_PAGE_LIMIT) limit: String,
        @Query(NetworkConfig.REST_QUERY_PARAM_PAGE_OFFSET) offset: String
    ): Call<PageData>

    // 查询库存转移列表
    @Headers("content-type:application/json; charset=UTF-8")
    @GET(NetworkConfig.REST_PATH_INVENTORY_TRANSFER)
    fun getInventoryTransfers(
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String,
        @Query(NetworkConfig.REST_QUERY_PARAM_PAGE_LIMIT) limit: String,
        @Query(NetworkConfig.REST_QUERY_PARAM_PAGE_OFFSET) offset: String
    ): Call<PageData>

    // 查询库存转移列表
    @Headers("content-type:application/json; charset=UTF-8")
    @GET(NetworkConfig.REST_PATH_INVENTORY_TRANSFER)
    fun getInventoryTransfers(
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String,
        @Query(NetworkConfig.REST_QUERY_PARAM_PAGE_Q) q: String,
    ): Call<PageData>

    // 查询库存转移列表
    @Headers("content-type:application/json; charset=UTF-8")
    @GET(NetworkConfig.REST_PATH_INVENTORY_TRANSFER)
    fun getInventoryTransfers(
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String
    ): Call<PageData>

    // 获取库存转移
    @Headers("content-type:application/json; charset=UTF-8")
    @GET("${NetworkConfig.REST_PATH_INVENTORY_TRANSFER}/{id}")
    fun getInventoryTransfer(
        @Path("id") id: String,
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String
    ): Call<InventoryTransfer>
}
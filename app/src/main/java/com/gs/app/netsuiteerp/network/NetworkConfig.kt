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

package com.gs.app.netsuiteerp.network

object NetworkConfig {
    const val DEBUG = true

    const val ERROR_CODE_NETWORK = -100
    const val ERROR_CODE_EMPTY_BODY = -101
    const val ERROR_CODE_GSON_CONVERT = -102
    const val ERROR_CODE_INVALID_REQUEST = 400
    const val ERROR_CODE_INVALID_TOKEN = 401

    const val ERROR_INVALID_REQUEST = "invalid_request"
    const val ERROR_UNAUTHORIZED_CLIENT = "unauthorized_client"
    const val ERROR_ACCESS_DENIED = "access_denied"
    const val ERROR_UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type"
    const val ERROR_INVALID_SCOPE = "invalid_scope"

    const val ERROR_INVALID_CLIENT = "invalid_client"
    const val ERROR_INVALID_GRANT = "invalid_grant"
    const val ERROR_UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type"

    const val ERROR_INVALID_TOKEN = "invalid_token"

//    const val OAUTH2_STEP1_AUTHORIZE_TOKEN_URL = "https://system.netsuite.com/app/login/oauth2/authorize.nl"
    const val OAUTH2_STEP1_AUTHORIZE_TOKEN_URL = "https://%s.app.netsuite.com/app/login/oauth2/authorize.nl"
    const val OAUTH2_STEP1_RESPONSE_TYPE_KEY = "response_type"
    const val OAUTH2_STEP1_RESPONSE_TYPE_VALUE = "code"
    const val OAUTH2_STEP1_CLIENT_ID_KEY = "client_id"
    const val OAUTH2_STEP1_REDIRECT_URI_KEY = "redirect_uri"
//    const val OAUTH2_STEP1_REDIRECT_URI_VALUE = "grandstream://com.gs.app.netsuiteerp/login/callback/8075c0014dc56ecf5d0e745af3b130b4"
//    const val OAUTH2_STEP1_REDIRECT_URI_SCHEME = "grandstream"
//    const val OAUTH2_STEP1_REDIRECT_URI_HOST = "com.gs.app.netsuiteerp"
//    const val OAUTH2_STEP1_REDIRECT_URI_PATH = "/login/callback/8075c0014dc56ecf5d0e745af3b130b4"
    const val OAUTH2_STEP1_SCOPE_KEY = "scope"
    const val OAUTH2_STEP1_SCOPE_VALUE = "restlets+rest_webservices"
    const val OAUTH2_STEP1_STATE_KEY = "state"
    const val OAUTH2_STEP1_STATE_VALUE = "a0c34b68b7b0b7e145bf6ccaab97c169f67a1c7f631e00c18adef4da4f1d1bfd"
    const val OAUTH2_STEP1_CODE_CHALLENGE_METHOD_KEY = "code_challenge_method"
    const val OAUTH2_STEP1_CODE_CHALLENGE_METHOD_VALUE = "S256"
    const val OAUTH2_STEP1_CODE_CHALLENGE_KEY = "code_challenge"
    const val OAUTH2_STEP1_CODE_VERIFIER = "f290f2bbef254e665be4dcae4043664b5cb4332c28934f267ae5bd533d0fa7b0"
    const val OAUTH2_STEP1_PROMPT_KEY = "prompt"
    const val OAUTH2_STEP1_PROMPT_VALUE = "login authorize"

    const val BASE_REST_URL = "https://%s.suitetalk.api.netsuite.com"
    const val HEADER_AUTHORIZATION = "Authorization"

    const val OAUTH2_STEP2_TOKEN_URL = "https://%s.suitetalk.api.netsuite.com/services/rest/auth/oauth2/v1/token"
    const val OAUTH2_STEP2_TOKEN_PATH = "/services/rest/auth/oauth2/v1/token"
    const val OAUTH2_STEP2_CODE = "code"
    const val OAUTH2_STEP2_REDIRECT_URI_KEY = OAUTH2_STEP1_REDIRECT_URI_KEY
//    const val OAUTH2_STEP2_REDIRECT_URI_VALUE = OAUTH2_STEP1_REDIRECT_URI_VALUE
    const val OAUTH2_STEP2_GRANT_TYPE_KEY = "grant_type"
    const val OAUTH2_STEP2_GRANT_TYPE_VALUE = "authorization_code"
    const val OAUTH2_STEP2_CODE_VERIFIER_KEY = "code_verifier"

    const val OAUTH2_REFRESH_TOKEN_URL = "https://%s.suitetalk.api.netsuite.com/services/rest/auth/oauth2/v1/token"
    const val OAUTH2_REFRESH_TOKEN_PATH = "/services/rest/auth/oauth2/v1/token"
    const val OAUTH2_REFRESH_TOKEN_GRANT_TYPE_KEY = "grant_type"
    const val OAUTH2_REFRESH_TOKEN_GRANT_TYPE_VALUE = "refresh_token"
    const val OAUTH2_REFRESH_TOKEN_KEY = "refresh_token"

    const val OAUTH2_REVOKE_TOKEN_URL = "https://%s.suitetalk.api.netsuite.com/services/rest/auth/oauth2/v1/revoke"
    const val OAUTH2_REVOKE_TOKEN_PATH = "/services/rest/auth/oauth2/v1/revoke"
    const val OAUTH2_REVOKE_TOKEN_KEY = "token"

    const val OAUTH2_LOGOUT_URL = "https://%s.suitetalk.api.netsuite.com/services/rest/auth/oauth2/v1/logout"
    const val OAUTH2_LOGOUT_PATH = "/services/rest/auth/oauth2/v1/logout"
    const val OAUTH2_LOGOUT_ID_TOKEN_HINT_KEY = "id_token_hint"

    const val REST_QUERY_PARAM_PAGE_LIMIT = "limit"
    const val REST_QUERY_PARAM_PAGE_OFFSET = "offset"
    const val REST_QUERY_PARAM_PAGE_Q = "q"

    const val REST_PATH_EMPLOYEE = "/services/rest/record/v1/employee"
    const val REST_PATH_CUSTOMER = "/services/rest/record/v1/customer"

    const val REST_PATH_INVENTORY_ADJUSTMENT = "/services/rest/record/v1/inventoryAdjustment"
    const val REST_PATH_SUBSIDIARY = "/services/rest/record/v1/subsidiary"
    const val REST_PATH_CURRENCY = "/services/rest/record/v1/currency"

    const val REST_PATH_INVENTORY_TRANSFER = "/services/rest/record/v1/inventoryTransfer"

    const val REST_PATH_INVENTORY_COUNT = "/services/rest/record/v1/inventoryCount"
    const val REST_PATH_INVENTORY_ITEM = "/services/rest/record/v1/inventoryItem"
}
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

package com.gs.app.netsuiteerp.common.scan;

public interface Constants {
    String ACTION_SCAN_GET_CFG = "com.gs.intent.action.SCAN_GET_CFG";
    String ACTION_SCAN_CFG_INFO = "com.gs.intent.action.SCAN_CFG_INFO";
    String EXTRA_PROFILE = "profile";
    String EXTRA_QUERY = "query";
    String EXTRA_INFO = "info";

    String ACTION_SCAN_SET_CFG = "com.gs.intent.action.SCAN_SET_CFG";
    String EXTRA_ACTION = "action";
    String EXTRA_REQUEST_INFO = "request_info";

    String SCAN_RESULT_OK = "ok";
    String ACTION_SCAN_RESULT = "com.gs.intent.action.SCAN_RESULT";
    String EXTRA_SCAN_BARCODE1 = "SCAN_BARCODE1";
    String EXTRA_SCAN_BARCODE2 = "SCAN_BARCODE2";
    String EXTRA_SCAN_STATE = "SCAN_STATE";
    String EXTRA_SCAN_BARCODE_TYPE = "SCAN_BARCODE_TYPE";
    String EXTRA_SCAN_BARCODE_TYPE_NAME = "SCAN_SBARCODE_TYPE";
}

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

public enum QueryType {
    Profile("profile");
    public String value;

    QueryType(String value) {
        this.value = value;
    }

    public static QueryType find(String query) {
        for (QueryType temp : QueryType.values()) {
            if (temp.value.equals(query)) {
                return temp;
            }
        }
        return null;
    }
}
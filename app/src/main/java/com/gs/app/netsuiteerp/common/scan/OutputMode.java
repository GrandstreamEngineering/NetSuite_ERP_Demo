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

public enum OutputMode {
    Filling("filling"),
    EmulateKey("emulateKey"),
    Broadcast("broadcast"),
    UsbHid("usbHid"),
    BluetoothHid("bluetoothHid"),
    Network("network");
    public final String value;
    public static OutputMode find(String action){
        for (OutputMode temp: OutputMode.values()){
            if(temp.value.equals(action)){
                return temp;
            }
        }
        return null;
    }

    OutputMode(String value) {
        this.value = value;
    }
}
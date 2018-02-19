/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.nisrulz.senseysample;

import android.app.Application;
import com.squareup.leakcanary.LeakCanary;

import org.json.JSONObject;

public class MyApplication extends Application {

    // STATE VARS
    private JSONObject accessToken;
    private String equipmentToken;

    public JSONObject getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(JSONObject token) {
        this.accessToken = token;
    }

    public String getEquipmentToken() {
        return this.equipmentToken;
    }
    public void setEquipmentToken(String token) {
        this.equipmentToken = token;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...

        this.accessToken = null;
    }
}

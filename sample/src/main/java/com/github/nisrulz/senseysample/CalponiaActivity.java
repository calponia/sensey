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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The type Touch activity.
 */
public class CalponiaActivity extends AppCompatActivity {

    private static final String LOGTAG = "CalponiaActivity";

    private static final boolean DEBUG = true;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calponia);

        Button connectButton = findViewById(R.id.btn_connect);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });

    }

    protected boolean IsError (JSONObject data) {
        if (data.has("error")) {
            try {
                Snackbar.make(findViewById(R.id.activity_calponia),
                        data.getString("error"),
                        Snackbar.LENGTH_LONG).show();
                return true;
            } catch (Exception e) {
                Log.i(LOGTAG, e.getMessage());
            }
        }
        return false;
    }

    protected void Login () {
        EditText username = findViewById(R.id.text_username);
        EditText password = findViewById(R.id.text_password);

        try {
            JSONObject data = new JSONObject();
            data.put("email", username.getText().toString());
            data.put("password", password.getText().toString());
            Log.i(LOGTAG, data.toString());

            // Login to calponia
            CalponiaREST.Call(new Request.Params(this, CalponiaREST.LOGIN, "POST", data) {
                @Override
                void onPostExecute (JSONObject data) {
                    try {
                        if (!IsError(data)) {
                            Snackbar.make(findViewById(R.id.activity_calponia),
                                    "Login Successful. Retrieving connectivity devices...",
                                    Snackbar.LENGTH_LONG).show();

                            ((MyApplication) getApplication()).setAccessToken(data.getJSONObject("response"));
                            GetProjects();
                        }
                    } catch (Exception e) {
                        Log.i(LOGTAG, e.getMessage());
                    }

                }
            });
        } catch (Exception e) {
            Log.i(LOGTAG, e.getMessage());
        }
    }

    protected void GetProjects () {
        try {
            String userId = ((MyApplication) getApplication()).getAccessToken().getString("userId");
            CalponiaREST.Call(new Request.Params(this, String.format(CalponiaREST.PROJECTS, userId)) {
                @Override
                void onPostExecute(JSONObject data) {
                    try {
                        if (!IsError(data)) {
                            ProcessProjects(data.getJSONArray("response"));
                        }
                    } catch (Exception e) {
                        Log.i(LOGTAG, e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Log.i(LOGTAG, e.getMessage());
        }
    }

    protected void ProcessProjects (JSONArray projects) {
        Log.i(LOGTAG, "here");
        Log.i(LOGTAG, projects.toString());
        try {
            String userId = ((MyApplication) getApplication()).getAccessToken().getString("userId");
            Log.i(LOGTAG, userId);
            final ArrayList<String> equipmentList = new ArrayList<String>();

            final ArrayAdapter<String> adapter = new ArrayAdapter<>
                    (this, android.R.layout.simple_list_item_1, equipmentList);

            ListView list = findViewById(R.id.listView_equipments);
            list.setAdapter(adapter);

            for (int i = 0; i < projects.length(); i++) {
                final JSONObject project = projects.getJSONObject(i);
                CalponiaREST.Call(new Request.Params(this, String.format(CalponiaREST.EQUIPMENTS, project.getString("id"))) {
                    @Override
                    void onPostExecute(JSONObject data) {
                        try {
                            if (!IsError(data)) {
                                JSONArray result = data.getJSONArray("response");

                                boolean hasIoT = false;
                                for (int i=0; i < result.length(); i++) {
                                    JSONObject equipment = result.getJSONObject(i);
                                    if (equipment.getBoolean("iotDevice")) {
                                        hasIoT = true;
                                        equipmentList.add(equipment.getString("id"));
                                    }
                                }
                                // Personal is a internal project
                                if (!hasIoT && !project.get("name").equals("Personal"))  {
                                    Snackbar.make(findViewById(R.id.activity_calponia),
                                            String.format("Project '%s' has no iot equipment.", project.get("name")),
                                            Snackbar.LENGTH_LONG).show();
                                }
                                adapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            Log.i(LOGTAG, e.getMessage());
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.i(LOGTAG, e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

package com.github.nisrulz.senseysample;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

public class CalponiaREST extends Application  {

    private static final String LOGTAG = "CalponiaREST";

    // URLS
    public static final String LOGIN = "https://api-internal.calponia-beta.de/api/users/login";
    public static final String PROJECTS = "https://api-internal.calponia-beta.de/api/users/%s/projects";
    public static final String EQUIPMENTS = "https://api-internal.calponia-beta.de/api/projects/%s/equipment";
    public static final String RELAY = "https://iot.calponia-beta.de/sensey";

    private static long LastRequest = 0;

    public static void Call (Request.Params params) {
        // limit request to every second
        if (params.URL == RELAY && LastRequest > System.currentTimeMillis() - 1000) return;

        Request req = new Request();
        req.execute(params);
        LastRequest = System.currentTimeMillis();
    }

    public static void Relay (Context ctx, String event) {
        Relay(ctx, event, new JSONObject());
    }
    public static void Relay (Context ctx, String event, JSONObject data) {
        if (((MyApplication) ((Activity) ctx).getApplication()).getAccessToken() == null) return;
        try {
            data.put("event", event);
        } catch (Exception e) {}
        Call(new Request.Params(ctx, RELAY, "PUT", data) {
            @Override
            void onPostExecute(JSONObject data) {
                Log.i(LOGTAG, data.toString());
            }
        });
    }
}

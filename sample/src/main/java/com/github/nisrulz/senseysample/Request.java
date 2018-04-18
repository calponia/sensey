package com.github.nisrulz.senseysample;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Request extends AsyncTask<Request.Params, Void, JSONObject> {

    private static final String LOGTAG = "Request";

    private Params params;

    public static class Params {
        String method;
        String URL;
        JSONObject data;
        Context ctx;

        Params(Context ctx, String URL) {
            this.ctx = ctx;
            this.method = "GET";
            this.URL = URL;
            this.data = null;
        }
        Params(Context ctx, String URL, String method, JSONObject data) {
            this.ctx = ctx;
            this.method = method;
            this.URL = URL;
            this.data = data;
        }

        void onPostExecute (JSONObject data) {
            Log.i("Params", data.toString());
        }
    }

    @Override
    protected JSONObject doInBackground(Request.Params... args) {
        params = args[0];

        HttpURLConnection connection = null;
        try {
            Log.i(LOGTAG, String.format("%s %s", params.method, params.URL));
            URL url = new URL(params.URL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod(params.method);
            connection.setInstanceFollowRedirects(false);
            connection.setDoInput(true);
            JSONObject token = ((MyApplication) ((Activity) params.ctx).getApplication()).getAccessToken();
            if (token != null) {
                connection.setRequestProperty("Authorization", token.getString("id"));
            }

            if (params.data != null) {
                Log.i(LOGTAG, params.data.toString());
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(params.data.toString());
                writer.flush();
            }

            if (connection.getResponseCode() != 200) {
                return new JSONObject(String.format("{\"error\": \"%s\"}", connection.getResponseMessage()));
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer res = new StringBuffer(1024);
            do {
                String line = reader.readLine();
                if (line == null) break;
                res.append(line).append("\n");
            } while (true);
            reader.close();

            Log.i(LOGTAG, res.toString());
            return new JSONObject(String.format("{\"response\": %s}", res.toString()));
        } catch(Exception e) {
            try {
                return new JSONObject(String.format("{\"error\": \"%s\"}", e.getMessage()));
            } catch (Exception _e) {}
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject data) {
        params.onPostExecute(data);
    }
}

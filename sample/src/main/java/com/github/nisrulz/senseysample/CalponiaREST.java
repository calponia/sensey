package com.github.nisrulz.senseysample;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.app.Activity;
import android.app.Application;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class CalponiaREST extends Application  {

    private static final String LOGTAG = "CalponiaREST";

    // URLS
    public static final String LOGIN = "https://api-internal.calponia-bcx.de/api/users/login";
    public static final String PROJECTS = "https://api-internal.calponia-bcx.de/api/users/%s/projects";
    public static final String EQUIPMENTS = "https://api-internal.calponia-bcx.de/api/projects/%s/equipment";

    public static void Call (Request.Params params) {
        Request req = new Request();
        req.execute(params);
    }
}

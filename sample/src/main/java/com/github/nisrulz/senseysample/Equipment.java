package com.github.nisrulz.senseysample;

import org.json.JSONObject;

public class Equipment {
    public String name;
    public JSONObject token;
    public String project;

    Equipment(JSONObject token, String name, String project) {
        this.name = name;
        this.token = token;
        this.project = project;
    }

    @Override
    public String toString() {
        try {
            return String.format("%s @ %s\n%s", this.name, this.project, this.token.getString("id"));
        } catch (Exception e) {
            return e.toString();
        }
    }
}

package com.oneminutebefore.workout.helpers;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by tahir on 13/5/17.
 */

public class UrlBuilder {

    private static final String BASE_URL = "http://1minutebefore.com";
    public static final String API_REGISTER = "/api/users";
    public static final String API_ME = API_REGISTER + "/me";
    public static final String API_AUTH = "/auth/local";
    public static final String API_ALL_VIDEOS = "/api/packages";
    public static final String API_ALL_CATEGORIES = "/api/categories";
    public static final String API_PRODUCTS = "/api/products";
    public static final String API_GET_WORKOUTS = API_PRODUCTS + "/me";
    public static final String API_SAVE_REPS = API_PRODUCTS + "/usertrack";

    private String uri;

    private ArrayList<String> parameters;
    private ArrayList<String> sections;

    public UrlBuilder(String uri){
        this.uri = uri;
        parameters = new ArrayList<>();
    }

    public UrlBuilder addParameters(String name, String value){
        parameters.add(name + "=" + value);
        return this;
    }

    public UrlBuilder addSection(String section){
        if(sections == null){
            sections = new ArrayList<>();
        }
        sections.add(section);
        return this;
    }

    public String build(){
        StringBuilder builder = new StringBuilder();
        builder.append(BASE_URL).append(uri);
        if(sections != null && !sections.isEmpty()){
            for(String section : sections){
                builder.append("/").append(section);
            }
        }
        if(parameters != null && !parameters.isEmpty()){
            builder.append("?").append(TextUtils.join("&",parameters));
        }
        return builder.toString();
    }

}

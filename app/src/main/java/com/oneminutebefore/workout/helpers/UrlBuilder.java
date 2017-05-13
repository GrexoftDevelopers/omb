package com.oneminutebefore.workout.helpers;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by tahir on 13/5/17.
 */

public class UrlBuilder {

    private static final String BASE_URL = "http://www.oneminutebefore.com/";
    public static final String API_REGISTER = "register.php";

    private String uri;

    private ArrayList<String> parameters;

    public UrlBuilder(String uri){
        this.uri = uri;
        parameters = new ArrayList<>();
    }

    public UrlBuilder addParameters(String name, String value){
        parameters.add(name + "=" + value);
        return this;
    }

    public String build(){
        StringBuilder builder = new StringBuilder();
        builder.append(BASE_URL).append(uri);
        if(parameters != null && !parameters.isEmpty()){
            builder.append("?").append(TextUtils.join("&",parameters));
        }
        return builder.toString();
    }

}
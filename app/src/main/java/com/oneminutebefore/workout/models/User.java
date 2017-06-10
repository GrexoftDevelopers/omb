package com.oneminutebefore.workout.models;

import org.json.JSONObject;

/**
 * Created by tahir on 28/5/17.
 */

public class User {





    private String salt;
    private String email;
    private String name;
    private String passwordHash;
    private int _v;
    private String phone;
    private String userLevel;
    private String groupCode;
    private String provider;
    private String role;
    private String timeZone;
    private String id;

    private boolean isActive;

    public User(String email, String name, String phone, String userLevel, String groupCode, String provider,String role,String timeZone) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.userLevel = userLevel;
        this.groupCode = groupCode;
        this.provider = provider;
        this.timeZone=timeZone;
        this.role=role;
    }

    public User(){

    }

    public String getSalt() {
        return salt;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public int get_v() {
        return _v;
    }

    public String getPhone() {
        return phone;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public String getProvider() {
        return provider;
    }

    public String getRole() {
        return role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static User createFromJson(JSONObject jsonObject){

        if(jsonObject != null){
            User user = new User();
            user.id = jsonObject.optString("_id");
            user.userLevel = jsonObject.optString("user_level");
            user.isActive = jsonObject.optBoolean("active");
            user.role = jsonObject.optString("role");
            user._v = jsonObject.optInt("__v");
            user.name = jsonObject.optString("name");
            user.provider = jsonObject.optString("provider");
            user.groupCode = jsonObject.optString("group_code");
            user.email = jsonObject.optString("email");
            user.phone = jsonObject.optString("phone");
            return user;
        }
        return null;
    }

}

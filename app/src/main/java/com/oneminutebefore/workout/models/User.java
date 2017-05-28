package com.oneminutebefore.workout.models;

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

    private boolean isActive;

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
}

package com.example.mms_scanner.model.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Mohammad Kobirul Islam
 * @version 1.0.0
 * @date today
 */

public class GetLogin {

    @SerializedName("token")
    @Expose
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "GetLogin{" +
                "token='" + token + '\'' +
                '}';
    }
}

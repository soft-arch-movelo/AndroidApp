package com.example.movelo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mensaje {
    @SerializedName("message")
    @Expose
    public String message;

    public String getMessage() {
        return message;
    }
}

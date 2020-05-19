package com.androidcorpo.lindapp.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PublicKeyResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("public_key")
    @Expose
    private String publicKey;

    @SerializedName("message")
    @Expose
    private String message;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

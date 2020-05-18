package com.androidcorpo.lindapp.model;

import java.security.PrivateKey;
import java.security.PublicKey;

public class MyKey {

    private long ID;

    private String contact;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public MyKey(String contact, PrivateKey privateKey, PublicKey publicKey) {
        this.contact = contact;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public MyKey(String contact, PublicKey publicKey) {
        this.contact = contact;
        this.publicKey = publicKey;
    }

    public MyKey() {
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }
}

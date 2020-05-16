package com.androidcorpo.lindapp.model;

public class MyKey {

    private long ID;

    private String contact;
    private String publicKey;
    private String privateKey;

    public MyKey(String contact, String privateKey, String publicKey) {
        this.contact = contact;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public MyKey(String contact, String publicKey) {
        this.contact = contact;
        this.publicKey = publicKey;
    }

    public MyKey() {
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
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

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }
}

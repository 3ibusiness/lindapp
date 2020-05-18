package com.androidcorpo.lindapp.network;


import android.os.AsyncTask;

import com.androidcorpo.lindapp.NetworkUtils;
import com.androidcorpo.lindapp.OpenJsonUtils;
import com.androidcorpo.lindapp.model.MyKey;
import com.androidcorpo.lindapp.resources.LindAppDbHelper;

import java.net.URL;
import java.security.PublicKey;

//AsyncTask get public key and save locally
public class GetOnlinePublicKey extends AsyncTask<String, String, MyKey> {
    private LindAppDbHelper lindAppDbHelper;
    PublicKey publicKey;

    public GetOnlinePublicKey(LindAppDbHelper lindAppDbHelper, PublicKey publicKey) {
        this.lindAppDbHelper = lindAppDbHelper;
        this.publicKey = publicKey;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected MyKey doInBackground(String... strings) {

        String contact = strings[0];
        URL publicKeyRequestUrl = NetworkUtils.buildGetPublicKeyUrl(contact);
        try {
            String jsonCoinResponse = NetworkUtils.getResponseFromHttpUrl(publicKeyRequestUrl);
            MyKey key = OpenJsonUtils.getPublicKeyFromJson(jsonCoinResponse);
            this.publicKey = key.getPublicKey();
            this.lindAppDbHelper.saveKey(key);
            return key;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected void onPostExecute(MyKey key) {
        super.onPostExecute(key);
    }
}

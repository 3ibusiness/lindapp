package com.androidcorpo.lindapp.network;

import android.os.AsyncTask;

import com.androidcorpo.lindapp.NetworkUtils;
import com.androidcorpo.lindapp.OpenJsonUtils;
import com.androidcorpo.lindapp.model.MyKey;

import java.net.URL;

public class PostOnlinePublicKey extends AsyncTask<MyKey, String, Integer> {


    @Override
    protected Integer doInBackground(MyKey... myKeys) {


        MyKey contact = myKeys[0];
            URL postKeyRequestUrl = NetworkUtils.postPublicKey(contact);
            try {
                String jsonCoinResponse = NetworkUtils.getResponseFromHttpUrl(postKeyRequestUrl);
                return OpenJsonUtils.resultPublicKeyFromJson(jsonCoinResponse);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }



    }
}

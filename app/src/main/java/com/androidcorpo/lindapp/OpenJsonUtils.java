package com.androidcorpo.lindapp;


/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.androidcorpo.lindapp.elipticurve.EEC;
import com.androidcorpo.lindapp.model.MyKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.PublicKey;


/**
 * Utility functions to handle public key JSON data.
 */
public final class OpenJsonUtils {

    public static Integer resultPublicKeyFromJson(String jsonCoinResponse) throws JSONException {
        JSONObject json = new JSONObject(jsonCoinResponse);
        return json.getInt("code");
    }

    public static MyKey getPublicKeyFromJson(String jsonCoinResponse) throws JSONException {
        JSONObject json = new JSONObject(jsonCoinResponse);
        MyKey myKey = null;
        if (json.getInt("code") == 200) {
            String s = json.getString("contact");
            String ss2 = json.getString("public_key");
            byte[] bytes = EEC.hexToBytes(ss2);
            PublicKey publicKey = null;
            try {
                publicKey = LindAppUtils.deSerializePublicKey(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            myKey = new MyKey(s, publicKey);
        }
        return myKey;
    }

}
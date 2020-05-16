package com.androidcorpo.lindapp;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the public key servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_ENDPOINT = "192.168.8.11";
    private static final String QUERY_PARAM_CONTACT = "contact";
    private static final String QUERY_PARAM_PUBLIC_KEY = "public_key";


    /**
     * Builds the URL used to handle public key  server using contact.
     *
     * @param contact The current contact will be queried for key.
     * @param publicKey The public_key that will be use by the other.
     * @return The URL to post query the public key.
     */
    public static URL postPublicKey(String contact,String publicKey) {
        Uri builtUri = Uri.parse(BASE_ENDPOINT+"/lindapp/create.php").buildUpon()
                .appendQueryParameter(QUERY_PARAM_CONTACT, contact)
                .appendQueryParameter(QUERY_PARAM_PUBLIC_KEY, publicKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }



    /**
     * Builds the URL to retrieve public key of user
     * on the query capabilities of the weather provider that we are using.
     *
     * @param contact The contact that we queried for key.
     * @return The URL to use to retrieve public key.
     */
    public static URL buildNewsUrl(String contact) {

        URL url = null;
        try {
            url = new URL(contact);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
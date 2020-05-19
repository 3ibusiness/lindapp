package com.androidcorpo.lindapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.androidcorpo.lindapp.elipticurve.EEC;
import com.androidcorpo.lindapp.model.MyKey;
import com.androidcorpo.lindapp.network.ApiClient;
import com.androidcorpo.lindapp.network.ApiInterface;
import com.androidcorpo.lindapp.network.response.PublicKeyResponse;
import com.androidcorpo.lindapp.resources.LindAppDbHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LindAppUtils {

    private static void readPublicKey(final LindAppDbHelper lindAppDbHelper, final String contact, final Context context) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PublicKeyResponse> call = apiService.read(contact);

        call.enqueue(new Callback<PublicKeyResponse>() {
            @Override
            public void onResponse(Call<PublicKeyResponse> call, Response<PublicKeyResponse> response) {
                PublicKeyResponse keyResponse = response.body();
                if (response.isSuccessful() && keyResponse.getCode() == 200) {
                    String responseContact = keyResponse.getContact();
                    String responsePublicKey = keyResponse.getPublicKey();
                    byte[] bytes = EEC.hexToBytes(responsePublicKey);
                    PublicKey publicKey = null;
                    try {
                        publicKey = LindAppUtils.deSerializePublicKey(bytes);
                        MyKey myKey = new MyKey(responseContact, publicKey);
                        lindAppDbHelper.saveKey(myKey);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context,"Public Key save ",Toast.LENGTH_LONG).show();
                }else if(keyResponse.getCode() == 404){
                    Toast.makeText(context,contact+" doesn't post is Public Key ",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PublicKeyResponse> call, Throwable t) {

                Toast.makeText(context,"Network issue try again later ",Toast.LENGTH_LONG).show();
            }
        });

    }

    public static void sendCypherMessage(final Context context, String plainText, String destiNumber) throws IOException {
        LindAppDbHelper lindAppDbHelper = LindAppDbHelper.getInstance(context);

        String phoneNumber = getCleanAdress(destiNumber);
        String myNumber = getMyContact(context);
        if (myNumber == null) {
            Toast.makeText(context, myNumber, Toast.LENGTH_LONG).show();
        } else {

            PublicKey publicKey = lindAppDbHelper.getPublicKey(phoneNumber);

            if (publicKey != null) {
                PrivateKey privateKey = lindAppDbHelper.getPrivateKey(myNumber);
                SecretKey secretKey = EEC.secretKey(privateKey, publicKey);

                String cypherText = EEC.crypt(secretKey, plainText);
                String SENT = "SMS_SENT";
                String DELIVERED = "SMS_DELIVERED";

                PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);

                PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED), 0);

                //---when the SMS has been sent---
                context.registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context arg0, Intent arg1) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                Toast.makeText(context, "Generic failure", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                Toast.makeText(context, "No service", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_NULL_PDU:
                                Toast.makeText(context, "Null PDU", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_RADIO_OFF:
                                Toast.makeText(context, "Radio off", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }, new IntentFilter(SENT));

                //---when the SMS has been delivered---
                context.registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context arg0, Intent arg1) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                                break;
                            case Activity.RESULT_CANCELED:
                                Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }, new IntentFilter(DELIVERED));

                /*
                 * These two lines below actually send the message via an intent.
                 * The default provider does not show up and this is backward compatible to 2.3.3
                 *
                 */
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(phoneNumber, null, cypherText, sentPI, deliveredPI);
            } else
                readPublicKey(lindAppDbHelper, phoneNumber,context);
        }
    }

    public static String getCleanAdress(String address) {
        if (address != null)
            if (address.length() == 9)
                return 237 + address;
            else if (address.length() >= 12)
                return address.substring(address.length() - 12);
        return "";
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri,
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }

    public static String decryptCypherText(Context context, String msg, String from) throws IOException {

        LindAppDbHelper lindAppDbHelper = LindAppDbHelper.getInstance(context);

        String phoneNumber = getCleanAdress(from);
        PublicKey publicKey = lindAppDbHelper.getPublicKey(phoneNumber);

        if (publicKey != null) {
            String myNumber = getMyContact(context);
            PrivateKey privateKey = lindAppDbHelper.getPrivateKey(myNumber);

            SecretKey secretKey = EEC.secretKey(privateKey, publicKey);

            assert secretKey != null;
            String decrypt = EEC.decrypt(secretKey, msg);
            return decrypt!=null?decrypt:"error decryption";
        } else
            readPublicKey(lindAppDbHelper, phoneNumber,context);
        return "Error fetching public key try again";
    }

    private static String getMyContact(Context context) {
        SharedPreferences pref = context.getSharedPreferences(Constant.PREFERENCE, 0); // 0 - for private mode

        if (pref.contains(Constant.MY_CONTACT)) {
            return pref.getString(Constant.MY_CONTACT, "");
        } else return null;

    }


    /**
     * To de-serialize a java object from database
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static PrivateKey deSerializePrivateKey(byte[] buf) throws IOException {

        ObjectInputStream objectIn = null;
        PrivateKey privateKey = null;
        if (buf != null)
            objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));

        try {
            privateKey = (PrivateKey) objectIn.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    /**
     * To de-serialize a java object from database
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static PublicKey deSerializePublicKey(byte[] buf) throws IOException {

        ObjectInputStream objectIn = null;
        PublicKey publicKey = null;
        if (buf != null)
            objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));

        try {
            publicKey = (PublicKey) objectIn.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return publicKey;
    }


    public static byte[] privateKeyToStream(PrivateKey stu) {
        // Reference for stream of bytes
        byte[] stream = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(stu);
            stream = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stream;

    }

    public static byte[] publicKeyToStream(PublicKey stu) {
        // Reference for stream of bytes
        byte[] stream = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(stu);
            stream = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream;
    }

}

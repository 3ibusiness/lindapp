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
import com.androidcorpo.lindapp.network.GetOnlinePublicKey;
import com.androidcorpo.lindapp.resources.LindAppDbHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

public class LindAppUtils {

    private static PublicKey getPublicKey(LindAppDbHelper lindAppDbHelper, String contact) throws IOException {
        PublicKey publicKey = lindAppDbHelper.getPublicKey(contact);
        if (publicKey == null) {
            new GetOnlinePublicKey(lindAppDbHelper,publicKey).execute(contact);
        }
        return publicKey;
    }

    public static void sendCypherMessage(final Context context, String plainText, String destiNumber) throws IOException {
        LindAppDbHelper lindAppDbHelper = LindAppDbHelper.getInstance(context);

        String phoneNumber = getCleanAdress(destiNumber);
        String myNumber = getMyContact(context);
        if (myNumber == null) {
            Toast.makeText(context, myNumber, Toast.LENGTH_LONG).show();
        } else {

            PrivateKey privateKey = lindAppDbHelper.getPrivateKey(myNumber);
            PublicKey publicKey = getPublicKey(lindAppDbHelper, phoneNumber);
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

        String myNumber = getMyContact(context);
        LindAppDbHelper lindAppDbHelper = LindAppDbHelper.getInstance(context);
        PrivateKey privateKey = lindAppDbHelper.getPrivateKey(myNumber);

        String phoneNumber = getCleanAdress(from);
        PublicKey publicKey = getPublicKey(lindAppDbHelper, phoneNumber);

        SecretKey secretKey = EEC.secretKey(privateKey, publicKey);

        assert secretKey != null;
        return EEC.decrypt(secretKey, msg);
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


    public static String convertBytesToHex(byte[] bytes) {

        StringBuilder result = new StringBuilder();

        for (byte temp : bytes) {

            int decimal = (int) temp & 0xff;  // bytes widen to int, need mask, prevent sign extension

            String hex = Integer.toHexString(decimal);

            result.append(hex);

        }
        return result.toString();


    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
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

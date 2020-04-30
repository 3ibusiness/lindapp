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

import com.androidcorpo.lindapp.elipticurve.BinaryConversions;
import com.androidcorpo.lindapp.elipticurve.CoreAlgorithm;

import java.util.Arrays;

public class LindAppUtils {

    public static void sendCypherMessage(final Context context, String plainText, String phoneNumber) {

        String myNumber = getMyContact(context);
        if (myNumber == null) {
            Toast.makeText(context, myNumber, Toast.LENGTH_LONG).show();
        } else {

            String cypherTextBin = CoreAlgorithm.crypt(plainText, publicKey(phoneNumber.replaceAll("\\s", ""), myNumber.replaceAll("\\s", "")));
            String cypherTextHex = BinaryConversions.binToHex(cypherTextBin);
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
            sms.sendTextMessage(phoneNumber, null, cypherTextHex, sentPI, deliveredPI);
        }
    }

    public static String getCleanAdress(String address) {
        if (address != null)
            if (address.length() == 9)
                return 237 + address;
            else if (address.length() > 12)
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

    private static String publicKey(String from, String to) {

        String subFrom = from.substring(from.length() - 6);
        String subTo = to.substring(to.length() - 6);

        String values = subFrom + "" + subTo;

        String[] array = values.split("");
        Arrays.sort(array);

        StringBuilder sb = new StringBuilder();
        for (String ch : array) {
            sb.append(ch);
        }

        String string = sb.toString();
        return string.substring(0, 3) + "" + string.substring(string.length() - 3, string.length());
    }

    public static String decryptCypherText(Context context, String msg, String phoneNumber) {
        String s = BinaryConversions.hexToBin(msg);
        String myNumber = getMyContact(context);

        assert myNumber != null;
        String key = publicKey(phoneNumber.replaceAll("\\s", ""), myNumber.replaceAll("\\s", ""));

        return CoreAlgorithm.decrypt(s, key);
    }

    private static String getMyContact(Context context) {
        SharedPreferences pref = context.getSharedPreferences(Constant.PREFERENCE, 0); // 0 - for private mode

        if (pref.contains(Constant.MY_CONTACT)) {
            return pref.getString(Constant.MY_CONTACT, "");
        } else return null;

    }
}

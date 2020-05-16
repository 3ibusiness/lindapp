package com.androidcorpo.lindapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.androidcorpo.lindapp.activities.MessagesActivity;


/**
 * Created by severin MBEKOU on 17-04-2020.
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    public SmsBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] message = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            String smsBody = "";
            String address = "";
            for (Object o : message) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) o);
                smsBody = smsMessage.getMessageBody();
                address = smsMessage.getOriginatingAddress();

                smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr += smsBody + "\n";

            }
            //Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();
            MessagesActivity messagesActivity = MessagesActivity.getInst();
            messagesActivity.finish();
            messagesActivity.refreshActivity(address, smsBody);
        }
    }


}

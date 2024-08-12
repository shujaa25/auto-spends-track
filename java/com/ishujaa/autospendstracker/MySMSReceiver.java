package com.ishujaa.autospendstracker;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.util.Locale;

public class MySMSReceiver extends BroadcastReceiver {
    public static final String pdu_type = "pdus";
    private MyNotificationHelper notification;
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent in) {
        notification = new MyNotificationHelper(context);

        Bundle bundle = in.getExtras();
        SmsMessage[] msgs;
        String format = bundle.getString("format");

        Object[] pdus = (Object[]) bundle.get(pdu_type);
        if (pdus != null) {
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                //strMessage += "SMS from " + msgs[i].getOriginatingAddress();
                //strMessage += " :" + msgs[i].getMessageBody() + "\n";

                //what if there is no space before or after?
                if (!msgs[i].getMessageBody().toLowerCase(Locale.ROOT).contains(" debited ") &&
                        !msgs[i].getMessageBody().toLowerCase(Locale.ROOT).contains(" spent ") &&
                        !msgs[i].getMessageBody().toLowerCase(Locale.ROOT).contains(" sent ") &&
                        !msgs[i].getMessageBody().toLowerCase(Locale.ROOT).contains(" paid ") &&
                        !msgs[i].getMessageBody().toLowerCase(Locale.ROOT).contains(" txn ") &&
                        !msgs[i].getMessageBody().toLowerCase(Locale.ROOT).contains(" transfer") &&
                        !msgs[i].getMessageBody().toLowerCase(Locale.ROOT).contains("used"))
                    continue;

                notification.postNotification(msgs[i].getOriginatingAddress(),
                        msgs[i].getMessageBody());
            }
        }
    }
}
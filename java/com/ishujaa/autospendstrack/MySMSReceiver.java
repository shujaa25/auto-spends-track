package com.ishujaa.autospendstrack;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class MySMSReceiver extends BroadcastReceiver {

    private static final String TAG = MySMSReceiver.class.getSimpleName();
    public static final String pdu_type = "pdus";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    private SQLiteOpenHelper openHelper;
    private MyNotification notification;
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent in) {
        notification = new MyNotification(context);

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

                //what if there is no space before or after
                if (!msgs[i].getMessageBody().toLowerCase(Locale.ROOT).contains(" debited ") &&
                        !msgs[i].getMessageBody().toLowerCase(Locale.ROOT).contains(" spent ") &&
                        !msgs[i].getMessageBody().toLowerCase(Locale.ROOT).contains(" sent ") &&
                        !msgs[i].getMessageBody().toLowerCase(Locale.ROOT).contains(" paid ") &&
                        !msgs[i].getMessageBody().toLowerCase(Locale.ROOT).contains(" txn "))
                    continue;

                notification.postNotification(msgs[i].getOriginatingAddress(),
                        msgs[i].getMessageBody());

                String time = "null";
                LocalDateTime now = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    now = LocalDateTime.now();
                    time = dtf.format(now);
                }


                //openHelper = new DBHelper(context);
                //SQLiteDatabase database = openHelper.getWritableDatabase();
                //ContentValues targetValues = new ContentValues();
                //targetValues.put("sender", msgs[i].getOriginatingAddress());
                //targetValues.put("message", msgs[i].getMessageBody());
                //targetValues.put("date", time);
                //database.insert("saved", null, targetValues);

                //database.close();
                //openHelper.close();

            }
        }
    }
}
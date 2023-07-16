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
    String CHANNEL_ID = "com.ishujaa.autospendstrack";

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ast_ishujaacom";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public int notification_id = new Random().nextInt();
    private SQLiteOpenHelper openHelper;

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent in) {
        createNotificationChannel(context);

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

                Intent txnIntent = new Intent(context, AddTxn.class);
                txnIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                txnIntent.putExtra(AddTxn.SENDER_EXTRA, msgs[i].getOriginatingAddress());
                txnIntent.putExtra(AddTxn.MSG_EXTRA, msgs[i].getMessageBody());
                PendingIntent pendingIntent = PendingIntent.getActivity(context, notification_id,
                        txnIntent, PendingIntent.FLAG_IMMUTABLE);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setContentTitle("Transaction detected from " + msgs[i].getOriginatingAddress());
                builder.setContentText(msgs[i].getMessageBody());
                builder.addAction(R.drawable.ic_launcher_foreground, "Add", pendingIntent);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                builder.setAutoCancel(false); //reset
                builder.setContentIntent(pendingIntent);


                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(context, "Notification permission needed. - AutoSpendsTracer",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                notificationManager.notify(notification_id++, builder.build());

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
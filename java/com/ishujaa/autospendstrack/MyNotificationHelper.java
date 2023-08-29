package com.ishujaa.autospendstrack;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class MyNotificationHelper {
    private final String CHANNEL_ID = "com.ishujaa.autospendstrack";
    private final Context context;
    private int notification_id = new Random().nextInt();

    MyNotificationHelper(Context context) {
        this.context = context;
        createNotificationChannel();

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Transaction Updates";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void postNotification(String title, String text){
        Intent txnIntent = new Intent(context, AddTxnActivity.class);
        txnIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        txnIntent.putExtra(AddTxnActivity.SENDER_EXTRA, title);
        txnIntent.putExtra(AddTxnActivity.MSG_EXTRA, text);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notification_id,
                txnIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Transaction detected from " + title);
        builder.setContentText(text);
        //builder.addAction(R.drawable.ic_launcher_foreground, "Add", pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        notificationManager.notify(notification_id++, builder.build());
    }

}

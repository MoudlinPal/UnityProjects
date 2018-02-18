package com.example.user.organizer.notifications;


import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.text.TimeZoneFormat;
import android.preference.PreferenceManager;
import android.renderscript.RenderScript;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.format.DateUtils;

import com.example.user.organizer.MainMenu;
import com.example.user.organizer.R;
import com.example.user.organizer.databases.DataBaseTasks;

import java.util.Calendar;
import java.util.Date;

public class MyIntentService extends IntentService{




    public MyIntentService() {
        super("MyIntentService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {

        int NOTIFICATION_ID;


        //String title = intent.getStringExtra("NotificationTitle");
        String message = intent.getStringExtra("NotificationMessage");
        int id = Integer.decode(intent.getStringExtra("id_Notification"));



        Intent notificationIntent = new Intent(this, MainMenu.class);
        //notificationIntent.putExtra("notificationMessage", message);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        PendingIntent contentIntent = PendingIntent.getActivity(this,
                id+850, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);





        long[] vibrate = new long[] {500, 1000, 500, 1000};


        // THAT IS BETTER (WE GET CURRENT TIME)
        String title = getString(R.string.task_at) + " " + DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setLargeIcon(BitmapFactory.decodeResource( this.getResources(), R.mipmap.ic_launcher))
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)

                .setVibrate(vibrate)
                .setLights(Color.BLUE, 3000, 3000)

                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                ;


        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(this);
        if(myPreference.getBoolean("vibration", true))
            notificationBuilder.setVibrate(vibrate);
        else
            notificationBuilder.setVibrate(new long[]{0,0,0,0});




        /*Intent notifyIntent = new Intent(this, MainMenu.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //to be able to launch your activity from the notification
        builder.setContentIntent(pendingIntent);*/


        /**               Create random ID  */
        NOTIFICATION_ID = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);


        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }


}

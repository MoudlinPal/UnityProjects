package com.example.user.organizer.notifications;


import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.renderscript.RenderScript;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.user.organizer.R;

public class MyIntentService extends IntentService{

    private static final int NOTIFICATION_ID = 3;



    public MyIntentService() {
        super("MyIntentService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
        .setContentTitle("My Title")
        .setContentText("This is the Body")
        .setSmallIcon(R.drawable.ic_menu_send)
                ;


        /*Intent notifyIntent = new Intent(this, MainMenu.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //to be able to launch your activity from the notification
        builder.setContentIntent(pendingIntent);*/



        Notification notificationCompat = builder.build();

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);

        managerCompat.notify(NOTIFICATION_ID, notificationCompat);
    }


}

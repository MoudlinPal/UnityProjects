package com.example.user.organizer.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.user.organizer.R;


public class NotificationHelper extends ContextWrapper {

    public static final String channelID = "channelID";
    public static final String channelName= "channelname";


    private NotificationManager manager;

    private static String contentTitle;
    private static String contentText;


    public NotificationHelper(Context base) {
        super(base);

        contentTitle = "Это крч заголовок емае!";
        contentText = "А ето описание хтыщ!";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();
    }



    @TargetApi(Build.VERSION_CODES.O)
    public void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(R.color.PrimaryColor);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel);
    }


    public NotificationManager getManager(){
        if(manager == null){
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return manager;
    }


    public NotificationCompat.Builder getChannelNotification(){

        return new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setChannel(channelID)
                .setSmallIcon(R.mipmap.ic_launcher);

    }












}

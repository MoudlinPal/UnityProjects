package com.example.user.organizer.notifications;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver{

    public MyReceiver() {}





    @Override
    public void onReceive(Context context, Intent intent) {


        String title = intent.getStringExtra("NotificationTitle");
        String message = intent.getStringExtra("NotificationMessage");


        Intent myIntent = new Intent(context, MyIntentService.class);

        myIntent.putExtra("NotificationTitle", title);
        myIntent.putExtra("NotificationMessage", message);

        context.startService(myIntent);

    }



}

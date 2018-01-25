package com.example.user.organizer.notifications;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver{

    public MyReceiver() {}





    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intent1 = new Intent(context, MyIntentService.class);
        context.startService(intent1);

    }



}

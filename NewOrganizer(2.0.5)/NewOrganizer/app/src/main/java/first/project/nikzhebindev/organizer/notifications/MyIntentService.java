package first.project.nikzhebindev.organizer.notifications;


import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;

import first.project.nikzhebindev.organizer.MainMenu;
import first.project.nikzhebindev.organizer.R;
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



        //////////////////////// check message ////////////////////////
/* // to update date, time & timeInMillis of task with repeating

        DataBaseTasks dataBaseTasks = new DataBaseTasks(this);
        SQLiteDatabase sqLiteDatabase = dataBaseTasks.getWritableDatabase();
        String[] taskData = DataBaseTasks.getCertainTask(sqLiteDatabase, message);


        // check repeating value
        if(taskData[3].compareTo(DataBaseLists.listRepeatNames[1]) == 0) { // once an hour

            taskData[1] = DateUtils.formatDateTime(this, System.currentTimeMillis()+3600000,
                    DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);

            taskData[2] = DateUtils.formatDateTime
                    (this, System.currentTimeMillis()+3600000, DateUtils.FORMAT_SHOW_TIME);

            long timeInMillis = (Long.valueOf(taskData[6]) + 3600000);
            taskData[6] = String.valueOf(timeInMillis);

            DataBaseTasks.updateDateTimeOfTaskById(sqLiteDatabase, taskData, taskData[5], this);

        }
        else if(taskData[3].compareTo(DataBaseLists.listRepeatNames[2]) == 0) { // once a day

            taskData[1] = DateUtils.formatDateTime(this, System.currentTimeMillis()+3600000*24,
                    DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);

            taskData[2] = DateUtils.formatDateTime
                    (this, System.currentTimeMillis()+3600000*24, DateUtils.FORMAT_SHOW_TIME);

            long timeInMillis = (Long.valueOf(taskData[6]) + 3600000*24);
            taskData[6] = String.valueOf(timeInMillis);

            DataBaseTasks.updateDateTimeOfTaskById(sqLiteDatabase, taskData, taskData[5], this);

        }
        else if(taskData[3].compareTo(DataBaseLists.listRepeatNames[3]) == 0) { // once a week

            taskData[1] = DateUtils.formatDateTime(this, System.currentTimeMillis()+3600000*24*7,
                    DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);

            taskData[2] = DateUtils.formatDateTime
                    (this, System.currentTimeMillis()+3600000*24*7, DateUtils.FORMAT_SHOW_TIME);

            long timeInMillis = (Long.valueOf(taskData[6]) + 3600000*24*7);
            taskData[6] = String.valueOf(timeInMillis);

            DataBaseTasks.updateDateTimeOfTaskById(sqLiteDatabase, taskData, taskData[5], this);

        }
        else{

        }

*/
        // set next DATE AND TIME because of repeating

        //////////////////////// check message ////////////////////////



        Intent notificationIntent = new Intent(this, MainMenu.class);
        //notificationIntent.putExtra("notificationMessage", message);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        PendingIntent contentIntent = PendingIntent.getActivity(this,
                id+850, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);



        //////////////////////////// Sound ////////////////////////////
        SharedPreferences getAlarms = PreferenceManager.
                getDefaultSharedPreferences(this);
        String alarms = getAlarms.getString("my_ringtone_preference", "true");
        Uri uriSound = Uri.parse(alarms);
        //////////////////////////// Sound ////////////////////////////


        long[] vibrate = new long[] {500, 1000, 500, 1000};


        // THAT IS BETTER (WE GET CURRENT TIME)
        String title = getString(R.string.task_at) + " " + DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME);


        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_black)
                .setLargeIcon(BitmapFactory.decodeResource( this.getResources(), R.mipmap.ic_launcher))
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)

                .setSound(uriSound)

                .setVibrate(vibrate)
                .setLights(Color.BLUE, 3000, 3000)

                .setPriority(Notification.PRIORITY_MAX)
                ;

        if(alarms.compareTo("true") == 0 || alarms.compareTo("false") == 0 ||
                alarms.compareTo("") == 0){
            notificationBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND); // Set Default
        }


        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(this);
        if(myPreference.getBoolean("vibration", true))
            notificationBuilder.setVibrate(vibrate);
        else
            notificationBuilder.setVibrate(new long[]{0,0,0,0});




        Notification notification  = notificationBuilder.build();


        /////////////////////   Alarm Mode   /////////////////////

        if(myPreference.getBoolean("alarm_mode", true)) {
            notification.flags = notification.flags | Notification.FLAG_INSISTENT;
        }
        else{
            notification.flags = notification.flags | Notification.FLAG_ONLY_ALERT_ONCE;
        }

        /////////////////////   Alarm Mode   /////////////////////




        /*Intent notifyIntent = new Intent(this, MainMenu.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //to be able to launch your activity from the notification
        builder.setContentIntent(pendingIntent);*/


        /**               Create random ID  */
        NOTIFICATION_ID = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);


        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


        try { notificationManager.notify(NOTIFICATION_ID, notification); } catch(Exception e){}
    }


}

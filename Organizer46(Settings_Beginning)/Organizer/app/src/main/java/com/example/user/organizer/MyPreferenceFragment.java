package com.example.user.organizer;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RemoteViews;

import com.example.user.organizer.databases.DataBaseTasks;

import static android.content.Context.NOTIFICATION_SERVICE;


public class MyPreferenceFragment extends PreferenceFragment {

    public Toolbar toolbar;


    CheckBoxPreference permanentNotification;
    CheckBoxPreference confirmFTasks;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);


        permanentNotification = (CheckBoxPreference)findPreference("permanentNotification");
        confirmFTasks = (CheckBoxPreference)findPreference("confirmFTasks");


        permanentNotification.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        public boolean onPreferenceClick(Preference preference) {

                if(permanentNotification.isChecked()) {
                    permanentNotification.setSummary("Enabled");
                    sendDefaultNotification();
                }
                else {
                    permanentNotification.setSummary("Disabled");
                    cancelDefaultNotification();
                }

                return false;}});


        confirmFTasks.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                if(confirmFTasks.isChecked()) {
                    confirmFTasks.setSummary("Enabled");
                }
                else {
                    confirmFTasks.setSummary("Disabled");
                }

                return false;}});






    }













    public void sendDefaultNotification() {
        //////////////////////// NOTIFICATION ////////////////////////
        NotificationCompat.Builder builder;
        NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(NOTIFICATION_SERVICE);


        RemoteViews remoteViews = new RemoteViews(getActivity().getPackageName(), R.layout.permanent_notification);
        remoteViews.setImageViewResource(R.id.imageViewAddTask, R.drawable.ic_add_task_notification);
        remoteViews.setImageViewResource(R.id.imageViewToSettings, R.drawable.ic_settings_notification);
        remoteViews.setImageViewResource(R.id.imageViewAppIcon, R.mipmap.ic_launcher);




        DataBaseTasks dbTasks = new DataBaseTasks(getContext());
        SQLiteDatabase databaseTasks = dbTasks.getWritableDatabase();
        int amount = DataBaseTasks.getAmountTasksToday(databaseTasks);

        if(amount < 1){
            remoteViews.setTextViewText(R.id.textViewTaskAmount, getString(R.string.no_tasks_today));
            remoteViews.setTextViewText(R.id.textViewGoodDay, getString(R.string.have_a_nice_day));
        }
        else {
            remoteViews.setTextViewText(R.id.textViewTaskAmount, getString(R.string.amount_of_tasks) + " " + amount);
            remoteViews.setTextViewText(R.id.textViewGoodDay, getString(R.string.have_a_nice_day));
        }



        //////////// FOR imageViewAddTask ////////////
        Intent addTaskIntent = new Intent(getContext(), NewTask.class);
        addTaskIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentAddTaskIntent = PendingIntent.getActivity(getContext(),
                111, addTaskIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.imageViewAddTask, contentAddTaskIntent);
        //////////// FOR imageViewAddTask ////////////



        //////////// FOR imageViewToSettings ////////////
        Intent toSettingsIntent = new Intent(getContext(), NewTask.class);
        toSettingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentToSettingsIntentIntent = PendingIntent.getActivity(getContext(),
                112, addTaskIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.imageViewToSettings, contentToSettingsIntentIntent);
        //////////// FOR imageViewToSettings ////////////



        Intent notifIntent = new Intent(getContext(), MainMenu.class);
        notifIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(),
                68571, notifIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        builder = new NotificationCompat.Builder(getContext());
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .setCustomContentView(remoteViews)
        ;

        notificationManager.notify(MainMenu.def_notification_id, builder.build());


        //////////////////////// NOTIFICATION ////////////////////////
    }

    public void cancelDefaultNotification(){

        NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(MainMenu.def_notification_id);

    }





































}

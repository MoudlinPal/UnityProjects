package com.example.user.organizer.Fragments;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.user.organizer.MainMenu;
import com.example.user.organizer.NewTask;
import com.example.user.organizer.R;
import com.example.user.organizer.databases.DataBaseLists;
import com.example.user.organizer.databases.DataBaseTasks;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import static android.content.Context.NOTIFICATION_SERVICE;


public class MyPreferenceFragment extends PreferenceFragment {

    public Toolbar toolbar;

    Preference versionOrganizer;

    CheckBoxPreference permanentNotification;
    CheckBoxPreference confirmFTasks;
    CheckBoxPreference Images;
    CheckBoxPreference vibration;

    ListPreference taskNotification;
    //ListPreference languageListPreference;
    ListPreference listShowStartup;



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        versionOrganizer = findPreference("versionOrganizer");
        permanentNotification = (CheckBoxPreference)findPreference("permanentNotification");
        confirmFTasks = (CheckBoxPreference)findPreference("confirmFTasks");
        Images = (CheckBoxPreference)findPreference("Images");
        vibration = (CheckBoxPreference)findPreference("vibration");


        taskNotification = (ListPreference)findPreference("taskNotification");
        //languageListPreference = (ListPreference)findPreference("languageListPreference");
        listShowStartup = (ListPreference)findPreference("listShowStartup");



        permanentNotification.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        public boolean onPreferenceClick(Preference preference) {
                if(permanentNotification.isChecked()) {
                    permanentNotification.setSummary(getActivity().getString(R.string.enabled));
                    sendDefaultNotification();}
                else {
                    permanentNotification.setSummary(getActivity().getString(R.string.disabled));
                    cancelDefaultNotification();}
                return false;
        }
        });
        if(permanentNotification.isChecked()) permanentNotification.setSummary(getActivity().getString(R.string.enabled));
        else permanentNotification.setSummary(getActivity().getString(R.string.disabled));



        confirmFTasks.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                if(confirmFTasks.isChecked()) {
                    confirmFTasks.setSummary(getActivity().getString(R.string.enabled));}
                else {
                    confirmFTasks.setSummary(getActivity().getString(R.string.disabled));}
                return false;
            }
        });
        if(confirmFTasks.isChecked()) confirmFTasks.setSummary(getActivity().getString(R.string.enabled));
        else confirmFTasks.setSummary(getActivity().getString(R.string.disabled));


        vibration.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(vibration.isChecked()) {
                    vibration.setSummary(getActivity().getString(R.string.enabled));}
                else {
                    vibration.setSummary(getActivity().getString(R.string.disabled));}
                return false;
            }
        });
        if(vibration.isChecked()) vibration.setSummary(getActivity().getString(R.string.enabled));
        else vibration.setSummary(getActivity().getString(R.string.disabled));


        Images.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                getActivity().recreate();
                return false;
            }
        });


        versionOrganizer.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                int randomNum = ThreadLocalRandom.current().nextInt(1, 4 + 1);
                String smile = "";
                switch (randomNum){
                    case 2: smile = ">_>"; break;
                    case 3: smile = "^_^"; break;
                    case 4: smile = "<_<"; break;
                    default: smile = "*^_^*";
                }
                Toast.makeText(getContext(), smile, Toast.LENGTH_SHORT).show();
                return false;}});


        taskNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                taskNotification.setValue(newValue.toString());
                taskNotification.setSummary(newValue.toString());
                return false;
            }
        });
        taskNotification.setSummary(taskNotification.getValue());


        /*languageListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                languageListPreference.setValue(newValue.toString());
                languageListPreference.setSummary(newValue.toString());

                switch (newValue.toString()){
                    case "German":      setLocale("de"); break;
                    case "English":     setLocale("en"); break;
                    case "Spanish":     setLocale("es"); break;
                    case "French":      setLocale("fr"); break;
                    case "Hindi":       setLocale("hi"); break;
                    case "Portuguese":  setLocale("pt"); break;
                    case "Russian":     setLocale("ru"); break;
                    case "Chinese":     setLocale("zh"); break;
                }

                return false;
            }
        });
        languageListPreference.setSummary(languageListPreference.getValue());*/




        setListPreferenceData(listShowStartup);
        listShowStartup.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                setListPreferenceData(listShowStartup);
                listShowStartup.setValue(newValue.toString());
                listShowStartup.setSummary(newValue.toString());
                return false;
            }
        });
        DataBaseLists dataBaseLists = new DataBaseLists(getContext());
        SQLiteDatabase sqLiteDatabase = dataBaseLists.getWritableDatabase();
        if(DataBaseLists.checkListPresence(sqLiteDatabase, listShowStartup.getValue()))
            listShowStartup.setSummary(listShowStartup.getValue());
        else {
            listShowStartup.setValue(MainMenu.KEY_ALL_TASKS);
            listShowStartup.setSummary(MainMenu.KEY_ALL_TASKS);
        }


    }













    public void sendDefaultNotification() {
        //////////////////////// NOTIFICATION ////////////////////////

        NotificationCompat.Builder builder;
        NotificationManager notificationManager = (NotificationManager)getContext().getSystemService(NOTIFICATION_SERVICE);


        RemoteViews remoteViews = new RemoteViews(getContext().getPackageName(), R.layout.permanent_notification);
        remoteViews.setImageViewResource(R.id.imageViewAddTask, R.drawable.ic_add_task_notification);
        remoteViews.setImageViewResource(R.id.imageViewToSettings, R.drawable.ic_settings_notification);
        remoteViews.setImageViewResource(R.id.imageViewAppIcon, R.mipmap.ic_launcher);

        //Icon icon = Icon.createWithResource(this, R.mipmap.ic_launcher);
        //remoteViews.setImageViewIcon(R.id.imageViewAppIcon, icon);



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
        Intent toSettingsIntent = new Intent(getContext(), MainMenu.class);
        toSettingsIntent.putExtra("requestCode", 112);
        toSettingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentToSettingsIntentIntent = PendingIntent.getActivity(getContext(),
                112, toSettingsIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.imageViewToSettings, contentToSettingsIntentIntent);
        //////////// FOR imageViewToSettings ////////////



        //Intent btnAddTask_intent = new Intent("btnAddTask_clicked");
        //btnAddTask_intent.putExtra("btnAddTask_id", notification_id);


        Intent notifIntent = new Intent(getContext(), MainMenu.class);
        notifIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(),
                68571, notifIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);


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










    private void setLocale(String lang){

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getContext().getResources().updateConfiguration(config, getContext().getResources().getDisplayMetrics());

        getActivity().recreate();

    }






    public void setListPreferenceData(ListPreference lp) {

        DataBaseLists dataBaseLists = new DataBaseLists(getContext());
        SQLiteDatabase sqLiteDatabase = dataBaseLists.getWritableDatabase();
        String[] listsFromDB = DataBaseLists.readDBLists(sqLiteDatabase);




        String[] lists = new String[listsFromDB.length+1];

        lists[0] = MainMenu.KEY_ALL_TASKS;
        for(int i = 1; i < lists.length; i++) {
            lists[i] = listsFromDB[i-1];
        }


        CharSequence[] entries = new CharSequence[lists.length];
        CharSequence[] entryValues = new CharSequence[lists.length];

        for(int i = 0; i < lists.length; i++){
            entries[i] = lists[i];
            entryValues[i] = lists[i];
        }



        lp.setEntries(entries);
        lp.setEntryValues(entryValues);
    }









}

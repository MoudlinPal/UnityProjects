package com.example.user.organizer;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;
import android.widget.Spinner;

import com.example.user.organizer.databases.DataBaseLists;
import com.example.user.organizer.notifications.MyReceiver;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, View.OnTouchListener {

    // Идентификатор уведомления
    private static final int PERMANENT_NOTIFY_ID = 103;


    public static String KEY_ALL_TASKS = "Все задачи";


    public static boolean openedMain, openedLists;


    DataBaseLists dbLists;

    //public static String ALL_TASKS = "Все задачи";


    Spinner spinner;
    ArrayAdapter<String> adapterSpinner;
    public static String selectedSpinnerList = DataBaseLists.defaultListsNames[0];



    android.widget.SearchView searchView;



    FloatingActionButton floatBtnToNewTask;



    Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        OpenedMain();


        floatBtnToNewTask = (FloatingActionButton)findViewById(R.id.floatBtnToNewTask);
        floatBtnToNewTask.setSize(75);
        floatBtnToNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(MainMenu.this, NewTask.class);
                    startActivity(intent);
            }
        });



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        toolbar.hideOverflowMenu();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);






        ////////////////////////////////////////////////////////////////////////////////////////////
        dbLists = new DataBaseLists(this);

        /////////////////////// DataBase //////////////////////
        final SQLiteDatabase db = dbLists.getWritableDatabase(); // доступен для чтения и записи
        /////////////////////// DataBase ///////////////////////

        // filling with defaults list names
        if(!DataBaseLists.checkListPresence(db, DataBaseLists.defaultListsNames[0])) {
            for(int i = 0; i < DataBaseLists.defaultListsNames.length; i++) {
                DataBaseLists.addToDBLists(db, DataBaseLists.defaultListsNames[i]);
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////////



        //////////////////////// Toolbar elements MainMenu ////////////////////////////
        spinner = (Spinner)findViewById(R.id.spinnerInsideToolbar);
        searchView = (android.widget.SearchView)findViewById(R.id.searchMainMenu);
        searchView.setOnClickListener(this);

        defineSpinnerLists();



        //MainFragment fragment = new MainFragment();
        //loadFragment(fragment);
        //openedMain = true;


        makeVisibleSpinnerAndSearchView();


        //sendDefaultNotification();
        //sendNotification();



    }




    private void defineSpinnerLists() {
        class defineSpinnerLists implements Callable<String[]> {

            SQLiteDatabase databaseLists;
            public defineSpinnerLists(SQLiteDatabase db)
            {
                this.databaseLists = db;
            }

            @Override
            public String[] call() throws Exception {

                String[] listBufDemo = DataBaseLists.readDBLists(databaseLists);

                String[] listBuf = new String[listBufDemo.length+1]; // for "All lists" and "Finished Tasks" and "Add new list"
                listBuf[0] = KEY_ALL_TASKS;
                for(int i = 0; i < listBufDemo.length; i++)
                {
                    listBuf[i+1] = listBufDemo[i];
                }

                return listBuf;
            }
        }

        /////////////////////// DataBase //////////////////////
        final SQLiteDatabase databaseLists = dbLists.getWritableDatabase(); // доступен для чтения и записи
        /////////////////////// DataBase ///////////////////////

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<String[]> futureString = exec.submit(new defineSpinnerLists(databaseLists));

        String[] listBuf = {};
        try{
            listBuf = futureString.get();
        }
        catch (Exception e) {

        }
        finally {
            exec.shutdown();
        }








        // Настраиваем адаптер
        adapterSpinner = new
                ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listBuf);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinner.setAdapter(adapterSpinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0) selectedSpinnerList = KEY_ALL_TASKS;
                else selectedSpinnerList = DataBaseLists.readDBLists(databaseLists)[position-1]; // -1 due to "Все задачи"

                loadTaskListFragment();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //////////////////////////////////////////////////////////////////////////////////////////////
    }



    public void loadTaskListFragment() {
        selectedSpinnerList = spinner.getSelectedItem().toString();
        TaskListFragment fragment = new TaskListFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayoutForListFragment, fragment).commit();
    }





    private void sendNotification() {
        // and then in your activity set the alarm manger to start the broadcast receiver
        // at a specific time and use AlarmManager setRepeating method to repeat it this
        // example bellow will repeat it every day.


        Intent notifyIntent = new Intent(this,MyReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (this,  2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis(),
                1000 * 60 , pendingIntent); // * 60 * 24


    }



    private void sendDefaultNotification() {
        //////////////////////// NOTIFICATION ////////////////////////
        NotificationCompat.Builder builder;
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        int notification_id = 3131;


        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.permanent_notification);
        remoteViews.setImageViewResource(R.id.imageViewAddTask, R.mipmap.ic_add_task);
        remoteViews.setImageViewResource(R.id.imageViewToSettings, R.mipmap.ic_add_task);
        remoteViews.setImageViewResource(R.id.imageViewAppIcon, R.mipmap.iconapp_launcher);

        //////////// FOR imageViewAddTask ////////////
        Intent addTaskIntent = new Intent(this, NewTask.class);
        addTaskIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentAddTaskIntent = PendingIntent.getActivity(this,
                111, addTaskIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.imageViewAddTask, contentAddTaskIntent);
        //////////// FOR imageViewAddTask ////////////

        //////////// FOR imageViewToSettings ////////////
        Intent toSettingsIntent = new Intent(this, NewTask.class);
        toSettingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentToSettingsIntentIntent = PendingIntent.getActivity(this,
                112, addTaskIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.imageViewToSettings, contentToSettingsIntentIntent);
        //////////// FOR imageViewToSettings ////////////



        //Intent btnAddTask_intent = new Intent("btnAddTask_clicked");
        //btnAddTask_intent.putExtra("btnAddTask_id", notification_id);




        Intent notificationIntent = new Intent(this, MainMenu.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);







        builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.iconapp_launcher)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .setCustomContentView(remoteViews)
        ;




        notificationManager.notify(notification_id, builder.build());

        //////////////////////// NOTIFICATION ////////////////////////
    }
/*

        Intent notificationIntent = new Intent();
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = this.getResources();


        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.permanent_notification);


        // до версии Android 8.0 API 26
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentIntent(contentIntent)
                .setOngoing(true)
                .setCustomBigContentView(remoteViews)

                // обязательные настройки
                .setSmallIcon(R.drawable.ic_sync_black_24dp);
                //.setContentTitle("Напоминание")
                //.setContentText("Пора покормить кота") // Текст уведомления

                // необязательные настройки
                //.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_menu_camera));

                //.setAutoCancel(true); // автоматически закрыть уведомление после нажатия

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Альтернативный вариант
        //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(PERMANENT_NOTIFY_ID, builder.build());

*/










    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onStart() {

        if(NewTask.wasSomeChangeWithSomeList)
        {
            defineSpinnerLists();
            NewTask.wasSomeChangeWithSomeList = false;
        }


        sendDefaultNotification();

        //Toast toast = Toast.makeText(MainMenu.this, "onStart", Toast.LENGTH_SHORT);
        //toast.show();
        super.onStart();
    }














    @Override
    protected void onPause() {
        //Toast toast = Toast.makeText(MainMenu.this, "onPause", Toast.LENGTH_SHORT);
        //toast.show();
        super.onPause();
    }

















    @Override
    public void onBackPressed() {

        if(TaskListFragment.openedTasksByListClick)
        {
            TaskListFragment.openedTasksByListClick = false;

            OpenedLists();
            loadTaskListFragment();

            hideSpinnerAndSearchView();
        }
        else
        {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                // <!-- DIALOG EXIT -->
                new AlertDialog.Builder(this)
                        .setTitle("ORGANIZER")
                        .setMessage("Вы действительно хотите выйти?")
                        .setNegativeButton(R.string.no, null)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                MainMenu.super.onBackPressed();
                            }
                        }).create().show();
                // <!-- DIALOG EXIT -->

            }
        }
    }












    /*
                   !! LifeCycle !!
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "MainActivity: onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "MainActivity: onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "MainActivity: onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MainActivity: onDestroy()");
    }
        */






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*//noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }






    private void loadFragment(final Fragment fragment) {

        /*if(fragment.getClass().getName().compareTo("MainFragment") == 0) // That WASssssss
        {
            openedMain = true;
            openedCalendar = openedLists = false;
        }
        else if (fragment.getClass().getName().compareTo("CalendarFragment") == 0)
        {
            openedCalendar = true;
            openedMain = openedLists = false;
        }
        else if(fragment.getClass().getName().compareTo("ListsFragment") == 0)
        {
            openedLists = true;
            openedMain = openedCalendar = false;
        }*/



        // create a transaction for transition here
        final FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        // put the fragment in place
        transaction.replace(R.id.frame_Main_Menu, fragment);

        // this is the part that will cause a fragment to be added to backstack,
        // this way we can return to it at any time using this tag

        if(fragment.getClass().getName().compareTo("ListsFragment") == 0)
        {
            //transaction.addToBackStack(fragment.getClass().getName());
        }


        transaction.commit();
    }
    private void backToFragment(final Fragment fragment) {

        // go back to something that was added to the backstack
        getSupportFragmentManager().popBackStackImmediate(
                fragment.getClass().getName(), 0);
        // use 0 or the below constant as flag parameter
        // FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }




    private void hideSpinnerAndSearchView() {
        spinner.setVisibility(View.INVISIBLE);
        searchView.setVisibility(View.INVISIBLE);
    }
    private void makeVisibleSpinnerAndSearchView() {
        spinner.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.VISIBLE);
    }







    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();



        /////////////////////// DataBase ///////////////////////
        // Вызываем метод вспомогательного класса, чтобы открыть и вернуть экхемпляр базы данных
        // с которой будем работать
        final SQLiteDatabase databaseLists = dbLists.getWritableDatabase(); // доступен для чтения и записи
        /////////////////////// DataBase ///////////////////////

        loadTaskListFragment();

        floatBtnToNewTask.setVisibility(View.GONE);

        hideSpinnerAndSearchView();

        if (id == R.id.nav_main) {
            if(NewTask.wasSomeChangeWithSomeList) {

                defineSpinnerLists();

                NewTask.wasSomeChangeWithSomeList = false;
            }
            OpenedMain();
            defineSpinnerLists();
            //MainFragment fragment = new MainFragment();
            //loadFragment(fragment);
            floatBtnToNewTask.setVisibility(View.VISIBLE);
            makeVisibleSpinnerAndSearchView();
        }
        else if (id == R.id.nav_calendardeeds) {


        }
        else if (id == R.id.nav_lists) {
            OpenedLists();  // Signal for TaskListFragment
        }
        else if (id == R.id.nav_travel) {
            //Intent intent = new Intent(this, TravelActivity.class);
            //startActivity(intent);
        }
        else if (id == R.id.nav_settings) {


        }
        else if (id == R.id.nav_about) {


        }
        else if (id == R.id.nav_themes) {


        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }







    @Override
    public void onClick(View v) {


    }


    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {


        return false;
    }




   public static void OpenedMain() {
       openedMain = true;
       openedLists = false;
   }
    public static void OpenedLists() {
        openedMain = false;
        openedLists = true;
    }



    public void makeVibration()
    {
        // Vibrate
        long mills = 100L;
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(mills);
    }


}

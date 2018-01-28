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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.user.organizer.databases.DataBaseLists;
import com.example.user.organizer.databases.DataBaseTasks;
import com.example.user.organizer.notifications.MyReceiver;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static boolean FloatBtnPressed = false;

    // Идентификатор уведомления
    private static final int PERMANENT_NOTIFY_ID = 103;


    public static String KEY_ALL_TASKS = "Все задачи";


    public static boolean openedMain, openedLists;


    DataBaseLists dbLists;



    public static int lastSpinnerSelection = 0;

    Spinner spinner;
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
                FloatBtnPressed = true;
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


        setSpinnerLists();

        makeVisibleSpinnerAndSearchView();
    }







    private void setSpinnerLists() {

        class defineSpinnerLists implements Callable<String[]> {

            private SQLiteDatabase databaseLists;
            private defineSpinnerLists(SQLiteDatabase db)
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





        // set sizes for EACH list //
        DataBaseTasks dataBaseTasks = new DataBaseTasks(this);
        SQLiteDatabase dbTasks = dataBaseTasks.getWritableDatabase();

        int sum = 0;
        int[] listsSize = new int[listBuf.length];
        for(int i = 0; i < listsSize.length; i++)
        {
            listsSize[i] = DataBaseTasks.getOnlyTasksByList(dbTasks, listBuf[i]).length;
            sum += listsSize[i];
        }
        // set sizes for EACH list //




        // Настраиваем СВОЙ адаптер
        SpinnerAdapter adapterSpinner = new
                SpinnerAdapter(this, R.layout.spinner_outside, R.id.nameOfList, listBuf, listsSize, sum);
        adapterSpinner.setDropDownViewResource(R.layout.spinner_item);

        // Настраиваем адаптер
        /*ArrayAdapter<String> adapterSpinner = new
                ArrayAdapter<>(this, R.layout.spinner_outside, R.id.nameOfList, listBuf);
        adapterSpinner.setDropDownViewResource(R.layout.spinner_item);*/

        spinner.setAdapter(adapterSpinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0) selectedSpinnerList = KEY_ALL_TASKS;
                else selectedSpinnerList = DataBaseLists.readDBLists(databaseLists)[position-1]; // -1 due to "Все задачи"
                lastSpinnerSelection = position;
                loadTaskListFragment();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


        //////////////////////////////////////////////////////////////////////////////////////////////
    }


    public class SpinnerAdapter extends ArrayAdapter<String>{

        private String[] lists;
        private int[] listsSize;
        private int sizeAllTasks;

        SpinnerAdapter(Context context, int resource, int textViewResourceId, String[] objects, int[] objectsSize, int sizeAllTasks) {
            super(context, resource, textViewResourceId, objects);

            this.lists = objects;
            this.listsSize = objectsSize;
            this.sizeAllTasks = sizeAllTasks;
        }


        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_item, parent, false);


            TextView label = (TextView) row.findViewById(R.id.nameOfListDropDown);
            label.setText(lists[position]);


            TextView numTasks = (TextView) row.findViewById(R.id.textView_numOfTasks);
            if(listsSize[position] > 0)
                numTasks.setText(String.valueOf(listsSize[position]));



            ImageView icon = (ImageView) row.findViewById(R.id.imageViewDef_spinner_item);

            if (lists[position].compareTo(KEY_ALL_TASKS) == 0) {
                icon.setImageResource(R.drawable.ic_home);
                numTasks.setText(String.valueOf(sizeAllTasks));
            }





            return row;
        }


    }








    ////////////////////////////////////////////////////////////////////////////////////////////////










    // Какой фаргмент загрпузить, определяется автоматический в классе фрагмента

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

        if(NewTask.wasSomeChangeWithSomeList) {
            setSpinnerLists();
            NewTask.wasSomeChangeWithSomeList = false;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(NewTask.wasSomeChangeWithSomeList) {
            setSpinnerLists();
            NewTask.wasSomeChangeWithSomeList = false;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }














    @Override
    protected void onPause() {
        super.onPause();

        if(NewTask.wasSomeChangeWithSomeList) {
            setSpinnerLists();
            NewTask.wasSomeChangeWithSomeList = false;
        }
    }

















    @Override
    public void onBackPressed() {

        if(TaskListFragment.openedTasksByListClick)
        {
            TaskListFragment.openedTasksByListClick = false;

            OpenedLists();
            loadTaskListFragment();

            //hideSpinnerAndSearchView();
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





/*
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


/*
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
    }*/
    /*
    private void backToFragment(final Fragment fragment) {

        // go back to something that was added to the backstack
        getSupportFragmentManager().popBackStackImmediate(
                fragment.getClass().getName(), 0);
        // use 0 or the below constant as flag parameter
        // FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }
*/



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


        loadTaskListFragment();

        hideSpinnerAndSearchView();

        toolbar.setTitle("");

        if (id == R.id.nav_main) {
            OpenedMain(); // Signal for TaskListFragment
            floatBtnToNewTask.setVisibility(View.VISIBLE);
            makeVisibleSpinnerAndSearchView();

        }
        else if (id == R.id.nav_lists) {
            OpenedLists();  // Signal for TaskListFragment
            floatBtnToNewTask.setVisibility(View.GONE);

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

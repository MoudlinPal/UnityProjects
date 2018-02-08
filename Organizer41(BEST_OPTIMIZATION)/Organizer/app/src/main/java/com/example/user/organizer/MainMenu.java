package com.example.user.organizer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.MenuInflater;
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
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.organizer.databases.DataBaseFinishedTasks;
import com.example.user.organizer.databases.DataBaseLists;
import com.example.user.organizer.databases.DataBaseTasks;
import com.example.user.organizer.notifications.MyReceiver;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public static boolean FloatBtnPressed = false;

    public static boolean NeedUpdateTaskAdapterFromToolbarTitle = false;
    public static String TitleToolbar;

    // Идентификатор уведомления
    private static final int PERMANENT_NOTIFY_ID = 103;


    public static String KEY_ALL_TASKS = "Все задачи";


    public static boolean openedMain, openedLists, openedFTasks;


    DataBaseLists dbLists;







    Spinner spinner;
    SpinnerAdapter adapterSpinner;

    public static String selectedSpinnerList = KEY_ALL_TASKS;
    public static int lastSpinnerSelection = 0;



    android.widget.SearchView searchView;



    FloatingActionButton floatBtnToNewTask;



    Toolbar toolbar;
    RelativeLayout RL_Spinner_Search;


    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        floatBtnToNewTask = (FloatingActionButton)findViewById(R.id.floatBtnToNewTask);
        floatBtnToNewTask.setSize(75);
        floatBtnToNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatBtnPressed = true;

                Animation anim = android.view.animation.AnimationUtils.loadAnimation(floatBtnToNewTask.getContext(),  R.anim.press_float_btn);
                floatBtnToNewTask.startAnimation(anim);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent intent = new Intent(MainMenu.this, NewTask.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        });




        RL_Spinner_Search = (RelativeLayout) findViewById(R.id.RL_Spinner_Search);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        toolbar.hideOverflowMenu();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
         //       this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Этот код вызывается, когда боковое меню переходит в полностью закрытое состояние. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                drawerOpenedOrClosed();
            }

            /** Этот код вызывается, когда боковое меню полностью открывается. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                drawerOpenedOrClosed();
            }
        };




        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
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

        OpenedMain();


    }







    public void setSpinnerLists() {

        class defineSpinnerLists implements Callable<ArrayList<String>> {

            private SQLiteDatabase databaseLists;
            private defineSpinnerLists(SQLiteDatabase db)
            {
                this.databaseLists = db;
            }

            @Override
            public ArrayList<String> call() throws Exception {

                String[] listBufDemo = DataBaseLists.readDBLists(databaseLists);

                ArrayList<String> listBuf = new ArrayList<String>();
                listBuf.add(KEY_ALL_TASKS);
                for(int i = 0; i < listBufDemo.length; i++)
                {
                    listBuf.add(listBufDemo[i]);
                }

                return listBuf;
            }
        }


        /////////////////////// DataBase //////////////////////
        final SQLiteDatabase databaseLists = dbLists.getWritableDatabase(); // доступен для чтения и записи
        /////////////////////// DataBase ///////////////////////


        ExecutorService exec = Executors.newCachedThreadPool();
        Future<ArrayList<String>> futureString = exec.submit(new defineSpinnerLists(databaseLists));

        ArrayList<String> listsNames = new ArrayList<>();
        try{
            listsNames = futureString.get();
        }
        catch (Exception e) {}
        finally {
            exec.shutdown();
        }









        // set sizes for EACH list //
        DataBaseTasks dataBaseTasks = new DataBaseTasks(this);
        SQLiteDatabase dbTasks = dataBaseTasks.getWritableDatabase();

        int sumListsSizes = 0;
        ArrayList<Integer> listsSizes = new ArrayList<>();
        for(int i = 0; i < listsNames.size(); i++)
        {
            listsSizes.add(i, DataBaseTasks.getAmountOfTasksByList(dbTasks, listsNames.get(i)));
            sumListsSizes += listsSizes.get(i);
        }
        // set sizes for EACH list //







        // Настраиваем СВОЙ адаптер
        adapterSpinner = new
                SpinnerAdapter(this, R.layout.spinner_outside, R.id.nameOfList, listsNames, listsSizes, sumListsSizes);
        adapterSpinner.setDropDownViewResource(R.layout.spinner_item);








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


    ArrayList<String> lists;
    ArrayList<Integer> listsSize;
    int sizeAllTasks;




    public class SpinnerAdapter extends ArrayAdapter<String>{



        SpinnerAdapter(Context context, int resource, int textViewResourceId, ArrayList<String> objects, ArrayList<Integer> objectsSize, int sizeAllObjects) {
            super(context, resource, textViewResourceId, objects);

            lists = objects;
            listsSize = objectsSize;
            sizeAllTasks = sizeAllObjects;
        }


        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_item, parent, false);


            TextView label = (TextView) row.findViewById(R.id.nameOfListDropDown);
            label.setText(lists.get(position));


            TextView numTasks = (TextView) row.findViewById(R.id.textView_numOfTasks);
            if(listsSize.get(position) > 0)
                numTasks.setText(String.valueOf(listsSize.get(position)));
            else
                numTasks.setText("");


            ImageView icon = (ImageView) row.findViewById(R.id.imageViewDef_spinner_item);

            if (lists.get(position).compareTo(KEY_ALL_TASKS) == 0) {
                icon.setImageResource(R.drawable.ic_home);
                numTasks.setText(String.valueOf(sizeAllTasks));
            }

            return row;
        }


    }








    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



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


        Intent notifyIntent = new Intent(this, MyReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (this,  2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis(),
                1000 * 60 , pendingIntent); // * 60 * 24


    }



    public void sendDefaultNotification() {
        //////////////////////// NOTIFICATION ////////////////////////
        NotificationCompat.Builder builder;
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        int def_notification_id = 311344;


        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.permanent_notification);
        remoteViews.setImageViewResource(R.id.imageViewAddTask, R.drawable.ic_add_task_notification);
        remoteViews.setImageViewResource(R.id.imageViewToSettings, R.drawable.ic_settings_notification);
        remoteViews.setImageViewResource(R.id.imageViewAppIcon, R.mipmap.ic_launcher);

        //Icon icon = Icon.createWithResource(this, R.mipmap.ic_launcher);
        //remoteViews.setImageViewIcon(R.id.imageViewAppIcon, icon);



        DataBaseTasks dbTasks = new DataBaseTasks(this);
        SQLiteDatabase databaseTasks = dbTasks.getWritableDatabase();
        int amount = DataBaseTasks.getAmountTasksToday(databaseTasks);

        if(amount < 1){
            remoteViews.setTextViewText(R.id.textViewTaskAmount, "Нет задач на сегодня");
            remoteViews.setTextViewText(R.id.textViewGoodDay, "Хорошего дня!");
        }
        else {
            remoteViews.setTextViewText(R.id.textViewTaskAmount, "Кол-во задач: " + amount);
            remoteViews.setTextViewText(R.id.textViewGoodDay, "Сегодня");
        }



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




        Intent notifIntent = new Intent(this, MainMenu.class);
        notifIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                68571, notifIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);











        builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .setCustomContentView(remoteViews)
        ;




        notificationManager.notify(def_notification_id, builder.build());

        //////////////////////// NOTIFICATION ////////////////////////
    }
















    @Override
    protected void onResume() {
        super.onResume();
        //Toast toast = Toast.makeText(MainMenu.this, "onResume()", Toast.LENGTH_SHORT);
        //toast.show();



    }


    @Override
    protected void onStart() {
        super.onStart();

        sendDefaultNotification();

        if(NewTask.wasSomeChangeWithSomeList) {
            setSpinnerLists();
            NewTask.wasSomeChangeWithSomeList = false;
        }


        if(!TaskListFragment.openedTasksByListClick){
            loadTaskListFragment();
        }
        else if(TaskListFragment.openedTasksByListClick){
            NeedUpdateTaskAdapterFromToolbarTitle = true;
            TitleToolbar = toolbar.getTitle().toString();

            loadTaskListFragment();
        }


        //Toast toast = Toast.makeText(MainMenu.this, "onStart()", Toast.LENGTH_SHORT);
        //toast.show();
    }

























    @Override
    public void onBackPressed() {

        if(TaskListFragment.ToolbarButtonBackIsPressed){
            toolbar.inflateMenu(R.menu.menu_lists);
            TaskListFragment.ToolbarButtonBackIsPressed = false;
        }

        if(TaskListFragment.openedTasksByListClick)
        {
            toolbar.hideOverflowMenu();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            toggle.syncState();

            TaskListFragment.openedTasksByListClick = false;

            OpenedLists();

            loadTaskListFragment();
            finishedListsAnimation = false;

            hideSpinnerAndSearchView();

            toolbar.setTitle("Списки задач");
            toolbar.setTitleTextColor(getColor(R.color.JustWhite));


            floatBtnToNewTask.setVisibility(View.GONE);

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













                   // LifeCycle !!
    /*@Override
    protected void onStart() {
        super.onStart();
        Toast toast = Toast.makeText(MainMenu.this, "onStart()", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast toast = Toast.makeText(MainMenu.this, "onResume()", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast toast = Toast.makeText(MainMenu.this, "onPause()", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast toast = Toast.makeText(MainMenu.this, "onStop()", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast toast = Toast.makeText(MainMenu.this, "onDestroy()", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Toast toast = Toast.makeText(MainMenu.this, "onResumeFragments()", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        super.onResumeFragments();
        Toast toast = Toast.makeText(MainMenu.this, "onPostResume()", Toast.LENGTH_SHORT);
        toast.show();
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




    private void hideSpinnerAndSearchView() {
        RL_Spinner_Search.setVisibility(View.GONE);
    }
    private void makeVisibleSpinnerAndSearchView() {
        RL_Spinner_Search.setVisibility(View.VISIBLE);
    }






    boolean finishedMainAnimation = false;
    boolean finishedListsAnimation = false;
    boolean finishedFTasksAnimation = false;
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        TaskListFragment.openedTasksByListClick = false;


        if (id == R.id.nav_main) {


            if(!openedMain) {
                finishedMainAnimation = true;
                OpenedMain(); // Signal for TaskListFragment
            }


        }
        else if (id == R.id.nav_lists) {


            if(!openedLists) {
                finishedListsAnimation = true;
                OpenedLists();  // Signal for TaskListFragment
            }


        }
        else if (id == R.id.nav_finished_tasks) {


            if (!openedFTasks) {
                finishedFTasksAnimation = true;
                OpenedFTasks();  // Signal for TaskListFragment
            }


        }
        else if (id == R.id.nav_settings) {


        }
        else if (id == R.id.nav_about) {


        }
        else if (id == R.id.nav_themes) {


        }



        item.setChecked(true);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }













    private void showDialogAddNewList(){

        // <!-- DIALOG ADD NEW LIST -->

        showKeyboard();

        dbLists = new DataBaseLists(this);
        final SQLiteDatabase databaseLists = dbLists.getWritableDatabase();


        ///////////////////////////////////////////////////////////////////////////////
        //Получаем вид с файла dialog_new_list.xml, который применим для диалогового окна:
        LayoutInflater li = LayoutInflater.from(this); // this = context !!!!!!!!!!!!!!!!!!!
        View viewDialogNewList = li.inflate(R.layout.dialog_new_list, null);

        final EditText editListName =
                (EditText)viewDialogNewList.findViewById(R.id.editListName); // !!!!!!!!!
        ///////////////////////////////////////////////////////////////////////////////


        ////////////////// <!-- DIALOG ADD NEW LIST --> //////////////////

        final AlertDialog dialogAddList = new AlertDialog.Builder(this)
                .setView(viewDialogNewList)
                .setTitle("Новый Список")
                .setPositiveButton("Добавить", null) //Set to null. We override the onclick
                .setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        showKeyboard();
                    }
                })
                .create();








        dialogAddList.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {



                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        ///////////////////////////////////////////////////////////////////////////////



                        // for DataBase !!!!!!!!!
                        String nameList = editListName.getEditableText().toString();

                        String bufDivider = "";
                        bufDivider += DataBaseLists.DIVIDER;

                        if(DataBaseLists.checkListPresence(databaseLists, nameList)) {
                            makeVibration();
                            Toast toast = Toast.makeText(MainMenu.this, "Такой список уже существует!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else if(nameList.compareTo("") == 0) // !!!!!!!!!!!!!
                        {
                            makeVibration();
                            Toast toast = Toast.makeText(MainMenu.this, "Введите название списка!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else if(nameList.contains(bufDivider))
                        {
                            makeVibration();
                            Toast toast = Toast.makeText(MainMenu.this, "Символ '"+ DataBaseLists.DIVIDER+"' недопустим!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else
                        {

                            DataBaseLists.addToDBLists(databaseLists, nameList);

                            //Dismiss once everything is OK.
                            dialogAddList.dismiss();

                            showKeyboard();

                            Toast toast = Toast.makeText(MainMenu.this, "Список добавлен!", Toast.LENGTH_SHORT);
                            toast.show();

                            loadTaskListFragment(); // UPDATE

                        }

                        ///////////////////////////////////////////////////////////////////////////////
                    }
                });
            }
        });


        dialogAddList.show();

        ////////////////// <!-- DIALOG ADD NEW LIST --> //////////////////

    }



    private void showDialogClearFinishedTasks(){

        // <!-- DIALOG Clear Finished Tasks -->

        ////////////////// <!-- DIALOG Clear Finished Tasks --> //////////////////

        new AlertDialog.Builder(this)
                .setTitle("Вы уверены?")
                .setMessage("Удалить завершенные задачи?")
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        DataBaseFinishedTasks dbFTasks = new DataBaseFinishedTasks(MainMenu.this);
                        SQLiteDatabase dbFinishedTasks = dbFTasks.getWritableDatabase();
                        DataBaseFinishedTasks.clearDataBase(dbFinishedTasks);

                        Toast.makeText(MainMenu.this, "Задачи удалены!", Toast.LENGTH_SHORT).show();
                        loadTaskListFragment();

                    }
                }).create().show();

        ////////////////// <!-- DIALOG ADD NEW LIST --> //////////////////

    }










   public static void OpenedMain() {
       openedMain = true;
       openedLists = false;
       openedFTasks = false;
   }
    public static void OpenedLists() {
        openedMain = false;
        openedLists = true;
        openedFTasks = false;
    }
    public static void OpenedFTasks() {
        openedMain = false;
        openedLists = false;
        openedFTasks = true;
    }



    public void makeVibration()
    {
        // Vibrate
        long mills = 100L;
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(mills);
    }



    //////////////////////////////////////// Hide/Show Keyboard
    public void hideKeyboard(View v)
    {
        try{
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch(Exception e){}
    }
    public void showKeyboard()
    {
        try{
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);}
        catch(Exception e){}
    }






















    public void drawerOpenedOrClosed() {

        if(finishedMainAnimation){
            final Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {


                    setSpinnerLists();
                    loadTaskListFragment();
                    finishedMainAnimation = false;


                    floatBtnToNewTask.setVisibility(View.VISIBLE);
                    makeVisibleSpinnerAndSearchView();

                    setSupportActionBar(toolbar); // вроде как так получается убрать раздутый до этого туулбар !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    toolbar.hideOverflowMenu();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainMenu.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

                        /** Этот код вызывается, когда боковое меню переходит в полностью закрытое состояние. */
                        public void onDrawerClosed(View view) {
                            super.onDrawerClosed(view);
                            drawerOpenedOrClosed();
                        }

                        /** Этот код вызывается, когда боковое меню полностью открывается. */
                        public void onDrawerOpened(View drawerView) {
                            super.onDrawerOpened(drawerView);
                            drawerOpenedOrClosed();
                        }
                    };
                    drawer.addDrawerListener(toggle);
                    toggle.syncState();


                }
            });
        }




        else if(finishedListsAnimation){
            final Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {


                    toolbar.getMenu().clear();
                    loadTaskListFragment();
                    finishedListsAnimation = false;


                    hideSpinnerAndSearchView();

                    toolbar.setTitle("Списки задач");
                    toolbar.setTitleTextColor(getColor(R.color.JustWhite));

                    toolbar.inflateMenu(R.menu.menu_lists);
                    toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            if (item.getItemId() == R.id.item_add_new_list)
                                showDialogAddNewList();

                            return true;

                        }
                    });


                    floatBtnToNewTask.setVisibility(View.GONE);


                }
            });
        }







        else if(finishedFTasksAnimation){
            final Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {


                    toolbar.getMenu().clear();
                    loadTaskListFragment();
                    finishedFTasksAnimation = false;


                    hideSpinnerAndSearchView();
                    floatBtnToNewTask.setVisibility(View.GONE);


                    //setSupportActionBar(toolbar); // вроде как так получается убрать раздутый до этого туулбар !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


                    toolbar.hideOverflowMenu();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainMenu.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

                        /** Этот код вызывается, когда боковое меню переходит в полностью закрытое состояние. */
                        public void onDrawerClosed(View view) {
                            super.onDrawerClosed(view);
                            drawerOpenedOrClosed();
                        }

                        /** Этот код вызывается, когда боковое меню полностью открывается. */
                        public void onDrawerOpened(View drawerView) {
                            super.onDrawerOpened(drawerView);
                            drawerOpenedOrClosed();
                        }
                    };
                    drawer.addDrawerListener(toggle);
                    toggle.syncState();


                    toolbar.setTitle("Завершенные задачи");
                    toolbar.setTitleTextColor(getColor(R.color.JustWhite));


                    toolbar.inflateMenu(R.menu.menu_finished_tasks);
                    toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            if (item.getItemId() == R.id.item_clear_finished_tasks) {

                                DataBaseFinishedTasks dbFTasks = new DataBaseFinishedTasks(MainMenu.this);
                                SQLiteDatabase databaseFTasks = dbFTasks.getWritableDatabase();
                                if (DataBaseFinishedTasks.getOnlyTasksWithArrayList(databaseFTasks).size() > 0) {
                                    showDialogClearFinishedTasks();
                                } else {
                                    Toast.makeText(MainMenu.this, "Всё отчищено!", Toast.LENGTH_SHORT).show();
                                    makeVibration();
                                }
                            }

                            return true;
                        }
                    });


                }
            });
        }
    }








}

package first.project.nikzhebindev.organizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.ViewDragHelper;
import android.view.LayoutInflater;
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

import first.project.nikzhebindev.organizer.Fragments.AboutAppFragment;
import first.project.nikzhebindev.organizer.Fragments.MyPreferenceFragment;
import first.project.nikzhebindev.organizer.databases.DataBaseFinishedTasks;
import first.project.nikzhebindev.organizer.databases.DataBaseLists;
import first.project.nikzhebindev.organizer.databases.DataBaseTasks;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class MainMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{


    /** /////////////////////////// Advertisement /////////////////////////// */

    //private static final String TAG = "MainActivity";

    private AdView mAdView;

    private InterstitialAd mInterstitialAd;

    public static boolean WE_CAME_FROM_NEWTASK = false;

    /** /////////////////////////// Advertisement /////////////////////////// */



    public static boolean FloatBtnPressed = false;

    public static boolean NeedUpdateTaskAdapterFromToolbarTitle = false;
    public static String TitleToolbar;

    // Идентификатор уведомления
    //private static final int PERMANENT_NOTIFY_ID = 103;


    public static String KEY_ALL_TASKS;


    public static boolean openedMain, openedLists, openedFTasks, openedSettings, openedAboutApp;


    DataBaseLists dbLists;







    Spinner spinner;
    SpinnerAdapter adapterSpinner;

    public static String selectedSpinnerList;
    public static int lastSpinnerSelection = 0;



    android.widget.SearchView searchView;



    FloatingActionButton floatBtnToNewTask;



    Toolbar toolbar;
    RelativeLayout RL_Spinner_Search;


    NavigationView navigationView;

    DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        // Sample AdMob app ID: ca-app-pub-5033052294993457~5980065515
        MobileAds.initialize(this, "ca-app-pub-5033052294993457~5980065515");


        ///////////////// for DB Lists /////////////////

        DataBaseLists.listRepeatNames[0] = this.getString(R.string.no_repeat);
        DataBaseLists.listRepeatNames[1] = this.getString(R.string.once_an_hour);
        DataBaseLists.listRepeatNames[2] = this.getString(R.string.once_a_day);
        DataBaseLists.listRepeatNames[3] = this.getString(R.string.once_a_week);
        DataBaseLists.listRepeatNames[4] = this.getString(R.string.once_a_year);

        DataBaseLists.defaultListsNames[0] = this.getString(R.string.defaultlist);
        DataBaseLists.defaultListsNames[1] = this.getString(R.string.personal);

        ///////////////// for DB Lists /////////////////


        KEY_ALL_TASKS = this.getString(R.string.all_lists);
        selectedSpinnerList = KEY_ALL_TASKS;

        floatBtnToNewTask = findViewById(R.id.floatBtnToNewTask);
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


        /** /////////////////////////// Advertisement /////////////////////////// */

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener(){

            @Override
            public void onAdLoaded() {
                mAdView.setClickable(true);
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                mAdView.setClickable(false);
                mAdView.setVisibility(View.GONE);
            }

        });
        /** /////////////////////////// Advertisement /////////////////////////// */




        RL_Spinner_Search = findViewById(R.id.RL_Spinner_Search);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        toolbar.hideOverflowMenu();


        drawer = findViewById(R.id.drawer_layout);
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


        setDrawerLeftEdgeSize(this, drawer, 0.6f);



        navigationView = findViewById(R.id.nav_view);
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
        spinner = findViewById(R.id.spinnerInsideToolbar);
        searchView = findViewById(R.id.searchMainMenu);


        setSpinnerLists();

        makeVisibleSpinnerAndSearchView();

        OpenedMain();







        ///////////////// List to show at Startup /////////////////

        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(this);
        String listStartup = myPreference.getString("listShowStartup", "0");

        if(listStartup.compareTo(KEY_ALL_TASKS) != 0) {

            DataBaseLists dataBaseLists = new DataBaseLists(this);
            SQLiteDatabase sqLiteDatabase = dataBaseLists.getWritableDatabase();
            String[] listsFromDB = DataBaseLists.readDBLists(sqLiteDatabase);
            String[] lists = new String[listsFromDB.length + 1];
            lists[0] = MainMenu.KEY_ALL_TASKS;
            for (int i = 1; i < lists.length; i++) {
                lists[i] = listsFromDB[i - 1];
            }
            for (int i = 0; i < lists.length; i++) {
                if (lists[i].compareTo(listStartup) == 0) {
                    spinner.setSelection(i, true);
                    break;
                }
            }

        }

        ///////////////// List to show at Startup /////////////////



        ///////////////// Show Images(Cats etc) or not /////////////////

        if(myPreference.getBoolean("Images", true)) {
            View headerLayout =
                    navigationView.getHeaderView(0);
            Drawable drawable = getResources().getDrawable(R.drawable.cats);
            headerLayout.setBackground(drawable);
            TextView textHeader = headerLayout.findViewById(R.id.text_of_header);
            textHeader.setVisibility(View.GONE);
        }
        else{
            View headerLayout =
                    navigationView.getHeaderView(0);
            Drawable drawable = getResources().getDrawable(R.color.PrimaryColor);
            headerLayout.setBackground(drawable);
            TextView textHeader = headerLayout.findViewById(R.id.text_of_header);
            textHeader.setVisibility(View.VISIBLE);
        }

        ///////////////// Show Images(Cats etc) or not /////////////////

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

                ArrayList<String> listBuf = new ArrayList<>();
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
        catch (Exception e) { //
             }
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
        public View getDropDownView(int position, View convertView,@NonNull ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_item, parent, false);


            TextView label = row.findViewById(R.id.nameOfListDropDown);
            label.setText(lists.get(position));


            TextView numTasks = row.findViewById(R.id.textView_numOfTasks);
            if(listsSize.get(position) > 0)
                numTasks.setText(String.valueOf(listsSize.get(position)));
            else
                numTasks.setText("");


            ImageView icon = row.findViewById(R.id.imageViewDef_spinner_item);

            if (lists.get(position).compareTo(KEY_ALL_TASKS) == 0) {
                icon.setImageResource(R.drawable.ic_home);
                numTasks.setText(String.valueOf(sizeAllTasks));
            }

            return row;
        }


    }













    // Какой фаргмент загрпузить, определяется автоматический в классе фрагмента

    public void loadTaskListFragment() {
        selectedSpinnerList = spinner.getSelectedItem().toString();
        TaskListFragment fragment = new TaskListFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayoutForListFragment, fragment).commit();
    }
    public void loadSettingsFragment() {
        MyPreferenceFragment fragment = new MyPreferenceFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayoutForListFragment, fragment).commit();
    }
    public void loadAboutAppFragment() {
        AboutAppFragment fragment = new AboutAppFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayoutForListFragment, fragment).commit();
    }






    /*public void changeLang(Context context, String lang) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("languageListPreference", lang);
        editor.apply();
    }*/






    public static void setDrawerLeftEdgeSize(Activity activity, DrawerLayout drawerLayout, float displayWidthPercentage) {
        if (activity == null || drawerLayout == null)
            return;

        try {
            // find ViewDragHelper and set it accessible
            Field leftDraggerField = drawerLayout.getClass().getDeclaredField("mLeftDragger");
            leftDraggerField.setAccessible(true);
            ViewDragHelper leftDragger = (ViewDragHelper) leftDraggerField.get(drawerLayout);
            // find edgesize and set is accessible
            Field edgeSizeField = leftDragger.getClass().getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);
            int edgeSize = edgeSizeField.getInt(leftDragger);
            // set new edgesize
            Point displaySize = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
            edgeSizeField.setInt(leftDragger, Math.max(edgeSize, (int) (displaySize.x * displayWidthPercentage)));
        } catch (NoSuchFieldException e) {
            // ignore
        } catch (IllegalArgumentException e) {
            // ignore
        } catch (IllegalAccessException e) {
            // ignore
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(this);
        if (myPreference.getBoolean("permanentNotification", true))
            sendDefaultNotification();
        else
            cancelDefaultNotification();

    }

    public static int def_notification_id = 311344;

    public void sendDefaultNotification() {
        //////////////////////// NOTIFICATION ////////////////////////

        NotificationCompat.Builder builder;
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);


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
            remoteViews.setTextViewText(R.id.textViewTaskAmount, getString(R.string.no_tasks_today));
            remoteViews.setTextViewText(R.id.textViewGoodDay, getString(R.string.have_a_nice_day));
        }
        else {
            remoteViews.setTextViewText(R.id.textViewTaskAmount, getString(R.string.amount_of_tasks) + " " + amount);
            remoteViews.setTextViewText(R.id.textViewGoodDay, getString(R.string.have_a_nice_day));
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
        Intent toSettingsIntent = new Intent(this, MainMenu.class);
        toSettingsIntent.putExtra("requestCode", 112);
        toSettingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentToSettingsIntentIntent = PendingIntent.getActivity(this,
                112, toSettingsIntent,
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
        builder.setSmallIcon(R.drawable.ic_notification_black)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .setCustomContentView(remoteViews)
        ;


        try{
            notificationManager.notify(def_notification_id, builder.build());
        }
        catch (NullPointerException e) {
        //
        }

        //////////////////////// NOTIFICATION ////////////////////////
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //PendingIntent.getActivity(this, 112, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            if (intent.getExtras().getInt("requestCode") == 112) {

                hideSpinnerAndSearchView();
                toolbar.getMenu().clear();
                floatBtnToNewTask.setVisibility(View.GONE);
                toolbar.setTitle("Settings"); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! MUST BE STRING LINK!!!!!
                finishedSettingsAnimation = true;
                OpenedSettings(); // Signal for MyReferenceFragment
                loadSettingsFragment();
                finishedSettingsAnimation = false;
                toolbar.setTitleTextColor(getResources().getColor(R.color.JustWhite));
                Toast.makeText(this, getString(R.string.u_can_enable_disable), Toast.LENGTH_SHORT).show();

            }
            /*else if(intent.getExtras().getString("notificationMessage").compareTo("") != 0){

                String task = intent.getExtras().getString("notificationMessage");

                Toast.makeText(this, "!!!!!!!", Toast.LENGTH_SHORT).show();

                DataBaseTasks databaseTasks = new DataBaseTasks(this);
                SQLiteDatabase sqLiteDatabase = databaseTasks.getWritableDatabase();
                String[] data = DataBaseTasks.getCertainTask(sqLiteDatabase, task);

                TaskListFragment.taskF = data[0];
                TaskListFragment.dateF = data[1];
                TaskListFragment.timeF = data[2];
                TaskListFragment.repeatF = data[3];
                TaskListFragment.listF = data[4];

                TaskListFragment.idF = data[5];

                // to NewTask
                TaskListFragment.setPrimaryNewTask = true;
                startActivity(new Intent(this, NewTask.class));

            }*/
        }catch(Exception e) {
            //
        }
    }



    public void cancelDefaultNotification(){

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        try { notificationManager.cancel(MainMenu.def_notification_id); } catch (Exception e) {
            //
        }

    }

















    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(this);
        if (myPreference.getBoolean("permanentNotification", true))
            sendDefaultNotification();
        else
            cancelDefaultNotification();
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        try { netInfo = cm.getActiveNetworkInfo();} catch (Exception e) {
            //
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }



    @Override
    protected void onResume() {
        super.onResume();

        //Toast toast = Toast.makeText(MainMenu.this, "onResume()", Toast.LENGTH_SHORT);
        //toast.show();

        ///////////////// LANGUAGE /////////////////
        /*
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("languageListPreference", "0");

        Toast.makeText(this, lang, Toast.LENGTH_SHORT).show();

        if(lang.length() > 2)
            switch (lang){
                case "German":      changeLang(this, "de");     break;
                case "English":     changeLang(this, "en");     break;
                case "Spanish":     changeLang(this, "es");     break;
                case "French":      changeLang(this, "fr");     break;
                case "Hindi":       changeLang(this, "hi");     break;
                case "Portuguese":  changeLang(this, "pt");     break;
                case "Russian":     changeLang(this, "ru");     break;
                case "Chinese":     changeLang(this, "zh");     break;
            }
        else
            switch (lang){
                case "de":     changeLang(this, "de");     break;
                case "en":     changeLang(this, "en");     break;
                case "es":     changeLang(this, "es");     break;
                case "fr":     changeLang(this, "fr");     break;
                case "hi":     changeLang(this, "hi");     break;
                case "pt":     changeLang(this, "pt");     break;
                case "ru":     changeLang(this, "ru");     break;
                case "zh":     changeLang(this, "zh");     break;
            }
        */

        ///////////////// LANGUAGE /////////////////

    }




    @Override
    protected void onStart() {
        super.onStart();






        if (WE_CAME_FROM_NEWTASK) {
            /** /////////////////////////// Advertisement /////////////////////////// */
            if (isOnline()) {
                mInterstitialAd = new InterstitialAd(this);
                mInterstitialAd.setAdUnitId(getString(R.string.on_start_ad));
                AdRequest adRequest = new AdRequest.Builder().build();
                mInterstitialAd.loadAd(adRequest);
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }
                    }

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                    }
                });
            }
            /** /////////////////////////// Advertisement /////////////////////////// */
            WE_CAME_FROM_NEWTASK = false;
        } else {
            WE_CAME_FROM_NEWTASK = false;
        }


        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(this);
        if (myPreference.getBoolean("permanentNotification", true))
            sendDefaultNotification();
        else
            cancelDefaultNotification();



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
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
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

            toolbar.setTitle(getString(R.string.lists_of_tasks));
            toolbar.setTitleTextColor(getResources().getColor(R.color.JustWhite));


            floatBtnToNewTask.setVisibility(View.GONE);

        }
        else
        {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                // <!-- DIALOG EXIT -->
                /** /////////////////////////// Advertisement /////////////////////////// */
                if(isOnline()){
                final AdView mAdViewDialogExit;

                //Получаем вид с файла dialog_new_list.xml, который применим для диалогового окна:
                LayoutInflater li = LayoutInflater.from(this); // this = context !!!!!!!!!!!!!!!!!!!
                View viewDialogAdMob = li.inflate(R.layout.dialog_exit_admob, null);

                mAdViewDialogExit = viewDialogAdMob.findViewById(R.id.mAdViewDialogExit);
                mAdViewDialogExit.setAdListener(new AdListener(){

                    @Override
                    public void onAdLoaded() {
                        mAdViewDialogExit.setClickable(true);
                        mAdViewDialogExit.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        mAdViewDialogExit.setClickable(false);
                        mAdViewDialogExit.setVisibility(View.GONE);
                    }

                });
                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                mAdViewDialogExit.loadAd(adRequest);

                /** /////////////////////////// Advertisement /////////////////////////// */

                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.do_u_want_to_exit))
                            /** /////////////////////////// Advertisement /////////////////////////// */
                            .setView(viewDialogAdMob)
                            /** /////////////////////////// Advertisement /////////////////////////// */
                            //.setMessage("Вы действительно хотите выйти?") // Тут будет реклама!!!!!!!!!!!!!
                            .setNegativeButton(R.string.no, null)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    MainMenu.super.onBackPressed();
                                }
                            }).create().show();
                }
                else{
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.do_u_want_to_exit))
                            //.setMessage("Вы действительно хотите выйти?") // Тут будет реклама!!!!!!!!!!!!!
                            .setNegativeButton(R.string.no, null)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    MainMenu.super.onBackPressed();
                                }
                            }).create().show();
                }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


















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
        //int id = item.getItemId();

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
    boolean finishedSettingsAnimation = false;
    boolean finishedAboutAppAnimation = false;
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                hideSpinnerAndSearchView();
                toolbar.getMenu().clear();
                floatBtnToNewTask.setVisibility(View.GONE);
                toolbar.setTitle(getString(R.string.lists_of_tasks));
                finishedListsAnimation = true;
                OpenedLists();  // Signal for TaskListFragment
            }


        }
        else if (id == R.id.nav_finished_tasks) {


            if (!openedFTasks) {
                hideSpinnerAndSearchView();
                toolbar.getMenu().clear();
                floatBtnToNewTask.setVisibility(View.GONE);
                toolbar.setTitle(getString(R.string.finished_tasks));
                finishedFTasksAnimation = true;
                OpenedFTasks();  // Signal for TaskListFragment
            }


        }
        else if (id == R.id.nav_settings) {


            if(!openedSettings) {
                hideSpinnerAndSearchView();
                toolbar.getMenu().clear();
                floatBtnToNewTask.setVisibility(View.GONE);
                toolbar.setTitle(getString(R.string.title_activity_settings));
                finishedSettingsAnimation = true;
                OpenedSettings(); // Signal for MyReferenceFragment
            }


        }
        else if (id == R.id.nav_about) {


            if(!openedAboutApp) {
                hideSpinnerAndSearchView();
                toolbar.getMenu().clear();
                floatBtnToNewTask.setVisibility(View.GONE);
                toolbar.setTitle(getString(R.string.title_activity_about_app));
                finishedAboutAppAnimation = true;
                OpenedAboutApp(); // Signal for AboutAppFragment Fragment
            }


        }
        //else if (id == R.id.nav_themes) {


        //}



        item.setChecked(true);



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
                viewDialogNewList.findViewById(R.id.editListName); // !!!!!!!!!
        ///////////////////////////////////////////////////////////////////////////////


        ////////////////// <!-- DIALOG ADD NEW LIST --> //////////////////

        final AlertDialog dialogAddList = new AlertDialog.Builder(this)
                .setView(viewDialogNewList)
                .setTitle(getString(R.string.new_list))
                .setPositiveButton(getString(R.string.add), null) //Set to null. We override the onclick
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
                            Toast toast = Toast.makeText(MainMenu.this, getString(R.string.this_list_already_exist), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else if(nameList.compareTo("") == 0) // !!!!!!!!!!!!!
                        {
                            makeVibration();
                            Toast toast = Toast.makeText(MainMenu.this, getString(R.string.enter_list_name), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else if(nameList.contains(bufDivider))
                        {
                            makeVibration();
                            Toast toast = Toast.makeText(MainMenu.this, "Character '"+ DataBaseLists.DIVIDER+"' is invalid!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else
                        {

                            DataBaseLists.addToDBLists(databaseLists, nameList);

                            //Dismiss once everything is OK.
                            dialogAddList.dismiss();

                            showKeyboard();

                            Toast toast = Toast.makeText(MainMenu.this, getString(R.string.list_is_added), Toast.LENGTH_SHORT);
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



    private void showDialogClearFinishedTasks() {

        // <!-- DIALOG Clear Finished Tasks -->
        if (isOnline()) {
            ////////////////// <!-- DIALOG Clear Finished Tasks --> //////////////////

            /** /////////////////////////// Advertisement /////////////////////////// */
            final AdView mAdViewDialogDelFTasks;

            //Получаем вид с файла dialog_new_list.xml, который применим для диалогового окна:
            LayoutInflater li = LayoutInflater.from(this); // this = context !!!!!!!!!!!!!!!!!!!
            View viewDialogAdMob = li.inflate(R.layout.dialog_delete_ftasks_admob, null);

            mAdViewDialogDelFTasks = viewDialogAdMob.findViewById(R.id.mAdViewDialogDelFTasks);
            mAdViewDialogDelFTasks.setAdListener(new AdListener() {

                @Override
                public void onAdLoaded() {
                    mAdViewDialogDelFTasks.setClickable(true);
                    mAdViewDialogDelFTasks.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    mAdViewDialogDelFTasks.setClickable(false);
                    mAdViewDialogDelFTasks.setVisibility(View.GONE);
                }

            });
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mAdViewDialogDelFTasks.loadAd(adRequest);

            /** /////////////////////////// Advertisement /////////////////////////// */


            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.delete_ftasks))
                    /** /////////////////////////// Advertisement /////////////////////////// */
                    .setView(viewDialogAdMob)
                    /** /////////////////////////// Advertisement /////////////////////////// */
                    //.setMessage("Удалить завершенные задачи?") // ТУТ БУДЕТ РЕКЛАМА
                    .setNegativeButton(R.string.no, null)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                            DataBaseFinishedTasks dbFTasks = new DataBaseFinishedTasks(MainMenu.this);
                            SQLiteDatabase dbFinishedTasks = dbFTasks.getWritableDatabase();
                            DataBaseFinishedTasks.clearDataBase(dbFinishedTasks);

                            Toast.makeText(MainMenu.this, getString(R.string.tasks_deleted), Toast.LENGTH_SHORT).show();
                            loadTaskListFragment();

                        }
                    }).create().show();

            ////////////////// <!-- DIALOG ADD NEW LIST --> //////////////////

        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.delete_ftasks))
                    .setNegativeButton(R.string.no, null)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                            DataBaseFinishedTasks dbFTasks = new DataBaseFinishedTasks(MainMenu.this);
                            SQLiteDatabase dbFinishedTasks = dbFTasks.getWritableDatabase();
                            DataBaseFinishedTasks.clearDataBase(dbFinishedTasks);

                            Toast.makeText(MainMenu.this, getString(R.string.tasks_deleted), Toast.LENGTH_SHORT).show();
                            loadTaskListFragment();

                        }
                    }).create().show();
        }
    }










   public static void OpenedMain() {
       openedMain = true;
       openedLists = false;
       openedFTasks = false;
       openedSettings = false;
       openedAboutApp = false;
   }
    public static void OpenedLists() {
        openedMain = false;
        openedLists = true;
        openedFTasks = false;
        openedSettings = false;
        openedAboutApp = false;
    }
    public static void OpenedFTasks() {
        openedMain = false;
        openedLists = false;
        openedFTasks = true;
        openedSettings = false;
        openedAboutApp = false;
    }
    public static void OpenedSettings() {
        openedMain = false;
        openedLists = false;
        openedFTasks = false;
        openedSettings = true;
        openedAboutApp = false;
    }
    public static void OpenedAboutApp() {
        openedMain = false;
        openedLists = false;
        openedFTasks = false;
        openedSettings = false;
        openedAboutApp = true;
    }





    public void makeVibration()
    {
        // Vibrate
        long mills = 100L;
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        try { vibrator.vibrate(mills); } catch(Exception e){
            //
        }
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

        if(finishedMainAnimation) {

            setSpinnerLists();
            loadTaskListFragment();
            finishedMainAnimation = false;


            floatBtnToNewTask.setVisibility(View.VISIBLE);
            makeVisibleSpinnerAndSearchView();

            setSupportActionBar(toolbar); // вроде как так получается убрать раздутый до этого туулбар !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            toolbar.hideOverflowMenu();
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainMenu.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

                /**
                 * Этот код вызывается, когда боковое меню переходит в полностью закрытое состояние.
                 */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    drawerOpenedOrClosed();
                }

                /**
                 * Этот код вызывается, когда боковое меню полностью открывается.
                 */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    drawerOpenedOrClosed();
                }
            };
            drawer.addDrawerListener(toggle);
            toggle.syncState();

        }




        else if(finishedListsAnimation) {
            loadTaskListFragment();
            finishedListsAnimation = false;



            toolbar.inflateMenu(R.menu.menu_lists);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if (item.getItemId() == R.id.item_add_new_list)
                        showDialogAddNewList();

                    return true;

                }
            });




        }







        else if(finishedFTasksAnimation) {
            loadTaskListFragment();
            finishedFTasksAnimation = false;



            //setSupportActionBar(toolbar); // вроде как так получается убрать раздутый до этого туулбар !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


            toolbar.hideOverflowMenu();
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainMenu.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

                /**
                 * Этот код вызывается, когда боковое меню переходит в полностью закрытое состояние.
                 */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    drawerOpenedOrClosed();
                }

                /**
                 * Этот код вызывается, когда боковое меню полностью открывается.
                 */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    drawerOpenedOrClosed();
                }
            };
            drawer.addDrawerListener(toggle);
            toggle.syncState();




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
                            Toast.makeText(MainMenu.this, getString(R.string.tasks_deleted), Toast.LENGTH_SHORT).show();
                            makeVibration();
                        }
                    }

                    return true;
                }
            });


        }

        else if(finishedSettingsAnimation){
            loadSettingsFragment();
            finishedSettingsAnimation = false;


        }

        else if(finishedAboutAppAnimation){
            loadAboutAppFragment();
            finishedAboutAppAnimation = false;

        }

    }


    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        /////////////////////////////////////////////////////////////////////////
        if(!openedAboutApp) {

            if (!NewTask.wasPressedBack) {

                if (NewTask.wasSomeChangeWithSomeList) {
                    setSpinnerLists();
                    spinner.setSelection(lastSpinnerSelection, true);
                    NewTask.wasSomeChangeWithSomeList = false;
                }


                if (!openedSettings) {
                    if (!TaskListFragment.openedTasksByListClick) {
                        loadTaskListFragment();
                    } else if (TaskListFragment.openedTasksByListClick) {
                        NeedUpdateTaskAdapterFromToolbarTitle = true;
                        TitleToolbar = toolbar.getTitle().toString();

                        loadTaskListFragment();
                    }
                }
            } else {
                NewTask.wasPressedBack = false;
            }
        }
        /////////////////////////////////////////////////////////////////////////
    }
}

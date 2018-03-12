package first.project.nikzhebindev.organizer;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import first.project.nikzhebindev.organizer.databases.DataBaseFinishedTasks;
import first.project.nikzhebindev.organizer.databases.DataBaseLists;
import first.project.nikzhebindev.organizer.databases.DataBaseTasks;
import first.project.nikzhebindev.organizer.notifications.MyReceiver;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class NewTask extends AppCompatActivity implements View.OnClickListener {

    String timeInMillis; // FOR DATABASE

    public static boolean wasSomeChangeWithSomeList = false;

    public static boolean wasPressedBack = false;

    public Toolbar toolbar;

    long date = System.currentTimeMillis(); // for Calendar Diaolg (Red color)

    Calendar dateAndTime = Calendar.getInstance();

    TextView editDate, editTime, textViewSetTime, textViewRepeat;

    ImageButton imageBtnDate, imageBtnTime, imageBtnSpeak, imgBtnClearDate, imgBtnClearTime, imgBtnNewList;

    Button btnRepeat, btnAddToList;//, btnReadDatabase, btnClearDatabase, btnReadDBTasks, btnReadDBFTasks, btnClearDBTasks, btnClearDBFTasks, btnCheckTime;

    EditText editTask;


    // DATA for Database !!!
    static int selectedRepeat = 0, selectedList = 0; // for Dialogs


    DataBaseLists dbLists; // Our DateBases
    DataBaseTasks dbTasks;

    String nameList = DataBaseLists.defaultListsNames[0]; // По умолчанию



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /////////////////////////////////// THEME ///////////////////////////////////

        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);
        String savedTHEME = sPref.getString("ThemeForNewTask", "");



        switch (savedTHEME){
            case "Default":  break;
            case "DefaultThemeN": setTheme(R.style.DefaultThemeN); break;

            case "TTheme": setTheme(R.style.TTheme); break;

            case "NightTheme": setTheme(R.style.NightTheme); break;

            case "IndigoTheme": setTheme(R.style.IndigoTheme); break;
            case "IndigoThemeN": setTheme(R.style.IndigoThemeN); break;

            case "GreenTheme": setTheme(R.style.GreenTheme); break;
            case "GreenThemeN": setTheme(R.style.GreenThemeN); break;

            case "PurpleTheme": setTheme(R.style.PurpleTheme); break;
            case "PurpleThemeN": setTheme(R.style.PurpleThemeN); break;


            case "LeoTheme": setTheme(R.style.LeoTheme); break;


            default: break;
        }

        /////////////////////////////////// THEME ///////////////////////////////////


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        timeInMillis = "0";

        editTask = findViewById(R.id.editTask);
        editTask.setOnClickListener(this);


        defineButtons();


        //////////////// DataBases ////////////////
 /*
        btnReadDatabase = (Button)findViewById(R.id.btnReadDatabase);
        btnReadDatabase.setOnClickListener(this);
        btnClearDatabase =(Button)findViewById(R.id.btnClearDatabase);
        btnClearDatabase.setOnClickListener(this);
        btnReadDBTasks = (Button)findViewById(R.id.btnReadDBTasks);
        btnReadDBTasks.setOnClickListener(this);
        btnClearDBTasks =(Button)findViewById(R.id.btnClearDBTasks);
        btnClearDBTasks.setOnClickListener(this);

        btnReadDBFTasks = (Button)findViewById(R.id.btnReadDBFTasks);
        btnReadDBFTasks.setOnClickListener(this);
        btnClearDBFTasks =(Button)findViewById(R.id.btnClearDBFTasks);
        btnClearDBFTasks.setOnClickListener(this);

        btnCheckTime = (Button)findViewById(R.id.btnCheckTime);
        btnCheckTime.setOnClickListener(this);
*/
        //////////////// DataBases ////////////////




        if(MainMenu.FloatBtnPressed && MainMenu.selectedSpinnerList.compareTo(MainMenu.KEY_ALL_TASKS) != 0)
            btnAddToList.setText(MainMenu.selectedSpinnerList);




        // init DateBase
        dbLists = new DataBaseLists(this);
        dbTasks = new DataBaseTasks(this);


        initToolbar();

        if(TaskListFragment.setPrimaryNewTask)
        {
            SQLiteDatabase databaseTasks = dbTasks.getWritableDatabase();
            timeInMillis = DataBaseTasks.getCertainTask(databaseTasks, TaskListFragment.taskF)[6];
            if(timeInMillis.compareTo("0") != 0)
                dateAndTime.setTimeInMillis(Long.valueOf(timeInMillis));

            if(TaskListFragment.dateF.compareTo(getString(R.string.no_date)) == 0) TaskListFragment.dateF ="";
            setPrimaryViewsNewTask(TaskListFragment.taskF, TaskListFragment.dateF, TaskListFragment.timeF, TaskListFragment.repeatF, TaskListFragment.listF);
        }

        if(editDate.getText().toString().compareTo("") == 0)
            imgBtnClearDate.setVisibility(View.INVISIBLE);
        if(editTime.getText().toString().compareTo("") == 0)
            imgBtnClearTime.setVisibility(View.INVISIBLE);



        SQLiteDatabase databaseTasks = dbTasks.getWritableDatabase();

        try {
            if (DataBaseTasks.getCertainTask(databaseTasks, editTask.getText().toString())[6].compareTo("0") != 0) {
                if ((System.currentTimeMillis()) > Long.valueOf(DataBaseTasks.getCertainTask(databaseTasks, editTask.getText().toString())[6])) {
                    editDate.setTextColor(getResources().getColor(R.color.OverdueColor));
                    editTime.setTextColor(getResources().getColor(R.color.OverdueColor));
                }
            }
        } catch (Exception e){}

    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        try { netInfo = cm.getActiveNetworkInfo(); } catch(Exception e) {}
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    private void defineButtons()
    {
        textViewSetTime = findViewById(R.id.textViewSetTime);
        textViewRepeat = findViewById(R.id.textViewRepeat);


        editDate = findViewById(R.id.editDate);
        editDate.setOnClickListener(this);
        imageBtnDate = findViewById(R.id.imageBtnDate);
        imageBtnDate.setOnClickListener(this);

        editTime = findViewById(R.id.editTime);
        editTime.setOnClickListener(this);
        imageBtnTime = findViewById(R.id.imageBtnTime);
        imageBtnTime.setOnClickListener(this);

        imgBtnClearDate = findViewById(R.id.imgBtnClearDate);
        imgBtnClearDate.setOnClickListener(this);
        imgBtnClearTime = findViewById(R.id.imgBtnClearTime);
        imgBtnClearTime.setOnClickListener(this);



        imgBtnNewList = findViewById(R.id.imgBtnNewList);
        imgBtnNewList.setOnClickListener(this);






        btnRepeat = findViewById(R.id.btnRepeat);
        btnRepeat.setOnClickListener(this);
        btnRepeat.setText(DataBaseLists.listRepeatNames[0]);

        btnAddToList = findViewById(R.id.btnAddToList);
        btnAddToList.setOnClickListener(this);
        btnAddToList.setText(DataBaseLists.defaultListsNames[0]);



        imageBtnSpeak = (ImageButton)findViewById(R.id.imageBtnSpeak);
        imageBtnSpeak.setOnClickListener(this);


    }





    private void initToolbar()
    {
        /////////////////////// DataBaseTasks ///////////////////////
        // Вызываем метод вспомогательного класса, чтобы открыть и вернуть экхемпляр базы данных
        // с которой будем работать
        final SQLiteDatabase databaseTasks = dbTasks.getWritableDatabase(); // доступен для чтения и записи
        /////////////////////// DataBaseTasks ///////////////////////


        /////////////////  Toolbar  /////////////////////
        toolbar = (Toolbar)findViewById(R.id.toolbar_new);
        toolbar.setNavigationIcon(R.drawable.ic_action_back_arrow);

        ///////////////// Toolbar Button BACK
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editTask.getEditableText().toString().compareTo("") != 0 && !TaskListFragment.setPrimaryNewTask) {

                    // <!-- DIALOG "ARE U SURE?" EXIT -->
                    new AlertDialog.Builder(NewTask.this)
                            .setTitle(getString(R.string.quit_without_saving))
                            //.setMessage("Несохраненные данные будут утеряны." // ТУТ будет реклама!!!
                            .setNegativeButton(R.string.no, null)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    wasPressedBack = true;
                                    MainMenu.WE_CAME_FROM_NEWTASK = true;
                                    finish();
                                    MainMenu.WE_CAME_FROM_NEWTASK = true;

                                }
                            }).create().show();
                    // <!-- DIALOG "ARE U SURE?" EXIT -->

                }
                else
                {
                    wasPressedBack = true;
                    MainMenu.WE_CAME_FROM_NEWTASK = true;
                    finish();
                    MainMenu.WE_CAME_FROM_NEWTASK = true;
                }
            }
        });

        toolbar.setTitle(getString(R.string.new_task));
        toolbar.setTitleTextColor(getResources().getColor(R.color.JustWhite));


        // Use menu buttons
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        // Button Apply
                        if(item.getItemId() == R.id.apply_new_task)
                        {

                            if(editTask.getEditableText().toString().compareTo("") == 0){
                                makeVibration();
                                Toast toast = Toast.makeText(NewTask.this, getString(R.string.enter_task), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            else {


                                DataBaseFinishedTasks dataBaseFinishedTasks = new DataBaseFinishedTasks(getApplicationContext());
                                SQLiteDatabase sqLiteDatabase = dataBaseFinishedTasks.getWritableDatabase();

                                ///////////////////////////////////// проверяем внесли ли изменения в открытую задачу (не новая)
                                if(TaskListFragment.setPrimaryNewTask) // если перешли в активити, нажав по кнопке
                                {


                                    String[] newData = new String[DataBaseTasks.AMOUNT_DATA+1];
                                    newData[0] = editTask.getText().toString();
                                    newData[1] = editDate.getText().toString();
                                    newData[2] = editTime.getText().toString();
                                    newData[3] = btnRepeat.getText().toString();
                                    newData[4] = btnAddToList.getText().toString();

                                    if(timeInMillis.compareTo("") != 0){
                                        newData[5] = timeInMillis;
                                    }

                                    if(newData[0].compareTo(TaskListFragment.taskF) == 0
                                            && newData[1].compareTo(TaskListFragment.dateF) == 0
                                            && newData[2].compareTo(TaskListFragment.timeF) == 0
                                            && newData[3].compareTo(TaskListFragment.repeatF) == 0
                                            && newData[4].compareTo(TaskListFragment.listF) == 0)
                                    {
                                        Toast toast = Toast.makeText(NewTask.this, getString(R.string.task_not_modified), Toast.LENGTH_SHORT);
                                        toast.show();
                                        makeVibration();
                                    }
                                    else if(Long.valueOf(timeInMillis) < System.currentTimeMillis()
                                            && timeInMillis.compareTo("0") != 0){

                                        Toast toast = Toast.makeText(NewTask.this, getString(R.string.wrong_time), Toast.LENGTH_SHORT);
                                        toast.show();
                                        makeVibration();

                                    }
                                     // ПРОВЕРЯЕМ есть ли уже такая задача !
                                    else if(DataBaseTasks.hasTask(databaseTasks, editTask.getText().toString()) && newData[0].compareTo(TaskListFragment.taskF) != 0) {
                                        Toast toast = Toast.makeText(NewTask.this, getString(R.string.task_already_exist), Toast.LENGTH_SHORT);
                                        toast.show();
                                        makeVibration();
                                    }
                                    // ПРОВЕРЯЕМ есть ли уже такая ЗАВЕРШЕННАЯ задача !
                                    else if(DataBaseFinishedTasks.hasTask(sqLiteDatabase, editTask.getText().toString())) {
                                        Toast toast = Toast.makeText(NewTask.this, getString(R.string.task_already_exist), Toast.LENGTH_SHORT);
                                        toast.show();
                                        makeVibration();
                                    }
                                    // Update opened task
                                    else
                                    {
                                        if(newData[1].compareTo("") == 0 && newData[2].compareTo("") == 0){
                                            newData[1] = getString(R.string.no_date);
                                        }
                                        else if(newData[2].compareTo("") == 0){ /** if Date installed, but Time didn't */
                                            Calendar dateWithoutTime = Calendar.getInstance();
                                            dateWithoutTime.setTimeInMillis(Long.valueOf(timeInMillis));

                                            SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                            // if 12-hour format
                                            if (myPreference.getString("time_format", "0")
                                                    .compareTo(getResources().getStringArray(R.array.array_time_formats)[0]) == 0) {
                                                dateWithoutTime.set(Calendar.HOUR, 23);
                                                dateWithoutTime.set(Calendar.MINUTE, 59);
                                            }
                                            else { // if 24-hour format
                                                dateWithoutTime.set(Calendar.HOUR_OF_DAY, 23);
                                                dateWithoutTime.set(Calendar.MINUTE, 59);
                                            }

                                            timeInMillis = Long.toString(dateWithoutTime.getTimeInMillis());

                                            newData[5] = timeInMillis;
                                        }




                                        DataBaseTasks.updateTaskById(databaseTasks, newData, TaskListFragment.idF, getApplicationContext());

                                        Toast toast = Toast.makeText(NewTask.this, getString(R.string.task_updated), Toast.LENGTH_SHORT);
                                        toast.show();


                                        //if (newData[1].compareTo("") != 0 && newData[2].compareTo("") != 0) { // If there are Date and Time
                                        //    /** Set Alarm Notification */
                                        //    final Calendar calendar = Calendar.getInstance();
                                        //    calendar.setTimeInMillis(Long.valueOf(timeInMillis));
                                        //    sendNotification(calendar, TaskListFragment.idF,  btnRepeat.getText().toString());
                                        //}



                                        wasSomeChangeWithSomeList = true;
                                        MainMenu.WE_CAME_FROM_NEWTASK = true;

                                        finish();
                                    }
                                }
                                else
                                {
                                    // ПРОВЕРЯЕМ есть ли уже такая задача !
                                    if(DataBaseTasks.hasTask(databaseTasks, editTask.getText().toString()))
                                    {
                                        Toast toast = Toast.makeText(NewTask.this, getString(R.string.task_already_exist), Toast.LENGTH_SHORT);
                                        toast.show();
                                        makeVibration();
                                    }
                                    // ПРОВЕРЯЕМ есть ли уже такая ЗАВЕРШЕННАЯ задача !
                                    else if(DataBaseFinishedTasks.hasTask(sqLiteDatabase, editTask.getText().toString())) {
                                        Toast toast = Toast.makeText(NewTask.this, getString(R.string.task_already_exist), Toast.LENGTH_SHORT);
                                        toast.show();
                                        makeVibration();
                                    }
                                    else if(Long.valueOf(timeInMillis) < System.currentTimeMillis()
                                            && timeInMillis.compareTo("0") != 0){

                                        Toast toast = Toast.makeText(NewTask.this, getString(R.string.wrong_time), Toast.LENGTH_SHORT);
                                        toast.show();
                                        makeVibration();

                                    }
                                    else {
                                        if(editDate.getText().toString().compareTo("") == 0 &&
                                                editTime.getText().toString().compareTo("") == 0){
                                            DataBaseTasks.addToDBTasks(databaseTasks, editTask.getText().toString(),
                                                    getString(R.string.no_date), "",
                                                    btnRepeat.getText().toString(), btnAddToList.getText().toString(), timeInMillis, getApplicationContext());
                                        }
                                        else if(editTime.getText().toString().compareTo("") == 0){
                                            Calendar dateWithoutTime = Calendar.getInstance();
                                            dateWithoutTime.setTimeInMillis(Long.valueOf(timeInMillis));

                                            SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                            // if 12-hour format
                                            if (myPreference.getString("time_format", "0")
                                                    .compareTo(getResources().getStringArray(R.array.array_time_formats)[0]) == 0) {
                                                dateWithoutTime.set(Calendar.HOUR, 23);
                                                dateWithoutTime.set(Calendar.MINUTE, 59);
                                            } // if 24-hour format
                                            else{
                                                dateWithoutTime.set(Calendar.HOUR_OF_DAY, 23);
                                                dateWithoutTime.set(Calendar.MINUTE, 59);
                                            }

                                            timeInMillis = Long.toString(dateWithoutTime.getTimeInMillis());

                                            DataBaseTasks.addToDBTasks(databaseTasks, editTask.getText().toString(),
                                                    editDate.getText().toString(), editTime.getText().toString(),
                                                    btnRepeat.getText().toString(), btnAddToList.getText().toString(), timeInMillis, getApplicationContext());
                                        }
                                        else {
                                            DataBaseTasks.addToDBTasks(databaseTasks, editTask.getText().toString(),
                                                    editDate.getText().toString(), editTime.getText().toString(),
                                                    btnRepeat.getText().toString(), btnAddToList.getText().toString(), timeInMillis, getApplicationContext());

                                            /** Set Alarm Notification */
                                            final Calendar calendar = Calendar.getInstance();
                                            calendar.setTimeInMillis(Long.valueOf(timeInMillis));


                                            //Toast.makeText(NewTask.this, "ID = " + DataBaseTasks.getCertainTaskID(databaseTasks, editTask.getText().toString()), Toast.LENGTH_SHORT).show();
                                            /*if(DataBaseTasks.getCertainTaskID(databaseTasks, editTask.getText().toString()).compareTo("") != 0)
                                                sendNotification(calendar, DataBaseTasks.getCertainTaskID(databaseTasks, editTask.getText().toString()),  btnRepeat.getText().toString());
                                            else
                                                sendNotification(calendar, "1",  btnRepeat.getText().toString());*/



                                        }







                                        Toast toast = Toast.makeText(NewTask.this, getString(R.string.task_added), Toast.LENGTH_SHORT);
                                        toast.show();

                                        //Toast.makeText(NewTask.this, "\""+timeInMillis+"\"", Toast.LENGTH_SHORT).show();

                                        wasSomeChangeWithSomeList = true;
                                        MainMenu.WE_CAME_FROM_NEWTASK = true;

                                        finish();

                                    }
                                }
                            }

                        }

                        return false;
                    }
        });

        toolbar.inflateMenu(R.menu.menu_new_task);
    }










    public void makeVibration()
    {
        // Vibrate
        long mills = 100L;
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        try { vibrator.vibrate(mills); } catch (Exception e) {}
    }



    public void makeViewsGone()
    {
        textViewRepeat.setVisibility(View.GONE);
        textViewSetTime.setVisibility(View.GONE);
        imageBtnTime.setVisibility(View.GONE);
        editTime.setVisibility(View.GONE);
        btnRepeat.setVisibility(View.GONE);

        btnRepeat.setText(DataBaseLists.listRepeatNames[0]);
        onClick(imgBtnClearTime);
    }
    public void makeViewsVisible()
    {
        textViewSetTime.setVisibility(View.VISIBLE);
        imageBtnTime.setVisibility(View.VISIBLE);
        editTime.setVisibility(View.VISIBLE);

        if(editTime.getEditableText().toString().compareTo("") != 0){
            makeRepeatVisible();
        }
        else{
            makeRepeatGone();
        }
    }
    public void makeRepeatVisible()
    {
        textViewRepeat.setVisibility(View.VISIBLE);
        btnRepeat.setVisibility(View.VISIBLE);
    }
    public void makeRepeatGone()
    {
        textViewRepeat.setVisibility(View.GONE);
        btnRepeat.setVisibility(View.GONE);
    }








    @Override
    public void onBackPressed() {

        if(editTask.getEditableText().toString().compareTo("") != 0 && !TaskListFragment.setPrimaryNewTask) {
            // <!-- DIALOG "ARE U SURE?" EXIT -->
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.quit_without_saving))
                    //.setMessage("Несохраненные данные будут утеряны.") // здесь будет РЕКЛАМА!!!!!!
                    .setNegativeButton(R.string.no, null)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            wasPressedBack = true;
                            NewTask.super.onBackPressed();
                        }
                    }).create().show();
            // <!-- DIALOG "ARE U SURE?" EXIT -->
        }
        else
        {
            wasPressedBack = true;
            super.onBackPressed();
        }

    }

    @Override
    protected void onDestroy() {

        MainMenu.WE_CAME_FROM_NEWTASK = true;

        TaskListFragment.setPrimaryNewTask = false; // If NewTask called from


        SharedPreferences myPreference= PreferenceManager.getDefaultSharedPreferences(this);
        if(myPreference.getBoolean("permanentNotification", true))
            sendDefaultNotification();
        else
            cancelDefaultNotification();



        super.onDestroy();

    }



    @Override
    protected void onResume() {

        //editDate.setFocusable(false);
        //editTime.setFocusable(false);
        if(editDate.getEditableText().toString().compareTo("") == 0) {
            makeViewsGone();
        }
        else
        {
            makeViewsVisible();
        }

        super.onResume();
    }


































    ////////////////// Voice Recognition ////////////////////
    public void getSpeechInput(View v)
    {
        Intent msg = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);



        msg.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        msg.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        msg.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak));


        try
        {
            startActivityForResult(msg, 10);
        }
        catch (ActivityNotFoundException exep)
        {
            Toast.makeText(NewTask.this, getString(R.string.sorry_device), Toast.LENGTH_LONG).show();
        }
    }
    // for Setting SpeechText
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        switch (requestCode)
        {
            case 10: // REQUEST_CODE

                try {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editTask.setText(editTask.getText() + result.get(0));
                }
                catch (Exception a) {
                    finishActivity(requestCode); // close our new Speak Activity!
                }

                break;

        }




        super.onActivityResult(requestCode, resultCode, data);

    }









    //////////////////////////////////////// Hide/Show Keyboard
    public void HideKeyboard(View v)
    {
        try{
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch(Exception e){}
    }
    public void ShowKeyboard()
    {
        try{
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);}
        catch(Exception e){}
    }
















    boolean isToday = false;
    // установка даты
    private void setInitialDate(long date) {
        //Toast.makeText(this, "Date: " + dateAndTime.getTime(), Toast.LENGTH_LONG).show();


        isToday = false;

        // SET TEXT DATE

        //Calendar curDate = Calendar.getInstance();

        //int dayOfYear = curDate.get(Calendar.DAY_OF_YEAR);
        //String yearDay = Integer.toString(dayOfYear);

        //int givenDayOfYear = dateAndTime.get(Calendar.DAY_OF_YEAR);
        //String givenYearDay = Integer.toString(dayOfYear);


        /*if(dayOfYear == givenDayOfYear) {
            editDate.setText("Сегодня");
            isToday = true;
        }
        else if(dayOfYear == givenDayOfYear+1) {
            editDate.setText("Вчера");
        }
        else if(dayOfYear == givenDayOfYear-1) {
            editDate.setText("Завтра");
        }
        else {*/
        if(editTime.getEditableText().toString().compareTo("") == 0){
            Calendar curCalendar = Calendar.getInstance();


            SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            // if 12-hour format
            if (myPreference.getString("time_format", "0")
                    .compareTo(getResources().getStringArray(R.array.array_time_formats)[0]) == 0) {

                int h = curCalendar.get(Calendar.HOUR_OF_DAY);
                if (h < 23)
                    dateAndTime.set(Calendar.HOUR_OF_DAY, h + 1);
                else
                    dateAndTime.set(Calendar.HOUR_OF_DAY, h);
                dateAndTime.set(Calendar.MINUTE, 0);

            }
            else { // if 24-hour format
                int h = curCalendar.get(Calendar.HOUR_OF_DAY);
                if (h < 23)
                    dateAndTime.set(Calendar.HOUR_OF_DAY, h + 1);
                else
                    dateAndTime.set(Calendar.HOUR_OF_DAY, h);
                dateAndTime.set(Calendar.MINUTE, 0);
            }


        }

        timeInMillis = Long.toString(dateAndTime.getTimeInMillis()); // Important part of SQL DATABASE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


        editDate.setText(DateUtils.formatDateTime(this, dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
        //}


        if(editDate.getText() != "") {
            imgBtnClearDate.setVisibility(View.VISIBLE);
            makeViewsVisible();
        }


        if(date - 432.5e5 > dateAndTime.getTimeInMillis()) {

            editDate.setTextColor(getResources().getColor(R.color.OverdueColor));
            editTime.setTextColor(getResources().getColor(R.color.OverdueColor));

        }
        else {
            editDate.setTextColor(getResources().getColor(R.color.PrimaryColor));
            editTime.setTextColor(getResources().getColor(R.color.PrimaryColor));
        }

        // there below was *isToday &&...*
        if(editTime.getEditableText().toString().compareTo("") != 0){

            Date currentTime = new Date();

            if(currentTime.getTime() > dateAndTime.getTime().getTime()) {
                editDate.setTextColor(getResources().getColor(R.color.OverdueColor));
                editTime.setTextColor(getResources().getColor(R.color.OverdueColor));
            }
            else
            {
                editDate.setTextColor(getResources().getColor(R.color.PrimaryColor));
                editTime.setTextColor(getResources().getColor(R.color.PrimaryColor));
            }
        }
    }

    // установка времени
    private void setInitialTime() {
        //Toast.makeText(this, "Time: " + dateAndTime.getTime(), Toast.LENGTH_LONG).show();


        timeInMillis = Long.toString(dateAndTime.getTimeInMillis()); // Important part of SQL DATABASE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        makeRepeatVisible();

        Date currentTime = new Date();

        // если текущее больше того, которое мы установили
        //if(isToday){
            if(currentTime.getTime() > dateAndTime.getTimeInMillis()) {
                editDate.setTextColor(Color.RED);
                editTime.setTextColor(Color.RED);
            }
            else
            {
                editDate.setTextColor(getResources().getColor(R.color.PrimaryColor));
                editTime.setTextColor(getResources().getColor(R.color.PrimaryColor));
            }

        //timeInMillis = Long.toString(dateAndTime.getTimeInMillis()); // Important part of SQL DATABASE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        //}
        //else
        //{
            if(editDate.getCurrentTextColor() == Color.RED)
                editTime.setTextColor(Color.RED);
            else
                editTime.setTextColor(getResources().getColor(R.color.PrimaryColor));
        //}

        editTime.setText(DateUtils.formatDateTime
                (this, dateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME)
        );

        if(editTime.getText() != "")
        {
            imgBtnClearTime.setVisibility(View.VISIBLE);
        }



    }






    /////////// HANDLERS DATE and TIME ////////////////////////
    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener HandlerDate = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);



            setInitialDate(date);

        }
    };
    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener HandlerTime =new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            // if 12-hour format
            if (myPreference.getString("time_format", "0")
                    .compareTo(getResources().getStringArray(R.array.array_time_formats)[0]) == 0) {
                dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            }
            else { // if 24-hour format
                dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            }


            dateAndTime.set(Calendar.MINUTE, minute);
            dateAndTime.set(Calendar.SECOND, 1);

            setInitialTime();

        }
    };
    /////////// HANDLERS DATE and TIME ////////////////////////









    public void pickDate()
    {
        // диалоговое окно для выбора Date
        new DatePickerDialog(NewTask.this, HandlerDate,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();

        date = System.currentTimeMillis();

    }

    public void pickTime()
    {
        // диалоговое окно для выбора Time

        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // if 12-hour format
        if (myPreference.getString("time_format", "0")
                .compareTo(getResources().getStringArray(R.array.array_time_formats)[0]) == 0) {
            new TimePickerDialog(NewTask.this, HandlerTime,
                    dateAndTime.get(Calendar.HOUR_OF_DAY),
                    dateAndTime.get(Calendar.MINUTE), false
            )
                    .show();
        }
        else { // if 24-hour format
            new TimePickerDialog(NewTask.this, HandlerTime,
                    dateAndTime.get(Calendar.HOUR_OF_DAY),
                    dateAndTime.get(Calendar.MINUTE), true)
                    .show();
        }

        date = System.currentTimeMillis();

    }

    @Override
    public void onClick(View v) {


        /////////////////////// DataBase ///////////////////////
        final SQLiteDatabase databaseLists = dbLists.getWritableDatabase(); // доступен для чтения и записи
        /////////////////////// DataBase ///////////////////////

        /////////////////////// DataBaseTasks ///////////////////////
        final SQLiteDatabase databaseTasks = dbTasks.getWritableDatabase(); // доступен для чтения и записи
        /////////////////////// DataBaseTasks ///////////////////////

        switch (v.getId())
        {

            // <!----- DIALOG DATE ----->
            case R.id.editDate:
                pickDate();
                HideKeyboard(editDate);
                break;

            case R.id.imageBtnDate:
                pickDate();
                HideKeyboard(imageBtnDate);
                break;



            // <!----- DIALOG TIME ----->
            case R.id.editTime:
                pickTime();
                HideKeyboard(editTime);
                break;

            case R.id.imageBtnTime:
                pickTime();
                HideKeyboard(imageBtnTime);
                break;



            // <!----- Clear Date & Time ----->
            case R.id.imgBtnClearDate:
                editDate.setText("");
                imgBtnClearDate.setVisibility(View.INVISIBLE);
                makeViewsGone();

                timeInMillis = "0";

                break;

            case R.id.imgBtnClearTime:
                editTime.setText("");
                imgBtnClearTime.setVisibility(View.INVISIBLE);
                makeRepeatGone();
                break;






            // <!----- Repeat ----->
            case R.id.btnRepeat:

                AlertDialog.Builder builder = new AlertDialog.Builder(NewTask.this);
                builder.setItems(DataBaseLists.listRepeatNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        btnRepeat.setText(DataBaseLists.listRepeatNames[item]);
                        selectedRepeat = item;
                    }
                });
                //builder.setCancelable(false);
                builder.show();

                break;








            // <!----- Add To List ----->
            case R.id.btnAddToList:
                AlertDialog.Builder builderList = new AlertDialog.Builder(NewTask.this);
                builderList.setItems(DataBaseLists.readDBLists(databaseLists), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        btnAddToList.setText(DataBaseLists.readDBLists(databaseLists)[item]);
                        selectedList = item;}
                });
                builderList.show();
                break;




            // VoiceRecognition !!!
            case R.id.imageBtnSpeak:
                getSpeechInput(editTask);
                break;





            case R.id.imgBtnNewList:

                // <!-- DIALOG ADD NEW LIST -->

                HideKeyboard(imgBtnNewList);
                ShowKeyboard();

                ///////////////////////////////////////////////////////////////////////////////
                //Получаем вид с файла dialog_new_list.xml, который применим для диалогового окна:
                LayoutInflater li = LayoutInflater.from(NewTask.this); // this = context !!!!!!!!!!!!!!!!!!!
                View viewDialogNewList = li.inflate(R.layout.dialog_new_list, null);

                final EditText editListName =
                        (EditText)viewDialogNewList.findViewById(R.id.editListName); // !!!!!!!!!
                ///////////////////////////////////////////////////////////////////////////////


                ////////////////// <!-- DIALOG ADD NEW LIST --> //////////////////

                final AlertDialog dialogAddList = new AlertDialog.Builder(NewTask.this)
                        .setView(viewDialogNewList)
                        .setTitle(getString(R.string.new_list))
                        .setPositiveButton(getString(R.string.add), null) //Set to null. We override the onclick
                        .setNegativeButton(getString(R.string.cancel), null)
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
                                nameList = editListName.getEditableText().toString();

                                String bufDivider = "";
                                bufDivider += DataBaseLists.DIVIDER;

                                if(DataBaseLists.checkListPresence(databaseLists, nameList)) {
                                    makeVibration();
                                    Toast toast = Toast.makeText(NewTask.this, getString(R.string.this_list_already_exist), Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                                else if(nameList.compareTo("") == 0) // !!!!!!!!!!!!!
                                {
                                    makeVibration();
                                    Toast toast = Toast.makeText(NewTask.this, getString(R.string.enter_list_name), Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                                else if(nameList.contains(bufDivider))
                                {
                                    makeVibration();
                                    Toast toast = Toast.makeText(NewTask.this, "Character '"+ DataBaseLists.DIVIDER+"' is not valid!", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                                else
                                {

                                    DataBaseLists.addToDBLists(databaseLists, nameList);

                                    //Dismiss once everything is OK.
                                    dialogAddList.dismiss();

                                    btnAddToList.setText(nameList);




                                    wasSomeChangeWithSomeList = true;






                                    ShowKeyboard();

                                    Toast toast = Toast.makeText(NewTask.this, getString(R.string.list_is_added), Toast.LENGTH_SHORT);
                                    toast.show();

                                }

                                ///////////////////////////////////////////////////////////////////////////////
                            }
                        });
                    }
                });


                dialogAddList.show();

                ////////////////// <!-- DIALOG ADD NEW LIST --> //////////////////

                break;


            /*case R.id.btnReadDatabase:

                // TESTING
                editTask.setText("");
                String[] str = DataBaseLists.readDBLists(databaseLists);

                String bufferStr = "";

                for(int i = 0; i < str.length; i++)
                {
                    bufferStr += str[i];
                    bufferStr += "\n";
                }
                editTask.setText(bufferStr);
                break;
            case R.id.btnReadDBTasks:

                // TESTING
                editTask.setText("");
                String[] str1 = DataBaseTasks.readDataBase(databaseTasks);

                bufferStr = "";

                for(int i = 0; i < str1.length; i++)
                {
                    bufferStr += str1[i];
                    bufferStr += "\n\n";
                }
                editTask.setText(bufferStr);
                break;

            case R.id.btnReadDBFTasks:

                // TESTING
                editTask.setText("");

                DataBaseFinishedTasks dbFT = new DataBaseFinishedTasks(this);
                SQLiteDatabase dbFinishedTasks = dbFT.getWritableDatabase();

                String[] str2 = DataBaseFinishedTasks.readDataBase(dbFinishedTasks);

                bufferStr = "";

                for(int i = 0; i < str2.length; i++)
                {
                    bufferStr += str2[i];
                    bufferStr += "\n\n";
                }
                editTask.setText(bufferStr);
                break;


            case R.id.btnClearDatabase:
                DataBaseLists.clearDataBase(databaseLists);
                editTask.setText("DataBase is DELETED!");
                break;
            case R.id.btnClearDBTasks:
                DataBaseTasks.clearDataBase(databaseTasks);
                editTask.setText("DataBaseTasks is DELETED!");
                break;
            case R.id.btnClearDBFTasks:
                DataBaseFinishedTasks dbFT2 = new DataBaseFinishedTasks(this);
                SQLiteDatabase dbFinishedTasks2 = dbFT2.getWritableDatabase();
                DataBaseFinishedTasks.clearDataBase(dbFinishedTasks2);
                editTask.setText("DataBaseFinished is DELETED!");
                break;

            case R.id.btnCheckTime:
                editTask.setText(dateAndTime.getTime().toString());
                break;*/
        }
    }



    private void setPrimaryViewsNewTask(String task, String date, String time, String repeat, String list)
    {
        editTask.setText(task);
        editTask.setSelection(editTask.getText().length());
        editDate.setText(date);
        editTime.setText(time);
        btnRepeat.setText(repeat);
        btnAddToList.setText(list);
    }






    /**private void startAlarmNotification(Calendar calendar){
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 4432, intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

    }*/


    private void endNotification(Calendar calendar, String id, String repeat) {
        // Calendar calendar, String repeating
        // and then in your activity set the alarm manger to start the broadcast receiver
        // at a specific time and use AlarmManager setRepeating method to repeat it this
        // example bellow will repeat it every day.


        //String title = getString(R.string.task_at) + " " + editTime.getEditableText().toString();
        String message = editTask.getEditableText().toString();


        Intent notifyIntent = new Intent(this, MyReceiver.class);
        //notifyIntent.putExtra("NotificationTitle", title);
        notifyIntent.putExtra("NotificationMessage", message);
        notifyIntent.putExtra("id_Notification", id);


        // ONE REAQUEST CODE FOR ALL.... That's bad I think !!!!!!!!!!!!!!!!!!
        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (this, (Integer.decode(id) + 850), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        try {
            if (repeat.compareTo(DataBaseLists.listRepeatNames[0]) == 0) { // Не повторять
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else if (repeat.compareTo(DataBaseLists.listRepeatNames[1]) == 0) { // Каждый час
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_HOUR, pendingIntent);
            } else if (repeat.compareTo(DataBaseLists.listRepeatNames[2]) == 0) { // Ежедневно
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            } else if (repeat.compareTo(DataBaseLists.listRepeatNames[3]) == 0) { // Еженедельно
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY * 7, pendingIntent);
            } else if (repeat.compareTo(DataBaseLists.listRepeatNames[4]) == 0) { // Ежегодно
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY * 365, pendingIntent);
            }
        } catch (Exception e) {
        }
    }





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




        try { notificationManager.notify(MainMenu.def_notification_id, builder.build()); } catch (Exception e) {}

        //////////////////////// NOTIFICATION ////////////////////////
    }


    public void cancelDefaultNotification(){

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        try { notificationManager.cancel(MainMenu.def_notification_id); } catch (Exception e) {}

    }



}


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package com.example.user.organizer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.user.organizer.databases.DataBaseLists;
import com.example.user.organizer.databases.DataBaseTasks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewTask extends AppCompatActivity implements View.OnClickListener {

    public static boolean wasSomeChangeWithSomeList = false;

    public static String FileNewLists = "NewLists";

    private Toolbar toolbar;

    long date = System.currentTimeMillis(); // for Calendar Diaolg (Red color)

    Calendar dateAndTime = Calendar.getInstance();

    TextView editDate, editTime, textViewSetTime, textViewRepeat;

    ImageButton imageBtnDate, imageBtnTime, imageBtnSpeak, imgBtnClearDate, imgBtnClearTime, imgBtnNewList;

    Button btnRepeat, btnAddToList, btnReadDatabase, btnClearDatabase, btnReadDBTasks, btnClearDBTasks;

    EditText editTask;


    // DATA for Database !!!
    static int selectedRepeat = 0, selectedList = 0; // for Dialogs


    DataBaseLists dbLists; // Our DateBases
    DataBaseTasks dbTasks;

    String nameList = DataBaseLists.defaultListsNames[0]; // По умолчанию



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);



        editTask = (EditText)findViewById(R.id.editTask);
        editTask.setOnClickListener(this);


        defineButtons();


        //////////////// DataBases ////////////////

        btnReadDatabase = (Button)findViewById(R.id.btnReadDatabase);
        btnReadDatabase.setOnClickListener(this);
        btnClearDatabase =(Button)findViewById(R.id.btnClearDatabase);
        btnClearDatabase.setOnClickListener(this);
        btnReadDBTasks = (Button)findViewById(R.id.btnReadDBTasks);
        btnReadDBTasks.setOnClickListener(this);
        btnClearDBTasks =(Button)findViewById(R.id.btnClearDBTasks);
        btnClearDBTasks.setOnClickListener(this);

        //////////////// DataBases ////////////////




        if(MainMenu.FloatBtnPressed && MainMenu.selectedSpinnerList.compareTo(MainMenu.KEY_ALL_TASKS) != 0)
            btnAddToList.setText(MainMenu.selectedSpinnerList);




        // init DateBase
        dbLists = new DataBaseLists(this);
        dbTasks = new DataBaseTasks(this);


        initToolbar();

        if(TaskListFragment.setPrimaryNewTask)
        {
            setPrimaryViewsNewTask(TaskListFragment.taskF, TaskListFragment.dateF, TaskListFragment.timeF, TaskListFragment.repeatF, TaskListFragment.listF);
        }

        if(editDate.getText().toString().compareTo("") == 0)
            imgBtnClearDate.setVisibility(View.INVISIBLE);
        if(editTime.getText().toString().compareTo("") == 0)
            imgBtnClearTime.setVisibility(View.INVISIBLE);

    }





    private void defineButtons()
    {
        textViewSetTime = (TextView)findViewById(R.id.textViewSetTime);
        textViewRepeat = (TextView)findViewById(R.id.textViewRepeat);


        editDate = (TextView)findViewById(R.id.editDate);
        editDate.setOnClickListener(this);
        imageBtnDate = (ImageButton)findViewById(R.id.imageBtnDate);
        imageBtnDate.setOnClickListener(this);

        editTime = (TextView)findViewById(R.id.editTime);
        editTime.setOnClickListener(this);
        imageBtnTime = (ImageButton)findViewById(R.id.imageBtnTime);
        imageBtnTime.setOnClickListener(this);

        imgBtnClearDate = (ImageButton)findViewById(R.id.imgBtnClearDate);
        imgBtnClearDate.setOnClickListener(this);
        imgBtnClearTime = (ImageButton)findViewById(R.id.imgBtnClearTime);
        imgBtnClearTime.setOnClickListener(this);



        imgBtnNewList = (ImageButton)findViewById(R.id.imgBtnNewList);
        imgBtnNewList.setOnClickListener(this);






        btnRepeat = (Button)findViewById(R.id.btnRepeat);
        btnRepeat.setOnClickListener(this);
        btnRepeat.setText(DataBaseLists.listRepeatNames[0]);

        btnAddToList = (Button)findViewById(R.id.btnAddToList);
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
                            .setTitle("Вы уверены?")
                            .setMessage("Несохраненные данные будут утеряны.")
                            .setNegativeButton(R.string.no, null)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    finish();
                                }
                            }).create().show();
                    // <!-- DIALOG "ARE U SURE?" EXIT -->
                }
                else
                {
                    finish();
                }
            }
        });

        toolbar.setTitle("Новая задача");
        toolbar.setTitleTextColor(getColor(R.color.JustWhite));


        // Use menu buttons
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        // Button Apply
                        if(item.getItemId() == R.id.apply_new_task)
                        {

                            if(editTask.getEditableText().toString().compareTo("") == 0){
                                makeVibration();
                                Toast toast = Toast.makeText(NewTask.this, "Введите задачу!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            else {



                                ///////////////////////////////////// проверяем внесли ли изменения в открытую задачу (не новая)
                                if(TaskListFragment.setPrimaryNewTask)
                                {

                                    String[] newData = new String[DataBaseTasks.AMOUNT_DATA+1];
                                    newData[0] = editTask.getText().toString();
                                    newData[1] = editDate.getText().toString();
                                    newData[2] = editTime.getText().toString();
                                    newData[3] = btnRepeat.getText().toString();
                                    newData[4] = btnAddToList.getText().toString();

                                    if(newData[0].compareTo(TaskListFragment.taskF) == 0
                                            && newData[1].compareTo(TaskListFragment.dateF) == 0
                                            && newData[2].compareTo(TaskListFragment.timeF) == 0
                                            && newData[3].compareTo(TaskListFragment.repeatF) == 0
                                            && newData[4].compareTo(TaskListFragment.listF) == 0)
                                    {
                                        Toast toast = Toast.makeText(NewTask.this, "Вы не внесли никаких изменений!", Toast.LENGTH_SHORT);
                                        toast.show();
                                        makeVibration();
                                    }
                                    // Update opened task
                                    else
                                    {
                                        DataBaseTasks.updateTaskById(databaseTasks, newData, TaskListFragment.idF);

                                        Toast toast = Toast.makeText(NewTask.this, "Задача обновлена!", Toast.LENGTH_SHORT);
                                        toast.show();
                                        finish();
                                    }
                                }
                                else
                                {
                                    // ПРОВЕРЯЕМ есть ли уже такая задача !
                                    if(DataBaseTasks.hasTask(databaseTasks, editTask.getText().toString()))
                                    {
                                        Toast toast = Toast.makeText(NewTask.this, "У вас уже имеется такая задача!", Toast.LENGTH_SHORT);
                                        toast.show();
                                        makeVibration();
                                    }
                                    else {
                                        DataBaseTasks.addToDBTasks(databaseTasks, editTask.getText().toString(),
                                                editDate.getText().toString(), editTime.getText().toString(),
                                                btnRepeat.getText().toString(), btnAddToList.getText().toString());

                                        Toast toast = Toast.makeText(NewTask.this, "Задача добавлена!", Toast.LENGTH_SHORT);
                                        toast.show();
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
        vibrator.vibrate(mills);
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
        textViewRepeat.setVisibility(View.VISIBLE);
        textViewSetTime.setVisibility(View.VISIBLE);
        imageBtnTime.setVisibility(View.VISIBLE);
        editTime.setVisibility(View.VISIBLE);
        btnRepeat.setVisibility(View.VISIBLE);
    }









    @Override
    public void onBackPressed() {

        if(editTask.getEditableText().toString().compareTo("") != 0 && !TaskListFragment.setPrimaryNewTask) {
            // <!-- DIALOG "ARE U SURE?" EXIT -->
            new AlertDialog.Builder(this)
                    .setTitle("Вы уверены?")
                    .setMessage("Несохраненные данные будут утеряны.")
                    .setNegativeButton(R.string.no, null)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            NewTask.super.onBackPressed();
                        }
                    }).create().show();
            // <!-- DIALOG "ARE U SURE?" EXIT -->
        }
        else
        {
            super.onBackPressed();
        }

    }

    @Override
    protected void onDestroy() {

        TaskListFragment.setPrimaryNewTask = false; // If NewTask called from

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


















    boolean isToday = false;
    // установка даты
    private void setInitialDate(long date) {

        isToday = false;

        // SET TEXT DATE

        Calendar curDate = Calendar.getInstance();

        int dayOfYear = curDate.get(Calendar.DAY_OF_YEAR);
        //String yearDay = Integer.toString(dayOfYear);

        int givenDayOfYear = dateAndTime.get(Calendar.DAY_OF_YEAR);
        //String givenYearDay = Integer.toString(dayOfYear);


        if(dayOfYear == givenDayOfYear) {
            editDate.setText("Сегодня");
            isToday = true;
        }
        else if(dayOfYear == givenDayOfYear+1) {
            editDate.setText("Вчера");
        }
        else if(dayOfYear == givenDayOfYear-1) {
            editDate.setText("Завтра");
        }
        else {
            editDate.setText(DateUtils.formatDateTime
                    (this,
                            dateAndTime.getTimeInMillis(),
                            DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR)
            );
        }


        if(editDate.getText() != "") {
            imgBtnClearDate.setVisibility(View.VISIBLE);
            makeViewsVisible();
        }

        //  - 865e5
        if(date - 432.5e5 > dateAndTime.getTimeInMillis()) {

            editDate.setTextColor(Color.RED);
            editTime.setTextColor(Color.RED);

            // Display smth
            //Toast toast = Toast.makeText(NewTask.this, "Warning!", Toast.LENGTH_SHORT);
            //toast.show();

        }
        else {
            editDate.setTextColor(getColor(R.color.PrimaryColor));
            editTime.setTextColor(getColor(R.color.PrimaryColor));
        }


        if(isToday && editTime.getEditableText().toString().compareTo("") != 0){

            Date currentTime = new Date();

            if(currentTime.getTime() > dateAndTime.getTime().getTime()) {
                editDate.setTextColor(Color.RED);
                editTime.setTextColor(Color.RED);
            }
            else
            {
                editDate.setTextColor(getColor(R.color.PrimaryColor));
                editTime.setTextColor(getColor(R.color.PrimaryColor));
            }
        }
        //setInitialTime();
    }

    // установка времени
    private void setInitialTime() {

        Date currentTime = new Date();

        // если текущее больше того, которое мы установили
        if(isToday){
            if(currentTime.getTime() > dateAndTime.getTime().getTime()) {
                editDate.setTextColor(Color.RED);
                editTime.setTextColor(Color.RED);
            }
            else
            {
                editDate.setTextColor(getColor(R.color.PrimaryColor));
                editTime.setTextColor(getColor(R.color.PrimaryColor));
            }
        }
        else
        {
            if(editDate.getCurrentTextColor() == Color.RED)
                editTime.setTextColor(Color.RED);
            else
                editTime.setTextColor(getColor(R.color.PrimaryColor));
        }

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
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);

            setInitialTime();

        }
    };
    /////////// HANDLERS DATE and TIME ////////////////////////















    ////////////////// Voice Recognition ////////////////////
    public void getSpeechInput(View v)
    {
        Intent msg = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);



        msg.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        msg.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        msg.putExtra(RecognizerIntent.EXTRA_PROMPT, "Говорите!");


        try
        {
            startActivityForResult(msg, 10);
        }
        catch (ActivityNotFoundException exep)
        {
            Toast.makeText(NewTask.this, "Извините! Ваше устройство не поддерживает данную возможность!", Toast.LENGTH_LONG).show();
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
        new TimePickerDialog(NewTask.this, HandlerTime,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();

        date = System.currentTimeMillis();
    }

    @Override
    public void onClick(View v) {


        /////////////////////// DataBase ///////////////////////
        // Вызываем метод вспомогательного класса, чтобы открыть и вернуть экхемпляр базы данных
        // с которой будем работать
        final SQLiteDatabase databaseLists = dbLists.getWritableDatabase(); // доступен для чтения и записи
        /////////////////////// DataBase ///////////////////////

        /////////////////////// DataBaseTasks ///////////////////////
        // Вызываем метод вспомогательного класса, чтобы открыть и вернуть экхемпляр базы данных
        // с которой будем работать
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
                break;

            case R.id.imgBtnClearTime:
                editTime.setText("");
                imgBtnClearTime.setVisibility(View.INVISIBLE);
                break;






            // <!----- Repeat ----->
            case R.id.btnRepeat:


                AlertDialog.Builder builder = new AlertDialog.Builder(NewTask.this);

                //builderSingle.setIcon(R.drawable.ic_launcher);
                //builderSingle.setTitle("Select One Name:-");
                /*
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(NewTask.this, android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add("Hardik");
                arrayAdapter.add("Archit");
                arrayAdapter.add("Jignesh");
                arrayAdapter.add("Umang");
                arrayAdapter.add("Gatti");
                */





                builder.setItems(DataBaseLists.listRepeatNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {


                        btnRepeat.setText(DataBaseLists.listRepeatNames[item]);


                        selectedRepeat = item;
                    }
                });
                //builder.setCancelable(false);

                builder.show();





                /*
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                */



                /* // Inner Dialog (Dialog inside Dialog)
                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(NewTask.this);
                        builderInner.setMessage(strName);
                        builderInner.setTitle("Your Selected Item is");
                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                dialog.dismiss();
                            }
                        });
                        builderInner.show();
                    }
                });
                builderSingle.show();
                */

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
                        .setTitle("Новый Список")
                        .setPositiveButton("Добавить", null) //Set to null. We override the onclick
                        .setNegativeButton("Отменить", null)
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
                                    Toast toast = Toast.makeText(NewTask.this, "Такой список уже существует!", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                                else if(nameList.compareTo("") == 0) // !!!!!!!!!!!!!
                                {
                                    makeVibration();
                                    Toast toast = Toast.makeText(NewTask.this, "Введите название списка!", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                                else if(nameList.contains(bufDivider))
                                {
                                    makeVibration();
                                    Toast toast = Toast.makeText(NewTask.this, "Символ '"+ DataBaseLists.DIVIDER+"' недопустим!", Toast.LENGTH_SHORT);
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

                                    Toast toast = Toast.makeText(NewTask.this, "Список добавлен!", Toast.LENGTH_SHORT);
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


            case R.id.btnReadDatabase:

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


            case R.id.btnClearDatabase:
                DataBaseLists.clearDataBase(databaseLists);
                editTask.setText("DataBase is DELETED!");
                break;
            case R.id.btnClearDBTasks:
                DataBaseTasks.clearDataBase(databaseTasks);
                editTask.setText("DataBase is DELETED!");
                break;

        }
    }



    private void setPrimaryViewsNewTask(String task, String date, String time, String repeat, String list)
    {
        editTask.setText(task);
        editDate.setText(date);
        editTime.setText(time);
        btnRepeat.setText(repeat);
        btnAddToList.setText(list);
    }




//////////////////////////////////////////////////////////
    public void readDBInNewThreat()
    {
        Thread t = new Thread(new Runnable() {
            public void run() {

            }});
    }
//////////////////////////////////////////////////////////









}


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

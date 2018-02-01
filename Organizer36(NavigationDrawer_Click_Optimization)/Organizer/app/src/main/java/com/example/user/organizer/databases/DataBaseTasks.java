package com.example.user.organizer.databases;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DataBaseTasks extends SQLiteOpenHelper {


    static char DIVIDER = '&';
    static char innerDIVIDER = '|';



    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "tasksDataBase";
    public static final String TABLE_TASKS = "tasks";

    /** поля таблицы для хранения */
    public static final String KEY_ID = "_id";
    public static final String KEY_TASK = "task";
    public static final String KEY_DATE = "date";
    public static final String KEY_TIME = "time";
    public static final String KEY_REPEAT = "repeat";
    public static final String KEY_LIST = "list";
    public static final String KEY_TIMEINMILLIS = "dateinmillis";

    // кол-во данных (которые выше), кроме id
    public static final int AMOUNT_DATA = 6;


    // формируем запрос для создания базы данных
    private static final String DATABASE_CREATE_TASKS = "create table " + TABLE_TASKS + "(" + KEY_ID
            + " integer primary key," + KEY_TASK + " text," + KEY_DATE + " text," + KEY_TIME + " text,"
            + KEY_REPEAT + " text," + KEY_LIST + " text," + KEY_TIMEINMILLIS + " text" + ")";



    public DataBaseTasks(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL - запрос, который создаёт таблицу
        db.execSQL(DATABASE_CREATE_TASKS);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Destroy old DataBase Table
        db.execSQL("drop table if exists " + TABLE_TASKS);

        // Create a new one with updated structure
        onCreate(db);

    }



    public static void clearDataBase(SQLiteDatabase db)
    {
        db.delete(DataBaseTasks.TABLE_TASKS, null, null);
    }










    /***
    public static void addToDBTasks(SQLiteDatabase db, String task, String date, String time, String repeat, String list) {

        class AddToDBTasks implements Runnable {

            SQLiteDatabase db;
            String task;
            String date;
            String time;
            String repeat;
            String list;

            public AddToDBTasks(SQLiteDatabase db, String task, String date, String time, String repeat, String list) {
                this.db = db;
                this.task = task;
                this.date = date;
                this.time = time;
                this.repeat = repeat;
                this.list = list;
            }

            @Override
            public void run() {
                // For DataBase
                // Этот Class используется для добавления новых строк в таблицу
                // каждый обьект этого класса - одна строка таблицы
                // (мол массив с именами столбцов и их значениями)
                ContentValues cv = new ContentValues();

                // add to DataBase (KEY_ID - заполняется автоматический)
                cv.put(DataBaseTasks.KEY_TASK, task);
                cv.put(DataBaseTasks.KEY_DATE, date);
                cv.put(DataBaseTasks.KEY_TIME, time);
                cv.put(DataBaseTasks.KEY_REPEAT, repeat);
                cv.put(DataBaseTasks.KEY_LIST, list);

                db.insert(DataBaseTasks.TABLE_TASKS, null, cv);
            }
        }

        Runnable r = new AddToDBTasks(db, task, date, time, repeat, list);
        new Thread(r).start();

    }*/

    public static void addToDBTasks(SQLiteDatabase db, String task, String date, String time, String repeat, String list, String timeInMillis) {

        class AddToDBTasks implements Runnable {

            SQLiteDatabase db;
            String task;
            String date;
            String time;
            String repeat;
            String list;
            String timeInMillis;

            public AddToDBTasks(SQLiteDatabase db, String task, String date, String time, String repeat, String list, String timeInMillis) {
                this.db = db;
                this.task = task;
                this.date = date;
                this.time = time;
                this.repeat = repeat;
                this.list = list;
                this.timeInMillis = timeInMillis;
            }

            @Override
            public void run() {
                // For DataBase
                // Этот Class используется для добавления новых строк в таблицу
                // каждый обьект этого класса - одна строка таблицы
                // (мол массив с именами столбцов и их значениями)
                ContentValues cv = new ContentValues();

                // add to DataBase (KEY_ID - заполняется автоматический)
                cv.put(DataBaseTasks.KEY_TASK, task);
                cv.put(DataBaseTasks.KEY_DATE, date);
                cv.put(DataBaseTasks.KEY_TIME, time);
                cv.put(DataBaseTasks.KEY_REPEAT, repeat);
                cv.put(DataBaseTasks.KEY_LIST, list);
                cv.put(DataBaseTasks.KEY_TIMEINMILLIS, timeInMillis);

                db.insert(DataBaseTasks.TABLE_TASKS, null, cv);
            }
        }

        Runnable r = new AddToDBTasks(db, task, date, time, repeat, list, timeInMillis);
        new Thread(r).start();

    }




    public static String[] readDataBase(SQLiteDatabase db) {

        class ReadDataBase implements Callable<String[]> {

            SQLiteDatabase db;
            public ReadDataBase(SQLiteDatabase db)
            {
                this.db = db;
            }

            @Override
            public String[] call() throws Exception {

                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);

                String strTasks = new String();

                int iterNewLists = 0;

                if(cursor.moveToFirst())
                {
                    int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int dateInMillisIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);

                    do {

                        strTasks += cursor.getString(taskIndex);
                        strTasks += innerDIVIDER;
                        strTasks += cursor.getString(dateIndex);
                        strTasks += innerDIVIDER;
                        strTasks += cursor.getString(timeIndex);
                        strTasks += innerDIVIDER;
                        strTasks += cursor.getString(repeatIndex);
                        strTasks += innerDIVIDER;
                        strTasks += cursor.getString(listIndex);
                        strTasks += innerDIVIDER;
                        strTasks += cursor.getString(dateInMillisIndex);

                        strTasks += DIVIDER;
                        iterNewLists++;

                    } while(cursor.moveToNext());
                }


                //////////////////// TRANSFORMING String TO String[] ////////////////////
                String[] strTasksArray = new String[iterNewLists];

                for(int i = 0; i < strTasksArray.length; i++)
                    strTasksArray[i] = "";

                int iter = 0;
                for(int q = 0; q < strTasks.length(); q++) {
                    if (strTasks.charAt(q) == DIVIDER) {
                        iter++;
                    }
                    else {
                        try {
                            strTasksArray[iter] += strTasks.charAt(q);
                        }
                        catch(Exception exception) {
                        }
                    }
                }
                //////////////////// TRANSFORMING String TO String[] ////////////////////
                cursor.close();

                return strTasksArray;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<String[]> futureString = exec.submit(new ReadDataBase(db));

        String[] result = {};
        try{
            result = futureString.get();
        }
        catch (Exception e) {

        }
        finally {
            exec.shutdown();
            return result;
        }

    }




    public static String[] getOnlyTasksByList(SQLiteDatabase db, String listName) {

        class GetOnlyTasksByList implements Callable<String[]> {

            SQLiteDatabase db;
            String listName;
            public GetOnlyTasksByList(SQLiteDatabase db, String listName)
            {
                this.db = db;
                this.listName = listName;
            }

            @Override
            public String[] call() throws Exception {
                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);

                String strTasks = "";

                int iterNewLists = 0;

                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);

                    do {

                        if(cursor.getString(listIndex).compareTo(listName) == 0)
                        {
                            strTasks += cursor.getString(taskIndex);
                            strTasks += DIVIDER;
                            iterNewLists++;
                        }


                    } while(cursor.moveToNext());
                }


                //////////////////// TRANSFORMING String TO String[] ////////////////////
                String[] strTasksArray = new String[iterNewLists];

                for(int i = 0; i < strTasksArray.length; i++)
                    strTasksArray[i] = "";

                int iter = 0;
                for(int q = 0; q < strTasks.length(); q++) {
                    if (strTasks.charAt(q) == DIVIDER) {
                        iter++;
                    } else {
                        try {
                            strTasksArray[iter] += strTasks.charAt(q);
                        }
                        catch(Exception exception) {
                        }
                    }
                }
                //////////////////// TRANSFORMING String TO String[] ////////////////////
                cursor.close();

                return strTasksArray;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<String[]> futureString = exec.submit(new GetOnlyTasksByList(db, listName));

        String[] result = {};
        try{
            result = futureString.get();
        }
        catch (Exception e) {

        }
        finally {
            exec.shutdown();
            return result;
        }

    }




    public static String[] getOnlyTasks(SQLiteDatabase db) {

        class GetOnlyTasks implements Callable<String[]> {

            SQLiteDatabase db;
            public GetOnlyTasks(SQLiteDatabase db)
            {
                this.db = db;
            }

            @Override
            public String[] call() throws Exception {
                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);

                String strTasks = "";

                int iterNewLists = 0;

                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    //int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);

                    do {

                        strTasks += cursor.getString(taskIndex);
                        strTasks += DIVIDER;
                        iterNewLists++;

                    } while(cursor.moveToNext());
                }


                //////////////////// TRANSFORMING String TO String[] ////////////////////
                String[] strTasksArray = new String[iterNewLists];

                for(int i = 0; i < strTasksArray.length; i++)
                    strTasksArray[i] = "";

                int iter = 0;
                for(int q = 0; q < strTasks.length(); q++) {
                    if (strTasks.charAt(q) == DIVIDER) {
                        iter++;
                    } else {
                        try {
                            strTasksArray[iter] += strTasks.charAt(q);
                        }
                        catch(Exception exception) {
                        }
                    }
                }
                //////////////////// TRANSFORMING String TO String[] ////////////////////
                cursor.close();

                return strTasksArray;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<String[]> futureString = exec.submit(new GetOnlyTasks(db));

        String[] result = {};
        try{
            result = futureString.get();
        }
        catch (Exception e) {

        }
        finally {
            exec.shutdown();
            return result;
        }

    }




    public static void renameTasksByList(SQLiteDatabase db, String oldName, String newName) {

        class RenameTasksByList implements Runnable {

            SQLiteDatabase db;
            String oldName;
            String newName;

            public RenameTasksByList(SQLiteDatabase db, String oldName, String newName) {
                this.db = db;
                this.oldName = oldName;
                this.newName = newName;
            }

            @Override
            public void run() {
                // создаем объект для данных
                ContentValues cv = new ContentValues();

                // подготовим значения для обновления
                cv.put(KEY_LIST, newName);


                //////////////////////////////////////////////////////////////////// find list with Old Name
                Cursor cursor = db.query(TABLE_TASKS, null, null, null, null, null, null);

                int idIndex = 0;
                int nameIndex = 0;

                int someId = 0;

                if(cursor.moveToFirst())
                {
                    idIndex = cursor.getColumnIndex(KEY_ID);
                    nameIndex = cursor.getColumnIndex(KEY_LIST);

                    String someName = "";

                    do {
                        someId = cursor.getInt(idIndex);
                        someName = cursor.getString(nameIndex);
                        if(someName.compareTo(oldName) == 0)
                        {
                            // обновляем по id
                            db.update(TABLE_TASKS, cv, KEY_ID + "=" + someId, null);
                        }
                    } while(cursor.moveToNext());
                }
                ////////////////////////////////////////////////////////////////////
                cursor.close();
            }
        }

        Runnable r = new RenameTasksByList(db, oldName, newName);
        new Thread(r).start();

    }




    public static String[] getCertainTask(SQLiteDatabase db, String task) {

        class GetCertainTask implements Callable<String[]> {

            SQLiteDatabase db;
            String task;
            public GetCertainTask(SQLiteDatabase db, String task)
            {
                this.db = db;
                this.task = task;
            }

            @Override
            public String[] call() throws Exception {
                String[] data = new String[AMOUNT_DATA+1]; // +1 - id

                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);

                if(cursor.moveToFirst())
                {
                    int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillisIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);

                    do {


                        if(cursor.getString(taskIndex).compareTo(task) == 0)
                        {
                            data[0] = cursor.getString(taskIndex);

                            data[1] = cursor.getString(dateIndex);

                            data[2] = cursor.getString(timeIndex);

                            data[3] = cursor.getString(repeatIndex);

                            data[4] = cursor.getString(listIndex);

                            data[5] = cursor.getString(idIndex);

                            data[6] = cursor.getString(timeInMillisIndex);
                        }

                    } while(cursor.moveToNext());
                }

                cursor.close();
                return data;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<String[]> futureString = exec.submit(new GetCertainTask(db, task));

        String[] result = {};
        try{
            result = futureString.get();
        }
        catch (Exception e) {

        }
        finally {
            exec.shutdown();
            return result;
        }

    }



    public static boolean hasTask(SQLiteDatabase db, String task) {

        class HasTask implements Callable<String> {

            SQLiteDatabase db;
            String task;
            public HasTask(SQLiteDatabase db, String task)
            {
                this.db = db;
                this.task = task;
            }

            @Override
            public String call() throws Exception {
                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);

                if(cursor.moveToFirst())
                {

                    do {

                        int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);

                        if(cursor.getString(taskIndex).compareTo(task) == 0)
                        {
                            return "true";
                        }

                    } while(cursor.moveToNext());

                }

                cursor.close();
                return "false";
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<String> futureString = exec.submit(new HasTask(db, task));

        String result = "";
        try{
            result = futureString.get();
        }
        catch (Exception e) {

        }
        finally {
            exec.shutdown();
        }

        if(result.compareTo("true") == 0) return true;
        else return false;

    }




    public static void updateTaskById(SQLiteDatabase db, String[] newData, String taskID) {

        class UpdateTaskById implements Runnable {

            SQLiteDatabase db;
            String[] newData;
            String taskID;

            public UpdateTaskById(SQLiteDatabase db, String[] newData, String taskID) {
                this.db = db;
                this.newData = newData;
                this.taskID = taskID;
            }

            @Override
            public void run() {

                String task = newData[0];
                String date= newData[1];
                String time = newData[2];
                String repeat = newData[3];
                String list = newData[4];
                String timeInMillis = newData[5];



                // создаем объект для данных
                ContentValues cv = new ContentValues();

                // подготовим значения для обновления
                cv.put(KEY_TASK, task);
                cv.put(KEY_DATE, date);
                cv.put(KEY_TIME, time);
                cv.put(KEY_REPEAT, repeat);
                cv.put(KEY_LIST, list);
                cv.put(KEY_TIMEINMILLIS, timeInMillis);


                //////////////////////////////////////////////////////////////////// find list with Old Name
                Cursor cursor = db.query(TABLE_TASKS, null, null, null, null, null, null);

                if(cursor.moveToFirst())
                {
                    int idIndex = cursor.getColumnIndex(KEY_ID);

                    String someId = "";

                    do {

                        someId = cursor.getString(idIndex);

                        if(someId.compareTo(taskID) == 0)
                        {
                            // обновляем по id
                            db.update(TABLE_TASKS, cv, KEY_ID + "=" + someId, null);
                        }
                    } while(cursor.moveToNext());
                }
                ////////////////////////////////////////////////////////////////////
                cursor.close();
            }
        }

        Runnable r = new UpdateTaskById(db, newData, taskID);
        new Thread(r).start();

    }




    public static void deleteCertainTask(SQLiteDatabase db, String task) {

        class DeleteCertainTask implements Runnable {

            SQLiteDatabase db;
            String task;

            public DeleteCertainTask(SQLiteDatabase db, String task) {
                this.db = db;
                this.task = task;
            }

            @Override
            public void run() {
                Cursor cursor = db.query(TABLE_TASKS, null, null, null, null, null, null);

                int idIndex = 0;
                int taskIndex = 0;

                int someId = 0;

                if(cursor.moveToFirst())
                {
                    idIndex = cursor.getColumnIndex(KEY_ID);
                    taskIndex = cursor.getColumnIndex(KEY_TASK);

                    String someTask = "";

                    do {
                        someId = cursor.getInt(idIndex);
                        someTask = cursor.getString(taskIndex);

                        if(someTask.compareTo(task) == 0)
                        {
                            db.delete(TABLE_TASKS, KEY_ID + "=" + someId, null);
                            break;
                        }
                    } while(cursor.moveToNext());
                }

                cursor.close();
            }
        }

        Runnable r = new DeleteCertainTask(db, task);
        new Thread(r).start();

    }




    public static void deleteTasksByList(SQLiteDatabase db, String listname) {

        class DeleteTasksByList implements Runnable {

            SQLiteDatabase db;
            String listname;

            public DeleteTasksByList(SQLiteDatabase db, String listname) {
                this.db = db;
                this.listname = listname;
            }

            @Override
            public void run() {
                Cursor cursor = db.query(TABLE_TASKS, null, null, null, null, null, null);

                int idIndex = 0;
                int listIndex = 0;

                int someId = 0;

                if(cursor.moveToFirst())
                {
                    idIndex = cursor.getColumnIndex(KEY_ID);
                    listIndex = cursor.getColumnIndex(KEY_LIST);

                    String someList = "";

                    do {
                        someId = cursor.getInt(idIndex);
                        someList = cursor.getString(listIndex);

                        if(someList.compareTo(listname) == 0)
                        {
                            db.delete(TABLE_TASKS, KEY_ID + "=" + someId, null);
                        }
                    } while(cursor.moveToNext());
                }

                cursor.close();
            }
        }

        Runnable r = new DeleteTasksByList(db, listname);
        new Thread(r).start();

    }

}

package first.project.nikzhebindev.organizer.databases;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import first.project.nikzhebindev.organizer.MainMenu;
import first.project.nikzhebindev.organizer.NewTask;
import first.project.nikzhebindev.organizer.R;
import first.project.nikzhebindev.organizer.notifications.MyReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Data Base for Tasks
 */

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











    public static void addToDBTasks(SQLiteDatabase db, String task, String date, String time, String repeat, String list, String timeInMillis, Context context) {

        class AddToDBTasks implements Runnable {

            SQLiteDatabase db;
            String task;
            String date;
            String time;
            String repeat;
            String list;
            String timeInMillis;
            Context context;

            public AddToDBTasks(SQLiteDatabase db, String task, String date, String time, String repeat, String list, String timeInMillis, Context context) {
                this.db = db;
                this.task = task;
                this.date = date;
                this.time = time;
                this.repeat = repeat;
                this.list = list;
                this.timeInMillis = timeInMillis;
                this.context = context;
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

                if(Long.valueOf(timeInMillis) > 1) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.valueOf(timeInMillis));

                    String id = getCertainTaskID(db, task);

                    sendNotification(calendar, id, repeat, task, context);
                }
            }


            private void sendNotification(Calendar calendar, String id, String repeat, String message, Context context) {
                // Calendar calendar, String repeating
                // and then in your activity set the alarm manger to start the broadcast receiver
                // at a specific time and use AlarmManager setRepeating method to repeat it this
                // example bellow will repeat it every day.


                //String title = getString(R.string.task_at) + " " + editTime.getEditableText().toString();
                //String message = editTask.getEditableText().toString();


                Intent notifyIntent = new Intent(context, MyReceiver.class);
                //notifyIntent.putExtra("NotificationTitle", title);
                notifyIntent.putExtra("NotificationMessage", message);
                notifyIntent.putExtra("id_Notification", id);



                int newID = 0;
                try{
                    newID = Integer.decode(id);
                }
                catch(Exception e){
                    newID = Integer.getInteger(id);
                }
                // ONE REAQUEST CODE FOR ALL.... That's bad I think !!!!!!!!!!!!!!!!!!
                PendingIntent pendingIntent = PendingIntent.getBroadcast
                        (context, (newID+850), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);



                int timeBefore = 0;
                SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(context);
                if(myPreference.getString("taskNotification", "0")
                        .compareTo(context.getResources().getStringArray(R.array.task_notification)[0]) == 0){      // 0 mins
                    timeBefore = 0;
                }
                else if(myPreference.getString("taskNotification", "0")
                        .compareTo(context.getResources().getStringArray(R.array.task_notification)[1]) == 0){ // 5 mins
                    timeBefore = 60000 * 5;
                }
                else if(myPreference.getString("taskNotification", "0")
                        .compareTo(context.getResources().getStringArray(R.array.task_notification)[2]) == 0){ // 15 mins
                    timeBefore = 60000 * 15;
                }
                else if(myPreference.getString("taskNotification", "0")
                        .compareTo(context.getResources().getStringArray(R.array.task_notification)[3]) == 0){ // 30 mins
                    timeBefore = 60000 * 30;
                }
                else if(myPreference.getString("taskNotification", "0")
                        .compareTo(context.getResources().getStringArray(R.array.task_notification)[4]) == 0){ // 1 hour
                    timeBefore = 60000 * 60;
                }
                else if(myPreference.getString("taskNotification", "0")
                        .compareTo(context.getResources().getStringArray(R.array.task_notification)[5]) == 0){ // 2 hours
                    timeBefore = 60000 * 60 * 2;
                }
                else if(myPreference.getString("taskNotification", "0")
                        .compareTo(context.getResources().getStringArray(R.array.task_notification)[6]) == 0){ // 3 hours
                    timeBefore = 60000 * 60 * 3;
                }


                try {
                    if (repeat.compareTo(DataBaseLists.listRepeatNames[0]) == 0) { // Не повторять
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - timeBefore, pendingIntent);
                    } else if (repeat.compareTo(DataBaseLists.listRepeatNames[1]) == 0) { // Каждый час
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - timeBefore,
                                AlarmManager.INTERVAL_HOUR, pendingIntent);
                    } else if (repeat.compareTo(DataBaseLists.listRepeatNames[2]) == 0) { // Ежедневно
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - timeBefore,
                                AlarmManager.INTERVAL_DAY, pendingIntent);
                    } else if (repeat.compareTo(DataBaseLists.listRepeatNames[3]) == 0) { // Еженедельно
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - timeBefore,
                                AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                    } else if (repeat.compareTo(DataBaseLists.listRepeatNames[4]) == 0) { // Ежегодно
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - timeBefore,
                                AlarmManager.INTERVAL_DAY * 365, pendingIntent);
                    }
                } catch(Exception e) {
                    //
                }
            }


        }

        Runnable r = new AddToDBTasks(db, task, date, time, repeat, list, timeInMillis, context);
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
                            //
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
            //
        }
        finally {
            exec.shutdown();
        }

        return result;
    }




    public static Integer getAmountOfTasksByList(SQLiteDatabase db, String listName) {

        class GetOnlyTasksByList implements Callable<Integer> {

            SQLiteDatabase db;
            String listName;
            public GetOnlyTasksByList(SQLiteDatabase db, String listName)
            {
                this.db = db;
                this.listName = listName;
            }

            @Override
            public Integer call() throws Exception {
                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);

                Integer amountOfTasks = 0;


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
                            amountOfTasks++;
                        }


                    } while(cursor.moveToNext());
                }


                cursor.close();

                return amountOfTasks;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<Integer> futureString = exec.submit(new GetOnlyTasksByList(db, listName));

        Integer result = 0;
        try{
            result = futureString.get();
        }
        catch (Exception e) {
            //
        }
        finally {
            exec.shutdown();
        }
        return result;
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
    public static ArrayList<String> getOnlyTasksByListWithArrayList(SQLiteDatabase db, String listName) {

        class GetOnlyTasksByList implements Callable<ArrayList<String>> {

            SQLiteDatabase db;
            String listName;
            public GetOnlyTasksByList(SQLiteDatabase db, String listName)
            {
                this.db = db;
                this.listName = listName;
            }

            @Override
            public ArrayList<String> call() throws Exception {
                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);

                ArrayList<String> strTasks = new ArrayList<>();


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
                            strTasks.add(cursor.getString(taskIndex));
                        }


                    } while(cursor.moveToNext());
                }


                cursor.close();

                return strTasks;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<ArrayList<String>> futureString = exec.submit(new GetOnlyTasksByList(db, listName));

        ArrayList<String> result = new ArrayList<>();
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
    public static ArrayList<String> getSortedTasksByListWithArrayList(SQLiteDatabase db, String listName) {

        class GetOnlyTasksByList implements Callable<ArrayList<String>> {

            SQLiteDatabase db;
            String listName;
            public GetOnlyTasksByList(SQLiteDatabase db, String listName)
            {
                this.db = db;
                this.listName = listName;
            }

            @Override
            public ArrayList<String> call() throws Exception {
                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);

                ArrayList<Long> longTasks = new ArrayList<>();
                ArrayList<String> strTasks = new ArrayList<>();

                /*************************** заполним по возрастанию задачи по времени в миллесекундах */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {
                        if(cursor.getString(listIndex).compareTo(listName) == 0) {

                            if (cursor.getString(timeInMillis).compareTo("0") != 0) {
                                if (Long.valueOf(cursor.getString(timeInMillis)) > 0) {
                                    longTasks.add(Long.valueOf(cursor.getString(timeInMillis)));
                                }
                            }

                        }
                    } while(cursor.moveToNext());
                }

                /** Sort ArrayList<Long> */

                Collections.sort(longTasks);


                /*************************** закидываем задачи по УЖЕ ОТСОРТИРОВАННОМУ СПИСКУ Лонг значений*/

                for(int i = 0; i < longTasks.size(); i++) {

                    if (cursor.moveToFirst()) {
                        //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                        int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                        //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                        //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                        //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                        int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                        int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                        do {
                            if(cursor.getString(listIndex).compareTo(listName) == 0) {

                                if (cursor.getString(timeInMillis).compareTo("0") != 0) {

                                    if (Long.valueOf(cursor.getString(timeInMillis)).equals(longTasks.get(i))) {

                                        strTasks.add(cursor.getString(taskIndex));

                                        break;
                                    }

                                }

                            }
                        } while (cursor.moveToNext());
                    }

                }


                /*************************** сначала закидываем просроченные задачи */
                /*if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);

                    long curTimeInMillis = System.currentTimeMillis();

                    do {
                        if(cursor.getString(listIndex).compareTo(listName) == 0) {

                            long fixedTime = 0;

                            try {
                                fixedTime = Long.valueOf(cursor.getString(timeInMillis));
                            } catch (Exception e) {
                            }

                            if (fixedTime != 0)
                                if (fixedTime < curTimeInMillis)
                                    strTasks.add(cursor.getString(taskIndex));

                        }
                    } while(cursor.moveToNext());
                }*/


                /*************************** теперь закидываем НЕ просроченные задачи */
                /*if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);

                    long curTimeInMillis = System.currentTimeMillis();

                    do {
                        if(cursor.getString(listIndex).compareTo(listName) == 0) {

                            long fixedTime = 0;

                            try {
                                fixedTime = Long.valueOf(cursor.getString(timeInMillis));
                            } catch (Exception e) {
                            }

                            if (fixedTime != 0)
                                if (fixedTime > curTimeInMillis)
                                    strTasks.add(cursor.getString(taskIndex));

                        }
                    } while(cursor.moveToNext());
                }*/


                /*************************** и наконец закидываем задачи, которые не установлены */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {
                        if(cursor.getString(listIndex).compareTo(listName) == 0) {

                            if (cursor.getString(timeInMillis).compareTo("0") == 0)
                                strTasks.add(cursor.getString(taskIndex));

                        }
                    } while(cursor.moveToNext());
                }


                cursor.close();

                return strTasks;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<ArrayList<String>> futureString = exec.submit(new GetOnlyTasksByList(db, listName));

        ArrayList<String> result = new ArrayList<>();
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
    public static ArrayList<Long> getSortedTimeInMillisByListWithArrayList(SQLiteDatabase db, String listName) {

        class GetOnlyTasksByList implements Callable<ArrayList<Long>> {

            SQLiteDatabase db;
            String listName;
            public GetOnlyTasksByList(SQLiteDatabase db, String listName)
            {
                this.db = db;
                this.listName = listName;
            }

            @Override
            public ArrayList<Long> call() throws Exception {
                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);

                ArrayList<Long> longTasks = new ArrayList<>();


                /*************************** заполним по возрастанию задачи по времени в миллесекундах */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {
                        if(cursor.getString(listIndex).compareTo(listName) == 0) {

                            if (cursor.getString(timeInMillis).compareTo("0") != 0) {
                                if (Long.valueOf(cursor.getString(timeInMillis)) > 0) {
                                    longTasks.add(Long.valueOf(cursor.getString(timeInMillis)));
                                }
                            }

                        }
                    } while(cursor.moveToNext());
                }

                /** Sort ArrayList<Long> */

                Collections.sort(longTasks);




                /*************************** и наконец закидываем задачи, которые не установлены */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {
                        if(cursor.getString(listIndex).compareTo(listName) == 0) {

                            if (cursor.getString(timeInMillis).compareTo("0") == 0)
                                longTasks.add(0L);

                        }
                    } while(cursor.moveToNext());
                }


                cursor.close();

                return longTasks;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<ArrayList<Long>> futureString = exec.submit(new GetOnlyTasksByList(db, listName));

        ArrayList<Long> result = new ArrayList<>();
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
    public static ArrayList<String> getSortedDateByListWithArrayList(SQLiteDatabase db, String listName) {

        class GetOnlyTasksByList implements Callable<ArrayList<String>> {

            SQLiteDatabase db;
            String listName;
            public GetOnlyTasksByList(SQLiteDatabase db, String listName)
            {
                this.db = db;
                this.listName = listName;
            }

            @Override
            public ArrayList<String> call() throws Exception {
                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);

                ArrayList<Long> longTasks = new ArrayList<>();
                ArrayList<String> strDate = new ArrayList<>();

                /*************************** заполним по возрастанию задачи по времени в миллесекундах */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {
                        if(cursor.getString(listIndex).compareTo(listName) == 0) {

                            if (cursor.getString(timeInMillis).compareTo("0") != 0) {
                                if (Long.valueOf(cursor.getString(timeInMillis)) > 0) {
                                    longTasks.add(Long.valueOf(cursor.getString(timeInMillis)));
                                }
                            }

                        }
                    } while(cursor.moveToNext());
                }

                /** Sort ArrayList<Long> */

                Collections.sort(longTasks);


                /*************************** закидываем задачи по УЖЕ ОТСОРТИРОВАННОМУ СПИСКУ Лонг значений*/

                for(int i = 0; i < longTasks.size(); i++) {

                    if (cursor.moveToFirst()) {
                        //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                        //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                        int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                        //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                        //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                        int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                        int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                        do {
                            if(cursor.getString(listIndex).compareTo(listName) == 0) {

                                if (cursor.getString(timeInMillis).compareTo("0") != 0) {

                                    if (Long.valueOf(cursor.getString(timeInMillis)).equals(longTasks.get(i))) {

                                        strDate.add(cursor.getString(dateIndex));

                                        break;
                                    }

                                }

                            }
                        } while (cursor.moveToNext());
                    }

                }





                /*************************** и наконец закидываем задачи, которые не установлены */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {
                        if(cursor.getString(listIndex).compareTo(listName) == 0) {

                            if (cursor.getString(timeInMillis).compareTo("0") == 0)
                                strDate.add(cursor.getString(dateIndex));

                        }
                    } while(cursor.moveToNext());
                }


                cursor.close();

                return strDate;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<ArrayList<String>> futureString = exec.submit(new GetOnlyTasksByList(db, listName));

        ArrayList<String> result = new ArrayList<>();
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
    public static ArrayList<String> getSortedTimeByListWithArrayList(SQLiteDatabase db, String listName) {

        class GetOnlyTasksByList implements Callable<ArrayList<String>> {

            SQLiteDatabase db;
            String listName;
            public GetOnlyTasksByList(SQLiteDatabase db, String listName)
            {
                this.db = db;
                this.listName = listName;
            }

            @Override
            public ArrayList<String> call() throws Exception {
                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);

                ArrayList<Long> longTasks = new ArrayList<>();
                ArrayList<String> strTime = new ArrayList<>();

                /*************************** заполним по возрастанию задачи по времени в миллесекундах */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {
                        if(cursor.getString(listIndex).compareTo(listName) == 0) {

                            if (cursor.getString(timeInMillis).compareTo("0") != 0) {
                                if (Long.valueOf(cursor.getString(timeInMillis)) > 0) {
                                    longTasks.add(Long.valueOf(cursor.getString(timeInMillis)));
                                }
                            }

                        }
                    } while(cursor.moveToNext());
                }

                /** Sort ArrayList<Long> */

                Collections.sort(longTasks);


                /*************************** закидываем задачи по УЖЕ ОТСОРТИРОВАННОМУ СПИСКУ Лонг значений*/

                for(int i = 0; i < longTasks.size(); i++) {

                    if (cursor.moveToFirst()) {
                        //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                        //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                        //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                        int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                        //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                        int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                        int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                        do {
                            if(cursor.getString(listIndex).compareTo(listName) == 0) {

                                if (cursor.getString(timeInMillis).compareTo("0") != 0) {

                                    if (Long.valueOf(cursor.getString(timeInMillis)).equals(longTasks.get(i))) {

                                        strTime.add(cursor.getString(timeIndex));

                                        break;
                                    }

                                }

                            }
                        } while (cursor.moveToNext());
                    }

                }





                /*************************** и наконец закидываем задачи, которые не установлены */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {
                        if(cursor.getString(listIndex).compareTo(listName) == 0) {

                            if (cursor.getString(timeInMillis).compareTo("0") == 0)
                                strTime.add(cursor.getString(timeIndex));

                        }
                    } while(cursor.moveToNext());
                }


                cursor.close();

                return strTime;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<ArrayList<String>> futureString = exec.submit(new GetOnlyTasksByList(db, listName));

        ArrayList<String> result = new ArrayList<>();
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
    public static ArrayList<String> getSortedRepeatByListWithArrayList(SQLiteDatabase db, String listName) {

        class GetOnlyTasksByList implements Callable<ArrayList<String>> {

            SQLiteDatabase db;
            String listName;
            public GetOnlyTasksByList(SQLiteDatabase db, String listName)
            {
                this.db = db;
                this.listName = listName;
            }

            @Override
            public ArrayList<String> call() throws Exception {
                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);

                ArrayList<Long> longTasks = new ArrayList<>();
                ArrayList<String> strRepeat = new ArrayList<>();

                /*************************** заполним по возрастанию задачи по времени в миллесекундах */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {
                        if(cursor.getString(listIndex).compareTo(listName) == 0) {

                            if (cursor.getString(timeInMillis).compareTo("0") != 0) {
                                if (Long.valueOf(cursor.getString(timeInMillis)) > 0) {
                                    longTasks.add(Long.valueOf(cursor.getString(timeInMillis)));
                                }
                            }

                        }
                    } while(cursor.moveToNext());
                }

                /** Sort ArrayList<Long> */

                Collections.sort(longTasks);


                /*************************** закидываем задачи по УЖЕ ОТСОРТИРОВАННОМУ СПИСКУ Лонг значений*/

                for(int i = 0; i < longTasks.size(); i++) {

                    if (cursor.moveToFirst()) {
                        //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                        //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                        //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                        //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                        int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                        int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                        int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                        do {
                            if(cursor.getString(listIndex).compareTo(listName) == 0) {

                                if (cursor.getString(timeInMillis).compareTo("0") != 0) {

                                    if (Long.valueOf(cursor.getString(timeInMillis)).equals(longTasks.get(i))) {

                                        strRepeat.add(cursor.getString(repeatIndex));

                                        break;
                                    }

                                }

                            }
                        } while (cursor.moveToNext());
                    }

                }





                /*************************** и наконец закидываем задачи, которые не установлены */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {
                        if(cursor.getString(listIndex).compareTo(listName) == 0) {

                            if (cursor.getString(timeInMillis).compareTo("0") == 0)
                                strRepeat.add(cursor.getString(repeatIndex));

                        }
                    } while(cursor.moveToNext());
                }


                cursor.close();

                return strRepeat;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<ArrayList<String>> futureString = exec.submit(new GetOnlyTasksByList(db, listName));

        ArrayList<String> result = new ArrayList<>();
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
    public static ArrayList<String> getSortedListByListWithArrayList(SQLiteDatabase db, String listName) {

        class GetOnlyTasksByList implements Callable<ArrayList<String>> {

            SQLiteDatabase db;
            String listName;
            public GetOnlyTasksByList(SQLiteDatabase db, String listName)
            {
                this.db = db;
                this.listName = listName;
            }

            @Override
            public ArrayList<String> call() throws Exception {
                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);

                ArrayList<Long> longTasks = new ArrayList<>();
                ArrayList<String> strRepeat = new ArrayList<>();

                /*************************** заполним по возрастанию задачи по времени в миллесекундах */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {
                        if(cursor.getString(listIndex).compareTo(listName) == 0) {

                            if (cursor.getString(timeInMillis).compareTo("0") != 0) {
                                if (Long.valueOf(cursor.getString(timeInMillis)) > 0) {
                                    longTasks.add(Long.valueOf(cursor.getString(timeInMillis)));
                                }
                            }

                        }
                    } while(cursor.moveToNext());
                }

                /** Sort ArrayList<Long> */

                Collections.sort(longTasks);


                /*************************** закидываем задачи по УЖЕ ОТСОРТИРОВАННОМУ СПИСКУ Лонг значений*/

                for(int i = 0; i < longTasks.size(); i++) {

                    if (cursor.moveToFirst()) {
                        //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                        //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                        //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                        //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                        //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                        int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                        int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                        do {
                            if(cursor.getString(listIndex).compareTo(listName) == 0) {

                                if (cursor.getString(timeInMillis).compareTo("0") != 0) {

                                    if (Long.valueOf(cursor.getString(timeInMillis)).equals(longTasks.get(i))) {

                                        strRepeat.add(cursor.getString(listIndex));

                                        break;
                                    }

                                }

                            }
                        } while (cursor.moveToNext());
                    }

                }





                /*************************** и наконец закидываем задачи, которые не установлены */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {
                        if(cursor.getString(listIndex).compareTo(listName) == 0) {

                            if (cursor.getString(timeInMillis).compareTo("0") == 0)
                                strRepeat.add(cursor.getString(listIndex));

                        }
                    } while(cursor.moveToNext());
                }


                cursor.close();

                return strRepeat;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<ArrayList<String>> futureString = exec.submit(new GetOnlyTasksByList(db, listName));

        ArrayList<String> result = new ArrayList<>();
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

    public static DataArrayList getSortedAllDataByListWithArrayList(SQLiteDatabase db, String listName) {

        class GetOnlyTasks implements Callable<DataArrayList> {

            SQLiteDatabase db;
            String listName;
            public GetOnlyTasks(SQLiteDatabase db, String listName)
            {
                this.db = db;
                this.listName = listName;
            }

            @Override
            public DataArrayList call() throws Exception {
                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);




                DataArrayList dataArrayList;




                ArrayList<Long> longTasks = getSortedTimeInMillisByListWithArrayList(db, listName);

                ArrayList<String> arrTasks = getSortedTasksByListWithArrayList(db, listName);
                ArrayList<String> arrDate = getSortedDateByListWithArrayList(db, listName);
                ArrayList<String> arrTime = getSortedTimeByListWithArrayList(db, listName);
                ArrayList<String> arrRepeat = getSortedRepeatByListWithArrayList(db, listName);
                ArrayList<String> arrList = getSortedListByListWithArrayList(db, listName);



                dataArrayList = new DataArrayList(arrTasks, longTasks, arrDate, arrTime, arrRepeat, arrList);



                cursor.close();

                return dataArrayList;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<DataArrayList> futureString = exec.submit(new GetOnlyTasks(db, listName));

        DataArrayList result = null;
        try{
            result = futureString.get();
        }
        catch (Exception e) {

        }
        finally {
            exec.shutdown();

        }

        return result;

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
    public static ArrayList<String> getOnlyTasksWithArrayList(SQLiteDatabase db) {

        class GetOnlyTasks implements Callable<ArrayList<String>> {

            SQLiteDatabase db;
            public GetOnlyTasks(SQLiteDatabase db)
            {
                this.db = db;
            }

            @Override
            public ArrayList<String> call() throws Exception {
                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);

                ArrayList<String> strTasks = new ArrayList<>();


                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    //int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);

                    do {

                        strTasks.add(cursor.getString(taskIndex));

                    } while(cursor.moveToNext());
                }



                cursor.close();

                return strTasks;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<ArrayList<String>> futureString = exec.submit(new GetOnlyTasks(db));

        ArrayList<String> result = new ArrayList<>();
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
    public static ArrayList<String> getSortedTasksWithArrayList(SQLiteDatabase db) {

        class GetOnlyTasks implements Callable<ArrayList<String>> {

            SQLiteDatabase db;
            public GetOnlyTasks(SQLiteDatabase db)
            {
                this.db = db;
            }

            @Override
            public ArrayList<String> call() throws Exception {
                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);



                ArrayList<Long> longTasks = new ArrayList<>();
                ArrayList<String> strTasks = new ArrayList<>();



                /*************************** заполним по возрастанию задачи по времени в миллесекундах */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    //int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {


                        if(cursor.getString(timeInMillis).compareTo("0") != 0) {
                            if (Long.valueOf(cursor.getString(timeInMillis)) > 0) {
                                longTasks.add(Long.valueOf(cursor.getString(timeInMillis)));
                            }
                        }


                    } while(cursor.moveToNext());
                }

                /** Sort ArrayList<Long> */

                Collections.sort(longTasks);


                /*************************** закидываем задачи по УЖЕ ОТСОРТИРОВАННОМУ СПИСКУ Лонг значений*/

                for(int i = 0; i < longTasks.size(); i++) {

                    if (cursor.moveToFirst()) {
                        //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                        int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                        //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                        //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                        //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                        //int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                        int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                        do {


                            if (cursor.getString(timeInMillis).compareTo("0") != 0) {

                                if (Long.valueOf(cursor.getString(timeInMillis)).equals(longTasks.get(i))) {

                                    strTasks.add(cursor.getString(taskIndex));

                                    break;
                                }

                            }


                        } while (cursor.moveToNext());
                    }

                }




                /*************************** и наконец закидываем задачи, которые не установлены */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    //int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {

                        if(cursor.getString(timeInMillis).compareTo("0") == 0)
                            strTasks.add(cursor.getString(taskIndex));


                    } while(cursor.moveToNext());
                }




                cursor.close();

                return strTasks;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<ArrayList<String>> futureString = exec.submit(new GetOnlyTasks(db));

        ArrayList<String> result = new ArrayList<>();
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
    public static ArrayList<Long> getSortedTimeInMillisWithArrayList(SQLiteDatabase db) {

        class GetOnlyTasks implements Callable<ArrayList<Long>> {

            SQLiteDatabase db;
            public GetOnlyTasks(SQLiteDatabase db)
            {
                this.db = db;
            }

            @Override
            public ArrayList<Long> call() throws Exception {
                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);

                ArrayList<Long> longTasks = new ArrayList<>();

                /*************************** заполним по возрастанию задачи по времени в миллесекундах */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    //int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {


                        if(cursor.getString(timeInMillis).compareTo("0") != 0) {
                            if (Long.valueOf(cursor.getString(timeInMillis)) > 0) {
                                longTasks.add(Long.valueOf(cursor.getString(timeInMillis)));
                            }
                        }


                    } while(cursor.moveToNext());
                }

                /** Sort ArrayList<Long> */

                Collections.sort(longTasks);




                /**********************  и наконец закидываем задачи, в которых не установленно время */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    //int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {

                        if(cursor.getString(timeInMillis).compareTo("0") == 0)
                            longTasks.add(0L);


                    } while(cursor.moveToNext());
                }




                cursor.close();

                return longTasks;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<ArrayList<Long>> futureString = exec.submit(new GetOnlyTasks(db));

        ArrayList<Long> result = new ArrayList<>();
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
    public static ArrayList<String> getSortedDateWithArrayList(SQLiteDatabase db) {

        class GetOnlyTasks implements Callable<ArrayList<String>> {

            SQLiteDatabase db;
            public GetOnlyTasks(SQLiteDatabase db)
            {
                this.db = db;
            }

            @Override
            public ArrayList<String> call() throws Exception {
                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);

                ArrayList<Long> longTasks = new ArrayList<>();
                ArrayList<String> strDate = new ArrayList<>();

                /*************************** заполним по возрастанию задачи по времени в миллесекундах */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    //int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {


                        if(cursor.getString(timeInMillis).compareTo("0") != 0) {
                            if (Long.valueOf(cursor.getString(timeInMillis)) > 0) {
                                longTasks.add(Long.valueOf(cursor.getString(timeInMillis)));
                            }
                        }


                    } while(cursor.moveToNext());
                }

                /** Sort ArrayList<Long> */

                Collections.sort(longTasks);


                /*************************** закидываем задачи по УЖЕ ОТСОРТИРОВАННОМУ СПИСКУ Лонг значений*/

                for(int i = 0; i < longTasks.size(); i++) {

                    if (cursor.moveToFirst()) {
                        //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                        //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                        int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                        //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                        //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                        //int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                        int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                        do {


                            if (cursor.getString(timeInMillis).compareTo("0") != 0) {

                                if (Long.valueOf(cursor.getString(timeInMillis)).equals(longTasks.get(i))) {

                                    strDate.add(cursor.getString(dateIndex));

                                    break;
                                }

                            }


                        } while (cursor.moveToNext());
                    }

                }




                /*************************** и наконец закидываем задачи, которые не установлены */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    //int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {

                        if(cursor.getString(timeInMillis).compareTo("0") == 0)
                            strDate.add(cursor.getString(dateIndex));


                    } while(cursor.moveToNext());
                }




                cursor.close();

                return strDate;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<ArrayList<String>> futureString = exec.submit(new GetOnlyTasks(db));

        ArrayList<String> result = new ArrayList<>();
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
    public static ArrayList<String> getSortedTimeWithArrayList(SQLiteDatabase db) {

        class GetOnlyTasks implements Callable<ArrayList<String>> {

            SQLiteDatabase db;
            public GetOnlyTasks(SQLiteDatabase db)
            {
                this.db = db;
            }

            @Override
            public ArrayList<String> call() throws Exception {
                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);

                ArrayList<Long> longTasks = new ArrayList<>();
                ArrayList<String> strTime = new ArrayList<>();

                /*************************** заполним по возрастанию задачи по времени в миллесекундах */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    //int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {


                        if(cursor.getString(timeInMillis).compareTo("0") != 0) {
                            if (Long.valueOf(cursor.getString(timeInMillis)) > 0) {
                                longTasks.add(Long.valueOf(cursor.getString(timeInMillis)));
                            }
                        }


                    } while(cursor.moveToNext());
                }

                /** Sort ArrayList<Long> */

                Collections.sort(longTasks);


                /*************************** закидываем задачи по УЖЕ ОТСОРТИРОВАННОМУ СПИСКУ Лонг значений*/

                for(int i = 0; i < longTasks.size(); i++) {

                    if (cursor.moveToFirst()) {
                        //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                        //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                        //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                        int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                        //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                        //int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                        int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                        do {


                            if (cursor.getString(timeInMillis).compareTo("0") != 0) {

                                if (Long.valueOf(cursor.getString(timeInMillis)).equals(longTasks.get(i))) {

                                    strTime.add(cursor.getString(timeIndex));

                                    break;
                                }

                            }


                        } while (cursor.moveToNext());
                    }

                }




                /*************************** и наконец закидываем задачи, которые не установлены */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    //int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {

                        if(cursor.getString(timeInMillis).compareTo("0") == 0)
                            strTime.add(cursor.getString(timeIndex));


                    } while(cursor.moveToNext());
                }




                cursor.close();

                return strTime;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<ArrayList<String>> futureString = exec.submit(new GetOnlyTasks(db));

        ArrayList<String> result = new ArrayList<>();
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
    public static ArrayList<String> getSortedRepeatWithArrayList(SQLiteDatabase db) {

        class GetOnlyTasks implements Callable<ArrayList<String>> {

            SQLiteDatabase db;
            public GetOnlyTasks(SQLiteDatabase db)
            {
                this.db = db;
            }

            @Override
            public ArrayList<String> call() throws Exception {
                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);

                ArrayList<Long> longTasks = new ArrayList<>();
                ArrayList<String> strRepeat = new ArrayList<>();

                /*************************** заполним по возрастанию задачи по времени в миллесекундах */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    //int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {


                        if(cursor.getString(timeInMillis).compareTo("0") != 0) {
                            if (Long.valueOf(cursor.getString(timeInMillis)) > 0) {
                                longTasks.add(Long.valueOf(cursor.getString(timeInMillis)));
                            }
                        }


                    } while(cursor.moveToNext());
                }

                /** Sort ArrayList<Long> */

                Collections.sort(longTasks);


                /*************************** закидываем задачи по УЖЕ ОТСОРТИРОВАННОМУ СПИСКУ Лонг значений*/

                for(int i = 0; i < longTasks.size(); i++) {

                    if (cursor.moveToFirst()) {
                        //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                        //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                        //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                        //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                        int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                        //int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                        int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                        do {


                            if (cursor.getString(timeInMillis).compareTo("0") != 0) {

                                if (Long.valueOf(cursor.getString(timeInMillis)).equals(longTasks.get(i))) {

                                    strRepeat.add(cursor.getString(repeatIndex));

                                    break;
                                }

                            }


                        } while (cursor.moveToNext());
                    }

                }




                /*************************** и наконец закидываем задачи, которые не установлены */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    //int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {

                        if(cursor.getString(timeInMillis).compareTo("0") == 0)
                            strRepeat.add(cursor.getString(repeatIndex));


                    } while(cursor.moveToNext());
                }




                cursor.close();

                return strRepeat;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<ArrayList<String>> futureString = exec.submit(new GetOnlyTasks(db));

        ArrayList<String> result = new ArrayList<>();
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
    public static ArrayList<String> getSortedListWithArrayList(SQLiteDatabase db) {

        class GetOnlyTasks implements Callable<ArrayList<String>> {

            SQLiteDatabase db;
            public GetOnlyTasks(SQLiteDatabase db)
            {
                this.db = db;
            }

            @Override
            public ArrayList<String> call() throws Exception {
                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);

                ArrayList<Long> longTasks = new ArrayList<>();
                ArrayList<String> strRepeat = new ArrayList<>();

                /*************************** заполним по возрастанию задачи по времени в миллесекундах */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    //int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {


                        if(cursor.getString(timeInMillis).compareTo("0") != 0) {
                            if (Long.valueOf(cursor.getString(timeInMillis)) > 0) {
                                longTasks.add(Long.valueOf(cursor.getString(timeInMillis)));
                            }
                        }


                    } while(cursor.moveToNext());
                }

                /** Sort ArrayList<Long> */

                Collections.sort(longTasks);


                /*************************** закидываем задачи по УЖЕ ОТСОРТИРОВАННОМУ СПИСКУ Лонг значений*/

                for(int i = 0; i < longTasks.size(); i++) {

                    if (cursor.moveToFirst()) {
                        //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                        //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                        //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                        //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                        //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                        int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                        int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                        do {


                            if (cursor.getString(timeInMillis).compareTo("0") != 0) {

                                if (Long.valueOf(cursor.getString(timeInMillis)).equals(longTasks.get(i))) {

                                    strRepeat.add(cursor.getString(listIndex));

                                    break;
                                }

                            }


                        } while (cursor.moveToNext());
                    }

                }




                /*************************** и наконец закидываем задачи, которые не установлены */
                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillis = cursor.getColumnIndex(DataBaseTasks.KEY_TIMEINMILLIS);


                    do {

                        if(cursor.getString(timeInMillis).compareTo("0") == 0)
                            strRepeat.add(cursor.getString(listIndex));


                    } while(cursor.moveToNext());
                }




                cursor.close();

                return strRepeat;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<ArrayList<String>> futureString = exec.submit(new GetOnlyTasks(db));

        ArrayList<String> result = new ArrayList<>();
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

    public static DataArrayList getSortedAllDataWithArrayList(SQLiteDatabase db) {

        class GetOnlyTasks implements Callable<DataArrayList> {

            SQLiteDatabase db;
            public GetOnlyTasks(SQLiteDatabase db)
            {
                this.db = db;
            }

            @Override
            public DataArrayList call() throws Exception {
                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);




                DataArrayList dataArrayList;




                ArrayList<Long> longTasks = getSortedTimeInMillisWithArrayList(db);

                ArrayList<String> arrTasks = getSortedTasksWithArrayList(db);
                ArrayList<String> arrDate = getSortedDateWithArrayList(db);
                ArrayList<String> arrTime = getSortedTimeWithArrayList(db);
                ArrayList<String> arrRepeat = getSortedRepeatWithArrayList(db);
                ArrayList<String> arrList = getSortedListWithArrayList(db);



                dataArrayList = new DataArrayList(arrTasks, longTasks, arrDate, arrTime, arrRepeat, arrList);



                cursor.close();

                return dataArrayList;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<DataArrayList> futureString = exec.submit(new GetOnlyTasks(db));

        DataArrayList result = null;
        try{
            result = futureString.get();
        }
        catch (Exception e) {

        }
        finally {
            exec.shutdown();

        }

        return result;

    }

    public static class DataArrayList{

        public ArrayList<String> arrTasks;
        public ArrayList<Long> arrTimeInMillis;
        public ArrayList<String> arrDate;
        public ArrayList<String> arrTime;
        public ArrayList<String> arrRepeat;
        public ArrayList<String> arrList;


        public DataArrayList(ArrayList<String> arrTasks,ArrayList<Long> arrTimeInMillis,
                             ArrayList<String> arrDate, ArrayList<String> arrTime,
                             ArrayList<String> arrRepeat, ArrayList<String> arrList){

        this.arrTasks = arrTasks;
        this.arrTimeInMillis = arrTimeInMillis;
        this.arrDate = arrDate;
        this.arrTime = arrTime;
        this.arrRepeat = arrRepeat;
        this.arrList = arrList;

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

                            break;
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
    public static String getCertainTaskID(SQLiteDatabase db, String task) {

        class GetCertainTask implements Callable<String> {

            SQLiteDatabase db;
            String task;
            public GetCertainTask(SQLiteDatabase db, String task)
            {
                this.db = db;
                this.task = task;
            }

            @Override
            public String call() throws Exception {
                String id = ""; // +1 - id

                Cursor cursor = db.query(DataBaseTasks.TABLE_TASKS, null, null, null, null, null, null);

                if(cursor.moveToFirst())
                {
                    int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);
                    int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);


                    do {


                        if(cursor.getString(taskIndex).compareTo(task) == 0)
                        {

                            id = cursor.getString(idIndex);

                            break;
                        }

                    } while(cursor.moveToNext());
                }

                cursor.close();
                return id;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<String> futureString = exec.submit(new GetCertainTask(db, task));

        String result = "";
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
            //
        }
        finally {
            exec.shutdown();
        }

        if(result.compareTo("true") == 0) return true;
        else return false;

    }




    public static void updateTaskById(SQLiteDatabase db, String[] newData, String taskID, Context context) {

        class UpdateTaskById implements Runnable {

            SQLiteDatabase db;
            String[] newData;
            String taskID;
            Context context;

            public UpdateTaskById(SQLiteDatabase db, String[] newData, String taskID, Context context) {
                this.db = db;
                this.newData = newData;
                this.taskID = taskID;
                this.context = context;
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

                String someId = "";

                if(cursor.moveToFirst())
                {
                    int idIndex = cursor.getColumnIndex(KEY_ID);


                    do {

                        someId = cursor.getString(idIndex);

                        if(someId.compareTo(taskID) == 0)
                        {
                            // cancel notification
                            cancelNotification(time, task, someId, context);
                            // обновляем по id
                            db.update(TABLE_TASKS, cv, KEY_ID + "=" + someId, null);
                            break;
                        }
                    } while(cursor.moveToNext());
                }
                ////////////////////////////////////////////////////////////////////
                cursor.close();
                if(Long.valueOf(timeInMillis) > 1) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.valueOf(timeInMillis));

                    sendNotification(calendar, someId, repeat, task, context);
                }
            }


            private void sendNotification(Calendar calendar, String id, String repeat, String message, Context context) {
                // Calendar calendar, String repeating
                // and then in your activity set the alarm manger to start the broadcast receiver
                // at a specific time and use AlarmManager setRepeating method to repeat it this
                // example bellow will repeat it every day.


                //String title = getString(R.string.task_at) + " " + editTime.getEditableText().toString();
                //String message = editTask.getEditableText().toString();


                Intent notifyIntent = new Intent(context, MyReceiver.class);
                //notifyIntent.putExtra("NotificationTitle", title);
                notifyIntent.putExtra("NotificationMessage", message);
                notifyIntent.putExtra("id_Notification", id);


                int newID = 0;
                try {
                    newID = Integer.decode(id);
                } catch (Exception e) {
                    newID = Integer.getInteger(id);
                }
                // ONE REAQUEST CODE FOR ALL.... That's bad I think !!!!!!!!!!!!!!!!!!
                PendingIntent pendingIntent = PendingIntent.getBroadcast
                        (context, (newID + 850), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


                int timeBefore = 0;
                SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(context);
                if (myPreference.getString("taskNotification", "0")
                        .compareTo(context.getResources().getStringArray(R.array.task_notification)[0]) == 0) {      // 0 mins
                    timeBefore = 0;
                } else if (myPreference.getString("taskNotification", "0")
                        .compareTo(context.getResources().getStringArray(R.array.task_notification)[1]) == 0) { // 5 mins
                    timeBefore = 60000 * 5;
                } else if (myPreference.getString("taskNotification", "0")
                        .compareTo(context.getResources().getStringArray(R.array.task_notification)[2]) == 0) { // 15 mins
                    timeBefore = 60000 * 15;
                } else if (myPreference.getString("taskNotification", "0")
                        .compareTo(context.getResources().getStringArray(R.array.task_notification)[3]) == 0) { // 30 mins
                    timeBefore = 60000 * 30;
                } else if (myPreference.getString("taskNotification", "0")
                        .compareTo(context.getResources().getStringArray(R.array.task_notification)[4]) == 0) { // 1 hour
                    timeBefore = 60000 * 60;
                } else if (myPreference.getString("taskNotification", "0")
                        .compareTo(context.getResources().getStringArray(R.array.task_notification)[5]) == 0) { // 2 hours
                    timeBefore = 60000 * 60 * 2;
                } else if (myPreference.getString("taskNotification", "0")
                        .compareTo(context.getResources().getStringArray(R.array.task_notification)[6]) == 0) { // 3 hours
                    timeBefore = 60000 * 60 * 3;
                }


                try {
                    if (repeat.compareTo(DataBaseLists.listRepeatNames[0]) == 0) { // Не повторять
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - timeBefore, pendingIntent);
                    } else if (repeat.compareTo(DataBaseLists.listRepeatNames[1]) == 0) { // Каждый час
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - timeBefore,
                                AlarmManager.INTERVAL_HOUR, pendingIntent);
                    } else if (repeat.compareTo(DataBaseLists.listRepeatNames[2]) == 0) { // Ежедневно
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - timeBefore,
                                AlarmManager.INTERVAL_DAY, pendingIntent);
                    } else if (repeat.compareTo(DataBaseLists.listRepeatNames[3]) == 0) { // Еженедельно
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - timeBefore,
                                AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                    } else if (repeat.compareTo(DataBaseLists.listRepeatNames[4]) == 0) { // Ежегодно
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - timeBefore,
                                AlarmManager.INTERVAL_DAY * 365, pendingIntent);
                    }
                } catch (Exception e) {
                }
            }

            private void cancelNotification(String editTime, String task, String id, Context context){

                if(editTime.compareTo("") != 0) {

                    String title = context.getString(R.string.task_at) + " " + editTime;


                    Intent notifyIntent = new Intent(context, MyReceiver.class);
                    notifyIntent.putExtra("NotificationTitle", title);
                    notifyIntent.putExtra("NotificationMessage", task);
                    notifyIntent.putExtra("id_Notification", id);


                    // ONE REAQUEST CODE FOR ALL.... That's bad I think !!!!!!!!!!!!!!!!!!
                    PendingIntent pendingIntent = PendingIntent.getBroadcast
                            (context, (Integer.decode(id)+850), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    try { alarmManager.cancel(pendingIntent); } catch(Exception e) {}

                }
            }

        }

        Runnable r = new UpdateTaskById(db, newData, taskID, context);
        new Thread(r).start();

    }
    public static void updateDateTimeOfTaskById(SQLiteDatabase db, String[] newData, String taskID, Context context) {

        class UpdateTaskById implements Runnable {

            SQLiteDatabase db;
            String[] newData;
            String taskID;
            Context context;

            public UpdateTaskById(SQLiteDatabase db, String[] newData, String taskID, Context context) {
                this.db = db;
                this.newData = newData;
                this.taskID = taskID;
                this.context = context;
            }

            @Override
            public void run() {

                String date= newData[1];
                String time = newData[2];
                String timeInMillis = newData[5];



                // создаем объект для данных
                ContentValues cv = new ContentValues();

                // подготовим значения для обновления
                cv.put(KEY_DATE, date);
                cv.put(KEY_TIME, time);
                cv.put(KEY_TIMEINMILLIS, timeInMillis);


                //////////////////////////////////////////////////////////////////// find list with Old Name
                Cursor cursor = db.query(TABLE_TASKS, null, null, null, null, null, null);

                String someId = "";

                if(cursor.moveToFirst())
                {
                    int idIndex = cursor.getColumnIndex(KEY_ID);

                    do {

                        someId = cursor.getString(idIndex);

                        if(someId.compareTo(taskID) == 0)
                        {
                            // обновляем по id
                            db.update(TABLE_TASKS, cv, KEY_ID + "=" + someId, null);
                            break;
                        }
                    } while(cursor.moveToNext());
                }
                ////////////////////////////////////////////////////////////////////

                cursor.close();

            }


        }

        Runnable r = new UpdateTaskById(db, newData, taskID, context);
        new Thread(r).start();

    }




    public static void deleteCertainTask(SQLiteDatabase db, SQLiteDatabase dbFinishedTasks, String task, Context context) {

        class DeleteCertainTask implements Runnable {

            SQLiteDatabase db;
            SQLiteDatabase dbFinishedTasks;
            String task;
            Context context;

            public DeleteCertainTask(SQLiteDatabase db, SQLiteDatabase dbFinishedTasks, String task, Context context) {
                this.db = db;
                this.dbFinishedTasks = dbFinishedTasks;
                this.task = task;
                this.context = context;
            }

            @Override
            public void run() {
                Cursor cursor = db.query(TABLE_TASKS, null, null, null, null, null, null);


                int someId = 0;

                if(cursor.moveToFirst())
                {
                    int idIndex = cursor.getColumnIndex(KEY_ID);
                    int taskIndex = cursor.getColumnIndex(KEY_TASK);
                    int dateIndex = cursor.getColumnIndex(KEY_DATE);
                    int timeIndex = cursor.getColumnIndex(KEY_TIME);
                    int repeatIndex = cursor.getColumnIndex(KEY_REPEAT);
                    int listIndex = cursor.getColumnIndex(KEY_LIST);
                    int timeInMillisIndex = cursor.getColumnIndex(KEY_TIMEINMILLIS);


                    String someTask = "";
                    String someDate = "";
                    String someTime = "";
                    String someRepeat = "";
                    String someList = "";
                    String someTimeInMillis = "";

                    do {
                        someId = cursor.getInt(idIndex);
                        someTask = cursor.getString(taskIndex);
                        someDate = cursor.getString(dateIndex);
                        someTime = cursor.getString(timeIndex);
                        someRepeat = cursor.getString(repeatIndex);
                        someList = cursor.getString(listIndex);
                        someTimeInMillis = cursor.getString(timeInMillisIndex);

                        if(someTask.compareTo(task) == 0)
                        {
                            // Cancel notification
                            cancelNotification(someTime, someTask, String.valueOf(someId), context);
                            // Add to Data Base of Finished Tasks
                            DataBaseFinishedTasks.addToDBFinishedTasks(dbFinishedTasks, someTask, someDate, someTime, someRepeat, someList, someTimeInMillis);
                            // Then delete this task
                            db.delete(TABLE_TASKS, KEY_ID + "=" + someId, null);
                            break;
                        }
                    } while(cursor.moveToNext());
                }

                cursor.close();
            }


            public void cancelNotification(String editTime, String task, String id, Context context){

                if(editTime.compareTo("") != 0) {

                    String title = context.getString(R.string.task_at) + " " + editTime;


                    Intent notifyIntent = new Intent(context, MyReceiver.class);
                    notifyIntent.putExtra("NotificationTitle", title);
                    notifyIntent.putExtra("NotificationMessage", task);
                    notifyIntent.putExtra("id_Notification", id);


                    // ONE REAQUEST CODE FOR ALL.... That's bad I think !!!!!!!!!!!!!!!!!!
                    PendingIntent pendingIntent = PendingIntent.getBroadcast
                            (context, (Integer.decode(id)+850), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    try { alarmManager.cancel(pendingIntent); } catch(Exception e) {}

                }
            }


        }

        Runnable r = new DeleteCertainTask(db, dbFinishedTasks, task, context);
        new Thread(r).start();

    }





    public static void deleteTasksByList(SQLiteDatabase db, SQLiteDatabase dbFinishedTasks, String listname, Context context) {

        class DeleteTasksByList implements Runnable {

            SQLiteDatabase db;
            String listname;
            SQLiteDatabase dbFinishedTasks;
            Context context;

            public DeleteTasksByList(SQLiteDatabase db, SQLiteDatabase dbFinishedTasks, String listname, Context context) {
                this.db = db;
                this.listname = listname;
                this.dbFinishedTasks = dbFinishedTasks;
                this.context = context;
            }

            @Override
            public void run() {
                Cursor cursor = db.query(TABLE_TASKS, null, null, null, null, null, null);


                int someId = 0;

                if(cursor.moveToFirst())
                {
                    int idIndex = cursor.getColumnIndex(KEY_ID);
                    int taskIndex = cursor.getColumnIndex(KEY_TASK);
                    int dateIndex = cursor.getColumnIndex(KEY_DATE);
                    int timeIndex = cursor.getColumnIndex(KEY_TIME);
                    int repeatIndex = cursor.getColumnIndex(KEY_REPEAT);
                    int listIndex = cursor.getColumnIndex(KEY_LIST);
                    int timeInMillisIndex = cursor.getColumnIndex(KEY_TIMEINMILLIS);

                    String someTask = "";
                    String someDate = "";
                    String someTime = "";
                    String someRepeat = "";
                    String someList = "";
                    String someTimeInMillis = "";

                    do {
                        someId = cursor.getInt(idIndex);
                        someTask = cursor.getString(taskIndex);
                        someDate = cursor.getString(dateIndex);
                        someTime = cursor.getString(timeIndex);
                        someRepeat = cursor.getString(repeatIndex);
                        someList = cursor.getString(listIndex);
                        someTimeInMillis = cursor.getString(timeInMillisIndex);

                        if(someList.compareTo(listname) == 0)
                        {
                            // Cancel notification
                            cancelNotification(someTime, someTask, String.valueOf(someId), context);
                            // Add to Data Base of Finished Tasks
                            DataBaseFinishedTasks.addToDBFinishedTasks(dbFinishedTasks, someTask, someDate, someTime, someRepeat, someList, someTimeInMillis);
                            // Then delete this task
                            db.delete(TABLE_TASKS, KEY_ID + "=" + someId, null);
                        }
                    } while(cursor.moveToNext());
                }

                cursor.close();
            }


            public void cancelNotification(String editTime, String task, String id, Context context){

                if(editTime.compareTo("") != 0) {

                    String title = context.getString(R.string.task_at) + " " + editTime;


                    Intent notifyIntent = new Intent(context, MyReceiver.class);
                    notifyIntent.putExtra("NotificationTitle", title);
                    notifyIntent.putExtra("NotificationMessage", task);
                    notifyIntent.putExtra("id_Notification", id);


                    // ONE REAQUEST CODE FOR ALL.... That's bad I think !!!!!!!!!!!!!!!!!!
                    PendingIntent pendingIntent = PendingIntent.getBroadcast
                            (context, (Integer.decode(id)+850), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    try { alarmManager.cancel(pendingIntent); } catch(Exception e) {}

                }
            }


        }

        Runnable r = new DeleteTasksByList(db, dbFinishedTasks, listname, context);
        new Thread(r).start();

    }




    public static Integer getAmountTasksToday(SQLiteDatabase db) {

        class getAmountTasksToday implements Callable<Integer> {

            SQLiteDatabase db;

            public getAmountTasksToday(SQLiteDatabase db)
            {
                this.db = db;
            }


            @Override
            public Integer call() throws Exception {
                Integer amount = 0;

                Cursor cursor = db.query(TABLE_TASKS, null, null, null, null, null, null);

                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    //int taskIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TASK);
                    //int dateIndex = cursor.getColumnIndex(DataBaseTasks.KEY_DATE);
                    //int timeIndex = cursor.getColumnIndex(DataBaseTasks.KEY_TIME);
                    //int repeatIndex = cursor.getColumnIndex(DataBaseTasks.KEY_REPEAT);
                    //int listIndex = cursor.getColumnIndex(DataBaseTasks.KEY_LIST);
                    int timeInMillisIndex = cursor.getColumnIndex(KEY_TIMEINMILLIS);

                    do {

                        Calendar todayCal = Calendar.getInstance();

                        Calendar establishedCal = Calendar.getInstance();
                        establishedCal.setTimeInMillis(Long.valueOf(cursor.getString(timeInMillisIndex)));


                        if(todayCal.get(Calendar.DAY_OF_YEAR) == establishedCal.get(Calendar.DAY_OF_YEAR) &&
                                todayCal.get(Calendar.YEAR) == establishedCal.get(Calendar.YEAR))
                        {
                            amount++;
                        }

                    } while(cursor.moveToNext());
                }

                cursor.close();
                return amount;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<Integer> futureString = exec.submit(new getAmountTasksToday(db));

        Integer result = 0;
        try{
            result = futureString.get();
        }
        catch (Exception e) {
            //
        }
        finally {
            exec.shutdown();
        }


        return result;
    }



}

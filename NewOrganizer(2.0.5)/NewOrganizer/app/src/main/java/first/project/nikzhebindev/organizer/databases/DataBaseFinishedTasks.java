package first.project.nikzhebindev.organizer.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import first.project.nikzhebindev.organizer.MainMenu;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Data Base for Finished Tasks
 */

public class DataBaseFinishedTasks extends SQLiteOpenHelper{


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "finishedTasksDataBase";
    public static final String TABLE_F_TASKS = "finishedTasks";


    /** поля таблицы для хранения */
    public static final String KEY_F_ID = "_id_f";
    public static final String KEY_F_TASK = "task_f";
    public static final String KEY_F_DATE = "date_f";
    public static final String KEY_F_TIME = "time_f";
    public static final String KEY_F_REPEAT = "repeat_f";
    public static final String KEY_F_LIST = "list_f";
    public static final String KEY_F_TIMEINMILLIS = "dateinmillis_f";

    // кол-во данных (которые выше), кроме id
    public static final int AMOUNT_DATA = 6;

    // формируем запрос для создания базы данных
    private static final String DATABASE_CREATE_FINISHED_TASKS = "create table " + TABLE_F_TASKS + "(" + KEY_F_ID
            + " integer primary key," + KEY_F_TASK + " text," + KEY_F_DATE + " text," + KEY_F_TIME + " text,"
            + KEY_F_REPEAT + " text," + KEY_F_LIST + " text," + KEY_F_TIMEINMILLIS + " text" + ")";





    public DataBaseFinishedTasks(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }




    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL - запрос, который создаёт таблицу
        db.execSQL(DATABASE_CREATE_FINISHED_TASKS);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Destroy old DataBase Table
        db.execSQL("drop table if exists " + TABLE_F_TASKS);

        // Create a new one with updated structure
        onCreate(db);

    }



    public static void clearDataBase(SQLiteDatabase db)
    {
        db.delete(TABLE_F_TASKS, null, null);
    }





    public static String[] readDataBase(SQLiteDatabase db) {

        class ReadDataBase implements Callable<String[]> {

            SQLiteDatabase db;
            public ReadDataBase(SQLiteDatabase db) {
                this.db = db;
            }

            @Override
            public String[] call() throws Exception {

                Cursor cursor = db.query(TABLE_F_TASKS, null, null, null, null, null, null);

                String strTasks = new String();

                int iterNewLists = 0;

                if(cursor.moveToFirst())
                {
                    int idIndex = cursor.getColumnIndex(KEY_F_ID);

                    int taskIndex = cursor.getColumnIndex(KEY_F_TASK);
                    int dateIndex = cursor.getColumnIndex(KEY_F_DATE);
                    int timeIndex = cursor.getColumnIndex(KEY_F_TIME);
                    int repeatIndex = cursor.getColumnIndex(KEY_F_REPEAT);
                    int listIndex = cursor.getColumnIndex(KEY_F_LIST);
                    int dateInMillisIndex = cursor.getColumnIndex(KEY_F_TIMEINMILLIS);

                    do {

                        strTasks += cursor.getString(taskIndex);
                        strTasks += DataBaseTasks.innerDIVIDER;
                        strTasks += cursor.getString(dateIndex);
                        strTasks += DataBaseTasks.innerDIVIDER;
                        strTasks += cursor.getString(timeIndex);
                        strTasks += DataBaseTasks.innerDIVIDER;
                        strTasks += cursor.getString(repeatIndex);
                        strTasks += DataBaseTasks.innerDIVIDER;
                        strTasks += cursor.getString(listIndex);
                        strTasks += DataBaseTasks.innerDIVIDER;
                        strTasks += cursor.getString(dateInMillisIndex);

                        strTasks += DataBaseTasks.DIVIDER;
                        iterNewLists++;

                    } while(cursor.moveToNext());
                }


                //////////////////// TRANSFORMING String TO String[] ////////////////////
                String[] strTasksArray = new String[iterNewLists];

                for(int i = 0; i < strTasksArray.length; i++)
                    strTasksArray[i] = "";

                int iter = 0;
                for(int q = 0; q < strTasks.length(); q++) {
                    if (strTasks.charAt(q) == DataBaseTasks.DIVIDER) {
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






    public static void addToDBFinishedTasks(SQLiteDatabase db, String task, String date, String time, String repeat, String list, String timeInMillis) {

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
                cv.put(KEY_F_TASK, task);
                cv.put(KEY_F_DATE, date);
                cv.put(KEY_F_TIME, time);
                cv.put(KEY_F_REPEAT, repeat);
                cv.put(KEY_F_LIST, list);
                cv.put(KEY_F_TIMEINMILLIS, timeInMillis);

                db.insert(TABLE_F_TASKS, null, cv);
            }
        }

        Runnable r = new AddToDBTasks(db, task, date, time, repeat, list, timeInMillis);
        new Thread(r).start();

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
                Cursor cursor = db.query(TABLE_F_TASKS, null, null, null, null, null, null);

                ArrayList<String> strTasks = new ArrayList<>();


                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    int taskIndex = cursor.getColumnIndex(KEY_F_TASK);
                    //int dateIndex = cursor.getColumnIndex();
                    //int timeIndex = cursor.getColumnIndex();
                    //int repeatIndex = cursor.getColumnIndex();
                    //int listIndex = cursor.getColumnIndex();

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
        }

        return result;
    }
    public static ArrayList<String> getOnlyDateWithArrayList(SQLiteDatabase db) {

        class GetOnlyTasks implements Callable<ArrayList<String>> {

            SQLiteDatabase db;
            public GetOnlyTasks(SQLiteDatabase db)
            {
                this.db = db;
            }

            @Override
            public ArrayList<String> call() throws Exception {
                Cursor cursor = db.query(TABLE_F_TASKS, null, null, null, null, null, null);

                ArrayList<String> strDate = new ArrayList<>();


                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    //int taskIndex = cursor.getColumnIndex(KEY_F_TASK);
                    int dateIndex = cursor.getColumnIndex(KEY_F_DATE);
                    //int timeIndex = cursor.getColumnIndex();
                    //int repeatIndex = cursor.getColumnIndex();
                    //int listIndex = cursor.getColumnIndex();

                    do {

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
        }

        return result;
    }
    public static ArrayList<String> getOnlyTimeWithArrayList(SQLiteDatabase db) {

        class GetOnlyTasks implements Callable<ArrayList<String>> {

            SQLiteDatabase db;
            public GetOnlyTasks(SQLiteDatabase db)
            {
                this.db = db;
            }

            @Override
            public ArrayList<String> call() throws Exception {
                Cursor cursor = db.query(TABLE_F_TASKS, null, null, null, null, null, null);

                ArrayList<String> strTime = new ArrayList<>();


                if(cursor.moveToFirst())
                {
                    //int idIndex = cursor.getColumnIndex(DataBaseTasks.KEY_ID);

                    //int taskIndex = cursor.getColumnIndex(KEY_F_TASK);
                    //int dateIndex = cursor.getColumnIndex();
                    int timeIndex = cursor.getColumnIndex(KEY_F_TIME);
                    //int repeatIndex = cursor.getColumnIndex();
                    //int listIndex = cursor.getColumnIndex();

                    do {

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
        }

        return result;
    }




    public static void deleteCertainFinishedTaskAndAddToDBTasks(SQLiteDatabase db, SQLiteDatabase databaseTasks, SQLiteDatabase databaseLists, String task, Context context) {

        class DeleteCertainTask implements Runnable {

            SQLiteDatabase db;
            String task;
            SQLiteDatabase databaseTasks;
            SQLiteDatabase databaseLists;
            Context context;

            public DeleteCertainTask(SQLiteDatabase db, SQLiteDatabase databaseTasks, SQLiteDatabase databaseLists, String task, Context context) {
                this.db = db;
                this.task = task;
                this.databaseTasks = databaseTasks;
                this.databaseLists = databaseLists;
                this.context = context;
            }

            @Override
            public void run() {
                Cursor cursor = db.query(TABLE_F_TASKS, null, null, null, null, null, null);



                if(cursor.moveToFirst())
                {
                    int idIndex = cursor.getColumnIndex(KEY_F_ID);
                    int taskIndex = cursor.getColumnIndex(KEY_F_TASK);
                    int dateIndex = cursor.getColumnIndex(KEY_F_DATE);
                    int timeIndex = cursor.getColumnIndex(KEY_F_TIME);
                    int repeatIndex = cursor.getColumnIndex(KEY_F_REPEAT);
                    int listIndex = cursor.getColumnIndex(KEY_F_LIST);
                    int timeInMillisIndex = cursor.getColumnIndex(KEY_F_TIMEINMILLIS);


                    String someId = "";
                    String someTask = "";
                    String someDate = "";
                    String someTime = "";
                    String someRepeat = "";
                    String someList = "";
                    String someTimeInMillis = "";


                    do {

                        someId = cursor.getString(idIndex);
                        someTask = cursor.getString(taskIndex);
                        someDate = cursor.getString(dateIndex);
                        someTime = cursor.getString(timeIndex);
                        someRepeat = cursor.getString(repeatIndex);
                        someList = cursor.getString(listIndex);
                        someTimeInMillis = cursor.getString(timeInMillisIndex);


                        if(someTask.compareTo(task) == 0)
                        {
                            // сделать проверку ЕСЛИ списка возвращаемой задачи нет, ТО добавить список, а потом задачу в него, вот!
                            if(!DataBaseLists.checkListPresence(databaseLists, someList))
                                DataBaseLists.addToDBLists(databaseLists, someList);

                            // Добавляем в базу данных обычных задач
                            DataBaseTasks.addToDBTasks(databaseTasks, someTask, someDate, someTime, someRepeat, someList, someTimeInMillis, context);

                            // теперь удаляем
                            db.delete(TABLE_F_TASKS, KEY_F_ID + "=" + someId, null);

                            break;
                        }
                    } while(cursor.moveToNext());
                }

                cursor.close();
            }
        }

        Runnable r = new DeleteCertainTask(db, databaseTasks, databaseLists, task, context);
        new Thread(r).start();

    }




    public static String[] getCertainFinishedTask(SQLiteDatabase db, String task) {

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

                Cursor cursor = db.query(TABLE_F_TASKS, null, null, null, null, null, null);

                if(cursor.moveToFirst())
                {
                    int idIndex = cursor.getColumnIndex(KEY_F_ID);

                    int taskIndex = cursor.getColumnIndex(KEY_F_TASK);
                    int dateIndex = cursor.getColumnIndex(KEY_F_DATE);
                    int timeIndex = cursor.getColumnIndex(KEY_F_TIME);
                    int repeatIndex = cursor.getColumnIndex(KEY_F_REPEAT);
                    int listIndex = cursor.getColumnIndex(KEY_F_LIST);
                    int timeInMillisIndex = cursor.getColumnIndex(KEY_F_TIMEINMILLIS);

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
                Cursor cursor = db.query(TABLE_F_TASKS, null, null, null, null, null, null);

                if(cursor.moveToFirst())
                {

                    do {

                        int taskIndex = cursor.getColumnIndex(KEY_F_TASK);

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



}

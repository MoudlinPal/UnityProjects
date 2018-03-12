package first.project.nikzhebindev.organizer.databases;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.RequiresPermission;

import first.project.nikzhebindev.organizer.R;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Data Base for Lists
 */

public class DataBaseLists extends SQLiteOpenHelper{


    public static String[] listRepeatNames ={"Не повторять", "Каждый час", "Ежедневно",
            "Еженедельно", "Ежегодно"};

    public static String[] defaultListsNames = {"Разное", "Личное" };



    public static char DIVIDER = '&'; // to divide lists


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "listsDataBase";
    public static final String TABLE_LISTS = "lists";

    /** поля таблицы для хранения */
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";


    // формируем запрос для создания базы данных
    private static final String DATABASE_CREATE_LISTS = "create table " + TABLE_LISTS + "(" + KEY_ID
            + " integer primary key," + KEY_NAME + " text" + ")";



    public DataBaseLists(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        listRepeatNames[0] = context.getString(R.string.no_repeat);
        listRepeatNames[1] = context.getString(R.string.once_an_hour);
        listRepeatNames[2] = context.getString(R.string.once_a_day);
        listRepeatNames[3] = context.getString(R.string.once_a_week);
        listRepeatNames[4] = context.getString(R.string.once_a_year);

        defaultListsNames[0] = context.getString(R.string.defaultlist);
        defaultListsNames[1] = context.getString(R.string.personal);

    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        // SQL - запрос, который создаёт таблицу
        db.execSQL(DATABASE_CREATE_LISTS);



    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Destroy old DataBase Table
        db.execSQL("drop table if exists " + TABLE_LISTS);

        // Create a new one with updated structure
        onCreate(db);

    }



    public static void clearDataBase(SQLiteDatabase db) {
        db.delete(DataBaseLists.TABLE_LISTS, null, null);
    }




    public static void addToDBLists(SQLiteDatabase db, String nameList) {

        class AddToDBLists implements Runnable {

            SQLiteDatabase db;
            String nameList;
            public AddToDBLists(SQLiteDatabase db, String nameList) {
                this.db = db;
                this.nameList = nameList;
            }

            @Override
            public void run() {
                // For DataBase
                // Этот Class используется для добавления новых строк в таблицу
                // каждый обьект этого класса - одна строка таблицы
                // (мол массив с именами столбцов и их значениями)
                ContentValues cv = new ContentValues();

                // add to DataBase (KEY_ID - заполняется автоматический)
                cv.put(DataBaseLists.KEY_NAME, nameList);

                db.insert(DataBaseLists.TABLE_LISTS, null, cv);
            }
        }

        Runnable r = new AddToDBLists(db, nameList);
        new Thread(r).start();
    }




    public static String[] readDBLists(SQLiteDatabase db) {

        class ReadDBLists implements Callable<String[]> {

            SQLiteDatabase db;
            public ReadDBLists(SQLiteDatabase db)
            {
                this.db = db;
            }

            @Override
            public String[] call() throws Exception {

                Cursor cursor = db.query(DataBaseLists.TABLE_LISTS, null, null, null, null, null, null);

                String strLists = "";

                // filling with THE DEFAULTS names of lists

                for(int i = 0; i < defaultListsNames.length; i++) {
                    if(defaultListsNames[i] != null) {
                        strLists += defaultListsNames[i];
                        strLists += DIVIDER;
                    }
                }

                int iterNewLists = 0;

                if(cursor.moveToFirst())
                {
                    int idIndex = cursor.getColumnIndex(DataBaseLists.KEY_ID);
                    int nameIndex = cursor.getColumnIndex(DataBaseLists.KEY_NAME);

                    do {
                        strLists += cursor.getString(nameIndex);
                        strLists += DIVIDER;
                        iterNewLists++;

                    } while(cursor.moveToNext());
                }


                //////////////////// TRANSFORMING String TO String[] ////////////////////
                String[] strListArray = new String[defaultListsNames.length+iterNewLists];

                for(int i = 0; i < strListArray.length; i++)
                    strListArray[i] = "";

                int iter = 0;
                for(int q = 0; q < strLists.length(); q++) {
                    if (strLists.charAt(q) == DIVIDER) {
                        iter++;
                    } else {
                        try
                        {
                            strListArray[iter] += strLists.charAt(q);
                        }
                        catch(Exception exception)
                        {

                        }
                    }
                }
                //////////////////// TRANSFORMING String TO String[] ////////////////////
                cursor.close();

                return strListArray;
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        Future<String[]> futureString = exec.submit(new ReadDBLists(db));

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




    public static void deleteList(SQLiteDatabase db, String name) {

        class DeleteList implements Runnable {

            SQLiteDatabase db;
            String name;
            public DeleteList(SQLiteDatabase db, String name) {
                this.db = db;
                this.name = name;
            }

            @Override
            public void run() {
                Cursor cursor = db.query(TABLE_LISTS, null, null, null, null, null, null);

                int idIndex = 0;
                int nameIndex = 0;

                int someId = 0;

                if(cursor.moveToFirst())
                {
                    idIndex = cursor.getColumnIndex(KEY_ID);
                    nameIndex = cursor.getColumnIndex(KEY_NAME);

                    String someName = "";

                    do {
                        someId = cursor.getInt(idIndex);
                        someName = cursor.getString(nameIndex);
                        if(someName.compareTo(name) == 0)
                        {
                            db.delete(TABLE_LISTS, KEY_ID + "=" + someId, null);
                            break;
                        }
                    } while(cursor.moveToNext());
                }

                cursor.close();
            }
        }

        Runnable r = new DeleteList(db, name);
        new Thread(r).start();
    }




    public static void renameList(SQLiteDatabase db, String oldName, String newName) {

        class RenameList implements Runnable {

            SQLiteDatabase db;
            String oldName;
            String newName;
            public RenameList(SQLiteDatabase db, String oldName, String newName) {
                this.db = db;
                this.oldName = oldName;
                this.newName = newName;
            }

            @Override
            public void run() {
                // создаем объект для данных
                ContentValues cv = new ContentValues();

                // подготовим значения для обновления
                cv.put(KEY_NAME, newName);


                //////////////////////////////////////////////////////////////////// find list with Old Name
                Cursor cursor = db.query(TABLE_LISTS, null, null, null, null, null, null);

                int idIndex = 0;
                int nameIndex = 0;

                int someId = 0;

                if(cursor.moveToFirst())
                {
                    idIndex = cursor.getColumnIndex(KEY_ID);
                    nameIndex = cursor.getColumnIndex(KEY_NAME);

                    String someName = "";

                    do {
                        someId = cursor.getInt(idIndex);
                        someName = cursor.getString(nameIndex);
                        if(someName.compareTo(oldName) == 0)
                        {
                            // обновляем по id
                            db.update(TABLE_LISTS, cv, KEY_ID + "=" + someId, null);
                            break;
                        }
                    } while(cursor.moveToNext());
                }

                cursor.close();
            }
        }

        Runnable r = new RenameList(db, oldName, newName);
        new Thread(r).start();
    }



    public static boolean checkListPresence(SQLiteDatabase db, String listName)
    {
        String[] strLists = readDBLists(db);

        for(int i = 0; i < strLists.length; i++)
        {
            if(strLists[i].compareTo(listName) == 0)
            {
                return true;
            }
        }

        return false;
    }

}

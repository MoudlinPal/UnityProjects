package com.example.user.organizer;


import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.organizer.databases.DataBaseLists;
import com.example.user.organizer.databases.DataBaseTasks;


/***
 * Main Fragment, which adapt for OnNavigationItemClickListener
 */

public class TaskListFragment extends ListFragment{

    public static boolean openedTasksByListClick = false;


    public static boolean setPrimaryNewTask = false;
    public static String idF = "", taskF = "", dateF = "", timeF = "", repeatF = "", listF = "";





    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if(MainMenu.openedMain) setTaskAdapter();
        else if(MainMenu.openedLists) setListAdapter();
    }






    private void setTaskAdapter()
    {
        DataBaseTasks dbTasks = new DataBaseTasks(getContext());

        /////////////////////// DataBaseTasks ///////////////////////
        // Вызываем метод вспомогательного класса, чтобы открыть и вернуть экхемпляр базы данных
        // с которой будем работать
        SQLiteDatabase databaseTasks = dbTasks.getWritableDatabase(); // доступен для чтения и записи
        /////////////////////// DataBaseTasks ///////////////////////


        if(MainMenu.selectedSpinnerList.compareTo(MainMenu.KEY_ALL_TASKS) == 0) {
            String[] strTasks = DataBaseTasks.getOnlyTasks(databaseTasks);
            MyTaskAdapter myTaskAdapter = new MyTaskAdapter(getActivity(),
                    R.layout.fragment_item_task, strTasks);
            setListAdapter(myTaskAdapter);
        }
        else {
        String[] strTasks = DataBaseTasks.getOnlyTasksByList(databaseTasks, MainMenu.selectedSpinnerList);


        // Использование собственного шаблона
        //adapter = new ArrayAdapter<>(getActivity(),
        //        R.layout.fragment_item_task, R.id.label, strTasks);
        //setListAdapter(adapter);

        MyTaskAdapter myTaskAdapter = new MyTaskAdapter(getActivity(),
                R.layout.fragment_item_task, strTasks);
        setListAdapter(myTaskAdapter);
        }
    }
    private void setListAdapter()
    {
        DataBaseLists dbLists = new DataBaseLists(getContext());

        /////////////////////// DataBaseTasks ///////////////////////
        // Вызываем метод вспомогательного класса, чтобы открыть и вернуть экхемпляр базы данных
        // с которой будем работать
        SQLiteDatabase databaseLists = dbLists.getWritableDatabase(); // доступен для чтения и записи
        /////////////////////// DataBaseTasks ///////////////////////

        String[] strLists = DataBaseLists.readDBLists(databaseLists);

        // Использование собственного шаблона
        //adapter = new ArrayAdapter<>(getActivity(),
        //        R.layout.fragment_item_task, R.id.label, strTasks);
        //setListAdapter(adapter);

        MyListAdapter myListAdapter = new MyListAdapter(getActivity(),
                R.layout.fragment_item_list, strLists);
        setListAdapter(myListAdapter);

    }







    @Override
    public void onResume() {
        super.onResume();
        // TESTIIING
        //Toast toast = Toast.makeText(getContext(), "TaskListOnResume!!!", Toast.LENGTH_LONG);
        //toast.show();

        //setTaskAdapter();

    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onStart() {
        super.onStart();
        if(MainMenu.openedMain) setTaskAdapter();
        else if(MainMenu.openedLists) setListAdapter();
    }







    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task, null);
    }











    public class MyTaskAdapter extends ArrayAdapter<String> {

        private Context mContext;

        public MyTaskAdapter(Context context, int textViewResourceId, String[] objects)
        {
            super(context, textViewResourceId, objects);
            mContext = context;
        }




        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // return super.getView(position, convertView, parent);



            DataBaseTasks dbTasks = new DataBaseTasks(getContext());
            /////////////////////// DataBaseTasks ///////////////////////
            // Вызываем метод вспомогательного класса, чтобы открыть и вернуть экхемпляр базы данных
            // с которой будем работать
            final SQLiteDatabase databaseTasks = dbTasks.getWritableDatabase(); // доступен для чтения и записи
            /////////////////////// DataBaseTasks ///////////////////////
            String[] strTasks;
            if(MainMenu.selectedSpinnerList.compareTo(MainMenu.KEY_ALL_TASKS) != 0)
                strTasks = DataBaseTasks.getOnlyTasksByList(databaseTasks, MainMenu.selectedSpinnerList);
            else
                strTasks = DataBaseTasks.getOnlyTasks(databaseTasks);


            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inflater.inflate(R.layout.fragment_item_task, parent,false);


            ////////////////////////////////////////////////////////////////////////////////////////
            Button btnLabel = (Button) row.findViewById(R.id.label);
            btnLabel.setText(strTasks[position]);
            btnLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String[] data = DataBaseTasks.getCertainTask(databaseTasks, ((Button) v).getText().toString());

                    taskF = data[0];
                    dateF = data[1];
                    timeF = data[2];
                    repeatF = data[3];
                    listF = data[4];

                    idF = data[5];

                    // to NewTask
                    setPrimaryNewTask = true;
                    startActivity(new Intent(getActivity(), NewTask.class));

                }
            });
            ////////////////////////////////////////////////////////////////////////////////////////


            ////////////////////////////////////////////////////////////////////////////////////////
            CheckBox check = (CheckBox) row.findViewById(R.id.check);
            check.setTag(btnLabel.getText().toString());
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final CompoundButton compoundButton, boolean b) {

                    if(b) {
                        // <!-- DIALOG "Finish this task?" -->
                        new AlertDialog.Builder(getContext())
                                .setTitle("Вы уверены?")
                                .setMessage("Завершить задачу?")
                                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialogInterface) {
                                        compoundButton.setChecked(false); // In any case
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {

                                        compoundButton.setChecked(false); // In any case
                                        onStart();

                                    }
                                })
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {

                                        DataBaseTasks.deleteCertainTask(databaseTasks, compoundButton.getTag().toString()); // IT'S TAG OF *check.setTag(btnLabel.getText().toString());*
                                        Toast.makeText(getActivity(), "Задача завершена!", Toast.LENGTH_SHORT).show();
                                        onStart();

                                    }
                                }).create().show();
                        // <!-- DIALOG "Finish this task?" -->
                    }


                }
            });
            ////////////////////////////////////////////////////////////////////////////////////////



            return row;
        }
    }



    public class MyListAdapter extends ArrayAdapter<String> {

        private Context mContext;

        public MyListAdapter(Context context, int textViewResourceId, String[] objects)
        {
            super(context, textViewResourceId, objects);
            mContext = context;
        }




        @Override
        public View getView(int position, View convertView, ViewGroup parent) {




            DataBaseLists dbLists = new DataBaseLists(getContext());
            /////////////////////// DataBaseTasks ///////////////////////
            // Вызываем метод вспомогательного класса, чтобы открыть и вернуть экхемпляр базы данных
            // с которой будем работать
            final SQLiteDatabase databaseLists = dbLists.getWritableDatabase(); // доступен для чтения и записи
            /////////////////////// DataBaseTasks ///////////////////////
            String[] strLists = DataBaseLists.readDBLists(databaseLists);



            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inflater.inflate(R.layout.fragment_item_list, parent,false);


            ////////////////////////////////////////////////////////////////////////////////////////
            Button btnLabelList = (Button) row.findViewById(R.id.labelList);
            btnLabelList.setText(strLists[position]);
            btnLabelList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DataBaseTasks dbTasks = new DataBaseTasks(getContext());
                    /////////////////////// DataBaseTasks ///////////////////////
                    // Вызываем метод вспомогательного класса, чтобы открыть и вернуть экхемпляр базы данных
                    // с которой будем работать
                    SQLiteDatabase databaseTasks = dbTasks.getWritableDatabase(); // доступен для чтения и записи
                    /////////////////////// DataBaseTasks ///////////////////////
                    String[] strTasks = DataBaseTasks.getOnlyTasksByList(databaseTasks, ((Button) v).getText().toString());
                    if(strTasks.length == 0) {
                        Toast.makeText(getActivity(), "Список пуст!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        openedTasksByListClick = true;
                        MainMenu.OpenedMain();
                        MainMenu.selectedSpinnerList = ((Button) v).getText().toString(); // way to open tasks by click on list
                        setTaskAdapter();
                    }
                }
            });
            btnLabelList.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    final Button selectedBtn = (Button)v; // !!!


                    // IF PRESSED DEFAULT LIST BUTTON
                    for(int i = 0; i < DataBaseLists.defaultListsNames.length; i++)
                    {
                        if (DataBaseLists.defaultListsNames[i].compareTo(selectedBtn.getText().toString()) == 0)
                        {
                            Toast.makeText(getContext(), "Нельзя изменить или удалить этот список!", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    }
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setPositiveButton("Удалить",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DataBaseLists dbLists = new DataBaseLists(getContext());
                                    /////////////////////// DataBase ///////////////////////
                                    final SQLiteDatabase databaseLists = dbLists.getWritableDatabase(); // доступен для чтения и записи
                                    /////////////////////// DataBase ///////////////////////
                                    DataBaseTasks dbTasks = new DataBaseTasks(getContext());
                                    /////////////////////// DataBaseTasks ///////////////////////
                                    final SQLiteDatabase databaseTasks = dbTasks.getWritableDatabase(); // доступен для чтения и записи
                                    /////////////////////// DataBaseTasks ///////////////////////


                                    DataBaseLists.deleteList(databaseLists, selectedBtn.getText().toString());
                                    DataBaseTasks.deleteTasksByList(databaseTasks, selectedBtn.getText().toString());


                                    Toast.makeText(getContext(), "Список \"" + selectedBtn.getText().toString() + "\" удален", Toast.LENGTH_SHORT).show();
                                    NewTask.wasSomeChangeWithSomeList = true;

                                    dialog.cancel();
                                    setListAdapter();
                                }
                            });
                    builder.setNegativeButton("Изменить",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    // ТУТ НУЖНО БУДЕТ ИЗМЕНИТЬ НА ОБНОВЛЕНИЕ СУЩЕСТВУЮЩЕГО ЛИСТА, А НЕ ДОБВАЛЕНИЕ НОВОГО !!!!

                                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                    //Получаем вид с файла dialog_new_list.xml, который применим для диалогового окна:
                                    LayoutInflater li = LayoutInflater.from(getContext()); // this = context !!!!!!!!!!!!!!!!!!!
                                    View viewDialogNewList = li.inflate(R.layout.dialog_new_list, null);

                                    final EditText editListName =
                                            (EditText)viewDialogNewList.findViewById(R.id.editListName); // !!!!!!!!!
                                    ///////////////////////////////////////////////////////////////////////////////


                                    ////////////////// <!-- DIALOG ADD NEW LIST --> //////////////////

                                    final AlertDialog dialogAddList = new AlertDialog.Builder(getContext())
                                            .setView(viewDialogNewList)
                                            .setTitle("Изменить название списка")
                                            .setPositiveButton("Изменить", null) //Set to null. We override the onclick
                                            .setNegativeButton("Отменить", null)
                                            .create();


                                    dialogAddList.setOnShowListener(new DialogInterface.OnShowListener() {

                                        @Override
                                        public void onShow(DialogInterface dialog) {

                                            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                            button.setOnClickListener(new View.OnClickListener() {



                                                @Override
                                                public void onClick(View view) {

                                                    // for DataBase !!!!!!!!!
                                                    String nameList = editListName.getEditableText().toString();

                                                    String bufDivider = "";
                                                    bufDivider += DataBaseLists.DIVIDER;

                                                    if(DataBaseLists.checkListPresence(databaseLists, nameList)) {
                                                        Toast toast = Toast.makeText(getContext(), "Такой список уже существует!", Toast.LENGTH_SHORT);
                                                        toast.show();
                                                    }
                                                    else if(nameList.compareTo("") == 0) // !!!!!!!!!!!!!
                                                    {
                                                        //makeVibration();

                                                        Toast toast = Toast.makeText(getContext(), "Введите название списка!", Toast.LENGTH_SHORT);
                                                        toast.show();
                                                    }
                                                    else if(nameList.contains(bufDivider))
                                                    {
                                                        //makeVibration();

                                                        Toast toast = Toast.makeText(getContext(), "Символ '"+ DataBaseLists.DIVIDER+"' недопустим!", Toast.LENGTH_SHORT);
                                                        toast.show();
                                                    }
                                                    else
                                                    {
                                                        DataBaseLists dbLists = new DataBaseLists(getContext());
                                                        /////////////////////// DataBaseTasks ///////////////////////
                                                        final SQLiteDatabase databaseLists = dbLists.getWritableDatabase(); // доступен для чтения и записи
                                                        /////////////////////// DataBaseTasks ///////////////////////
                                                        DataBaseTasks dbTasks = new DataBaseTasks(getContext());
                                                        /////////////////////// DataBaseTasks ///////////////////////
                                                        final SQLiteDatabase databaseTasks = dbTasks.getWritableDatabase(); // доступен для чтения и записи
                                                        /////////////////////// DataBaseTasks ///////////////////////
                                                        DataBaseLists.renameList(databaseLists, selectedBtn.getText().toString(), nameList);
                                                        DataBaseTasks.renameTasksByList(databaseTasks, selectedBtn.getText().toString(), nameList);



                                                        //Dismiss once everything is OK.
                                                        dialogAddList.dismiss();

                                                        selectedBtn.setText(nameList);

                                                        Toast toast = Toast.makeText(getContext(), "Название изменено!", Toast.LENGTH_SHORT);
                                                        toast.show();
                                                        NewTask.wasSomeChangeWithSomeList = true;
                                                        setListAdapter();
                                                    }
                                                    ///////////////////////////////////////////////////////////////////////////////
                                                }
                                            });
                                        }
                                    });


                                    dialogAddList.show();

                                    ////////////////// <!-- DIALOG ADD NEW LIST --> /////////////////////////////////////////////////////////////////////////////////////////////////



                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();



                    return false;
                }
            });
            ////////////////////////////////////////////////////////////////////////////////////////



            return row;
        }
    }


}

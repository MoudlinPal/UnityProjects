package com.example.user.organizer;


import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.organizer.databases.DataBaseLists;
import com.example.user.organizer.databases.DataBaseTasks;

import java.util.ArrayList;
import java.util.Collection;


/***
 * Main Fragment, which adapt for OnNavigationItemClickListener
 */
public class TaskListFragment extends ListFragment{

    public static boolean openedTasksByListClick = false;

    MyTaskAdapter myTaskAdapter;
    MyListAdapter myListAdapter;

    public static boolean setPrimaryNewTask = false;
    public static String idF = "", taskF = "", dateF = "", timeF = "", repeatF = "", listF = "";


    DataBaseTasks dbTasks;
    SQLiteDatabase databaseTasks;

    DataBaseLists dbLists;
    SQLiteDatabase databaseLists;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if(MainMenu.openedMain) setTaskAdapter();
        else if(MainMenu.openedLists) setListAdapter();
    }






    private void setTaskAdapter()
    {
        dbTasks = new DataBaseTasks(getContext());

        /////////////////////// DataBaseTasks ///////////////////////
        databaseTasks = dbTasks.getWritableDatabase(); // доступен для чтения и записи
        /////////////////////// DataBaseTasks ///////////////////////

        if(MainMenu.selectedSpinnerList.compareTo(MainMenu.KEY_ALL_TASKS) == 0) {

            String[] strTasks = DataBaseTasks.getOnlyTasks(databaseTasks);


            ArrayList<String> arrayTasks = new ArrayList<>();
            for(String list: strTasks)
                arrayTasks.add(list);


            myTaskAdapter = new MyTaskAdapter(getActivity(),
                    R.layout.fragment_item_task, arrayTasks);
            setListAdapter(myTaskAdapter);
        }
        else {

            String[] strTasks = DataBaseTasks.getOnlyTasksByList(databaseTasks, MainMenu.selectedSpinnerList);


            ArrayList<String> arrayTasks = new ArrayList<>();
            for(String list: strTasks)
                arrayTasks.add(list);


            myTaskAdapter = new MyTaskAdapter(getActivity(),
                    R.layout.fragment_item_task, arrayTasks);
            setListAdapter(myTaskAdapter);
        }
    }

    private void setListAdapter()
    {
        dbLists = new DataBaseLists(getContext());

        /////////////////////// DataBaseTasks ///////////////////////
        databaseLists = dbLists.getWritableDatabase(); // доступен для чтения и записи
        /////////////////////// DataBaseTasks ///////////////////////

        String[] strLists = DataBaseLists.readDBLists(databaseLists);


        ArrayList<String> arrayLists = new ArrayList<>();
        for(String list: strLists)
            arrayLists.add(list);


        myListAdapter = new MyListAdapter(getActivity(),
                R.layout.fragment_item_list, arrayLists);
        setListAdapter(myListAdapter);

    }









    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task, null);
    }



















    public class MyTaskAdapter extends ArrayAdapter<String> {

        private ArrayList<String> arrayTasks;
        private LayoutInflater inflater;
        private int layout;

        private MyTaskAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);

            this.layout = textViewResourceId;
            this.inflater = LayoutInflater.from(context);

            this.arrayTasks = objects;
        }




        @Override
        @NonNull
        public View getView(final int position, View convertView, ViewGroup parent) {
            // return super.getView(position, convertView, parent);


            ViewHolder viewHolder;

            if(convertView == null) {
                convertView = inflater.inflate(this.layout, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder); // save to Tag
            }
            else{
                viewHolder = (ViewHolder)convertView.getTag();
            }



            ////////////////////////////////////////////////////////////////////////////////////////



            viewHolder.btnLabel.setText(this.arrayTasks.get(position));
            viewHolder.btnLabel.setOnClickListener(new View.OnClickListener() {
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


            viewHolder.check.setTag(viewHolder.btnLabel.getText().toString());
            viewHolder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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


                                    }
                                })
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {

                                        DataBaseTasks.deleteCertainTask(databaseTasks, compoundButton.getTag().toString()); // IT'S TAG OF *check.setTag(btnLabel.getText().toString());*
                                        Toast.makeText(getActivity(), "Задача завершена!", Toast.LENGTH_SHORT).show();
                                        NewTask.wasSomeChangeWithSomeList = true;


                                        arrayTasks.remove(position);
                                        myTaskAdapter.notifyDataSetChanged();


                                    }
                                }).create().show();
                        // <!-- DIALOG "Finish this task?" -->
                    }


                }
            });
            ////////////////////////////////////////////////////////////////////////////////////////

            viewHolder.check.setChecked(false);


            return convertView;
        }


        private class ViewHolder{
            final Button btnLabel;
            final CheckBox check;

            ViewHolder(View v)
            {
                btnLabel = (Button) v.findViewById(R.id.label);
                check = (CheckBox) v.findViewById(R.id.check);
            }
        }


    }



    ///////////////////////////////////////////////////////////////////////////////////////////



    public class MyListAdapter extends ArrayAdapter<String> {

        private ArrayList<String> arrayLists;
        private LayoutInflater inflater;
        private int layout;

        private MyListAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);

            this.arrayLists = objects;

            this.layout = textViewResourceId;
            this.inflater = LayoutInflater.from(context);
        }




        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {


            final ViewHolder viewHolder;

            if(convertView == null) {
                convertView = inflater.inflate(this.layout, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder); // save to Tag
            }
            else{
                viewHolder = (ViewHolder)convertView.getTag();
            }


            ////////////////////////////////////////////////////////////////////////////////////////


            viewHolder.imageBtnSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean canChange = makeLongClick(viewHolder.btnLabelList, viewHolder, arrayLists, position);

                }
            });


            viewHolder.btnLabelList.setText(this.arrayLists.get(position));
            viewHolder.btnLabelList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dbTasks = new DataBaseTasks(getContext());
                    /////////////////////// DataBaseTasks ///////////////////////
                    databaseTasks = dbTasks.getWritableDatabase();
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
            viewHolder.btnLabelList.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    return makeLongClick(v, viewHolder, arrayLists, position);
                }
            });
            ////////////////////////////////////////////////////////////////////////////////////////



            return convertView;
        }


            private class ViewHolder{
                final Button btnLabelList;
                final ImageButton imageBtnSettings;

                ViewHolder(View v){
                    btnLabelList = (Button) v.findViewById(R.id.labelList);
                    imageBtnSettings = (ImageButton) v.findViewById(R.id.image_btn_list_settings);
                }
            }


    }






    private boolean makeLongClick(View v, final MyListAdapter.ViewHolder viewHolder, final ArrayList<String> arrayLists, final int position)
    {
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        dbLists = new DataBaseLists(getContext());
        /////////////////////// DataBaseTasks ///////////////////////
        databaseLists = dbLists.getWritableDatabase();
        /////////////////////// DataBaseTasks ///////////////////////
        dbTasks = new DataBaseTasks(getContext());
        /////////////////////// DataBaseTasks ///////////////////////
        databaseTasks = dbTasks.getWritableDatabase();
        /////////////////////// DataBaseTasks ///////////////////////

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


                        DataBaseLists.deleteList(databaseLists, selectedBtn.getText().toString());
                        DataBaseTasks.deleteTasksByList(databaseTasks, selectedBtn.getText().toString());


                        Toast.makeText(getContext(), "Список \"" + selectedBtn.getText().toString() + "\" удален", Toast.LENGTH_SHORT).show();
                        NewTask.wasSomeChangeWithSomeList = true;

                        arrayLists.remove(position);
                        myListAdapter.notifyDataSetChanged();

                        dialog.cancel();
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
                        editListName.setText(viewHolder.btnLabelList.getText().toString());

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

                                            DataBaseLists.renameList(databaseLists, selectedBtn.getText().toString(), nameList);
                                            DataBaseTasks.renameTasksByList(databaseTasks, selectedBtn.getText().toString(), nameList);



                                            //Dismiss once everything is OK.
                                            dialogAddList.dismiss();

                                            selectedBtn.setText(nameList);

                                            Toast toast = Toast.makeText(getContext(), "Название изменено!", Toast.LENGTH_SHORT);
                                            toast.show();
                                            NewTask.wasSomeChangeWithSomeList = true;


                                            arrayLists.set(position, nameList);
                                            myListAdapter.notifyDataSetChanged();


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
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }










}

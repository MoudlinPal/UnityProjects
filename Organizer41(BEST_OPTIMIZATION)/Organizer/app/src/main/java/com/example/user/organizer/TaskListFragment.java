package com.example.user.organizer;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.organizer.databases.DataBaseFinishedTasks;
import com.example.user.organizer.databases.DataBaseLists;
import com.example.user.organizer.databases.DataBaseTasks;


import java.util.ArrayList;
import java.util.Calendar;



/***
 * Main Fragment, which adapt for OnNavigationItemClickListener
 */
public class TaskListFragment extends ListFragment{

    public static boolean ToolbarButtonBackIsPressed = false;

    public static boolean openedTasksByListClick = false;

    MyTaskAdapter myTaskAdapter;
    MyListAdapter myListAdapter;
    MyFTasksAdapter myFTasksAdapter;

    public static boolean setPrimaryNewTask = false;
    public static String idF = "", taskF = "", dateF = "", timeF = "", repeatF = "", listF = "";


    DataBaseTasks dbTasks;
    SQLiteDatabase databaseTasks;

    DataBaseLists dbLists;
    SQLiteDatabase databaseLists;

    DataBaseFinishedTasks dbFTasks;
    SQLiteDatabase databaseFTasks;


    //ListView listView;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(MainMenu.openedMain) setTaskAdapter();
        else if(MainMenu.openedLists) setListAdapter();
        else if(MainMenu.openedFTasks) setFTasksAdapter();
    }






    private void setTaskAdapter()
    {
        dbTasks = new DataBaseTasks(getContext());
        databaseTasks = dbTasks.getWritableDatabase(); // доступен для чтения и записи



        if(MainMenu.NeedUpdateTaskAdapterFromToolbarTitle){         // чтобы правильлно обновить ТаскАдаптер
            MainMenu.NeedUpdateTaskAdapterFromToolbarTitle = false; // после изменения списка задачи
            MainMenu.selectedSpinnerList = MainMenu.TitleToolbar;
        }

        if(MainMenu.selectedSpinnerList.compareTo(MainMenu.KEY_ALL_TASKS) == 0) {


            ArrayList<String> arrayTasks = DataBaseTasks.getSortedTasksWithArrayList(databaseTasks);
            ArrayList<Long> longTasks = DataBaseTasks.getSortedTimeInMillisWithArrayList(databaseTasks);
            ArrayList<String> arrayDate = DataBaseTasks.getSortedDateWithArrayList(databaseTasks);
            ArrayList<String> arrayTime = DataBaseTasks.getSortedTimeWithArrayList(databaseTasks);
            ArrayList<String> arrayRepeat = DataBaseTasks.getSortedRepeatWithArrayList(databaseTasks);


            myTaskAdapter = new MyTaskAdapter(getActivity(),
                    R.layout.fragment_item_task, arrayTasks, longTasks, arrayDate, arrayTime, arrayRepeat);
            setListAdapter(myTaskAdapter);
        }
        else {


            ArrayList<String> arrayTasks = DataBaseTasks.getSortedTasksByListWithArrayList(databaseTasks, MainMenu.selectedSpinnerList);
            ArrayList<Long> longTasks = DataBaseTasks.getSortedTimeInMillisByListWithArrayList(databaseTasks, MainMenu.selectedSpinnerList);
            ArrayList<String> arrayDate = DataBaseTasks.getSortedDateByListWithArrayList(databaseTasks, MainMenu.selectedSpinnerList);
            ArrayList<String> arrayTime = DataBaseTasks.getSortedTimeByListWithArrayList(databaseTasks, MainMenu.selectedSpinnerList);
            ArrayList<String> arrayRepeat = DataBaseTasks.getSortedRepeatByListWithArrayList(databaseTasks, MainMenu.selectedSpinnerList);


            myTaskAdapter = new MyTaskAdapter(getActivity(),
                    R.layout.fragment_item_task, arrayTasks, longTasks, arrayDate, arrayTime, arrayRepeat);
            setListAdapter(myTaskAdapter);
        }
    }

    private void setListAdapter()
    {

        dbLists = new DataBaseLists(getContext());
        databaseLists = dbLists.getWritableDatabase(); // доступен для чтения и записи

        String[] strLists = DataBaseLists.readDBLists(databaseLists);


        ArrayList<String> arrayLists = new ArrayList<>();
        for(String list: strLists)
            arrayLists.add(list);


        myListAdapter = new MyListAdapter(getActivity(),
                R.layout.fragment_item_list, arrayLists);
        setListAdapter(myListAdapter);

    }

    private void setFTasksAdapter(){

        dbFTasks = new DataBaseFinishedTasks(getContext());
        databaseFTasks = dbFTasks.getWritableDatabase(); // доступен для чтения и записи


        ArrayList<String> arrayFTasks = DataBaseFinishedTasks.getOnlyTasksWithArrayList(databaseFTasks);
        ArrayList<String> arrayFDate = DataBaseFinishedTasks.getOnlyDateWithArrayList(databaseFTasks);
        ArrayList<String> arrayFTime = DataBaseFinishedTasks.getOnlyTimeWithArrayList(databaseFTasks);


        myFTasksAdapter = new MyFTasksAdapter(getActivity(),
                R.layout.fragment_item_finished_task, arrayFTasks, arrayFDate, arrayFTime);
        setListAdapter(myFTasksAdapter);

    }








    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //LayoutInflater inflater2 = LayoutInflater.from(getContext());
        //View contentView = inflater2.inflate(R.layout.fragment_task, null,false);
        //listView = contentView.findViewWithTag("MyListViewTag");

        return inflater.inflate(R.layout.fragment_task, null);
    }




















    public class MyTaskAdapter extends ArrayAdapter<String> {

        private ArrayList<String> arrayTasks;
        private ArrayList<Long> arrayTimeInMillis;
        private ArrayList<String> arrayDate;
        private ArrayList<String> arrayTime;
        private ArrayList<String> arrayRepeat;
        private LayoutInflater inflater;
        private int layout;

        private MyTaskAdapter(Context context, int textViewResourceId, ArrayList<String> objects, ArrayList<Long> objTimeInMillis,
                              ArrayList<String> objDate, ArrayList<String> objTime, ArrayList<String> objRepeat) {
            super(context, textViewResourceId, objects);

            this.layout = textViewResourceId;
            this.inflater = LayoutInflater.from(context);
            this.arrayTasks = objects;
            this.arrayTimeInMillis = objTimeInMillis;
            this.arrayDate = objDate;
            this.arrayTime = objTime;
            this.arrayRepeat = objRepeat;
        }




        @Override
        @NonNull
        public View getView(final int position, View convertView, ViewGroup parent) {
            // return super.getView(position, convertView, parent);


            final ViewHolder viewHolder;
            //final View convertViewAnim;

            if (convertView == null) {
                convertView = inflater.inflate(this.layout, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder); // save to Tag
            } else if (((ViewHolder) convertView.getTag()).needInflate) { // FOR ANIMATION
                convertView = inflater.inflate(this.layout, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder); // save to Tag
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //convertViewAnim = convertView;


            ////////////////////////////////////////////////////////////////////////////////////////


            viewHolder.btnLabel.setText(MyTaskAdapter.this.arrayTasks.get(position));
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
                    startActivity(new Intent(getContext(), NewTask.class));
                }
            });


            viewHolder.dateOfItemTask.setText(arrayDate.get(position)); // get and set DATE
            viewHolder.timeOfItemTask.setText(arrayTime.get(position)); // get and set DATE

            if (viewHolder.dateOfItemTask.getText().toString().compareTo("Дата не установлена") == 0) {

                viewHolder.dateOfItemTask.setTextColor(getContext().getColor(R.color.SecondaryText));

            } else if ((System.currentTimeMillis()) < arrayTimeInMillis.get(position)) {

                viewHolder.dateOfItemTask.setTextColor(getContext().getColor(R.color.PrimaryColor));
                viewHolder.timeOfItemTask.setTextColor(getContext().getColor(R.color.PrimaryColor));

            } else {

                viewHolder.dateOfItemTask.setTextColor(getContext().getColor(R.color.OverdueColor));
                viewHolder.timeOfItemTask.setTextColor(getContext().getColor(R.color.OverdueColor));

            }


            ////////////////////////////////////////////////////////////////////////////////////////
            // Set Type of Tasks



                    if (position == 0) {

                        //String repeat = DataBaseTasks.getCertainTask(databaseTasks, this.arrayTasks.get(position))[3];

                        //long establishedData = arrayTimeInMillis.get(position);

                        Calendar currentCal = Calendar.getInstance();

                        Calendar establishedCalendar = Calendar.getInstance();
                        establishedCalendar.setTimeInMillis(arrayTimeInMillis.get(position));

                        if (arrayDate.get(position).compareTo("Дата не установлена") == 0) {

                            viewHolder.typeTasks.setVisibility(View.VISIBLE);
                            viewHolder.typeTasks.setText("Нет даты");
                            viewHolder.typeTasks.setTextColor(getContext().getColor(R.color.SecondaryText));

                        } else if (currentCal.get(Calendar.DAY_OF_YEAR) <= establishedCalendar.get(Calendar.DAY_OF_YEAR) &&
                                currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR)) {

                            if (currentCal.get(Calendar.DAY_OF_YEAR) == establishedCalendar.get(Calendar.DAY_OF_YEAR) &&
                                    currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR)) {

                                viewHolder.typeTasks.setVisibility(View.VISIBLE);
                                viewHolder.typeTasks.setText("Сегодня");
                                viewHolder.typeTasks.setTextColor(getContext().getColor(R.color.PrimaryColor));

                            } else if ((currentCal.get(Calendar.DAY_OF_YEAR) + 1) == establishedCalendar.get(Calendar.DAY_OF_YEAR) &&
                                    currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR)) {

                                viewHolder.typeTasks.setVisibility(View.VISIBLE);
                                viewHolder.typeTasks.setText("Завтра");
                                viewHolder.typeTasks.setTextColor(getContext().getColor(R.color.PrimaryColor));

                            } else if ((currentCal.get(Calendar.DAY_OF_YEAR) + 2) == establishedCalendar.get(Calendar.DAY_OF_YEAR) &&
                                    currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR)) {

                                viewHolder.typeTasks.setVisibility(View.VISIBLE);
                                viewHolder.typeTasks.setText("Послезавтра");
                                viewHolder.typeTasks.setTextColor(getContext().getColor(R.color.PrimaryColor));

                            } else if ((currentCal.get(Calendar.DAY_OF_YEAR) + 2) < establishedCalendar.get(Calendar.DAY_OF_YEAR) &&
                                    currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR)) {

                                viewHolder.typeTasks.setVisibility(View.VISIBLE);
                                viewHolder.typeTasks.setText("Этот месяц");
                                viewHolder.typeTasks.setTextColor(getContext().getColor(R.color.PrimaryColor));

                            } else if (((((currentCal.get(Calendar.MONTH) + 1) == (establishedCalendar.get(Calendar.MONTH))) &&
                                    currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR)) ||
                                    (((currentCal.get(Calendar.MONTH) + 1) == 12 && (establishedCalendar.get(Calendar.MONTH)) == 0) &&
                                            currentCal.get(Calendar.YEAR) == (establishedCalendar.get(Calendar.YEAR) + 1)))) {

                                viewHolder.typeTasks.setVisibility(View.VISIBLE);
                                viewHolder.typeTasks.setText("Следующий месяц");
                                viewHolder.typeTasks.setTextColor(getContext().getColor(R.color.PrimaryColor));

                            } else if (currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR)) {

                                viewHolder.typeTasks.setVisibility(View.VISIBLE);
                                viewHolder.typeTasks.setText("В этом году");
                                viewHolder.typeTasks.setTextColor(getContext().getColor(R.color.PrimaryColor));

                            } else if (currentCal.get(Calendar.YEAR) != establishedCalendar.get(Calendar.YEAR)) {

                                viewHolder.typeTasks.setVisibility(View.VISIBLE);
                                viewHolder.typeTasks.setText("Более поздние");
                                viewHolder.typeTasks.setTextColor(getContext().getColor(R.color.PrimaryColor));

                            } else {
                                viewHolder.typeTasks.setVisibility(View.GONE);
                            }


                        } else if (currentCal.getTimeInMillis() > arrayTimeInMillis.get(position)) {

                            viewHolder.typeTasks.setVisibility(View.VISIBLE);
                            viewHolder.typeTasks.setText("Просроченные");
                            viewHolder.typeTasks.setTextColor(getContext().getColor(R.color.OverdueColor));

                        } else {
                            viewHolder.typeTasks.setVisibility(View.GONE);
                        }
                    } else {

                        //String repeat = DataBaseTasks.getCertainTask(databaseTasks, this.arrayTasks.get(position))[3];

                        //long establishedData = Long.valueOf(DataBaseTasks.getCertainTask(databaseTasks, MyTaskAdapter.this.arrayTasks.get(position))[6]);
                        //long establishedDataPrevious = Long.valueOf(DataBaseTasks.getCertainTask(databaseTasks, MyTaskAdapter.this.arrayTasks.get(position - 1))[6]);

                        //String dateOfItemTaskPrevious = arrayDate.get(position-1);

                        Calendar currentCal = Calendar.getInstance();

                        Calendar establishedCalendar = Calendar.getInstance();
                        establishedCalendar.setTimeInMillis(arrayTimeInMillis.get(position));

                        Calendar establishedCalendarPrevious = Calendar.getInstance();
                        establishedCalendarPrevious.setTimeInMillis(arrayTimeInMillis.get(position-1));


                        if (arrayDate.get(position).compareTo("Дата не установлена") == 0 &&
                                arrayDate.get(position-1).compareTo("Дата не установлена") != 0) {

                            viewHolder.typeTasks.setVisibility(View.VISIBLE);
                            viewHolder.typeTasks.setText("Нет даты");
                            viewHolder.typeTasks.setTextColor(getContext().getColor(R.color.SecondaryText));

                        } else if (currentCal.get(Calendar.DAY_OF_YEAR) <= establishedCalendar.get(Calendar.DAY_OF_YEAR) &&
                                currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR)) {

                            if ((currentCal.get(Calendar.DAY_OF_YEAR) == establishedCalendar.get(Calendar.DAY_OF_YEAR) &&
                                    currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR))
                                    &&
                                    !(currentCal.get(Calendar.DAY_OF_YEAR) == establishedCalendarPrevious.get(Calendar.DAY_OF_YEAR) &&
                                            currentCal.get(Calendar.YEAR) == establishedCalendarPrevious.get(Calendar.YEAR))
                                    ) {

                                viewHolder.typeTasks.setVisibility(View.VISIBLE);
                                viewHolder.typeTasks.setText("Сегодня");
                                viewHolder.typeTasks.setTextColor(getContext().getColor(R.color.PrimaryColor));

                            } else if (((currentCal.get(Calendar.DAY_OF_YEAR) + 1) == establishedCalendar.get(Calendar.DAY_OF_YEAR) &&
                                    currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR))
                                    &&
                                    !((currentCal.get(Calendar.DAY_OF_YEAR) + 1) == establishedCalendarPrevious.get(Calendar.DAY_OF_YEAR) &&
                                            currentCal.get(Calendar.YEAR) == establishedCalendarPrevious.get(Calendar.YEAR))) {

                                viewHolder.typeTasks.setVisibility(View.VISIBLE);
                                viewHolder.typeTasks.setText("Завтра");
                                viewHolder.typeTasks.setTextColor(getContext().getColor(R.color.PrimaryColor));

                            } else if (((currentCal.get(Calendar.DAY_OF_YEAR) + 2) == establishedCalendar.get(Calendar.DAY_OF_YEAR) &&
                                    currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR))
                                    &&
                                    !((currentCal.get(Calendar.DAY_OF_YEAR) + 2) == establishedCalendarPrevious.get(Calendar.DAY_OF_YEAR) &&
                                            currentCal.get(Calendar.YEAR) == establishedCalendarPrevious.get(Calendar.YEAR))) {

                                viewHolder.typeTasks.setVisibility(View.VISIBLE);
                                viewHolder.typeTasks.setText("Послезавтра");
                                viewHolder.typeTasks.setTextColor(getContext().getColor(R.color.PrimaryColor));

                            } else if (((currentCal.get(Calendar.DAY_OF_YEAR) + 2) < establishedCalendar.get(Calendar.DAY_OF_YEAR) &&
                                    ((currentCal.get(Calendar.MONTH)) == (establishedCalendar.get(Calendar.MONTH))) &&
                                    currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR))
                                    &&
                                    !((currentCal.get(Calendar.MONTH) + 1) == (establishedCalendar.get(Calendar.MONTH)))
                                    &&
                                    !((currentCal.get(Calendar.DAY_OF_YEAR) + 2) < establishedCalendarPrevious.get(Calendar.DAY_OF_YEAR) &&
                                            currentCal.get(Calendar.YEAR) == establishedCalendarPrevious.get(Calendar.YEAR))) {

                                viewHolder.typeTasks.setVisibility(View.VISIBLE);
                                viewHolder.typeTasks.setText("Этот месяц");
                                viewHolder.typeTasks.setTextColor(getContext().getColor(R.color.PrimaryColor));

                            } else if (((((currentCal.get(Calendar.MONTH) + 1) == (establishedCalendar.get(Calendar.MONTH))) &&
                                    currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR)) ||
                                    (((currentCal.get(Calendar.MONTH)) == Calendar.DECEMBER && (establishedCalendar.get(Calendar.MONTH)) == Calendar.JANUARY) &&
                                            currentCal.get(Calendar.YEAR) == (establishedCalendar.get(Calendar.YEAR) + 1)))
                                    &&
                                    !((((currentCal.get(Calendar.MONTH) + 1) == (establishedCalendarPrevious.get(Calendar.MONTH))) &&
                                            currentCal.get(Calendar.YEAR) == establishedCalendarPrevious.get(Calendar.YEAR)) ||
                                            (((currentCal.get(Calendar.MONTH)) == Calendar.DECEMBER && (establishedCalendarPrevious.get(Calendar.MONTH)) == Calendar.JANUARY) &&
                                                    currentCal.get(Calendar.YEAR) == (establishedCalendarPrevious.get(Calendar.YEAR) + 1)))) {

                                viewHolder.typeTasks.setVisibility(View.VISIBLE);
                                viewHolder.typeTasks.setText("Следующий месяц");
                                viewHolder.typeTasks.setTextColor(getContext().getColor(R.color.PrimaryColor));

                            } else if ((currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR))
                                    &&
                                    (currentCal.get(Calendar.MONTH) + 1) < establishedCalendar.get(Calendar.MONTH)
                                    &&
                                    !((currentCal.get(Calendar.MONTH) + 1) < establishedCalendarPrevious.get(Calendar.MONTH))) {

                                viewHolder.typeTasks.setVisibility(View.VISIBLE);
                                viewHolder.typeTasks.setText("В этом году");
                                viewHolder.typeTasks.setTextColor(getContext().getColor(R.color.PrimaryColor));

                            } else if ((currentCal.get(Calendar.YEAR) != establishedCalendar.get(Calendar.YEAR))
                                    &&
                                    !(currentCal.get(Calendar.YEAR) != establishedCalendarPrevious.get(Calendar.YEAR))) {

                                viewHolder.typeTasks.setVisibility(View.VISIBLE);
                                viewHolder.typeTasks.setText("Более поздние");
                                viewHolder.typeTasks.setTextColor(getContext().getColor(R.color.PrimaryColor));

                            } else {
                                viewHolder.typeTasks.setVisibility(View.GONE);
                            }

                        } else if (currentCal.getTimeInMillis() > arrayTimeInMillis.get(position) && !(currentCal.getTimeInMillis() > arrayTimeInMillis.get(position-1))) {

                            viewHolder.typeTasks.setVisibility(View.VISIBLE);
                            viewHolder.typeTasks.setText("Просроченные");
                            viewHolder.typeTasks.setTextColor(getContext().getColor(R.color.OverdueColor));
                        } else {
                            viewHolder.typeTasks.setVisibility(View.GONE);
                        }
                    }





            ////////////////////////////////////////////////////////////////////////////////////////


            if (MainMenu.selectedSpinnerList.compareTo(MainMenu.KEY_ALL_TASKS) == 0 && !openedTasksByListClick) {
                //viewHolder.btnLabel.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_empty_camera), null, null, getResources().getDrawable(R.drawable.ic_empty_camera));
                viewHolder.listOfItemTask.setText(DataBaseTasks.getCertainTask(databaseTasks, viewHolder.btnLabel.getText().toString())[4]);
            }


            ////////////////////////////////////////////////////////////////////////////////////////


            viewHolder.check.setTag(R.id.textView_list_of_itemTask, viewHolder.listOfItemTask.getText().toString());
            viewHolder.check.setTag(viewHolder.btnLabel.getText().toString());
            viewHolder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final CompoundButton compoundButton, boolean b) {

                    if (b) {
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

                                        DataBaseFinishedTasks dbFinishedTasks = new DataBaseFinishedTasks(getContext());
                                        SQLiteDatabase databaseFinishedTasks = dbFinishedTasks.getWritableDatabase();

                                        DataBaseTasks.deleteCertainTask(databaseTasks, databaseFinishedTasks, compoundButton.getTag().toString()); // IT'S TAG OF *check.setTag(btnLabel.getText().toString());*
                                        Toast.makeText(getContext(), "Задача завершена!", Toast.LENGTH_SHORT).show();


                                        ((MainMenu) getContext()).sendDefaultNotification();


                                        ((MainMenu) getContext()).sizeAllTasks--;
                                        ((MainMenu) getContext()).adapterSpinner.notifyDataSetChanged();

                                        if (openedTasksByListClick) {
                                            String list = ((MainMenu) getContext()).toolbar.getTitle().toString();

                                            DataBaseLists dbListsssss = new DataBaseLists(getContext());    // Ошибку ниже пофиксил только полностью обновив обьекты
                                            SQLiteDatabase databaseli = dbListsssss.getWritableDatabase();  // базы данных !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                                            String[] lists = DataBaseLists.readDBLists(databaseli);         // here error

                                            int index = 0;

                                            for (int i = 0; i < lists.length; i++) {
                                                if (lists[i].compareTo(list) == 0) {
                                                    index = i + 1;
                                                    break;
                                                }
                                            }

                                            ((MainMenu) getContext()).listsSize.set(index, (((MainMenu) getContext()).listsSize.get(index) - 1));
                                            ((MainMenu) getContext()).adapterSpinner.notifyDataSetChanged();


                                        } else {
                                            if (MainMenu.selectedSpinnerList.compareTo(MainMenu.KEY_ALL_TASKS) != 0) {
                                                ((MainMenu) getContext()).listsSize.set(MainMenu.lastSpinnerSelection, (((MainMenu) getContext()).listsSize.get(MainMenu.lastSpinnerSelection) - 1));
                                                ((MainMenu) getContext()).adapterSpinner.notifyDataSetChanged();
                                            } else {
                                                String list = compoundButton.getTag(R.id.textView_list_of_itemTask).toString();

                                                DataBaseLists dbListsssss = new DataBaseLists(getContext());    // Ошибку ниже пофиксил только полностью обновив обьекты
                                                SQLiteDatabase databaseli = dbListsssss.getWritableDatabase();  // базы данных !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                                                String[] lists = DataBaseLists.readDBLists(databaseli);         // here error

                                                int index = 0;

                                                for (int i = 0; i < lists.length; i++) {
                                                    if (lists[i].compareTo(list) == 0) {
                                                        index = i + 1;
                                                        break;
                                                    }
                                                }


                                                ((MainMenu) getContext()).listsSize.set(index, (((MainMenu) getContext()).listsSize.get(index) - 1));
                                                ((MainMenu) getContext()).adapterSpinner.notifyDataSetChanged();
                                            }
                                        }


                                        /** THIS ANIMATION IS WORKED */

                                        /*Animation anim = android.view.animation.AnimationUtils.loadAnimation(convertViewAnim.getContext(), R.anim.slide_to_right);
                                        convertViewAnim.startAnimation(anim);
                                        anim.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {


                                                //arrayTasks.remove(position);
                                                //myTaskAdapter.notifyDataSetChanged();
                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //setTaskAdapter();
                                                        arrayTasks.remove(position);
                                                        myTaskAdapter.notifyDataSetChanged();
                                                    }
                                                }, 100);

                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {
                                            }
                                        });*/


                                        /** THIS ANIMATION IS WORKED (Alternative Variant) */

                                        //deleteCell(convertViewAnim, position);


                                        /**  WITHOUT ANIMATION  */

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

            ////////////////////////////////////////////////////////////////////////////////////////


            if (arrayRepeat.get(position).compareTo(DataBaseLists.listRepeatNames[0]) == 0)
                viewHolder.imageViewRepeat.setVisibility(View.GONE);
            else
                viewHolder.imageViewRepeat.setVisibility(View.VISIBLE);


            /** BEST ANIMATION OF APPEARANCE OF LIST ITEMS EVER !!!!!!!!!!!!!!!!!!! */


            AlphaAnimation alpha = new AlphaAnimation(0.0F, 1.0F);
            alpha.setDuration(400); // Make animation instant
            alpha.setFillAfter(true); // Tell it to persist after the animation ends
            convertView.startAnimation(alpha);


            return convertView;
        }





        private class ViewHolder{
            public boolean needInflate;
            final Button btnLabel;
            final CheckBox check;
            final TextView dateOfItemTask;
            final TextView timeOfItemTask;
            final TextView typeTasks;
            final ImageView imageViewRepeat;
            final TextView listOfItemTask;

            ViewHolder(View v)
            {
                btnLabel = v.findViewById(R.id.label);
                check = v.findViewById(R.id.check);
                dateOfItemTask = v.findViewById(R.id.textView_date_of_itemTask);
                timeOfItemTask = v.findViewById(R.id.textView_time_of_itemTask);
                typeTasks = v.findViewById(R.id.textView_type_tasks);
                imageViewRepeat = v.findViewById(R.id.imageView_item_repeat);
                listOfItemTask = v.findViewById(R.id.textView_list_of_itemTask);
            }
        }










        private void deleteCell(final View v, final int position) {
            Animation.AnimationListener al = new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation arg0) {
                    arrayTasks.remove(position);

                    ViewHolder vh = (ViewHolder)v.getTag();
                    vh.needInflate = true;

                    myTaskAdapter.notifyDataSetChanged();
                }
                @Override public void onAnimationRepeat(Animation animation) {}
                @Override public void onAnimationStart(Animation animation) {}
            };

            collapse(v, al);
        }

        private void collapse(final View v, Animation.AnimationListener al) {
            final int initialHeight = v.getMeasuredHeight();

            Animation anim = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if (interpolatedTime == 1) {
                        v.setVisibility(View.GONE);
                    }
                    else {
                        v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                        v.requestLayout();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            if (al!=null) {
                anim.setAnimationListener(al);
            }
            anim.setDuration(200L);
            v.startAnimation(anim);
        }









    }




    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




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

            //final View convertViewAnim;

            if (convertView == null) {
                convertView = inflater.inflate(this.layout, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder); // save to Tag
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //convertViewAnim = convertView;


            ////////////////////////////////////////////////////////////////////////////////////////
            dbTasks = new DataBaseTasks(getContext());
            /////////////////////// DataBaseTasks ///////////////////////
            databaseTasks = dbTasks.getWritableDatabase();
            /////////////////////// DataBaseTasks ///////////////////////


            String kolvo = Integer.toString(DataBaseTasks.getAmountOfTasksByList(databaseTasks, MyListAdapter.this.arrayLists.get(position)));
            if (kolvo.compareTo("0") == 0 || kolvo.compareTo("null") == 0) {
                viewHolder.amountOfTasks.setText("Нет задач");
                viewHolder.amountOfTasks.setTextColor(getContext().getColor(R.color.SecondaryText));
            } else {
                String newKolvo = "Кол-во задач: " + kolvo;
                viewHolder.amountOfTasks.setText(newKolvo);
                viewHolder.amountOfTasks.setTextColor(getContext().getColor(R.color.PrimaryColor));
            }


            viewHolder.btnLabelList.setText(MyListAdapter.this.arrayLists.get(position));
            viewHolder.btnLabelList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dbTasks = new DataBaseTasks(getContext());
                    /////////////////////// DataBaseTasks ///////////////////////
                    databaseTasks = dbTasks.getWritableDatabase();
                    /////////////////////// DataBaseTasks ///////////////////////

                    if (DataBaseTasks.getAmountOfTasksByList(databaseTasks, ((Button) v).getText().toString()) == 0) {
                        Toast.makeText(getContext(), "Список пуст!", Toast.LENGTH_SHORT).show();
                    } else {

                        final Activity MM = ((MainMenu) getContext());

                        ((MainMenu) getContext()).toolbar.getMenu().clear();

                        ///////////////////////////// Toolbar Button BACK /////////////////////////////
                        ((MainMenu) getContext()).toolbar.setNavigationIcon(R.drawable.ic_action_back_arrow);
                        ((MainMenu) getContext()).toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                ToolbarButtonBackIsPressed = true;
                                MM.onBackPressed();

                            }
                        });
                        ///////////////////////////// Toolbar Button BACK /////////////////////////////


                        ((MainMenu) getContext()).toolbar.setTitle(((Button) v).getText().toString());
                        ((MainMenu) getContext()).toolbar.setTitleTextColor(((MainMenu) getContext()).getColor(R.color.JustWhite));


                        openedTasksByListClick = true;
                        MainMenu.OpenedMain();
                        MainMenu.selectedSpinnerList = ((Button) v).getText().toString(); // way to open tasks by click on list
                        setTaskAdapter();
                    }
                }
            });
            /*viewHolder.btnLabelList.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    return makeLongClick(v, viewHolder, arrayLists, position);
                }
            });*/
            ////////////////////////////////////////////////////////////////////////////////////////


            viewHolder.imageBtnDeleteList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dbLists = new DataBaseLists(getContext());
                    /////////////////////// DataBaseTasks ///////////////////////
                    databaseLists = dbLists.getWritableDatabase();
                    /////////////////////// DataBaseTasks ///////////////////////
                    dbTasks = new DataBaseTasks(getContext());
                    /////////////////////// DataBaseTasks ///////////////////////
                    databaseTasks = dbTasks.getWritableDatabase();
                    /////////////////////// DataBaseTasks ///////////////////////

                    boolean DefaultList = false;


                    // IF PRESSED DEFAULT LIST BUTTON
                    for (int i = 0; i < DataBaseLists.defaultListsNames.length; i++) {
                        if (DataBaseLists.defaultListsNames[i].compareTo(arrayLists.get(position)) == 0) {
                            Toast.makeText(getContext(), "Нельзя удалить этот список!", Toast.LENGTH_SHORT).show();
                            DefaultList = true;
                        }
                    }
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    if (!DefaultList) {

                        // <!-- DIALOG ARE U SURE? -->
                        new AlertDialog.Builder(getContext())
                                .setTitle("Вы уверены?")
                                .setMessage("Список \"" + arrayLists.get(position) + "\" будет удален.")
                                .setNegativeButton(R.string.no, null)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {

                                        String list = arrayLists.get(position);

                                        Toast.makeText(getContext(), "Список \"" + list + "\" удален", Toast.LENGTH_SHORT).show();


                                        /*DataBaseLists dbListsssss = new DataBaseLists(getContext());    // Ошибку ниже пофиксил только полностью обновив обьекты
                                        SQLiteDatabase databaseli = dbListsssss.getWritableDatabase();  // базы данных !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                                        String[] lists = DataBaseLists.readDBLists(databaseli);         // here error


                                        int index = 0;

                                        for (int i = 0; i < lists.length; i++) {
                                            if (lists[i].compareTo(list) == 0) {
                                                index = i + 1;
                                                break;
                                            }
                                        }*/

                                        /*
                                        ((MainMenu)getActivity()).lists.remove(index);
                                        ((MainMenu)getActivity()).adapterSpinner.notifyDataSetChanged();

                                        ((MainMenu)getActivity()).listsSize.remove(index);
                                        ((MainMenu)getActivity()).adapterSpinner.notifyDataSetChanged();

                                        ((MainMenu)getActivity()).sizeAllTasks -= DataBaseTasks.getOnlyTasksByList(databaseTasks, list).length;
                                        ((MainMenu)getActivity()).adapterSpinner.notifyDataSetChanged();
                                        */


                                        /*Animation anim = android.view.animation.AnimationUtils.loadAnimation(convertViewAnim.getContext(), R.anim.slide_to_left);
                                        convertViewAnim.startAnimation(anim);
                                        anim.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {


                                                //arrayLists.remove(position);
                                                //myListAdapter.notifyDataSetChanged();
                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //setListAdapter();
                                                        arrayLists.remove(position);
                                                        myListAdapter.notifyDataSetChanged();
                                                    }
                                                }, 100);

                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {
                                            }
                                        });*/


                                        DataBaseFinishedTasks dbFinishedTasks = new DataBaseFinishedTasks(getContext());
                                        SQLiteDatabase databaseFinishedTasks = dbFinishedTasks.getWritableDatabase();

                                        DataBaseLists.deleteList(databaseLists, list);
                                        DataBaseTasks.deleteTasksByList(databaseTasks, databaseFinishedTasks, list);



                                        /**  WITHOUT ANIMATION  */

                                        arrayLists.remove(position);
                                        myListAdapter.notifyDataSetChanged();



                                        arg0.cancel();

                                    }
                                }).create().show();
                        // <!-- DIALOG ARE U SURE? -->

                    }

                }
            });


            viewHolder.imageBtnRenameList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dbLists = new DataBaseLists(getContext());
                    /////////////////////// DataBaseTasks ///////////////////////
                    databaseLists = dbLists.getWritableDatabase();
                    /////////////////////// DataBaseTasks ///////////////////////
                    dbTasks = new DataBaseTasks(getContext());
                    /////////////////////// DataBaseTasks ///////////////////////
                    databaseTasks = dbTasks.getWritableDatabase();
                    /////////////////////// DataBaseTasks ///////////////////////


                    boolean DefaultList = false;


                    // IF PRESSED DEFAULT LIST BUTTON
                    for (int i = 0; i < DataBaseLists.defaultListsNames.length; i++) {
                        if (DataBaseLists.defaultListsNames[i].compareTo(arrayLists.get(position)) == 0) {
                            Toast.makeText(getContext(), "Нельзя переименовать этот список!", Toast.LENGTH_SHORT).show();
                            DefaultList = true;
                        }
                    }
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    if (!DefaultList) {

                        //Получаем вид с файла dialog_new_list.xml, который применим для диалогового окна:
                        LayoutInflater li = LayoutInflater.from(getContext()); // this = context !!!!!!!!!!!!!!!!!!!
                        View viewDialogNewList = li.inflate(R.layout.dialog_new_list, null);

                        final EditText editListName =
                                (EditText) viewDialogNewList.findViewById(R.id.editListName); // !!!!!!!!!
                        ///////////////////////////////////////////////////////////////////////////////
                        editListName.setText(viewHolder.btnLabelList.getText().toString());
                        editListName.setSelection(editListName.getText().length());
                        ////////////////// <!-- DIALOG ADD NEW LIST --> //////////////////

                        final AlertDialog dialogAddList = new AlertDialog.Builder(getContext())
                                .setView(viewDialogNewList)
                                .setTitle("Изменить название списка")
                                .setPositiveButton("Изменить", null) //Set to null. We override the onclick
                                .setNegativeButton("Отменить", null)
                                .create();

                        ((MainMenu) getContext()).showKeyboard();

                        dialogAddList.setOnShowListener(new DialogInterface.OnShowListener() {

                            @Override
                            public void onShow(DialogInterface dialog) {

                                Button button2 = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                button2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        ((MainMenu) getContext()).showKeyboard();
                                        //Dismiss once everything is OK.
                                        dialogAddList.dismiss();

                                    }
                                });


                                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                button.setOnClickListener(new View.OnClickListener() {


                                    @Override
                                    public void onClick(View view) {

                                        // for DataBase !!!!!!!!!
                                        final String nameList = editListName.getEditableText().toString();

                                        String bufDivider = "";
                                        bufDivider += DataBaseLists.DIVIDER;

                                        if (DataBaseLists.checkListPresence(databaseLists, nameList)) {
                                            Toast toast = Toast.makeText(getContext(), "Такой список уже существует!", Toast.LENGTH_SHORT);
                                            toast.show();
                                        } else if (nameList.compareTo("") == 0) // !!!!!!!!!!!!!
                                        {
                                            //makeVibration();

                                            Toast toast = Toast.makeText(getContext(), "Введите название списка!", Toast.LENGTH_SHORT);
                                            toast.show();
                                        } else if (nameList.contains(bufDivider)) {
                                            //makeVibration();

                                            Toast toast = Toast.makeText(getContext(), "Символ '" + DataBaseLists.DIVIDER + "' недопустим!", Toast.LENGTH_SHORT);
                                            toast.show();
                                        } else {


                                            ((MainMenu) getContext()).showKeyboard();


                                            DataBaseLists.renameList(databaseLists, arrayLists.get(position), nameList);
                                            DataBaseTasks.renameTasksByList(databaseTasks, arrayLists.get(position), nameList);


                                            //Dismiss once everything is OK.
                                            dialogAddList.dismiss();

                                            //selectedBtn.setText(nameList);

                                            Toast toast = Toast.makeText(getContext(), "Название изменено!", Toast.LENGTH_SHORT);
                                            toast.show();


                                            arrayLists.set(position, nameList);
                                            myListAdapter.notifyDataSetChanged();


                                            /*
                                            ((MainMenu)getActivity()).lists.set(position, nameList);
                                            ((MainMenu)getActivity()).adapterSpinner.notifyDataSetChanged();
                                            */


                                        }
                                        ///////////////////////////////////////////////////////////////////////////////
                                    }
                                });
                            }
                        });

                        dialogAddList.show();

                        ////////////////// <!-- DIALOG ADD NEW LIST --> /////////////////////////////////////////////////////////////////////////////////////////////////
                    }
                }
            });


            AlphaAnimation alpha = new AlphaAnimation(0.0F, 1.0F);
            alpha.setDuration(450); // Make animation instant
            alpha.setFillAfter(true); // Tell it to persist after the animation ends
            convertView.startAnimation(alpha);


            return convertView;
        }


            private class ViewHolder{
                final Button btnLabelList;
                final ImageButton imageBtnDeleteList;
                final ImageButton imageBtnRenameList;
                final TextView amountOfTasks;

                ViewHolder(View v){
                    btnLabelList = v.findViewById(R.id.labelList);
                    imageBtnDeleteList = v.findViewById(R.id.image_btn_delete_list);
                    imageBtnRenameList = v.findViewById(R.id.image_btn_rename_list);
                    amountOfTasks = v.findViewById(R.id.textView_AmountOfTasks_listItem);
                }
            }


    }




    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




    public class MyFTasksAdapter extends ArrayAdapter<String>{

        private ArrayList<String> arrayFTasks;
        private ArrayList<String> arrayFDate;
        private ArrayList<String> arrayFTime;
        private LayoutInflater inflater;
        private int layout;



        private MyFTasksAdapter(Context context, int textViewResourceId, ArrayList<String> objects,
                                ArrayList<String> objFDate, ArrayList<String> objFTime) {
            super(context, textViewResourceId, objects);

            this.arrayFTasks = objects;
            this.arrayFDate = objFDate;
            this.arrayFTime = objFTime;

            this.layout = textViewResourceId;
            this.inflater = LayoutInflater.from(context);
        }





        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder viewHolder;

            //final View convertViewAnim;

            if (convertView == null) {
                convertView = inflater.inflate(this.layout, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder); // save to Tag
            } else if (((MyFTasksAdapter.ViewHolder) convertView.getTag()).needInflate) { // FOR ANIMATION
                convertView = inflater.inflate(this.layout, parent, false);
                viewHolder = new MyFTasksAdapter.ViewHolder(convertView);
                convertView.setTag(viewHolder); // save to Tag
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //convertViewAnim = convertView;

            ////////////////////////////////////////////////////////////////////////////////////////


            viewHolder.btnLabelFinished.setText(MyFTasksAdapter.this.arrayFTasks.get(position));
            viewHolder.btnLabelFinished.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    // <!-- DIALOG "Return this task?" -->
                    new AlertDialog.Builder(getContext())
                            .setTitle("Вы уверены?")
                            .setMessage("Восстановить задачу?")
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {


                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {


                                }
                            })
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {

                                    DataBaseFinishedTasks dbFinishedTasks = new DataBaseFinishedTasks(getContext());
                                    SQLiteDatabase databaseFinishedTasks = dbFinishedTasks.getWritableDatabase();

                                    dbTasks = new DataBaseTasks(getContext());
                                    databaseTasks = dbTasks.getWritableDatabase();

                                    dbLists = new DataBaseLists(getContext());
                                    databaseLists = dbLists.getWritableDatabase();


                                    DataBaseFinishedTasks.deleteCertainFinishedTaskAndAddToDBTasks(
                                            databaseFinishedTasks, databaseTasks, databaseLists, ((Button) v).getText().toString());


                                    Toast.makeText(getActivity(), "Задача восстановлена!", Toast.LENGTH_SHORT).show();


                                    /** THIS ANIMATION IS WORKED */
                                    /*
                                    Animation anim = android.view.animation.AnimationUtils.loadAnimation(convertViewAnim.getContext(), R.anim.slide_to_left);
                                    convertViewAnim.startAnimation(anim);
                                    anim.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {
                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {


                                            //arrayFTasks.remove(position);
                                            //myFTasksAdapter.notifyDataSetChanged();
                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //setFTasksAdapter();
                                                    arrayFTasks.remove(position);
                                                    myFTasksAdapter.notifyDataSetChanged();
                                                }
                                            }, 100);


                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {
                                        }
                                    });*/


                                    /** THIS ANIMATION IS WORKED (Alternative Variant) */

                                    //deleteCell(convertViewAnim, position);



                                    /**  WITHOUT ANIMATION  */

                                    arrayFTasks.remove(position);
                                    myFTasksAdapter.notifyDataSetChanged();


                                }
                            }).create().show();
                    // <!-- DIALOG "Finish this task?" -->

                }
            });

            ////////////////////////////////////////////////////////////////////////////////////////

            viewHolder.dateOfItemTask.setText(arrayFDate.get(position)); // get and set DATE
            viewHolder.timeOfItemTask.setText(arrayFTime.get(position)); // get and set TIME

            ////////////////////////////////////////////////////////////////////////////////////////

            viewHolder.listOfItemTask.setText(DataBaseFinishedTasks.getCertainFinishedTask(databaseFTasks, viewHolder.btnLabelFinished.getText().toString())[4]);

            ////////////////////////////////////////////////////////////////////////////////////////

            viewHolder.checkFinished.setChecked(true);
            viewHolder.checkFinished.setClickable(false);

            ////////////////////////////////////////////////////////////////////////////////////////


            if (DataBaseFinishedTasks.getCertainFinishedTask(databaseFTasks, MyFTasksAdapter.this.arrayFTasks.get(position))[3].compareTo(DataBaseLists.listRepeatNames[0]) == 0)
                viewHolder.imageViewRepeat.setVisibility(View.GONE);
            else
                viewHolder.imageViewRepeat.setVisibility(View.VISIBLE);


            AlphaAnimation alpha = new AlphaAnimation(0.0F, 1.0F);
            alpha.setDuration(450); // Make animation instant
            alpha.setFillAfter(true); // Tell it to persist after the animation ends
            convertView.startAnimation(alpha);


            return convertView;
        }









        private class ViewHolder{
            public boolean needInflate;
            final Button btnLabelFinished;
            final CheckBox checkFinished;
            final TextView dateOfItemTask;
            final TextView timeOfItemTask;
            final TextView listOfItemTask;
            final ImageView imageViewRepeat;

            ViewHolder(View v)
            {
                btnLabelFinished = v.findViewById(R.id.labelFinished);
                checkFinished = v.findViewById(R.id.checkFinished);
                dateOfItemTask = v.findViewById(R.id.textView_date_of_itemFinishedTask);
                timeOfItemTask = v.findViewById(R.id.textView_time_of_itemFinishedTask);
                listOfItemTask = v.findViewById(R.id.textView_list_of_itemFinishedTask);
                imageViewRepeat = v.findViewById(R.id.imageView_item_finished_repeat);
            }
        }






        private void deleteCell(final View v, final int position) {
            Animation.AnimationListener al = new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation arg0) {
                    arrayFTasks.remove(position);

                    MyFTasksAdapter.ViewHolder vh = (MyFTasksAdapter.ViewHolder)v.getTag();
                    vh.needInflate = true;

                    myFTasksAdapter.notifyDataSetChanged();
                }
                @Override public void onAnimationRepeat(Animation animation) {}
                @Override public void onAnimationStart(Animation animation) {}
            };

            collapse(v, al);
        }

        private void collapse(final View v, Animation.AnimationListener al) {
            final int initialHeight = v.getMeasuredHeight();

            Animation anim = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if (interpolatedTime == 1) {
                        v.setVisibility(View.GONE);
                    }
                    else {
                        v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                        v.requestLayout();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            if (al!=null) {
                anim.setAnimationListener(al);
            }
            anim.setDuration(200L);
            v.startAnimation(anim);
        }









    }




    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////










/***
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

                        String list = selectedBtn.getText().toString();

                        Toast.makeText(getContext(), "Список \"" + list + "\" удален", Toast.LENGTH_SHORT).show();


                        DataBaseLists dbListsssss = new DataBaseLists(getContext());    // Ошибку ниже пофиксил только полностью обновив обьекты
                        SQLiteDatabase databaseli = dbListsssss.getWritableDatabase();  // базы данных !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                        String[] lists = DataBaseLists.readDBLists(databaseli);         // here error


                        int index = 0;

                        for(int i = 0; i < lists.length; i++){
                            if(lists[i].compareTo(list) == 0){
                                index = i+1; break;
                            }
                        }

                        /*
                        ((MainMenu)getActivity()).lists.remove(index);
                        ((MainMenu)getActivity()).adapterSpinner.notifyDataSetChanged();

                        ((MainMenu)getActivity()).listsSize.remove(index);
                        ((MainMenu)getActivity()).adapterSpinner.notifyDataSetChanged();

                        ((MainMenu)getActivity()).sizeAllTasks -= DataBaseTasks.getOnlyTasksByList(databaseTasks, list).length;
                        ((MainMenu)getActivity()).adapterSpinner.notifyDataSetChanged();
                        */

/**
                        arrayLists.remove(position);
                        myListAdapter.notifyDataSetChanged();

                        DataBaseLists.deleteList(databaseLists, list);
                        DataBaseTasks.deleteTasksByList(databaseTasks, list);

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

                                            //selectedBtn.setText(nameList);

                                            Toast toast = Toast.makeText(getContext(), "Название изменено!", Toast.LENGTH_SHORT);
                                            toast.show();

                                            /*
                                            ((MainMenu)getActivity()).lists.set(position, nameList);
                                            ((MainMenu)getActivity()).adapterSpinner.notifyDataSetChanged();
                                            */
/**
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
    */









}

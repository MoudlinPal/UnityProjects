package first.project.nikzhebindev.organizer;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
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

import first.project.nikzhebindev.organizer.databases.DataBaseFinishedTasks;
import first.project.nikzhebindev.organizer.databases.DataBaseLists;
import first.project.nikzhebindev.organizer.databases.DataBaseTasks;
import first.project.nikzhebindev.organizer.notifications.MyReceiver;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


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




    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(MainMenu.openedMain) setTaskAdapter();
        else if(MainMenu.openedLists) setListAdapter();
        else if(MainMenu.openedFTasks) setFTasksAdapter();
    }






    private void setTaskAdapter()
    {
        dbTasks = new DataBaseTasks(getActivity());
        databaseTasks = dbTasks.getWritableDatabase(); // доступен для чтения и записи



        if(MainMenu.NeedUpdateTaskAdapterFromToolbarTitle){         // чтобы правильлно обновить ТаскАдаптер
            MainMenu.NeedUpdateTaskAdapterFromToolbarTitle = false; // после изменения списка задачи
            MainMenu.selectedSpinnerList = MainMenu.TitleToolbar;
        }

        if(MainMenu.selectedSpinnerList.compareTo(MainMenu.KEY_ALL_TASKS) == 0) {


            DataBaseTasks.DataArrayList dataArrayList = DataBaseTasks.getSortedAllDataWithArrayList(databaseTasks);

            ArrayList<String> arrayTasks = dataArrayList.arrTasks;
            ArrayList<Long> longTasks = dataArrayList.arrTimeInMillis;
            ArrayList<String> arrayDate = dataArrayList.arrDate;
            ArrayList<String> arrayTime = dataArrayList.arrTime;
            ArrayList<String> arrayRepeat = dataArrayList.arrRepeat;
            ArrayList<String> arrayList = dataArrayList.arrList;

            /*ArrayList<String> arrayTasks = DataBaseTasks.getSortedTasksWithArrayList(databaseTasks);
            ArrayList<Long> longTasks = DataBaseTasks.getSortedTimeInMillisWithArrayList(databaseTasks);
            ArrayList<String> arrayDate = DataBaseTasks.getSortedDateWithArrayList(databaseTasks);
            ArrayList<String> arrayTime = DataBaseTasks.getSortedTimeWithArrayList(databaseTasks);
            ArrayList<String> arrayRepeat = DataBaseTasks.getSortedRepeatWithArrayList(databaseTasks);
            ArrayList<String> arrayList = null;*/


            myTaskAdapter = new MyTaskAdapter(getActivity(),
                    R.layout.fragment_item_task, arrayTasks, longTasks, arrayDate, arrayTime, arrayRepeat, arrayList);
            setListAdapter(myTaskAdapter);
        }
        else {

            DataBaseTasks.DataArrayList dataArrayList = DataBaseTasks.getSortedAllDataByListWithArrayList(databaseTasks, MainMenu.selectedSpinnerList);

            ArrayList<String> arrayTasks = dataArrayList.arrTasks;
            ArrayList<Long> longTasks = dataArrayList.arrTimeInMillis;
            ArrayList<String> arrayDate = dataArrayList.arrDate;
            ArrayList<String> arrayTime = dataArrayList.arrTime;
            ArrayList<String> arrayRepeat = dataArrayList.arrRepeat;
            ArrayList<String> arrayList = dataArrayList.arrList;

            /*ArrayList<String> arrayTasks = DataBaseTasks.getSortedTasksByListWithArrayList(databaseTasks, MainMenu.selectedSpinnerList);
            ArrayList<Long> longTasks = DataBaseTasks.getSortedTimeInMillisByListWithArrayList(databaseTasks, MainMenu.selectedSpinnerList);
            ArrayList<String> arrayDate = DataBaseTasks.getSortedDateByListWithArrayList(databaseTasks, MainMenu.selectedSpinnerList);
            ArrayList<String> arrayTime = DataBaseTasks.getSortedTimeByListWithArrayList(databaseTasks, MainMenu.selectedSpinnerList);
            ArrayList<String> arrayRepeat = DataBaseTasks.getSortedRepeatByListWithArrayList(databaseTasks, MainMenu.selectedSpinnerList);
            ArrayList<String> arrayList = null;*/


            myTaskAdapter = new MyTaskAdapter(getActivity(),
                    R.layout.fragment_item_task, arrayTasks, longTasks, arrayDate, arrayTime, arrayRepeat, arrayList);
            setListAdapter(myTaskAdapter);
        }
    }

    private void setListAdapter()
    {

        dbLists = new DataBaseLists(getActivity());
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

        dbFTasks = new DataBaseFinishedTasks(getActivity());
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
        private ArrayList<String> arrayList;
        private LayoutInflater inflater;
        private int layout;

        private MyTaskAdapter(Context context, int textViewResourceId, ArrayList<String> objects, ArrayList<Long> objTimeInMillis,
                              ArrayList<String> objDate, ArrayList<String> objTime, ArrayList<String> objRepeat, ArrayList<String> objList) {
            super(context, textViewResourceId, objects);

            this.layout = textViewResourceId;
            this.inflater = LayoutInflater.from(context);
            this.arrayTasks = objects;
            this.arrayTimeInMillis = objTimeInMillis;
            this.arrayDate = objDate;
            this.arrayTime = objTime;
            this.arrayRepeat = objRepeat;
            this.arrayList = objList;
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
            } //else if (((ViewHolder) convertView.getTag()).needInflate) { // FOR ANIMATION
             //   convertView = inflater.inflate(this.layout, parent, false);
             //   viewHolder = new ViewHolder(convertView);
            //    convertView.setTag(viewHolder); // save to Tag
            //}
            else {
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

            if (viewHolder.dateOfItemTask.getText().toString().compareTo(getString(R.string.no_date)) == 0) {

                viewHolder.dateOfItemTask.setTextColor(getActivity().getResources().getColor(R.color.SecondaryText));

            } else if ((System.currentTimeMillis()) < arrayTimeInMillis.get(position)) {

                viewHolder.dateOfItemTask.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));
                viewHolder.timeOfItemTask.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

            } else {

                viewHolder.dateOfItemTask.setTextColor(getActivity().getResources().getColor(R.color.OverdueColor));
                viewHolder.timeOfItemTask.setTextColor(getActivity().getResources().getColor(R.color.OverdueColor));

            }


            ////////////////////////////////////////////////////////////////////////////////////////
            // Set Type of Tasks


            if (position == 0) {

                // Current time
                Calendar currentCal = Calendar.getInstance();

                // Established time
                Calendar establishedCalendar = Calendar.getInstance();
                establishedCalendar.setTimeInMillis(arrayTimeInMillis.get(position));


                if (arrayDate.get(position).compareTo(getString(R.string.no_date)) == 0) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.no_date));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.SecondaryText));

                } else if  (currentCal.get(Calendar.DAY_OF_YEAR) == establishedCalendar.get(Calendar.DAY_OF_YEAR)
                        && currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR)) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.today));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if ((currentCal.get(Calendar.DAY_OF_YEAR) + 1) == establishedCalendar.get(Calendar.DAY_OF_YEAR)
                        && currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR)) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.tomorrow));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if ((currentCal.get(Calendar.DAY_OF_YEAR) + 2) == establishedCalendar.get(Calendar.DAY_OF_YEAR)
                        && currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR)) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.day_after_tomorrow));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if ((currentCal.get(Calendar.DAY_OF_YEAR) + 2) < establishedCalendar.get(Calendar.DAY_OF_YEAR)
                        && currentCal.get(Calendar.MONTH) == establishedCalendar.get(Calendar.MONTH)
                        && currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR)) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.this_month));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if (((((currentCal.get(Calendar.MONTH) + 1) == (establishedCalendar.get(Calendar.MONTH))) &&
                            currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR)) ||
                            (((currentCal.get(Calendar.MONTH) + 1) == 12 && (establishedCalendar.get(Calendar.MONTH)) == 0) &&
                                    currentCal.get(Calendar.YEAR) == (establishedCalendar.get(Calendar.YEAR) + 1)))) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.next_month));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if (currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR)
                        && ((currentCal.get(Calendar.MONTH) + 1) < (establishedCalendar.get(Calendar.MONTH)))) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.this_year));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if (currentCal.get(Calendar.YEAR) != establishedCalendar.get(Calendar.YEAR)) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.later));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if (currentCal.getTimeInMillis() > arrayTimeInMillis.get(position)) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.overdue));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.OverdueColor));

                } else {
                    viewHolder.typeTasks.setVisibility(View.GONE);
                }





                }
                else {







                //String repeat = DataBaseTasks.getCertainTask(databaseTasks, this.arrayTasks.get(position))[3];

                //long establishedData = Long.valueOf(DataBaseTasks.getCertainTask(databaseTasks, MyTaskAdapter.this.arrayTasks.get(position))[6]);
                //long establishedDataPrevious = Long.valueOf(DataBaseTasks.getCertainTask(databaseTasks, MyTaskAdapter.this.arrayTasks.get(position - 1))[6]);

                //String dateOfItemTaskPrevious = arrayDate.get(position-1);

                Calendar currentCal = Calendar.getInstance();

                Calendar establishedCalendar = Calendar.getInstance();
                establishedCalendar.setTimeInMillis(arrayTimeInMillis.get(position));

                Calendar establishedCalendarPrevious = Calendar.getInstance();
                establishedCalendarPrevious.setTimeInMillis(arrayTimeInMillis.get(position-1));


                if (arrayDate.get(position).compareTo(getString(R.string.no_date)) == 0 &&
                        arrayDate.get(position-1).compareTo(getString(R.string.no_date)) != 0) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.no_date));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.SecondaryText));

                }
                else if ((currentCal.get(Calendar.DAY_OF_YEAR) == establishedCalendar.get(Calendar.DAY_OF_YEAR) &&
                            currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR))
                            &&
                            !(currentCal.get(Calendar.DAY_OF_YEAR) == establishedCalendarPrevious.get(Calendar.DAY_OF_YEAR) &&
                                    currentCal.get(Calendar.YEAR) == establishedCalendarPrevious.get(Calendar.YEAR))
                            ) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.today));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                }
                else if (((currentCal.get(Calendar.DAY_OF_YEAR) + 1) == establishedCalendar.get(Calendar.DAY_OF_YEAR) &&
                            currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR))
                            &&
                            !((currentCal.get(Calendar.DAY_OF_YEAR) + 1) == establishedCalendarPrevious.get(Calendar.DAY_OF_YEAR) &&
                                    currentCal.get(Calendar.YEAR) == establishedCalendarPrevious.get(Calendar.YEAR))) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.tomorrow));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                }
                else if (((currentCal.get(Calendar.DAY_OF_YEAR) + 2) == establishedCalendar.get(Calendar.DAY_OF_YEAR) &&
                            currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR))
                            &&
                            !((currentCal.get(Calendar.DAY_OF_YEAR) + 2) == establishedCalendarPrevious.get(Calendar.DAY_OF_YEAR) &&
                                    currentCal.get(Calendar.YEAR) == establishedCalendarPrevious.get(Calendar.YEAR))) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.day_after_tomorrow));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                }
                else if (((currentCal.get(Calendar.DAY_OF_YEAR) + 2) < establishedCalendar.get(Calendar.DAY_OF_YEAR) &&
                            ((currentCal.get(Calendar.MONTH)) == (establishedCalendar.get(Calendar.MONTH))) &&
                            currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR))
                            &&
                            !((currentCal.get(Calendar.MONTH) + 1) == (establishedCalendar.get(Calendar.MONTH)))
                            &&
                            !((currentCal.get(Calendar.DAY_OF_YEAR) + 2) < establishedCalendarPrevious.get(Calendar.DAY_OF_YEAR) &&
                                    currentCal.get(Calendar.YEAR) == establishedCalendarPrevious.get(Calendar.YEAR))) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.this_month));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                }
                else if (((((currentCal.get(Calendar.MONTH) + 1) == (establishedCalendar.get(Calendar.MONTH))) &&
                            currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR)) ||
                            (((currentCal.get(Calendar.MONTH)) == Calendar.DECEMBER && (establishedCalendar.get(Calendar.MONTH)) == Calendar.JANUARY) &&
                                    currentCal.get(Calendar.YEAR) == (establishedCalendar.get(Calendar.YEAR) + 1)))
                            &&
                            !((((currentCal.get(Calendar.MONTH) + 1) == (establishedCalendarPrevious.get(Calendar.MONTH))) &&
                                    currentCal.get(Calendar.YEAR) == establishedCalendarPrevious.get(Calendar.YEAR)) ||
                                    (((currentCal.get(Calendar.MONTH)) == Calendar.DECEMBER && (establishedCalendarPrevious.get(Calendar.MONTH)) == Calendar.JANUARY) &&
                                            currentCal.get(Calendar.YEAR) == (establishedCalendarPrevious.get(Calendar.YEAR) + 1)))) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.next_month));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                }
                else if ((currentCal.get(Calendar.YEAR) == establishedCalendar.get(Calendar.YEAR))
                            &&
                            (currentCal.get(Calendar.MONTH) + 1) < establishedCalendar.get(Calendar.MONTH)
                            &&
                            !((currentCal.get(Calendar.MONTH) + 1) < establishedCalendarPrevious.get(Calendar.MONTH))) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.this_year));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                }
                else if ((currentCal.get(Calendar.YEAR) != establishedCalendar.get(Calendar.YEAR))
                            &&
                            (currentCal.get(Calendar.YEAR) == establishedCalendarPrevious.get(Calendar.YEAR))) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.later));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                }
                else if (currentCal.getTimeInMillis() > arrayTimeInMillis.get(position) && !(currentCal.getTimeInMillis() > arrayTimeInMillis.get(position-1))) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.overdue));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.OverdueColor));

                }
                else {
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


                        SharedPreferences myPreference= PreferenceManager.getDefaultSharedPreferences(getContext());

                        if(myPreference.getBoolean("confirmFTasks", true)){




                        // <!-- DIALOG "Finish this task?" -->
                        new AlertDialog.Builder(getContext())
                                .setTitle(getString(R.string.finish_task))
                                //.setMessage("Завершить задачу?")  ТУТТ РЕКЛАААААААААААААМУУУУУУУУУУУу
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


                                        //Toast.makeText(getContext(), "ID = " + DataBaseTasks.getCertainTaskID(databaseTasks, arrayTasks.get(position)), Toast.LENGTH_LONG).show();

                                        DataBaseFinishedTasks dbFinishedTasks = new DataBaseFinishedTasks(getContext());
                                        SQLiteDatabase databaseFinishedTasks = dbFinishedTasks.getWritableDatabase();

                                        DataBaseTasks.deleteCertainTask(databaseTasks, databaseFinishedTasks,
                                                compoundButton.getTag().toString(), getContext());
                                        // IT'S TAG OF *check.setTag(btnLabel.getText().toString());*
                                        Toast.makeText(getContext(), getString(R.string.task_finished), Toast.LENGTH_SHORT).show();



                                        SharedPreferences myPreference2= PreferenceManager.getDefaultSharedPreferences(getContext());
                                        if(myPreference2.getBoolean("permanentNotification", true))
                                            ((MainMenu) getContext()).sendDefaultNotification();
                                        else
                                            ((MainMenu) getContext()).cancelDefaultNotification();



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
/*
                                        final Animation anim = android.view.animation.AnimationUtils.loadAnimation(viewHolder.btnLabel.getContext(), R.anim.slide_to_right);
                                        anim.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {


                                                //arrayTasks.remove(position);
                                                //myTaskAdapter.notifyDataSetChanged();
                                                // final Handler handler = new Handler();
                                                // handler.postDelayed(new Runnable() {
                                                //     @Override
                                                //    public void run() {


                                                anim.cancel();
                                                arrayTasks.remove(position);
                                                arrayDate.remove(position);
                                                arrayTime.remove(position);
                                                arrayRepeat.remove(position);
                                                arrayTimeInMillis.remove(position);
                                                myTaskAdapter.notifyDataSetChanged();
                                                //arrayList.remove(position);


                                            }
                                            @Override
                                            public void onAnimationRepeat(Animation animation) {}
                                        });
                                        anim.start();


*/
                                        /** THIS ANIMATION IS WORKED (Alternative Variant) */

                                        //deleteCell(convertViewAnim, position);


                                        arrayTasks.remove(position);
                                        arrayDate.remove(position);
                                        arrayTime.remove(position);
                                        arrayRepeat.remove(position);
                                        arrayTimeInMillis.remove(position);
                                        //arrayList.remove(position);
                                        myTaskAdapter.notifyDataSetChanged();





                                    }
                                }).create().show();
                        // <!-- DIALOG "Finish this task?" -->
                        }

                        else{

                            DataBaseFinishedTasks dbFinishedTasks = new DataBaseFinishedTasks(getContext());
                            SQLiteDatabase databaseFinishedTasks = dbFinishedTasks.getWritableDatabase();

                            DataBaseTasks.deleteCertainTask(databaseTasks, databaseFinishedTasks,
                                    compoundButton.getTag().toString(), getContext());
                            // IT'S TAG OF *check.setTag(btnLabel.getText().toString());*
                            Toast.makeText(getContext(), getString(R.string.task_finished), Toast.LENGTH_SHORT).show();



                            SharedPreferences myPreference2= PreferenceManager.getDefaultSharedPreferences(getContext());
                            if(myPreference2.getBoolean("permanentNotification", true))
                                ((MainMenu) getContext()).sendDefaultNotification();
                            else
                                ((MainMenu) getContext()).cancelDefaultNotification();



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


                            arrayTasks.remove(position);
                            arrayDate.remove(position);
                            arrayTime.remove(position);
                            arrayRepeat.remove(position);
                            arrayTimeInMillis.remove(position);
                            //arrayList.remove(position);
                            myTaskAdapter.notifyDataSetChanged();


                        }


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
                viewHolder.amountOfTasks.setText(getString(R.string.no_tasks));
                viewHolder.amountOfTasks.setTextColor(getActivity().getResources().getColor(R.color.SecondaryText));
            } else {
                String newKolvo = getString(R.string.amount_list_tasks) + " " + kolvo;
                viewHolder.amountOfTasks.setText(newKolvo);
                viewHolder.amountOfTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));
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
                        Toast.makeText(getContext(), getString(R.string.empty), Toast.LENGTH_SHORT).show();
                    } else {

                        final Activity MM = ((MainMenu) getContext());

                        ((MainMenu) getContext()).toolbar.getMenu().clear();


                        ///////////////////////////// Toolbar Button BACK /////////////////////////////
                        ((MainMenu) getContext()).drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        ((MainMenu) getContext()).toolbar.setNavigationIcon(R.drawable.ic_action_back_arrow);
                        ((MainMenu) getContext()).toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                ((MainMenu) getContext()).drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                                ToolbarButtonBackIsPressed = true;
                                MM.onBackPressed();

                            }
                        });
                        ///////////////////////////// Toolbar Button BACK /////////////////////////////


                        ((MainMenu) getContext()).toolbar.setTitle(((Button) v).getText().toString());
                        ((MainMenu) getContext()).toolbar.setTitleTextColor(getActivity().getResources().getColor(R.color.JustWhite));


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
                            Toast.makeText(getContext(), getString(R.string.u_cant_delete_this_list), Toast.LENGTH_SHORT).show();
                            DefaultList = true;
                        }
                    }
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    if (!DefaultList) {

                        if(((MainMenu)getContext()).isOnline()) {
                            /** /////////////////////////// Advertisement /////////////////////////// */
                            final AdView mAdViewDialogDeleteList;

                            //Получаем вид с файла dialog_new_list.xml, который применим для диалогового окна:
                            LayoutInflater li = LayoutInflater.from(getContext()); // this = context !!!!!!!!!!!!!!!!!!!
                            View viewDialogAdMob = li.inflate(R.layout.dialog_del_list_admob, null);

                            mAdViewDialogDeleteList = (AdView) viewDialogAdMob.findViewById(R.id.mAdViewDialogDeleteList);
                            mAdViewDialogDeleteList.setAdListener(new AdListener() {

                                @Override
                                public void onAdLoaded() {
                                    mAdViewDialogDeleteList.setClickable(true);
                                    mAdViewDialogDeleteList.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAdFailedToLoad(int errorCode) {
                                    mAdViewDialogDeleteList.setClickable(false);
                                    mAdViewDialogDeleteList.setVisibility(View.GONE);
                                }

                            });
                            AdRequest adRequest = new AdRequest.Builder()
                                    .build();
                            mAdViewDialogDeleteList.loadAd(adRequest);

                            /** /////////////////////////// Advertisement /////////////////////////// */


                            // <!-- DIALOG ARE U SURE? -->
                            new AlertDialog.Builder(getContext())
                                    .setTitle(getString(R.string.list) + " " + "\"" + arrayLists.get(position) + "\"" + " " + getString(R.string.will_be_deleted))
                                    /** /////////////////////////// Advertisement /////////////////////////// */
                                    .setView(viewDialogAdMob)
                                    /** /////////////////////////// Advertisement /////////////////////////// */
                                    //.setMessage("Список \"" + arrayLists.get(position) + "\" будет удален.") ТУТ БУДЕТ РЕКЛАМА
                                    .setNegativeButton(R.string.no, null)
                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {

                                            String list = arrayLists.get(position);

                                            Toast.makeText(getContext(), getString(R.string.list) + " " + "\"" + list + "\"" + " " + getString(R.string.deleted), Toast.LENGTH_SHORT).show();


                                            DataBaseFinishedTasks dbFinishedTasks = new DataBaseFinishedTasks(getContext());
                                            SQLiteDatabase databaseFinishedTasks = dbFinishedTasks.getWritableDatabase();

                                            DataBaseLists.deleteList(databaseLists, list);
                                            DataBaseTasks.deleteTasksByList(databaseTasks, databaseFinishedTasks, list, getContext());


                                            /**  WITHOUT ANIMATION  */

                                            arrayLists.remove(position);
                                            myListAdapter.notifyDataSetChanged();


                                            arg0.cancel();

                                        }
                                    }).create().show();
                            // <!-- DIALOG ARE U SURE? -->
                        }
                        else{
                            // <!-- DIALOG ARE U SURE? -->
                            new AlertDialog.Builder(getContext())
                                    .setTitle(getString(R.string.list) + " " + "\"" + arrayLists.get(position) + "\"" + " " + getString(R.string.will_be_deleted))
                                    //.setMessage("Список \"" + arrayLists.get(position) + "\" будет удален.") ТУТ БУДЕТ РЕКЛАМА
                                    .setNegativeButton(R.string.no, null)
                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {

                                            String list = arrayLists.get(position);

                                            Toast.makeText(getContext(), getString(R.string.list) + " " + "\"" + list + "\"" + " " + getString(R.string.deleted), Toast.LENGTH_SHORT).show();


                                            DataBaseFinishedTasks dbFinishedTasks = new DataBaseFinishedTasks(getContext());
                                            SQLiteDatabase databaseFinishedTasks = dbFinishedTasks.getWritableDatabase();

                                            DataBaseLists.deleteList(databaseLists, list);
                                            DataBaseTasks.deleteTasksByList(databaseTasks, databaseFinishedTasks, list, getContext());


                                            /**  WITHOUT ANIMATION  */

                                            arrayLists.remove(position);
                                            myListAdapter.notifyDataSetChanged();


                                            arg0.cancel();

                                        }
                                    }).create().show();
                            // <!-- DIALOG ARE U SURE? -->
                        }
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
                            Toast.makeText(getContext(), getString(R.string.u_cant_rename_this_list), Toast.LENGTH_SHORT).show();
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
                                .setTitle(getString(R.string.rename_list_name))
                                .setPositiveButton(getString(R.string.rename), null) //Set to null. We override the onclick
                                .setNegativeButton(getString(R.string.cancel), null)
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
                                            Toast toast = Toast.makeText(getContext(), getString(R.string.this_list_already_exist), Toast.LENGTH_SHORT);
                                            toast.show();
                                        } else if (nameList.compareTo("") == 0) // !!!!!!!!!!!!!
                                        {
                                            //makeVibration();

                                            Toast toast = Toast.makeText(getContext(), getString(R.string.enter_list_name), Toast.LENGTH_SHORT);
                                            toast.show();
                                        } else if (nameList.contains(bufDivider)) {
                                            //makeVibration();

                                            Toast toast = Toast.makeText(getContext(), "Character '" + DataBaseLists.DIVIDER + "' is not valid!", Toast.LENGTH_SHORT);
                                            toast.show();
                                        } else {


                                            ((MainMenu) getContext()).showKeyboard();


                                            DataBaseLists.renameList(databaseLists, arrayLists.get(position), nameList);
                                            DataBaseTasks.renameTasksByList(databaseTasks, arrayLists.get(position), nameList);


                                            //Dismiss once everything is OK.
                                            dialogAddList.dismiss();

                                            //selectedBtn.setText(nameList);

                                            Toast toast = Toast.makeText(getContext(), getString(R.string.renamed), Toast.LENGTH_SHORT);
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
                            .setTitle(getString(R.string.resume_task))
                            //.setMessage("Восстановить задачу?") ТУТ БУДЕТ РЕКЛАААМААА
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
                                            databaseFinishedTasks, databaseTasks, databaseLists, ((Button) v).getText().toString(), getContext());



                                    Toast.makeText(getActivity(), getString(R.string.resumed), Toast.LENGTH_SHORT).show();


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


                                    dbTasks = new DataBaseTasks(getContext());
                                    databaseTasks = dbTasks.getWritableDatabase();

                                    // For notification
                                    //String[] data = DataBaseTasks.getCertainTask(databaseTasks, MyFTasksAdapter.this.arrayFTasks.get(position));
                                    //Calendar calendar = Calendar.getInstance();



                                    //String id = data[5];
                                    //String time = data[2];
                                    //String task = data[0];
                                    //String repeat = data[3];
                                    //String timeInMillis = data[6];





                                    /*Toast.makeText(getContext(), "TimeInMillis = " + "+" +timeInMillis+"+", Toast.LENGTH_LONG).show();
                                    Toast.makeText(getContext(), "id = " + "+" +id+"+", Toast.LENGTH_LONG).show();
                                    Toast.makeText(getContext(), "time = " + "+" +time+"+", Toast.LENGTH_LONG).show();
                                    Toast.makeText(getContext(), "task = " + "+" +task+"+", Toast.LENGTH_LONG).show();
                                    Toast.makeText(getContext(), "repeat = " + "+" +repeat+"+", Toast.LENGTH_LONG).show();*/



                                    //calendar.setTimeInMillis(Long.valueOf(timeInMillis));


                                    //sendNotification(calendar, id, time, task, repeat);




                                    /**  WITHOUT ANIMATION  */

                                    arrayFTasks.remove(position);
                                    arrayFDate.remove(position);
                                    arrayFTime.remove(position);
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




        private void sendNotification(Calendar calendar, String id, String editTime, String editTask, String repeat) {
            // Calendar calendar, String repeating
            // and then in your activity set the alarm manger to start the broadcast receiver
            // at a specific time and use AlarmManager setRepeating method to repeat it this
            // example bellow will repeat it every day.


            String title = getString(R.string.task_at) + " " + editTime;


            Intent notifyIntent = new Intent(getContext(), MyReceiver.class);
            notifyIntent.putExtra("NotificationTitle", title);
            notifyIntent.putExtra("NotificationMessage", editTask);
            notifyIntent.putExtra("id_Notification", id);


            // ONE REAQUEST CODE FOR ALL.... That's bad I think !!!!!!!!!!!!!!!!!!
            PendingIntent pendingIntent = PendingIntent.getBroadcast
                    (getContext(), (Integer.decode(id) + 850), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

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

package sallycs.com.pomoapp;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends Fragment {

    BarChart taskCompletion;
    Spinner monthSelect;
    int currentYear;
    int currentMonth;
    int currentDay;

    int getYear;
    int getMonth;
    int getDay;

    int getMonthMax;

    private int numCompletedTask;


    private ArrayList<String> months;

    private List<BarChart> monthlyCharts;


    private ArrayList<ArrayList<Integer>> monthlyValues;

    TaskNum mCallback;

    private SharedPreferences sharedPreferencesData;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (TaskNum) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DataCommunication");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_data, container, false);


     //   sharedPreferencesData = getContext().getSharedPreferences("example", MODE_PRIVATE);

       loadData();
       loadMonths();

        Calendar now = Calendar.getInstance();
        int getDay = now.get(Calendar.DATE);

        if(currentDay != getDay){
            currentDay = getDay;
            numCompletedTask = 0;
        }
        else {
            numCompletedTask = sharedPreferencesData.getInt("num", -1);
        }
        return rootView;
    }


    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadData();
        loadMonths();

        taskCompletion = v.findViewById(R.id.bar_graph);
        taskCompletion.setDescription("");

        monthSelect = v.findViewById(R.id.selectMonth);
        Calendar now = Calendar.getInstance();
        getYear = now.get(Calendar.YEAR);
        getMonth = now.get(Calendar.MONTH);
        getDay = now.get(Calendar.DATE) - 1;
        Log.d("day" , Integer.toString(getDay));
        getMonthMax = now.getActualMaximum(now.DATE);

        if (currentYear != getYear) {
            currentYear = getYear;
        }
        if (currentMonth != getMonth) {
            currentMonth = getMonth;
            months.add(getMonthForInt(currentMonth) + " " + currentYear);
            saveMonths();
        }

        if(currentDay!=getDay){
            currentDay = getDay;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, months);
        monthSelect.setAdapter(adapter);

        // monthlyValues = null;
        monthSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if(monthlyValues == null || monthlyValues.size() == 0){
                numCompletedTask = mCallback.getTaskNum();
                Log.d("if", "null");}
                else if(monthlyValues.get(i).get(currentDay) < mCallback.getTaskNum()){
                    Log.d("else if", Integer.toString(monthlyValues.get(i).get(currentDay)));
                    numCompletedTask = mCallback.getTaskNum();
                }
                else{
                numCompletedTask = monthlyValues.get(i).get(currentDay);
                    Log.d("else", "KMSS");
                }

                if(i<monthlyValues.size()-1){
                    String dateS = monthSelect.getSelectedItem().toString();
                    String[] monthYear = dateS.split("\\s+");
                    String month = monthYear[0].toUpperCase();
                    String year = monthYear[1];

                    int iYear = Integer.parseInt(year);
                    int iMonth = monthAsNumber(month, Locale.getDefault()); // 1 (months begin with 0)
                    int iDay = 1;
                    Calendar mycal = new GregorianCalendar(iYear, iMonth, iDay);
                    int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

                    ArrayList<String> date = new ArrayList<>();
                    for (int d = 1; d <= daysInMonth; d++) {
                        date.add(dayMonth(iMonth, d));
                    }

                    ArrayList<BarEntry> numTaskToday = new ArrayList<>();

                    for(int k=0; k<monthlyValues.get(i).size(); k++){
                        numTaskToday.add(new BarEntry((float) monthlyValues.get(i).get(k), k));
                    }

                    BarDataSet setVals = new BarDataSet(numTaskToday, "Number of Tasks Completed");
                    BarData combo = new BarData(date, setVals);
                    setVals.setColor(ContextCompat.getColor(getContext(), R.color.green));

                    taskCompletion.setData(combo);
                    taskCompletion.invalidate();
                }
                else if(i==monthlyValues.size()-1){
                    ArrayList<BarEntry> numTaskToday = new ArrayList<>();
                    ArrayList<Integer> numTask = new ArrayList<>();
                    ArrayList<String> date = new ArrayList<>();
                    for (int d = 1; d <= getMonthMax; d++) {
                        date.add(dayMonth(currentMonth, d));
                    }
                    while(monthlyValues.get(i).size() < date.size()){
                            monthlyValues.get(i).add(0);
                    }
                    for(int c=0; c<monthlyValues.get(i).size(); c++){
                        numTask.add(monthlyValues.get(i).get(c));
                    }
                    monthlyValues.get(i).set(currentDay, numCompletedTask);
                    for(int k=0; k<monthlyValues.get(i).size(); k++){
                        numTaskToday.add(new BarEntry((float) monthlyValues.get(i).get(k), k));
                    }
                    BarDataSet setVals = new BarDataSet(numTaskToday, "Number of Tasks Completed");
                    BarData combo = new BarData(date, setVals);
                    setVals.setColor(ContextCompat.getColor(getContext(), R.color.green));

                    taskCompletion.setData(combo);
                    taskCompletion.invalidate();
                    saveData();
                }
                else {
                    ArrayList<BarEntry> numTaskToday = new ArrayList<>();
                    ArrayList<Integer> numTask = new ArrayList<>();
                    ArrayList<String> date = new ArrayList<>();
                    for (int d = 1; d <= getMonthMax; d++) {
                        date.add(dayMonth(currentMonth, d));
                    }

                    for(int da = 0; da<date.size(); da++){
                            numTask.add(0);
                    }
                    numTask.set(currentDay, numCompletedTask);
                    Log.d("size", String.valueOf(numTask.size()));
                    monthlyValues.add(numTask);
                    for(int k=0; k<monthlyValues.get(i).size(); k++){
                        numTaskToday.add(new BarEntry((float) monthlyValues.get(i).get(k), k));
                    }

                    Log.d("here", Integer.toString(numCompletedTask));

                    BarDataSet setVals = new BarDataSet(numTaskToday, "Number of Tasks Completed");
                    BarData combo = new BarData(date, setVals);
                    setVals.setColor(ContextCompat.getColor(getContext(), R.color.green));

                        //monthlyCharts.add(combo);

                    taskCompletion.setData(combo);
                    //    taskCompletion.animateY(500);
                    taskCompletion.invalidate();


                    saveData();
                        // setContentView(chart);
                    }

            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView){

                }

            });
    }


    private String getMonthForInt(int num) {
        String month = "month";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }

    private String dayMonth(int month, int day){
        return (month+1) + "/" + day;
    }

    private void saveData(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String json = gson.toJson(monthlyValues);

        editor.putString("chartList", json);

        editor.commit();
        editor.apply();


    }

    private void loadData() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("chartList", null);
        Type type = new TypeToken<ArrayList<ArrayList<Integer>>>() {
        }.getType();

        monthlyValues = gson.fromJson(json, type);

        if (monthlyValues == null) {
            monthlyValues = new ArrayList<ArrayList<Integer>>();
        }

    }

    public static int monthAsNumber(String  month, Locale  locale) {
        DateFormatSymbols dfs = new DateFormatSymbols(locale);
        String[] months = (dfs.getMonths());

        for (int i = 0; i < 12; i++) {
            if (months[i].equalsIgnoreCase(month)) {
                return i; // month index is zero-based as usual in old JDK pre 8!
            }
        }

        return -1;
    }

    private void saveMonths(){

        SharedPreferences sP = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        SharedPreferences.Editor edits = sP.edit();

        Gson gson = new Gson();

        String json = gson.toJson(months);

        edits.putString("monthList", json);

        edits.commit();
        edits.apply();


    }

    private void loadMonths() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("monthList", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();

        months = gson.fromJson(json, type);

        if (months == null) {
            months = new ArrayList<String>();
        }

    }
}

package sallycs.com.pomoapp;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.net.sip.SipErrorCode.TIME_OUT;

//HomeFragment makes up the home tab.

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public int numTaskCompleted;

    private static final String CHECK = "check";
    private FloatingActionButton add;
    private TextView greet;
    private Button start;
    private Button mButtonSet;


    public static List<Task> input = new ArrayList<>();
    public static List<Task> taskList = new ArrayList<>();
    public static ArrayList<Task> flipped = (ArrayList<Task>) taskList;


    private int taskLength;
    private int breakLength;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    int currentDay;


    TaskNum mCallback;
    private SharedPreferences sharedPreferences;

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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        sharedPreferences = getContext().getSharedPreferences("example", Context.MODE_PRIVATE);

        /*Checks Program's Date
         *if program's date is different from actual date
         *   change program's date to actual date
         *   set the number of task completed to 0
         *else
         *   get the number of tasks completed today
         */

        Calendar now = Calendar.getInstance();
        int getDay = now.get(Calendar.DATE);

        if(currentDay != getDay){
            Log.d("check", Integer.toString(currentDay));
            Log.d("check2", Integer.toString(getDay));
            numTaskCompleted = 0;
            currentDay = getDay;
        }
        else {
            getNumTask();
        }
        return rootView;
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment

        Calendar now = Calendar.getInstance();
        int getDay = now.get(Calendar.DATE);

        Log.d("currentDate", Integer.toString(currentDay));
        Log.d("getDate", Integer.toString(getDay));
        if(currentDay != getDay){
            Log.d("check", Integer.toString(currentDay));
            Log.d("check2", Integer.toString(getDay));
            saveNumTask();
            currentDay = getDay;
        }
        else {
            getNumTask();
        }


        /* Initiates the button that sets the time for tasks and breaks
         *if there are no arguments
         *  set button is invisible
         *  time for tasks is 25 minutes
         *  time for break is 5 minutes
         *else
         *  make the button visible
         *when button is clicked
         *  set the time for break & tasks according to argument
         *  button becomes invisible
         */
        //Arguments made in the settings fragment
        mButtonSet = v.findViewById(R.id.set);
        if (getArguments() == null) {
            mButtonSet.setVisibility(View.INVISIBLE);
            taskLength = 25;
            breakLength = 5;
        }
        else{
            mButtonSet.setVisibility(View.VISIBLE);
        }
        mButtonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getArguments() != null) {
                    taskLength = Integer.parseInt(getArguments().getString("TaskLength"));
                    breakLength = Integer.parseInt(getArguments().getString("BreakLength"));
                    setListTime(taskLength, breakLength);

                    Log.d("check", Integer.toString(taskLength));
                }
                mButtonSet.setVisibility(View.INVISIBLE);
            }
        });


        start = v.findViewById(R.id.start_btn);
        greet = v.findViewById(R.id.textView);

        start.setVisibility(View.INVISIBLE);
        recyclerView = v.findViewById(R.id.recycleView);
        layoutManager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(layoutManager);

        /*if the list of task is NOT empty
         *  start button is visible
         *  home tab will include list in its page
         *else
         *  only greeting statement will be visible.
         */
        if(taskList.size()>0) {
            greet.setVisibility(View.INVISIBLE);
            Log.d("size", String.valueOf(taskList.size()));
            adapter = new Adapt(taskList, getActivity());
            recyclerView.setAdapter(adapter);
            start.setVisibility(View.VISIBLE);
        }
        else{
            greet.setVisibility(View.VISIBLE);
        }


        //Clicking on add button starts an activity that returns a task
        add = v.findViewById(R.id.floatingActionButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(CHECK, "onClick:pressed");
                Intent InputTask = new Intent(getActivity(), InputTask.class);
                startActivityForResult(InputTask, 1); //method that grabs the task back
            }
        });

        //When Start button clicked, program iterates through list of tasks and starts activity for each of them
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.reverse(flipped);
                Iterator<Task> iterator = flipped.iterator();
                while(iterator.hasNext()){
                    Task t = iterator.next();
                    Intent i = new Intent(getActivity(), TimerClass.class);
                    i.putExtra("TaskObj", t);
                    Log.d("taskName", t.getName());

                    startActivityForResult(i, 2); //checks if current task is complete
                    iterator.remove(); //remove task after iterated through
                    taskList.remove(t); //when activity is complete, remove the task from task list
                    input.remove(t);
                }

                adapter = new Adapt(taskList, getActivity()); //empty screen
                recyclerView.setAdapter(adapter); //sets the home tab to empty screen
                greet.setText("Move on!\n Add another Task!");
                greet.setVisibility(View.VISIBLE); //greet text becomes visible
                start.setVisibility(View.INVISIBLE); //the start button becomes invisible
            }
        });
        }


    //onActivityResult() function is called by startActivityForResult()
    @SuppressLint("RestrictedApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if requestCode is 1
         * if there is data sent over
         *  grab the task created
         *  if inputList array has less than 4 items
         *      add the task into the inputList array list
         *  else
         *      hide add button
         *  set length of the task to taskLength
         *  set break time to be breakLength
         *if requestCode is 2
         *  increment the number of tasks completed today
         */
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Task val = data.getParcelableExtra("TASK_KEY");
                    val.setTime(taskLength);
                    input.add(val);
                    if (input.size() >= 4) {
                        add.setVisibility(View.INVISIBLE);
                    } else {
                        add.setVisibility(View.VISIBLE);
                    }

                    setListTime(taskLength, breakLength);

                    if (taskList.size() > 0) {
                        greet.setVisibility(View.INVISIBLE);
                        Log.d("size", String.valueOf(taskList.size()));
                        adapter = new Adapt(taskList, getActivity());
                        recyclerView.setAdapter(adapter);
                        start.setVisibility(View.VISIBLE);
                    } else {
                        greet.setVisibility(View.VISIBLE);
                    }

                }
            }
        }

        if (requestCode == 2) {
            if(resultCode == Activity.RESULT_OK) {
                if (data != null) {
                   // numTaskCompleted = sharedPreferences.getInt("num", -1);
                    int num = data.getIntExtra("numTask", 1);
                    numTaskCompleted += num;
                    Log.d("TaskCompleted", Integer.toString(numTaskCompleted));
                    saveNumTask();

                    mCallback.setTaskNum(numTaskCompleted);
                }
            }
        }
    }


    //sets the time length for each task and break in the list
    private void setListTime(int taskMin, int breakMin){
       taskList.clear();
       for(int i = 0; i<input.size(); i++){
           input.get(i).setTime(taskMin);
           taskList.add(input.get(i));
           //following if statement adds break in between each taskg
           if(input.size() > i+1) {
               Task breakTask = new Task("Break", breakMin);
               taskList.add(breakTask);
           }

       }

       for(int k = 0; k<taskList.size();k++){
           Log.d("Array Check: ", taskList.get(k).getName());
       }
        adapter = new Adapt(taskList, getActivity());
        recyclerView.setAdapter(adapter);
    }

    //saves the number of tasks completed for relaunching
    private void saveNumTask(){
        SharedPreferences sp = getActivity().getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("int_key", numTaskCompleted);
        editor.commit();
    }

    //gets the number of tasks saved
    private void getNumTask(){
        SharedPreferences sp = getActivity().getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        numTaskCompleted = sp.getInt("int_key", -1);
    }

}

package sallycs.com.pomoapp;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int MIN_VALUE= 1;

    private String mParam1;
    private String mParam2;

    //EditText taskLength;
    TextView breakLength;
    TextView taskLength;
    SeekBar  taskLengthSeekBar;
    SeekBar breakSeek;
    Button setBtn;

    String breakLengthNum;
    String taskLengthNum;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View settings = inflater.inflate(R.layout.fragment_settings, container, false);
        taskLength = settings.findViewById(R.id.taskLength_text);
        breakLength = settings.findViewById(R.id.break_length);


        taskLengthSeekBar = settings.findViewById(R.id.task_length_bar);
        taskLengthSeekBar.setMax(60);
//        taskLengthSeekBar.setMin(1);
        taskLengthSeekBar.setProgress(25);
        taskLengthNum = Integer.toString(taskLengthSeekBar.getProgress());
        taskLength.setText(Integer.toString(taskLengthSeekBar.getProgress())+ " minutes");
        taskLengthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                if (taskLengthSeekBar.getProgress() < MIN_VALUE) {
                    taskLengthSeekBar.setProgress(MIN_VALUE);
                    taskLengthNum = Integer.toString(MIN_VALUE);
                    taskLength.setText(Integer.toString(MIN_VALUE)+ " minutes");
                }
                else {
                    taskLengthNum = Integer.toString(i);
                    taskLength.setText(Integer.toString(i) + " minutes");
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (taskLengthSeekBar.getProgress() < MIN_VALUE) {
                    taskLengthSeekBar.setProgress(MIN_VALUE);
                    taskLengthNum = Integer.toString(MIN_VALUE);
                    taskLength.setText(Integer.toString(MIN_VALUE)+ " minutes");
                }
                else {
                    taskLengthNum = Integer.toString(progress);
                    taskLength.setText(Integer.toString(progress) + " minutes");
                }
            }
        });

        breakSeek = settings.findViewById(R.id.break_seek);
        breakSeek.setMax(60);
      //  breakSeek.setMin(1);
        breakSeek.setProgress(5);
        breakLengthNum = Integer.toString(breakSeek.getProgress());
        breakLength.setText(Integer.toString(breakSeek.getProgress()) +" minutes");
        breakSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                if (breakSeek.getProgress() < MIN_VALUE) {
                    breakSeek.setProgress(MIN_VALUE);
                    breakLengthNum = Integer.toString(MIN_VALUE);
                    breakLength.setText(Integer.toString(MIN_VALUE)+ " minutes");
                }
                else{
                    breakLengthNum = Integer.toString(i);
                    breakLength.setText(Integer.toString(i)+ " minutes");
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (breakSeek.getProgress() < MIN_VALUE) {
                    breakSeek.setProgress(MIN_VALUE);
                    breakLengthNum = Integer.toString(MIN_VALUE);
                    breakLength.setText(Integer.toString(MIN_VALUE)+ " minutes");
                }
                else {
                    breakLengthNum = Integer.toString(progress);
                    breakLength.setText(Integer.toString(progress) + " minutes");
                }
            }
        });

        setBtn = settings.findViewById(R.id.set_btn);
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String taskLengthSend = taskLengthNum;
                String breakLengthSend = breakLengthNum;
                if(taskLengthSend.length()==0 || breakLengthSend.length()==0){
                    String warning = "Both the task time and break time need values!";
                    Toast note = Toast.makeText(getActivity(), warning, Toast.LENGTH_SHORT);
                    note.show();
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putString("TaskLength", taskLengthSend);
                    bundle.putString("BreakLength", breakLengthSend);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    HomeFragment homeFragment = new HomeFragment();
                    homeFragment.setArguments(bundle);

                    ((MainActivity) getActivity()).setTab(0);

                    String success ="Click on \"Apply Changes\" to apply changes";
                    Toast.makeText(getActivity(), success, Toast.LENGTH_SHORT).show();

                    fragmentTransaction.replace(R.id.main_frame, homeFragment);
                    fragmentTransaction.commit();
                }
            }
        });

        return settings;
    }

}

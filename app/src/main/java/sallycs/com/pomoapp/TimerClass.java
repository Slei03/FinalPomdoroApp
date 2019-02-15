package sallycs.com.pomoapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;
import java.util.Locale;

public class TimerClass extends AppCompatActivity {

    private Vibrator v;

    private Button endEarly;
    private Button endSession;
    private Button pauseCont;

    private String name;
    private String description;
    private String timer;

    private TextView nameView;
    private TextView descriptionView;
    private TextView timerView;

    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;

    private long mStartTimeInMillis;
    private long mTimeLeftInMillis;
    private long mEndTime;


    private int taskComplete = 0;

    Intent data = new Intent();

    Task task;

    boolean clickedOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

       
        Intent receiver = getIntent();
        task = receiver.getParcelableExtra("TaskObj");

        nameView = findViewById(R.id.name_txt);
        descriptionView = findViewById(R.id.description_txt);
        timerView = findViewById(R.id.time_txt);

        name = task.getName();
        description = task.getDescription();
        timer = task.getTime();

        nameView.setText(name);
        descriptionView.setText(description);

        endEarly = findViewById(R.id.complete_task);
        endEarly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskComplete++;
                if(!name.equals("Break")) {
                    data.putExtra("numTask", taskComplete);
                    setResult(RESULT_OK, data);
                }
                pauseTimer();
                clickedOn = true;
                TimerClass.this.finish();
                return;
            }
        });
        endSession = findViewById(R.id.end_all);
        endSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseTimer();
                //resetTimer();
                clickedOn = true;
                TimerClass.this.finish();
                return;
            }
        });
        pauseCont = findViewById(R.id.pause_btn);
        pauseCont.setVisibility(View.INVISIBLE);
        pauseCont.setText("Pause");
        pauseCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTimerRunning) {
                    pauseTimer();
                    //pauseCont.setText("");
                } else {
                    startTimer();
                   // pauseCont.setText("Pause");
                }
            }
        });
        if(name.equals("Break")) {
            endEarly.setVisibility(View.INVISIBLE);
        }
        long inputMillis = Long.parseLong(String.valueOf(Integer.parseInt(timer))) * 60000;
        setTime(inputMillis);
        mTimerRunning = true;
        startTimer();
    }

    private void startTimer(){
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                Log.d("reached", "well it got here");
                notificationShow();
                //onDestroy();
                taskComplete++;
                if(!name.equals("Break")) {
                    data.putExtra("numTask", taskComplete);
                    setResult(RESULT_OK, data);
                }
                finish();
            }
        }.start();


        mTimerRunning = true;
    }

    private void pauseTimer() {
            mCountDownTimer.cancel();
            mTimerRunning = false;
          //  updateWatchInterface();
    }

    private void notificationShow() {
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;


        myIntent = new Intent(this.getApplicationContext(),Alarm.class);
        myIntent.putExtra("task", task);
        pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(),0,myIntent,0);


        manager.set(AlarmManager.RTC, System.currentTimeMillis(),pendingIntent);
        Uri alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        final Ringtone ringtoneAlarm = RingtoneManager.getRingtone(getApplicationContext(), alarmTone);
        ringtoneAlarm.play();
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);  //30000 is for 30 seconds, 1 sec =1000
                    if (ringtoneAlarm.isPlaying())
                        ringtoneAlarm.stop();   // for stopping the ringtone
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
        long n[] = {1,2250, 500, 2250};
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(n, -1);

        Log.d("Alarm", "It got here");

    }

    private void setTime(long milliseconds) {
        mStartTimeInMillis = milliseconds;
        resetTimer();
    }

    private void resetTimer() {
        mTimeLeftInMillis = mStartTimeInMillis;
        updateCountDownText();
    }

    private void updateCountDownText() {
        int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted;
        if (hours > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }

        timerView.setText(timeLeftFormatted);
    }

    private void updateWatchInterface() {
        if (mTimerRunning) {
            //EditTextInput.setVisibility(View.INVISIBLE);
            //  mButtonSet.setVisibility(View.INVISIBLE);
            pauseCont.setText("Pause");
        } else {
//            mEditTextInput.setVisibility(View.VISIBLE);
            //          mButtonSet.setVisibility(View.VISIBLE);
            pauseCont.setText("Start");

            if (mTimeLeftInMillis < 1000) {
                pauseCont.setVisibility(View.INVISIBLE);
            } else {
                pauseCont.setVisibility(View.VISIBLE);
            }

        }
    }

    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("startTimeInMillis", mStartTimeInMillis);
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);

        editor.apply();

        if (clickedOn && mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }



    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        mStartTimeInMillis = prefs.getLong("startTimeInMillis", 600000);
        mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis);
        mTimerRunning = prefs.getBoolean("timerRunning", true);

        updateCountDownText();

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
              //  updateWatchInterface();
            } else {
                startTimer();
            }
        }
    }
}


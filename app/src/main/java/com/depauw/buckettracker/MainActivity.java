package com.depauw.buckettracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.CompoundButton;

import com.depauw.buckettracker.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener {


    private ActivityMainBinding binding;

    // additional variables
    private static final int DEFAULT_NUM_MINS = 20;
    private static final int MILLIS_PER_MIN = 60000;
    private static final int MILLIS_PER_SEC = 1000;
    private static final int SECS_PER_MIN = 60;
    private CountDownTimer timer;
    private long updatedTime = DEFAULT_NUM_MINS * MILLIS_PER_MIN;
    private static final int minimumTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toggleIsGuest.setOnCheckedChangeListener(this);
        binding.buttonAddScore.setOnLongClickListener(this);
        binding.switchGameClock.setOnCheckedChangeListener(this);
        binding.buttonSetTime.setOnClickListener(this);

        // setting default to home
        setHome();
    }

    @Override
    public boolean onLongClick(View view) {

        // checking if no score needs to be addes
        if(!binding.checkboxAddOne.isChecked() && !binding.checkboxAddTwo.isChecked() && !binding.checkboxAddThree.isChecked())
        {
            return true;
        }

        // declaring variable to track score
        int addScore = 0;

        // calculating score to add
        if(binding.checkboxAddOne.isChecked())
        {
            addScore += 1;
        }
        if(binding.checkboxAddTwo.isChecked())
        {
            addScore += 2;
        }
        if(binding.checkboxAddThree.isChecked())
        {
            addScore += 3;
        }

        // checking and updating score
        if(!binding.toggleIsGuest.isChecked())
        {
            int score = Integer.valueOf(binding.textviewHomeScore.getText().toString());
            score += addScore;
            binding.textviewHomeScore.setText(String.valueOf(score));
        }
        else
        {
            int score = Integer.valueOf(binding.textviewGuestScore.getText().toString());
            score+= addScore;
            binding.textviewGuestScore.setText(String.valueOf(score));
        }

        // resetting buttons
        binding.checkboxAddOne.setChecked(false);
        binding.checkboxAddTwo.setChecked(false);
        binding.checkboxAddThree.setChecked(false);

        // switching possession
        if(!binding.toggleIsGuest.isChecked())
        {
            binding.toggleIsGuest.setChecked(true);
            setGuest();
        }
        else
        {
            binding.toggleIsGuest.setChecked(false);
            setHome();
        }

        return true;
    }

    // method to change view to home possession
    public void setHome()
    {
        binding.labelHome.setTextColor(getResources().getColor(R.color.red));
        binding.textviewHomeScore.setTextColor(getResources().getColor(R.color.red));
        binding.labelGuest.setTextColor(getResources().getColor(R.color.black));
        binding.textviewGuestScore.setTextColor(getResources().getColor(R.color.black));
    }

    // method to change view to guest possession
    public void setGuest()
    {
        binding.labelHome.setTextColor(getResources().getColor(R.color.black));
        binding.textviewHomeScore.setTextColor(getResources().getColor(R.color.black));
        binding.labelGuest.setTextColor(getResources().getColor(R.color.red));
        binding.textviewGuestScore.setTextColor(getResources().getColor(R.color.red));
    }


    // countdown timer class
    public CountDownTimer getNewTimer(long totalLength, long tickLength)
    {
        return new CountDownTimer(totalLength, tickLength)
        {

            @Override
            public void onTick(long l) {
                long minutes = (l / MILLIS_PER_SEC) / SECS_PER_MIN;
                long seconds = (l / MILLIS_PER_SEC) % SECS_PER_MIN;
                binding.textviewTimeRemaining.setText(minutes + ":" + seconds);
            }

            @Override
            public void onFinish() {
                binding.switchGameClock.setChecked(false);
            }
        };
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch(compoundButton.getId())
        {
            // making toggle button functional
            case R.id.toggle_is_guest:
            {
                if(binding.toggleIsGuest.isChecked())
                {
                    setGuest();
                }
                else
                {
                    setHome();
                }
                break;
            }
            //clock switch
            case R.id.switch_game_clock:
            {
                if(binding.switchGameClock.isChecked())
                {
                    timer = getNewTimer(updatedTime, MILLIS_PER_SEC);
                    timer.start();
                }
                else
                {
                    //storing time when clock switched off
                    String text1 = binding.textviewTimeRemaining.getText().toString();
                    //splitting time
                    String[] splitTime = text1.split(":");
                    long minutesLeft = Integer.valueOf(splitTime[0]) * MILLIS_PER_MIN;
                    long secondsLeft = Integer.valueOf(splitTime[1]) * MILLIS_PER_SEC;

                    //storing time
                    updatedTime = minutesLeft + secondsLeft;
                    timer.cancel();
                }
                break;
            }

        }
    }

    @Override
    public void onClick(View view) {
        String inputMins = binding.edittextNumMins.getText().toString();
        String inputSecs = binding.edittextNumSecs.getText().toString();
        // checking if input is empty
        if(inputMins.trim().length()>0 && inputSecs.trim().length()>0)
        {
            // store values
            int inMins = Integer.valueOf(inputMins);
            int inSecs = Integer.valueOf(inputSecs);

            // further validation
            if(inMins >= minimumTime && inMins <= DEFAULT_NUM_MINS && inSecs >= minimumTime && inSecs < SECS_PER_MIN)
            {
                // stop timer
                binding.switchGameClock.setChecked(false);

                // check to see if timer started
                if(timer != null)
                {
                    timer.cancel();
                }

                // updating time
                long newMinutes = inMins * MILLIS_PER_MIN;
                long newSecs = inSecs * MILLIS_PER_SEC;
                updatedTime = newMinutes + newSecs;

                // change output text
                //long textMin = (updatedTime / MILLIS_PER_SEC) / SECS_PER_MIN;
                //long textSec = (updatedTime / MILLIS_PER_SEC) % SECS_PER_MIN;

                binding.textviewTimeRemaining.setText(inMins + ":" + inSecs);

            }

        }
    }
}
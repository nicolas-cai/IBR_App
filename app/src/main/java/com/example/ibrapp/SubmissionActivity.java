package com.example.ibrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SubmissionActivity extends AppCompatActivity {
    private String pass = "Pass", fail = "Fail";

    Connection connect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);

        TextView textScore = findViewById(R.id.textView5);
        TextView message = findViewById(R.id.textView7);
        textScore.setTextSize(30);
        message.setTextSize(30);

        if(MainActivity.getIncorrect() > 0){
            textScore.setText(fail);
            getWindow().getDecorView().setBackgroundColor(Color.RED);
        }
        else{
            textScore.setText(pass);
            getWindow().getDecorView().setBackgroundColor(Color.GREEN);
        }

        Button btnExit = (Button) findViewById(R.id.button2);
        btnExit.bringToFront();
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mStartActivity = new Intent(SubmissionActivity.this, MainActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(SubmissionActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager)SubmissionActivity.this.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
            }
        });
    }




}
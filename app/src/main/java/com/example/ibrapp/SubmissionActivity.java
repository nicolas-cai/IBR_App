package com.example.ibrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SubmissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);

        Intent intent = getIntent();
        Long timeStamp = getCurrentTimeStamp();
        String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (timeStamp*1000));
        TextView textView = findViewById(R.id.textViewTime);
        textView.setText(date);
        TextView textScore = findViewById(R.id.textView5);
        String pass = "Pass", fail = "Fail";
        if(MainActivity.getIncorrect() > 0){
            textScore.setText(fail);
        }
        else{
            textScore.setText(pass);
        }

        Button btnExit = (Button) findViewById(R.id.button2);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
    }

    private Long getCurrentTimeStamp(){
        Long timestamp = System.currentTimeMillis()/1000;
        return timestamp;
    }
}
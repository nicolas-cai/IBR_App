package com.example.ibrapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity  {
    private static int incorrect = 0;
    private int currentQuestionIndex = 0;
    //private static String ip = "192.168.1.78";
    //private static final String url = "jdbc:mysql://127.0.0.1:3306/questionnaire";
    //private static final String user = "root";
    //private static final String pass = "";
    private static final String connectionString =
            "jdbc:mysql://localhost:3306/questionnaire?user=root&password=&useUnicode=true&characterEncoding=UTF-8";
    private Connection connection = null;
    private Question[] questionBank = new Question[]{
            new Question(R.string.question_1, 1), //0 = blank, 1 = yes, 2 = no
            new Question(R.string.question_2, 2),
            new Question(R.string.question_3, 1),
            new Question(R.string.question_4, 2)
    };
    private int [] responses = new int [questionBank.length];

    TextView question;
    private Spinner [] menus = new Spinner[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.setProperty("java.net.preferIPv4Stack" , "true");

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
        question = (TextView) findViewById(R.id.textView);
        menus[0] = findViewById(R.id.spinnerYesNo1);
        menus[1] = findViewById(R.id.spinnerYesNo2);
        menus[2] = findViewById(R.id.spinnerYesNo3);
        menus[3] = findViewById(R.id.spinnerYesNo4);

        String [] responses = getResources().getStringArray(R.array.yes_no);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, responses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for(Spinner menu: menus){
            menu.setAdapter(adapter);
        }

    }

    public void buttonPressed(View v){
        EditText id = (EditText) findViewById(R.id.editText);
        EditText name = (EditText) findViewById(R.id.editTextPersonName);
        Intent intent = new Intent(this, SubmissionActivity.class);
        if(TextUtils.isEmpty(id.getText()) && TextUtils.isEmpty(name.getText())) {
            id.setError("Required field");
            id.setTextColor(Color.RED);
            name.setError("Required field");
            name.setTextColor(Color.RED);
        }
        else if(TextUtils.isEmpty(id.getText())){
            id.setError("Required field");
            id.setTextColor(Color.RED);
        }
        else if(TextUtils.isEmpty(name.getText())){
            name.setError("Required field");
            name.setTextColor(Color.RED);
        }
        else{
             boolean clear = true;
            for(int i = 0; i < questionBank.length; i++){
                int answerChoice = menus[i].getSelectedItemPosition();
                if(answerChoice == 0){
                    setSpinnerError(menus[i], "Required field"); //display error message for each blank---FIX
                    clear = false;
                }
                else {
                    responses[i] = answerChoice;
                    if(!(questionBank[i].isAnswerTrue() == responses[i])){
                        incorrect++;
                    }
                }
            }
            if(clear){
                startActivity(intent);
            }
        }
    }

    private void setSpinnerError(Spinner spinner, String error){
        TextView errorText = (TextView)spinner.getSelectedView();
        errorText.setError("Error message");
        errorText.setTextColor(Color.RED);//just to highlight that this is an error
        errorText.setText(error);//changes the selected item text to this*/
    }

    public static int getIncorrect(){
        return incorrect;
    }

    private class ConnectMySql extends AsyncTask<String, Void, String> { //MySQL connection class
        String res = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                Connection con = DriverManager.getConnection( connectionString+
                       "&autoReconnect=true&failOverReadOnly=false&maxReconnects=10");
                System.out.println("Database Connection success");

                String result = "Database Connection Successful\n";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT `question` FROM questionnaire.questions;"); //mySQL code
                ResultSetMetaData rsmd = rs.getMetaData();

                while (rs.next()) {
                    result += rs.getString(1).toString() + "\n";
                }
                res = result;
            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            question.setText(result);
        }
    }
}


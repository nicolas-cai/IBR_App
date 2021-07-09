package com.example.ibrapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
    private Question[] questionBank = new Question[]{
            new Question(R.string.question_1, 1), //0 = blank, 1 = yes, 2 = no
            new Question(R.string.question_2, 2),
            new Question(R.string.question_3, 1),
            new Question(R.string.question_4, 2)
    };
    private int [] responses = new int [questionBank.length];

    private TextView [] qs = new TextView[4];
    private Spinner [] menus = new Spinner[4];
    private String [] answerKey = new String[4];

    Long timeStamp = getCurrentTimeStamp();
    private String date = new java.text.SimpleDateFormat("yyy-MM-dd HH:mm:ss").format(new java.util.Date (timeStamp*1000));
    Connection connect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.setProperty("java.net.preferIPv4Stack" , "true");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        qs[0] = findViewById(R.id.textView);
        qs[1] = findViewById(R.id.textView2);
        qs[2] = findViewById(R.id.textView3);
        qs[3] = findViewById(R.id.textView4);

        GetTextFromSQL();
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
                    setSpinnerError(menus[i], "Required field"); //display error message for each blank
                    clear = false;
                }
                else {
                    responses[i] = answerChoice;
                    String correct = "Yes";
                    if(!(questionBank[i].isAnswerTrue() == responses[i])){
                        incorrect++;
                        correct = "No";
                    }
                    SendTextFromSQL(name.getText().toString(), (i+1), menus[i].getSelectedItem().toString(), correct, date);
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

    public void GetTextFromSQL(){
        //TextView question = (TextView) findViewById(R.id.textView);
        TextView errorMessage = (TextView) findViewById(R.id.textView2); //error message in question 2
        String records = "", error = "";
        try {
            ConnectionHelper con = new ConnectionHelper();
            connect = con.getConnection();
            for(int i = 0; i < qs.length; i++) {
                records = "";
                if (connect != null) {
                    String query = "SELECT `id`, `question`, `answer` FROM questionnaire.questions WHERE `id` = " + (i+1)+";";
                    Statement st = connect.createStatement();
                    ResultSet rs = st.executeQuery(query);

                    while (rs.next()) {
                        records += rs.getString(1) + " - " + rs.getString(2) + "\n";
                        answerKey[i] = rs.getString(3);
                        if(answerKey[i].equals("Yes")){
                            questionBank[i].setAnswerTrue(1);
                        }
                        else{
                            questionBank[i].setAnswerTrue(2);
                        }
                    }
                }
                qs[i].setText(records);
            }
            connect.close();
        }catch (Exception e){
            error = e.getMessage();
            errorMessage.setText(error);
            Log.e("Error", e.getMessage());
        }
    }

    public void SendTextFromSQL(String name, int question_id, String response, String correct, String datetime){
        String records = "", error = "";
        try {
            ConnectionHelper con = new ConnectionHelper();
            connect = con.getConnection();
            if (connect != null) {
                String query = "INSERT INTO `questionnaire`.`responses` (`name`, `question_id`, `response`, `correct`, `datetime`) VALUES ('"+name+"', '"+question_id+"', '"+response+"', '"+correct+"', '"+datetime+"');";
                Statement st = connect.createStatement();
                st.executeUpdate(query);
                connect.close();
            }
        }catch (Exception e){
            error = e.getMessage();
            Log.e("Error", e.getMessage());
        }
    }

    public static String getScore(){
        if(getIncorrect() > 0){
            return "Fail";
        }
        else{
            return "Pass";
        }
    }
    private Long getCurrentTimeStamp(){
        Long timestamp = System.currentTimeMillis()/1000;
        return timestamp;
    }
    //make a get score method and call it from Submission Activity

}


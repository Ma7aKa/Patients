package com.example.serge.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.Random;

public class PatientAddingActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView name;
    private TextView surname;
    private TextView patronymic;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding);

        name = (TextView) findViewById(R.id.imia);
        surname = (TextView) findViewById(R.id.familija);
        patronymic = (TextView) findViewById(R.id.otchestvo);
        Button button = (Button) findViewById(R.id.registration_button);
        button.setOnClickListener(this);
    }


    @Override
    public void onClick(View v)
    {
        if (!name.getText().toString().equals("") && !surname.getText().toString().equals("") && !patronymic.getText().toString().equals(""))
        {
            long range = Long.MAX_VALUE;
            Random r = new Random();
            long ID = (long)(r.nextDouble()*range);
            Date date = new Date();
            Patient patient = new Patient(name.getText().toString(),surname.getText().toString(),patronymic.getText().toString());
            Intent intent = getIntent();
            intent.putExtra("Registration",date);
            intent.putExtra("Patient",patient);
            intent.putExtra("ID",ID);
            setResult(RESULT_OK,intent);
            finish();
        }
    }
}

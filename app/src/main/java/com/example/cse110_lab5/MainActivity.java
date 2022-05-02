package com.example.cse110_lab5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    //Create application
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(" ZooSeeker");

        Intent notEmpty = new Intent(this, GraphActivity.class);

        final Button button = findViewById(R.id.plan_bttn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(notEmpty);
            }
        });

    }
}
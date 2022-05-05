package com.example.cse110_lab5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.cse110_lab5.GraphActivity;
import com.example.cse110_lab5.R;

public class MainActivity extends AppCompatActivity {

    //Create application
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String start = "entrance_exit_gate";
        String path = "sample_zoo_graph.json";
        String[] toVisit = {"lions", "gators"};

        Intent notEmpty = new Intent(this, GraphActivity.class);
        notEmpty.putExtra("path", path);
        notEmpty.putExtra("start", start);
        notEmpty.putExtra("toVisit", toVisit);

        final Button button = findViewById(R.id.plan_bttn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(notEmpty);
            }
        });

    }
}
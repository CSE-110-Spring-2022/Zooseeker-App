package com.example.cse110_lab5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse110_lab5.template.ExhibitItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public RecyclerView recyclerView;

    //Create application
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] sampleExhibits = {"lions", "gators", "gorillas", "arctic_foxes"};
        ArrayList<ExhibitItem> sampleExhibitItems = new ArrayList<>();
        for (String exhibit : sampleExhibits) {
            sampleExhibitItems.add(new ExhibitItem(exhibit));
        }
        ExhibitListAdapter adapter = new ExhibitListAdapter();
        adapter.setHashStableIds(true);

        recyclerView = findViewById(R.id.exhibit_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setExhibitItems(sampleExhibitItems);
        adapter.setOnCheckBoxClickedHandler(ExhibitListAdapter::toggleCompleted);

        Intent notEmpty = new Intent(this, GraphActivity.class);

        final Button button = findViewById(R.id.plan_bttn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ArrayList<String> testExhibitList = adapter.getSelectedExhibits();
                System.out.println(testExhibitList.size());
                String[] testToVisit = testExhibitList.toArray(new String[testExhibitList.size()]);
                String start = "entrance_exit_gate";
                String path = "sample_zoo_graph.json";
                String[] toVisit = {"lions", "gators"};
                notEmpty.putExtra("path", path);
                notEmpty.putExtra("start", start);
                notEmpty.putExtra("toVisit", testToVisit);
                startActivity(notEmpty);
            }
        });

    }
}
package com.example.cse110_lab5;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse110_lab5.database.GraphDatabase;
import com.example.cse110_lab5.database.NodeDao;
import com.example.cse110_lab5.database.ZooData;
import com.example.cse110_lab5.template.ExhibitItem;
import com.example.cse110_lab5.template.ExhibitListViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public RecyclerView recyclerView;

    //Create application
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExhibitListViewModel viewModel = new ViewModelProvider(this).get(ExhibitListViewModel.class);

        String[] sampleExhibits = {"lions", "gators", "gorillas", "arctic_foxes"};
        //for (String exhibit : sampleExhibits) {
        //    sampleExhibitItems.add(new ZooData.Node(exhibit));
        //}
        ExhibitListAdapter adapter = new ExhibitListAdapter();
        adapter.setHashStableIds(true);

        //Instantiate Database
        NodeDao nodeDao = GraphDatabase.getSingleton(this).nodeDao();
        List<ZooData.Node> sampleExhibitItems = nodeDao.getExhibits();

        //Search bar
        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("search_bar_before",charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count){
                Log.d("search_bar",charSequence.toString());
                List<ZooData.Node> sampleExhibitItems = nodeDao.getFiltered(charSequence.toString());
                adapter.setExhibitItems(sampleExhibitItems);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        adapter.setExhibitItems(sampleExhibitItems);
        adapter.setOnCheckBoxClickedHandler(viewModel::toggleSelected);
        viewModel.getTodoListItems().observe(this, adapter::setExhibitItems);
        recyclerView = findViewById(R.id.exhibit_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Intent notEmpty = new Intent(this, GraphActivity.class);

        final Button button = findViewById(R.id.plan_bttn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String start = "entrance_exit_gate";
                String path = "sample_zoo_graph.json";
                notEmpty.putExtra("path", path);
                notEmpty.putExtra("start", start);
                notEmpty.putExtra("toVisit", nodeDao.getSelected().toArray(new String[]{}));
                startActivity(notEmpty);
            }
        });

    }
}
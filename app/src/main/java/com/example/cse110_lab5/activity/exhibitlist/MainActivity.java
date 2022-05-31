package com.example.cse110_lab5.activity.exhibitlist;

import static com.example.cse110_lab5.database.ZooData.loadZooGraphJSON;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.cse110_lab5.R;
import com.example.cse110_lab5.activity.graph.GraphActivity;
import com.example.cse110_lab5.activity.navigation.NavigationActivity;
import com.example.cse110_lab5.database.Converters;
import com.example.cse110_lab5.database.GraphDatabase;
import com.example.cse110_lab5.database.NodeDao;
import com.example.cse110_lab5.database.ZooData;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public RecyclerView recyclerView;

    //Create application
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ZooData.graph = loadZooGraphJSON(this, "sample_zoo_graph.json");

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int curr_exhibit = sharedPref.getInt("curr_exhibit", -1);
        Log.d("Loading Preferences Current Exhibit", curr_exhibit + "");
        String plan = sharedPref.getString("plan", "");
        Log.d("Loading Preferences Plan", plan);
        if (curr_exhibit != -1) {
            Intent nav = new Intent(this, NavigationActivity.class);
            nav.putExtra("plan", Converters.fromString(plan).toArray(new String[0]));
            nav.putExtra("curr_exhibit", curr_exhibit);
            startActivity(nav);
        } else {
            setContentView(R.layout.activity_main);
            ExhibitListViewModel viewModel = new ViewModelProvider(this).get(ExhibitListViewModel.class);
            ExhibitListAdapter adapter = new ExhibitListAdapter();
            adapter.setHashStableIds(true);

            //Instantiate Database
            NodeDao nodeDao = GraphDatabase.getSingleton(this).nodeDao();
            List<ZooData.Node> sampleExhibitItems = nodeDao.getExhibits();

            //Search bar
            EditText searchBar = findViewById(R.id.search_bar);
            searchBar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    Log.d("search_bar_before", charSequence.toString());
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    Log.d("search_bar", charSequence.toString());
                    List<ZooData.Node> sampleExhibitItems = nodeDao.getFiltered(charSequence.toString());
                    adapter.setExhibitItems(sampleExhibitItems);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
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
                    String start = nodeDao.getGate().id;
                    String path = "sample_zoo_graph.json";
                    notEmpty.putExtra("filepath", path);
                    notEmpty.putExtra("start", start);
                    notEmpty.putExtra("toVisit", nodeDao.getSelected().toArray(new String[]{}));
                    startActivity(notEmpty);
                }
            });
        }
    }

    /**
     * Sets all exhibits to unselected in the Node Database and on the display
     *
     * @param view the corresponding display view for the MainActivity
     */
    public void onClearExhibitsPressed(View view){
        NodeDao nodeDao = GraphDatabase.getSingleton(this).nodeDao();
        nodeDao.clearAll();
    }
}
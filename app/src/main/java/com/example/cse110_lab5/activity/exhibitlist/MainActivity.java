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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
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

/**
 * This Activity is the initial app screen that has the list of all exhibits that you can search
 * through and select for planning. It has functionality to clear the exhibits and shows a compact
 * list of the exhibits selected so far at the bottom of the screen
 */
public class MainActivity extends AppCompatActivity {

    // RecyclerView for the list of all the exhibits to select
    public RecyclerView recyclerView;

    //Create application
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initially load the graph from the JSON file
        ZooData.graph = loadZooGraphJSON(this, "sample_zoo_graph.json");

        // Get the shared preferences for the app and see if there's already a current exhibit and plan set
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int curr_exhibit = sharedPref.getInt("curr_exhibit", -1);
        Log.d("Loading Preferences Current Exhibit", curr_exhibit + "");
        String plan = sharedPref.getString("plan", "");
        Log.d("Loading Preferences Plan", plan);

        // if there is a current exhibit then the value is the index of the target exhibit in the plan
        if (curr_exhibit != -1) {
            // go straight to the navigation activity with the retained plan and current exhibit bundled
            Intent nav = new Intent(this, NavigationActivity.class);
            nav.putExtra("plan", Converters.fromString(plan).toArray(new String[0]));
            nav.putExtra("curr_exhibit", curr_exhibit);
            startActivity(nav);
        }

        // Set up the UI for MainActivity with its adapter and viewmodel
        setContentView(R.layout.activity_main);
        ExhibitListViewModel viewModel = new ViewModelProvider(this).get(ExhibitListViewModel.class);
        ExhibitListAdapter adapter = new ExhibitListAdapter();
        adapter.setHashStableIds(true);

        //Instantiate Database
        NodeDao nodeDao = GraphDatabase.getSingleton(this).nodeDao();
        List<ZooData.Node> exhibitItems = nodeDao.getExhibits();

        //Search bar
        EditText searchBar = findViewById(R.id.search_bar);

        // Listener for the search to see when text changes
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("search_bar_before", charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // when text changes we want to filter the exhibits by the search string and update the adapter
                Log.d("search_bar", charSequence.toString());
                List<ZooData.Node> filteredExhibits = nodeDao.getFiltered(charSequence.toString());
                adapter.setExhibitItems(filteredExhibits);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Initially set the exhibit items to all exhibits
        adapter.setExhibitItems(exhibitItems);

        // Add a handler for when a checkbox is clicked, and setting adapter's exhibit items when
        // view model notices changes in the data
        adapter.setOnCheckBoxClickedHandler(viewModel::toggleSelected);
        viewModel.getExhibitItems().observe(this, adapter::setExhibitItems);

        // Gets the compact list view and adds an observer to check when data is selected
        TextView compactList = findViewById(R.id.compact_list);
        viewModel.getSelectedItems().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                // When the data is changed, update the text of the compact list to include all
                // selected exhibits
                compactList.setText("Selected Exhibits: \n" + String.join(", ", strings));
            }
        });

        // Setup recyclerView object with everything we've initialized previously
        recyclerView = findViewById(R.id.exhibit_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Create a new intent to our GraphActivity where the plan planning occurs
        Intent planPaths = new Intent(this, GraphActivity.class);

        final Button button = findViewById(R.id.plan_bttn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When plan is clicked, setup start, path, and grab all selected exhibits to visit
                // for planning
                String start = nodeDao.getGate().id;
                String path = "sample_zoo_graph.json";
                planPaths.putExtra("filepath", path);
                planPaths.putExtra("start", start);
                planPaths.putExtra("toVisit", nodeDao.getSelected().toArray(new String[]{}));
                startActivity(planPaths);
            }
        });

    }

    /**
     * Sets all exhibits to unselected in the Node Database and on the display
     *
     * @param view the corresponding display view for the MainActivity
     */
    public void onClearExhibitsPressed(View view){
        NodeDao nodeDao = GraphDatabase.getSingleton(this).nodeDao();
        // Sets selected to false for all exhibits
        nodeDao.clearAll();
    }
}
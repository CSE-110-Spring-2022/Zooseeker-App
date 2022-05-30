package com.example.cse110_lab5.activity.navigation;

import static com.example.cse110_lab5.database.ZooData.loadZooGraphJSON;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse110_lab5.R;
import com.example.cse110_lab5.activity.graph.GraphActivity;
import com.example.cse110_lab5.activity.location.Coord;
import com.example.cse110_lab5.activity.location.LocationModel;
import com.example.cse110_lab5.database.GraphDatabase;
import com.example.cse110_lab5.database.NodeDao;
import com.example.cse110_lab5.database.ZooData;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NavigationActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public Switch directionsSwitch;
    private int curr_exhibit = 1;
    private final PermissionChecker permissionChecker = new PermissionChecker(this);
    Pair<String,GraphPath<String, ZooData.IdentifiedEdge>> nextDirections; 
    NodeDao nodeDao;
    String[] exhibitList;
    String[] toVisit;
    Bundle bundle;
    List<Pair<String,GraphPath<String, ZooData.IdentifiedEdge>>> plan;
    Graph<String, ZooData.IdentifiedEdge> graph;

    public static final String EXTRA_USE_LOCATION_SERVICE = "use_location_updated";
    private final String TAG = "Location";

    private final String gateID = "entrance_exit_gate";

    private boolean useLocationService;
    private LocationModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);

        Intent intent = getIntent();
        bundle = intent.getExtras();

        Button prevButton = findViewById(R.id.prev_bttn);
        prevButton.setBackgroundColor(Color.GRAY);
        prevButton.setClickable(false);

        Pair<String,GraphPath<String, ZooData.IdentifiedEdge>> exhibitDirections
                = new Pair<>("", null);

        if (bundle != null) {
            exhibitDirections = (Pair<String,GraphPath<String, ZooData.IdentifiedEdge>>) bundle.get(String.valueOf(curr_exhibit));
            exhibitList = bundle.getStringArray("toVisit");
            toVisit = exhibitList;
            graph = ZooData.graph;

            plan = new ArrayList<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>>();
            // minus 3 because the bundle's extras has each pair in the plan AND the toVisit array
            for(int i = 0; i < bundle.size()-2; i++){
                Log.d("bundle", i+"");
                plan.add((Pair<String,GraphPath<String, ZooData.IdentifiedEdge>>)bundle.get(String.valueOf(i)));
                Log.d("exhibit", plan.get(i).getFirst());
            }
            nextDirections = plan.get(curr_exhibit);
        }

        recyclerView = (RecyclerView) findViewById(R.id.path_to_exhibit);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        NavigationAdapter pathAdapter = new NavigationAdapter(this, exhibitDirections.getSecond(), false);

        directionsSwitch = (Switch) findViewById(R.id.directionsSwitch);
        directionsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton directionsButton, boolean toggled) {
                pathAdapter.refreshView(toggled);
                recyclerView.setAdapter(pathAdapter);
            }
        });

        nodeDao = GraphDatabase.getSingleton(this).nodeDao();
        TextView total = findViewById(R.id.Exhibit_Name);
        String name = nodeDao.get(exhibitDirections.getFirst()).name;
        total.setText(name);

        recyclerView.setAdapter(pathAdapter);

        useLocationService = getIntent().getBooleanExtra(EXTRA_USE_LOCATION_SERVICE, false);

        // Set up the model.
        model = new ViewModelProvider(this).get(LocationModel.class);

        // If GPS is enabled, then update the model from the Location service.
        if (useLocationService) {
            var permissionChecker = new PermissionChecker(this);
            permissionChecker.ensurePermissions();

            var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            var provider = LocationManager.GPS_PROVIDER;
            model.addLocationProviderSource(locationManager, provider);
        }

        /* Listen for location Updates */

            var exhibits = nodeDao.getExhibits();
            // Observe the model and detect off track when location is updated.
            model.getLastKnownCoords().observe(this, (coord) -> {
                Log.i(TAG, String.format("Observing location model update to %s", coord));
                ZooData.Node targetNode = nodeDao.get(nextDirections.getFirst());
                String newStartID = detectOffTrack(coord, exhibits, targetNode); //need access to list of all exhibits in path
                Log.d(TAG, newStartID);
                /*
                plan is a list and we have curr exhibit
                split plan into 0:currExhibit and currExhibit:size()
                compute TSP on [newStartID + currExhibit:size()]
                remove newStartID (first index) from new plan
                add 0:currExhibit + newPlan

                [A,B,C,E,F,G]
                [A,B,C] -> 0:currExhibit
                TSP(D + [E,F,G])
                [D,F,G,E]
                [F,G,E]
                [A,B,C,F,G,E]
                 */
                List<Pair<String,GraphPath<String, ZooData.IdentifiedEdge>>> initial = plan.subList(0, curr_exhibit);
                List<Pair<String,GraphPath<String, ZooData.IdentifiedEdge>>> newPlan = plan.subList(curr_exhibit, plan.size());
                List<String> remainderIds = new ArrayList<String>();
                for (Pair<String,GraphPath<String, ZooData.IdentifiedEdge>> node : newPlan){
                    if(!node.getFirst().equals(gateID))remainderIds.add(node.getFirst());
                }
                if(remainderIds.size() == 0){
                    remainderIds.add(gateID);
                }
                String[] toVisit = new String[remainderIds.size()];
                remainderIds.toArray(toVisit);
                Log.d("plan/toVisit", Arrays.toString(toVisit));
                newPlan = GraphActivity.tsp(graph, newStartID, toVisit);
                
                // this removes the directions to the newStartID at the beginning and end of TSP since it's useless for us
                // apart from rerouting
                Log.d("plan/newOriginal", newPlan.toString());
                newPlan.remove(0);
                newPlan.remove(newPlan.size()-1);
                Log.d("plan/initial", initial.toString());
                Log.d("plan/new", newPlan.toString());

                if(newPlan.size() > 0){
                    String lastExhibit = newPlan.get(newPlan.size()-1).getFirst();
                    if(!lastExhibit.equals(gateID)) {
                        newPlan.add(new Pair<>(gateID, new DijkstraShortestPath<>(graph).getPath(newPlan.get(newPlan.size()-1).getFirst(), gateID)));
                    }

                    if(!newPlan.get(0).getFirst().equals(plan.get(curr_exhibit))){
                        // prompt for replan to see if they actually want to accept our replan
                    }
                    initial.addAll(newPlan);
                }
                plan = initial;

                nextDirections =
                        plan.get(curr_exhibit);

                String exhibitName = nodeDao.get(nextDirections.getFirst()).name;
                total.setText(exhibitName);
                boolean toggled = directionsSwitch.isChecked();
                recyclerView.setAdapter(new NavigationAdapter(this, nextDirections.getSecond(), toggled));
            });
    }

    public void onMockPressed(View v){
        String lat = ((EditText)findViewById(R.id.lat)).getText().toString();
        String lon = ((EditText)findViewById(R.id.lon)).getText().toString();
        Log.d("lat", lat);
        Log.d("lon", lon);
        if(!lat.equals("") && !lon.equals(""))
            model.mockLocation(new Coord(Double.parseDouble(lat),Double.parseDouble(lon)));
    }

    public void onNextPressed(View v) {
        //List<Coord> json = LocationModel.loadJSON(this, "test_route.json");
//        Log.d("Location", json.toString());
//        model.mockRoute(json, 2000, TimeUnit.MILLISECONDS);
        Log.d("Navigation", "Next button pressed");
        if (curr_exhibit != plan.size() - 1) {
            curr_exhibit += 1;

            Button button = findViewById(R.id.prev_bttn);
            button.setBackgroundColor(getResources().getColor(R.color.green_500));
            button.setClickable(true);
        }
        if (curr_exhibit == plan.size() - 1){
            Button button = findViewById(R.id.next_bttn);
            button.setBackgroundColor(Color.GRAY);
            button.setClickable(false);
        }
	nextDirections =
		plan.get(curr_exhibit);

        TextView total = findViewById(R.id.Exhibit_Name);
        String name = nodeDao.get(nextDirections.getFirst()).name;
        total.setText(name);
        boolean toggled = directionsSwitch.isChecked();
        NavigationAdapter pathAdapter = new NavigationAdapter(this, nextDirections.getSecond(), toggled);
        directionsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton directionsButton, boolean toggled) {
                pathAdapter.refreshView(toggled);
                recyclerView.setAdapter(pathAdapter);
            }
        });
        recyclerView.setAdapter(pathAdapter);

    }

    public void onPrevPressed(View v) {
        Log.d("Navigation", "Prev button pressed");
        if (curr_exhibit != 1) {
            curr_exhibit -= 1;
            Button button = findViewById(R.id.next_bttn);
            button.setBackgroundColor(getResources().getColor(R.color.green_500));
            button.setClickable(true);
        }
        if(curr_exhibit == 1){
            Button button = findViewById(R.id.prev_bttn);
            button.setBackgroundColor(Color.GRAY);
            button.setClickable(false);
        }
	nextDirections = 
		plan.get(curr_exhibit);

        TextView total = findViewById(R.id.Exhibit_Name);
        String name = nodeDao.get(nextDirections.getFirst()).name;
        total.setText(name);
        boolean toggled = directionsSwitch.isChecked();
        NavigationAdapter pathAdapter = new NavigationAdapter(this, nextDirections.getSecond(), toggled);
        directionsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton directionsButton, boolean toggled) {
                pathAdapter.refreshView(toggled);
                recyclerView.setAdapter(pathAdapter);
            }
        });
        recyclerView.setAdapter(pathAdapter);    }

    //Refactor name
    public String detectOffTrack(Coord coord, List<ZooData.Node> exhibits, ZooData.Node target) {
        //Check if distance from your current location to the start is still the minimum distance out of the distance
        //from your current location to every exhibit
        double min_distance = Double.MAX_VALUE;
        String startExhibit = "";
        for(ZooData.Node exhibit: exhibits){
            if(!exhibit.id.equals(target.id)){
                Coord current_exhibit_location = new Coord(exhibit.lat, exhibit.lng);
                double distance = current_exhibit_location.distanceTo(coord);
                if(distance < min_distance) {
                    min_distance = distance;
                    startExhibit = exhibit.id;
                }
            }
        }
        return startExhibit;
    }


}

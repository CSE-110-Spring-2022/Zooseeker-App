package com.example.cse110_lab5.activity.navigation;

import static com.example.cse110_lab5.database.ZooData.loadZooGraphJSON;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NavigationActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
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
            graph = loadZooGraphJSON(this, bundle.getString("path"), toVisit);

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

        NavigationAdapter pathAdapter = new NavigationAdapter(this, exhibitDirections.getSecond());

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
//                plan = GraphActivity.tsp(graph, newStartID, toVisit);
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

	if(curr_exhibit == plan.size() - 1){
        toVisit = new String[]{"entrance_exit_gate"}; // since when you're at the last node you can only visit the exit next
    } else{
        toVisit = Arrays.copyOfRange(exhibitList, curr_exhibit, exhibitList.length);
    }

        TextView total = findViewById(R.id.Exhibit_Name);
        String name = nodeDao.get(nextDirections.getFirst()).name;
        total.setText(name);
        recyclerView.setAdapter(new NavigationAdapter(this, nextDirections.getSecond()));

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
	toVisit = Arrays.copyOfRange(exhibitList, curr_exhibit, exhibitList.length);
        TextView total = findViewById(R.id.Exhibit_Name);
        String name = nodeDao.get(nextDirections.getFirst()).name;
        total.setText(name);
        recyclerView.setAdapter(new NavigationAdapter(this, nextDirections.getSecond()));
    }

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

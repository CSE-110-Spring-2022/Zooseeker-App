package com.example.cse110_lab5.activity.navigation;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse110_lab5.R;
import com.example.cse110_lab5.database.GraphDatabase;
import com.example.cse110_lab5.database.ZooData;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.util.Pair;

import java.util.List;

public class NavigationActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    private int curr_exhibit = 1;
    private final PermissionChecker permissionChecker = new PermissionChecker(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        Button prevButton = findViewById(R.id.prev_bttn);
        prevButton.setBackgroundColor(Color.GRAY);
        prevButton.setClickable(false);

        Pair<String,GraphPath<String, ZooData.IdentifiedEdge>> exhibitDirections
                = new Pair<>("", null);

        if (bundle != null) {
            exhibitDirections = (Pair<String,GraphPath<String, ZooData.IdentifiedEdge>>) bundle.get(String.valueOf(curr_exhibit));
        }

        recyclerView = (RecyclerView) findViewById(R.id.path_to_exhibit);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        NavigationAdapter pathAdapter = new NavigationAdapter(this, exhibitDirections.getSecond());

        TextView total = findViewById(R.id.Exhibit_Name);
        String name = GraphDatabase.getSingleton(this).nodeDao().get(exhibitDirections.getFirst()).name;
        total.setText(name);

        recyclerView.setAdapter(pathAdapter);

        //Permission checker (refactor later)
        if (permissionChecker.ensurePermissions()){
            Log.d("Permissions","Being checked");
        }

        /* Listen for location Updates */

            var provider = LocationManager.GPS_PROVIDER;
            var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            var nodeDAO = GraphDatabase.getSingleton(this).nodeDao();
            var exhibits = nodeDAO.getExhibits();
            var locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Log.d("LAB7", String.format("Location changed: %s", location));
                    if (curr_exhibit != getIntent().getExtras().size() - 1) {
                        Pair<String,GraphPath<String, ZooData.IdentifiedEdge>> nextDirections =
                                (Pair<String,GraphPath<String, ZooData.IdentifiedEdge>>) getIntent().getExtras().get(String.valueOf(curr_exhibit+1));
                        ZooData.Node targetNode = nodeDAO.get(nextDirections.getFirst());
                        String newStartID = detectOffTrack(location, exhibits, targetNode); //need access to list of all exhibits in path
                        List<Pair<String,GraphPath<String, ZooData.IdentifiedEdge>>> graph;
                    }
                }
            };

            locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
    }

    public void onNextPressed(View v) {
        Log.d("Navigation", "Next button pressed");
        if (curr_exhibit != getIntent().getExtras().size() - 1) {
            curr_exhibit += 1;

            Button button = findViewById(R.id.prev_bttn);
            button.setBackgroundColor(getResources().getColor(R.color.green_500));
            button.setClickable(true);
        } else {
            Button button = findViewById(R.id.next_bttn);
            button.setBackgroundColor(Color.GRAY);
            button.setClickable(false);
        }
        Pair<String,GraphPath<String, ZooData.IdentifiedEdge>> nextDirections =
                (Pair<String,GraphPath<String, ZooData.IdentifiedEdge>>) getIntent().getExtras().get(String.valueOf(curr_exhibit));
        TextView total = findViewById(R.id.Exhibit_Name);
        String name = GraphDatabase.getSingleton(this).nodeDao().get(nextDirections.getFirst()).name;
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
        } else {
            Button button = findViewById(R.id.prev_bttn);
            button.setBackgroundColor(Color.GRAY);
            button.setClickable(false);
        }
        Pair<String,GraphPath<String, ZooData.IdentifiedEdge>> nextDirections =
                (Pair<String,GraphPath<String, ZooData.IdentifiedEdge>>) getIntent().getExtras().get(String.valueOf(curr_exhibit));
        TextView total = findViewById(R.id.Exhibit_Name);
        String name = GraphDatabase.getSingleton(this).nodeDao().get(nextDirections.getFirst()).name;
        total.setText(name);
        recyclerView.setAdapter(new NavigationAdapter(this, nextDirections.getSecond()));
    }

    //Refactor name
    public String detectOffTrack(Location location, List<ZooData.Node> exhibits, ZooData.Node target) {
        //Check if distance from your current location to the start is still the minimum distance out of the distance
        //from your current location to every exhibit
        double min_distance = Double.MAX_VALUE;
        String startExhibit = "";
        for(ZooData.Node exhibit: exhibits){
            if(!exhibit.id.equals(target.id)){
                Location current_exhibit_location = new Location("");
                current_exhibit_location.setLatitude(exhibit.lat);
                current_exhibit_location.setLongitude(exhibit.lng);
                double distance = current_exhibit_location.distanceTo(location);
                if(distance < min_distance) {
                    min_distance = distance;
                    startExhibit = exhibit.id;
                }
            }
        }
        return startExhibit;
    }


}

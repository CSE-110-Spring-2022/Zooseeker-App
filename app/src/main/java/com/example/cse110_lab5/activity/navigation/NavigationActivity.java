package com.example.cse110_lab5.activity.navigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse110_lab5.R;
import com.example.cse110_lab5.activity.location.Coord;
import com.example.cse110_lab5.activity.location.LocationModel;
import com.example.cse110_lab5.database.Converters;

import java.util.Arrays;
import com.example.cse110_lab5.database.GraphDatabase;
import com.example.cse110_lab5.database.NodeDao;

/**
 * This activity handles displaying instructions navigating from the user's location to the exhibits
 * they want to visit, in the optimal route minimizing the distance they have to walk. This activity
 * also handles establishing the user's location, and prompting the user when the optimal plan
 * is different from the one currently instructed.
 */
public class NavigationActivity extends AppCompatActivity {

    Bundle bundle;

    public RecyclerView recyclerView;
    private NavigationViewModel viewModel;

    private LocationModel model;

    TextView total;

    public static final String EXTRA_USE_LOCATION_SERVICE = "use_location_updated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);

        viewModel = new ViewModelProvider(this)
                .get(NavigationViewModel.class);

        /* Get the plan and the current exhibit destination from the calling Activity */
        Intent intent = getIntent();
        bundle = intent.getExtras();
        if(bundle.containsKey("curr_exhibit") && bundle.containsKey("plan")){
            viewModel.setPlan((String[]) bundle.get("plan"));
            viewModel.setCurrExhibit((int)bundle.get("curr_exhibit"));
        }

        /* Update the Adapter based on the ViewModel */
        NavigationAdapter adapter = new NavigationAdapter();
        viewModel.getDisplayStrings().observe(this, adapter::update);
        total = findViewById(R.id.Exhibit_Name);
        total.setText(viewModel.getCurrExhibitName());
        viewModel.updateFromLocation();

        /* Button Handlers */
        {
            Button prevButton = findViewById(R.id.prev_bttn);
            prevButton.setOnClickListener(view -> {
                viewModel.toPrevExhibit();
                total.setText(viewModel.getCurrExhibitName());
            });

            Button nextButton = findViewById(R.id.next_bttn);
            nextButton.setOnClickListener(view -> {
                viewModel.toNextExhibit();
                total.setText(viewModel.getCurrExhibitName());
            });

            Button skipButton = findViewById(R.id.skip_bttn);
            skipButton.setOnClickListener(view -> {
                viewModel.skipExhibit();
                total.setText(viewModel.getCurrExhibitName());
            });

            Button mockButton = findViewById(R.id.mock_btn);
            mockButton.setOnClickListener(view -> {
                String lat = ((EditText) findViewById(R.id.lat)).getText().toString();
                String lon = ((EditText) findViewById(R.id.lon)).getText().toString();
                Log.d("lat", lat);
                Log.d("lon", lon);
                if (!lat.equals("") && !lon.equals("")) {
                    model.mockLocation(new Coord(Double.parseDouble(lat), Double.parseDouble(lon)));
                }
            });

            Switch directionsSwitch = findViewById(R.id.directionsSwitch);
            directionsSwitch.setOnCheckedChangeListener((directionsButton, toggled) -> viewModel.toggleDetailedDirections());

        }

        /* Bind RecyclerView to the Adapter */
        recyclerView = (RecyclerView) findViewById(R.id.path_to_exhibit);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        /* Location Service Setup */
        {
            boolean useLocationService = getIntent().getBooleanExtra(EXTRA_USE_LOCATION_SERVICE, false);

            // Set up the model
            model = new ViewModelProvider(this).get(LocationModel.class);

            // If GPS is enabled, then update the model from the Location service.
            if (useLocationService) {
                var permissionChecker = new PermissionChecker(this);
                if (permissionChecker.ensurePermissions()) return;

                var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                var provider = LocationManager.GPS_PROVIDER;
                model.addLocationProviderSource(locationManager, provider);
            }

            // Update ViewModel whenever the location changes
            model.getLastKnownCoords().observe(this, coord -> {
                viewModel.updateFromLocation(coord);
                total.setText(viewModel.getCurrExhibitName());
            });
        }

        /* Handle prompt Dialog when user is offtrack */
        {
            AlertDialog.Builder replanAlertBuilder = new AlertDialog.Builder(this);
            replanAlertBuilder.setMessage("Do you want to replan?");
            replanAlertBuilder.setCancelable(true);

            // User wants to replan
            replanAlertBuilder.setPositiveButton(
                    "Yes",
                    (dialog, id) -> {
                        viewModel.replan();
                        total.setText(viewModel.getCurrExhibitName());
                        dialog.cancel();
                    });

            // User does not want to replan
            replanAlertBuilder.setNegativeButton(
                    "No",
                    (dialog, id) -> dialog.cancel());

            AlertDialog replanAlert = replanAlertBuilder.create();

            // Determine when to show Dialog by observing the ViewModel
            viewModel.getOffTrack().observe(this, aBoolean -> {
                if (aBoolean) {
                    replanAlert.show();
                }
            });
        }
    }

    /**
     * Preserve data when app is closed in any way
     */
    @Override
    protected void onPause() {
        super.onPause();
        String plan = Converters.fromArrayList(Arrays.asList(viewModel.getPlan()));
        int curr_exhibit = viewModel.getCurrExhibit();

        // Load plan and current exhibit into SharedPreferences
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("plan", plan);
        editor.putInt("curr_exhibit", curr_exhibit);
        editor.apply();

        Log.d("Navigation Saving Preferences Plan", plan);
        Log.d("Navigation Saving Preferences Exhibit", curr_exhibit + "");
    }

    /**
     * Clear the plan and current exhibit when button is pressed
     */
    public void onClearPlanPressed(View view){
        NodeDao nodeDao = GraphDatabase.getSingleton(this).nodeDao();
        nodeDao.clearAll();

        viewModel.setPlan(new String[0]);
        viewModel.setCurrExhibit(-1);

        finish();
    }
}

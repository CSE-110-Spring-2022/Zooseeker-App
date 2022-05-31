package com.example.cse110_lab5.activity.navigation;

import android.content.Context;
import android.content.Intent;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse110_lab5.R;
import com.example.cse110_lab5.activity.location.Coord;
import com.example.cse110_lab5.activity.location.LocationModel;

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

        Intent intent = getIntent();
        bundle = intent.getExtras();

        viewModel = new ViewModelProvider(this)
                .get(NavigationViewModel.class);

        if (bundle != null) {
            viewModel.setPlan((String[]) bundle.get("plan"));
        }

        total = findViewById(R.id.Exhibit_Name);

        NavigationAdapter adapter = new NavigationAdapter();
        viewModel.getDisplayStrings().observe(this, adapter::update);
        total.setText(viewModel.getCurrExhibitName());
        viewModel.updateFromLocation();

        Button prevButton = findViewById(R.id.prev_bttn);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.toPrevExhibit();
                total.setText(viewModel.getCurrExhibitName());
            }
        });

        Button nextButton = findViewById(R.id.next_bttn);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.toNextExhibit();
                total.setText(viewModel.getCurrExhibitName());
            }
        });

        Button skipButton = findViewById(R.id.skip_bttn);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.skipExhibit();
                total.setText(viewModel.getCurrExhibitName());
            }
        });

        Button mockButton = findViewById(R.id.mock_btn);
        mockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lat = ((EditText)findViewById(R.id.lat)).getText().toString();
                String lon = ((EditText)findViewById(R.id.lon)).getText().toString();
                Log.d("lat", lat);
                Log.d("lon", lon);
                if(!lat.equals("") && !lon.equals("")) {
                    model.mockLocation(new Coord(Double.parseDouble(lat), Double.parseDouble(lon)));
                }
            }
        });

        Switch directionsSwitch = findViewById(R.id.directionsSwitch);
        directionsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton directionsButton, boolean toggled) {
                viewModel.toggleDetailedDirections();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.path_to_exhibit);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        boolean useLocationService = getIntent().getBooleanExtra(EXTRA_USE_LOCATION_SERVICE, false);

        // Set up the model
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
        model.getLastKnownCoords().observe(this, new Observer<Coord>() {
            @Override
            public void onChanged(Coord coord) {
                viewModel.updateFromLocation(coord);
                total.setText(viewModel.getCurrExhibitName());
            }
        });
    }
}

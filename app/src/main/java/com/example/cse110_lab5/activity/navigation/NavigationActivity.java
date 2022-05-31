package com.example.cse110_lab5.activity.navigation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse110_lab5.R;
import com.example.cse110_lab5.activity.location.Coord;
import com.example.cse110_lab5.activity.location.LocationModel;
import com.example.cse110_lab5.database.Converters;

import java.util.ArrayList;
import java.util.Arrays;
import com.example.cse110_lab5.activity.exhibitlist.MainActivity;
import com.example.cse110_lab5.database.GraphDatabase;
import com.example.cse110_lab5.database.NodeDao;
import com.example.cse110_lab5.database.ZooData;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.util.Pair;

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

        int curr_exhibit = -1;
        String plan = "";
        if(bundle.containsKey("curr_exhibit") && bundle.containsKey("plan")){
            viewModel.setPlan((String[]) bundle.get("plan"));
            viewModel.setCurrExhibit((int)bundle.get("curr_exhibit"));
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
            if(permissionChecker.ensurePermissions()) return;

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

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Do you wants to Replan?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        viewModel.replan();
                        total.setText(viewModel.getCurrExhibitName());
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        //alert11.show();

        viewModel.getOt().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                   alert11.show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        String plan = Converters.fromArrayList(Arrays.asList(viewModel.getPlan()));
        int curr_exhibit = viewModel.getCurrExhibit();

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("plan", plan);
        editor.putInt("curr_exhibit", curr_exhibit);
        editor.apply();
        Log.d("Navigation Saving Preferences Plan", plan);
        Log.d("Navigation Saving Preferences Exhibit", curr_exhibit + "");
    }

    public void onClearPlanPressed(View view){
        NodeDao nodeDao = GraphDatabase.getSingleton(this).nodeDao();
        nodeDao.clearAll();
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
    }
}

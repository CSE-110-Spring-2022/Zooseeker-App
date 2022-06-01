package com.example.cse110_lab5.activity.location;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cse110_lab5.database.ZooData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class LocationModel extends AndroidViewModel {
    private final String TAG = "Location";
    private final MediatorLiveData<Coord> lastKnownCoords;

    private LiveData<Coord> locationProviderSource = null;
    private MutableLiveData<Coord> mockSource = null;

    public LocationModel(@NonNull Application application) {
        super(application);
        lastKnownCoords = new MediatorLiveData<>();

        // Create and add the mock source.
        mockSource = new MutableLiveData<>();

        // update last known coords using mock source as its source for setting the coordinate values
        lastKnownCoords.addSource(mockSource, lastKnownCoords::setValue);
    }

    public LiveData<Coord> getLastKnownCoords() {
        return lastKnownCoords;
    }

    /**
     * @param locationManager the location manager to request updates from.
     * @param provider        the provider to use for location updates (usually GPS).
     * @apiNote This method should only be called after location permissions have been obtained.
     * @implNote If a location provider source already exists, it is removed.
     */
    @SuppressLint("MissingPermission")
    public void addLocationProviderSource(LocationManager locationManager, String provider) {
        // If a location provider source is already added, remove it.
        if (locationProviderSource != null) {
            removeLocationProviderSource();
        }

        // Create a new GPS source.
        var providerSource = new MutableLiveData<Coord>();
        var locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                var coord = Coord.fromLocation(location);
                Log.i(TAG, String.format("Model received GPS location update: %s", coord));
                // get the location manager's gps data ready for use for updating last known coords
                providerSource.postValue(coord);
            }
        };
        // Register for updates.
        // Just for realistic purposes, we used 3seconds and 2meters as the update constraints
        locationManager.requestLocationUpdates(provider, 3000, 2f, locationListener);

        locationProviderSource = providerSource;
        lastKnownCoords.addSource(locationProviderSource, lastKnownCoords::setValue);
    }

    void removeLocationProviderSource() {
        if (locationProviderSource == null) return;
        lastKnownCoords.removeSource(locationProviderSource);
    }

    // simple function that uses a coordinate input to to mock the location
    @VisibleForTesting
    public void mockLocation(Coord coords) {
        mockSource.postValue(coords);
    }

    @VisibleForTesting
    public Future<?> mockRoute(List<Coord> route, long delay, TimeUnit unit) {
        return Executors.newSingleThreadExecutor().submit(() -> {
            // take in a list of coordinates on the route and mock the location with the specified
            // delay in between
            int i = 1;
            int n = route.size();
            for (var coord : route) {
                // Mock the location...
                Log.i(TAG, String.format("Model mocking route (%d / %d): %s", i++, n, coord));
                mockLocation(coord);

                // Sleep for a while...
                try {
                    Thread.sleep(unit.toMillis(delay));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

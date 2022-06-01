package com.example.cse110_lab5.activity.location;

import android.location.Location;

import androidx.annotation.NonNull;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.maps.model.LatLng;

public class Coord {
    public Coord(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public final double lat;
    public final double lng;

    public static Coord of(double lat, double lng) {
        return new Coord(lat, lng);
    }

    /**
     * @param latLng the input LatLng to create a Coordinate from
     * @return the transformed Coord
     */
    public static Coord fromLatLng(LatLng latLng) {
        return Coord.of(latLng.latitude, latLng.longitude);
    }

    /**
     * @return a Latitude and Longitude object with the Coord attributes
     */
    public LatLng toLatLng() {
        return new LatLng(lat, lng);
    }

    public static Coord fromLocation(Location location) {
        return Coord.of(location.getLatitude(), location.getLongitude());
    }

    /**
     *  Calculates the Great-Circle distance on a sphere between the current Coordinate
     *  and the input parameter
     *
     * @param coord the Coordinate to find the distance from
     * @return the distance between Coordinates
     */
    public double distanceTo(Coord coord){
        double rad = 6371; // Earth's radius in km - change to change output units
        double lat1 = lat * Math.PI/180;
        double lat2 = coord.lat * Math.PI/180;
        double lon1 = lng * Math.PI/180;
        double lon2 = coord.lng * Math.PI/180;

        double dLon = lon2 - lon1;
        double dLat = lat2 - lat1;

        double a = Math.pow(Math.sin(dLat/2), 2) + Math.cos(lat1)*Math.cos(lat2)*Math.pow(Math.sin(dLon/2),2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return c*rad;
    }

    /**
     * @param o object to compare with
     * @return true if the longitude and latitude of the object match
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coord coord = (Coord) o;
        return Double.compare(coord.lat, lat) == 0 && Double.compare(coord.lng, lng) == 0;
    }

    /**
     *  Finds the hash given Coordinate latitude and longitude
     *
     * @return the corresponding hash for the Coordinate
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(lat, lng);
    }

    /**
     * Formats a coordinate with its given latitude and longitude
     *
     * @return the formatted String for the Coordinate
     */
    @NonNull
    @Override
    public String toString() {
        return String.format("Coord{lat=%s, lng=%s}", lat, lng);
    }
}

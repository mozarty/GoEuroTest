package com.goeuro.goeurotest.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.goeuro.goeurotest.R;
import com.goeuro.goeurotest.dto.Place;
import com.goeuro.goeurotest.services.ServiceManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by salem on 8/29/15.
 */
public class HomeController implements LocationListener, Comparator<Place>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int LOCATION_REFRESH_TIME = 60 * 1000;


    Location currentLocation;
    private static HomeController ourInstance = null;
    Context context;

    public static HomeController getInstance(Context context) {

        if (ourInstance == null)
            ourInstance = new HomeController(context);

        return ourInstance;
    }

    LocationManager mLocationManager;
    GoogleApiClient mGoogleApiClient;

    protected synchronized void buildGoogleApiClient(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private HomeController(Context context) {
        this.context = context;

        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        buildGoogleApiClient(context);

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("onConnected", "Google onConnected");

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledAlertToUser();
        }

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_REFRESH_TIME);
        mLocationRequest.setFastestInterval(LOCATION_REFRESH_TIME / 2);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);


        currentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (currentLocation != null)
            Log.i("onConnected", "Google currentLocation " + currentLocation.toString());

    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(context.getString(R.string.gps_off))
                .setCancelable(false)
                .setPositiveButton(context.getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton(context.getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("onConnectionSuspended", "Google onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("onConnectionFailed", "Google onConnectionFailed");
    }

    public List<Place> findPlaces(String placeName) {
        List<Place> results = ServiceManager.getInstance().getSuggestedPlaces(Locale.getDefault().getLanguage(), placeName);

        Collections.sort(results, this);

        return results;
    }

    public void stopLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        } catch (Exception e) {
            //not connected yet
        }
    }

    @Override
    public int compare(Place lhs, Place rhs) {
        if (currentLocation != null) {

            Location lhsLocation = new Location(lhs.getFullName());
            Location rhsLocation = new Location(rhs.getFullName());

            lhsLocation.setLatitude(Double.parseDouble(lhs.getGeo_position().getLatitude()));
            lhsLocation.setLongitude(Double.parseDouble(lhs.getGeo_position().getLongitude()));

            float lhsDistance = currentLocation.distanceTo(lhsLocation);

            rhsLocation.setLatitude(Double.parseDouble(rhs.getGeo_position().getLatitude()));
            rhsLocation.setLongitude(Double.parseDouble(rhs.getGeo_position().getLongitude()));

            float rhsDistance = currentLocation.distanceTo(rhsLocation);

            return Float.valueOf(lhsDistance).compareTo(rhsDistance);
        }

        return 0;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("onLocationChanged", "Google location " + location.toString());
        currentLocation = location;
        stopLocationUpdates();
    }

}

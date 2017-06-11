package com.example.bilal.findfastestambulance;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import com.google.android.gms.location.LocationListener;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.ExecutionException;

public class LocationRecieverService extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener {
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location mLastLocation;
    private String OriginalPhoneNumber,type;

    public LocationRecieverService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        OriginalPhoneNumber = intent.getStringExtra("phoneNumber");
        type = intent.getStringExtra("type");
        Log.d("billal -> phonenumber"," in service"+OriginalPhoneNumber);
        Log.d("billal","test1 -> OnStartCommand");

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        googleApiClient.connect();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("billal","test1 -> Onconnected");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient,
                locationRequest,
                this
        );
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("billlal ->","Connection suspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mLastLocation == null){ //If it is first time, DO NOT PROCEED
            mLastLocation = location;
        }else{
            mLastLocation = location;
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            Log.d("billal","latitude -> "+latitude);
            Log.d("billal","longitude -> "+longitude);
            Log.d("billal","distance -> "+location.distanceTo(mLastLocation));

            if (type.equals("driver")) {
                    new BackgroundDriverLocationUpdate()
                            .execute(OriginalPhoneNumber, String.valueOf(latitude), String.valueOf(longitude));
            }
            if (type.equals("user")) {
                    new BackgroundUserLocationUpdate()
                            .execute(OriginalPhoneNumber, String.valueOf(latitude), String.valueOf(longitude));
            }
        }
    }

    private void requestLocationUpdates() {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("billlal ->","Connection failed");
    }
}

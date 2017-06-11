package com.example.bilal.findfastestambulance;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class DriverMapActivity extends AppCompatActivity implements OnMapReadyCallback{

    private int RESULT_LOCATION_UPDATE = 12312;
    private GoogleMap mMap;
    private EditText txtSearch;
    private Button btnSearch;
    private AlertDialog.Builder networkdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
//                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        btnSearch = (Button)findViewById(R.id.searchbtn);
        txtSearch = (EditText)findViewById(R.id.searchtxt);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.DriverMapView);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        turnOnLocation();
//        btnSearch.setOnClickListener();
                ConnectivityManager manager = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
                Boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
                Boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
                if (isWifi) {
                    onSearch();
                } else if (is3g) {
                    onSearch();
                } else {
                    networkdialog = new AlertDialog.Builder(this)
                            .setTitle("No Network Connection")
                            .setMessage("Connect to WIfi network or turn on Mobile Data and Try again...")
                            .setIcon(R.drawable.ic_error_black_24dp)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setNegativeButton("Settings", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                                }
                            });
                    networkdialog.show();
                }
        super.onStart();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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
        mMap.setMyLocationEnabled(true);
    }

    public void onSearch()
    {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtaddress = txtSearch.getText().toString();
                List<Address> addressList = null;
                int error =0;
                if (txtaddress != null || txtaddress.equals("")) {
                    Geocoder geocoder = new Geocoder(DriverMapActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(txtaddress, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                        error++;
                    }
                }
                if (error==0)
                {
                    Address address = addressList.get(0);
                    Log.d("billal ->","latitude : "+address.getLatitude());
                    Log.d("billal ->","longitude : "+address.getLongitude());
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(txtaddress));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                }
                else {
                    AlertDialog.Builder notifi = new AlertDialog.Builder(DriverMapActivity.this)
                            .setTitle("Error")
                            .setMessage("No location Found")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    notifi.show();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULT_LOCATION_UPDATE) {
            Log.e("Jozeb", "Result code: " + resultCode);
//              if (resultCode ==RESULT_OK){
//              }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void turnOnLocation() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(800);// Vibrate for 800 milliseco nds

            Toast.makeText(this, "Please turn on Location", Toast.LENGTH_SHORT).show();
            Text_To_Speech txt = new Text_To_Speech(this,"Please turn on Location");
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), RESULT_LOCATION_UPDATE);
        }
    }
}

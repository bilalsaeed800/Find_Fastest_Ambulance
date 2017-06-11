package com.example.bilal.findfastestambulance;

import android.*;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UserMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String return_data;
    private String[] FullName, CompanyName, CompanyPhoneNumber, VehicleName, VehicleNumber, PersonalPhoneNumber;
    private double[] Latitude, Longitude;
    private AlertDialog.Builder dialog, networkdialog;
    private AlertDialog dialog1;
    private Location location;
    private LatLng[] pointer ;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.UserMapView);
        mapFragment.getMapAsync(this);

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
//                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

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
    }

    @Override
    protected void onStart() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ConnectivityManager manager = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
                Boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
                Boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
                if (isWifi) {
                    if (Retreive_Data_from_db())
                    {
                        show_content_map();
                        create_dialog();
                        handler.postDelayed(this,120*1000);
                        Log.d("billal -> "," not wifi");
                    }
                } else if (is3g) {
                    if (Retreive_Data_from_db()) {
                        show_content_map();
                        create_dialog();
                        handler.postDelayed(this, 120*1000);
                        Log.d("billal -> ","3g");
                    }
                } else {
                    networkdialog.show();
                    Log.d("billal -> ","nothing");
                    handler.postDelayed(this,15*1000);
                }
            }
        }, 1000);
        super.onStart();
    }

    private boolean Retreive_Data_from_db() {
        try {
            return_data = new BackgroundGetAllDriversLocation()
                    .execute()
                    .get();

            JSONArray jsonArray = new JSONArray(return_data);
//            driverFullData = new ArrayList<DriverFullData>();
            FullName = new String[jsonArray.length()];
            CompanyName = new String[jsonArray.length()];
            CompanyPhoneNumber = new String[jsonArray.length()];
            VehicleName = new String[jsonArray.length()];
            VehicleNumber = new String[jsonArray.length()];
            PersonalPhoneNumber = new String[jsonArray.length()];
            Latitude = new double[jsonArray.length()];
            Longitude = new double[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jobj = jsonArray.getJSONObject(i);
                FullName[i] = jobj.optString("fullname");
                CompanyName[i] = jobj.optString("CompanyName");
                CompanyPhoneNumber[i] = jobj.optString("CompanyPhoneNumber");
                VehicleName[i] = jobj.optString("VehicleName");
                VehicleNumber[i] = jobj.optString("VehicleNumber");
                PersonalPhoneNumber[i] = jobj.optString("PersonalPhoneNumber");
                Latitude[i] = jobj.optDouble("Latitude");
                Longitude[i] = jobj.optDouble("Longitude");

            }
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void show_content_map() {
        ResourceUtil resourceUtil = new ResourceUtil();
        pointer = new LatLng[PersonalPhoneNumber.length];
        for (int i = 0; i < PersonalPhoneNumber.length; i++) {
            pointer[i] = new LatLng(Latitude[i], Longitude[i]);
            mMap.addMarker(new MarkerOptions()
                    .position(pointer[i])
                    .title(FullName[i].toString() + " is the driver of :" + CompanyName[i].toString() + " is currently present here")
                    .icon(BitmapDescriptorFactory.fromBitmap(resourceUtil.getBitmap(UserMapActivity.this, R.drawable.ic_local_hospital_black_24dp))));
        }
    }
    private void create_dialog()
    {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        location = locationManager.getLastKnownLocation(locationProvider);
        Log.d("billal -> ","runnnong 1");
        if (location != null)
        {
            double lattde = location.getLatitude(), longtde = location.getLongitude();
            final int postion = findNearest(lattde,longtde);
            Log.d("billal -> ","runnnong 2");
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pointer[postion], 20));

            dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(false);
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                    {
                        handler.removeCallbacksAndMessages(null);
                        UserMapActivity.this.finish();
                    }
                    return false;
                }
            });
            View myView = getLayoutInflater().inflate(R.layout.call_dialog_form,null);
            final TextView txtmsg= (TextView)myView.findViewById(R.id.txtmsg);
            final TextView txtdriver = (TextView)myView.findViewById(R.id.driverinfo);
            final TextView txtcompany = (TextView)myView.findViewById(R.id.companyinfo);
            final ImageButton driverbtn = (ImageButton)myView.findViewById(R.id.driverbtn);
            final ImageButton companybtn = (ImageButton)myView.findViewById(R.id.companybtn);
            final Button cancelbtn = (Button)myView.findViewById(R.id.cancelbtn);

            txtmsg.setText(FullName[postion]+" is the Driver of "+CompanyName[postion]);
            txtdriver.setText(PersonalPhoneNumber[postion]);
            txtcompany.setText(CompanyPhoneNumber[postion]);
            driverbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1.dismiss();
                    MakeCall(PersonalPhoneNumber[postion]);
                }
            });
            companybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1.dismiss();
                    MakeCall(CompanyPhoneNumber[postion]);
                }
            });
            cancelbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1.dismiss();
                }
            });

            dialog.setView(myView);
            dialog1 = dialog.create();
            dialog1.show();
        }
        else
        {
            Toast.makeText(this,"Your location is null",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
    private void MakeCall(String str) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + str));
        if (ActivityCompat.checkSelfPermission(UserMapActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }
    public int findNearest(double lat,double lon)
    {
        float previous = 100000;
        int postion = 0;
        Location loc1 = new Location("");
        loc1.setLatitude(lat);
        loc1.setLongitude(lon);

        for(int i=0;i<FullName.length;i++)
        {
            Location loc2 = new Location("");
            loc2.setLatitude(Latitude[i]);
            loc2.setLongitude(Longitude[i]);

            float distanceInMeters = loc1.distanceTo(loc2);
            if (distanceInMeters < previous)
            {
                previous = distanceInMeters;
                postion = i;
            }
        }
        return postion;
    }
    public static class ResourceUtil {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private Bitmap getBitmap(VectorDrawable vectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
            return bitmap;
        }

        private Bitmap getBitmap(VectorDrawableCompat vectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
            return bitmap;
        }

        public Bitmap getBitmap(Context context, @DrawableRes int drawableResId) {
            Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof VectorDrawableCompat) {
                return getBitmap((VectorDrawableCompat) drawable);
            } else if (drawable instanceof VectorDrawable) {
                return getBitmap((VectorDrawable) drawable);
            } else {
                throw new IllegalArgumentException("Unsupported drawable type");
            }
        }
    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacksAndMessages(null);
        super.onBackPressed();
    }
}

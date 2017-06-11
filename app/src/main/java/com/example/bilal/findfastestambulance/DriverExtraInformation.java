package com.example.bilal.findfastestambulance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.concurrent.ExecutionException;

public class DriverExtraInformation extends AppCompatActivity {

    private EditText txtFullName, txtVehicleName,txtVehicleNo;
    private ImageButton btnGoNow;
    private Spinner txtCompanyName;
    private String StrFullName,StrVehicleName,StrVehicleNo,StrCompanyName,StrCompanyPhone;
    private String returndata,OriginalPhoneNumber;
    private AlertDialog.Builder  networkdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_extra_information);
        txtFullName = (EditText)findViewById(R.id.FullNametxt);
        txtVehicleName = (EditText)findViewById(R.id.VehicleName);
        txtVehicleNo = (EditText)findViewById(R.id.VehicleNo);
        btnGoNow = (ImageButton)findViewById(R.id.GoNowBtn);
        txtCompanyName = (Spinner)findViewById(R.id.Companytxt);

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
//                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        OriginalPhoneNumber = getIntent().getStringExtra("phoneNumber");

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
        onClick();
    }

    private void onClick() {
        btnGoNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrCompanyName = (String)txtCompanyName.getSelectedItem();
                StrFullName = txtFullName.getText().toString();
                StrVehicleName = txtVehicleName.getText().toString();
                StrVehicleNo = txtVehicleNo.getText().toString();

                Log.d("billal -> ",StrFullName);
                Log.d("billal -> ",StrCompanyName);
                Log.d("billal -> ",StrVehicleName);
                Log.d("billal -> ",StrVehicleNo);

                int error=0;
                AlertDialog.Builder alert = new AlertDialog.Builder(DriverExtraInformation.this);
                alert.setTitle("Error");

                if (StrFullName.equals(""))
                {
                    alert.setMessage("You Must Enter Your Name");
                    alert.show();
                    error++;
                }
                else if (StrVehicleName.equals(""))
                {
                    alert.setMessage("You Must Enter Your Vehicle Name");
                    alert.show();
                    error++;
                }
                else if (StrCompanyName.equals(""))
                {
                    alert.setMessage("You Must Choose Your Company");
                    alert.show();
                    error++;
                }
                else if (StrVehicleNo.equals(""))
                {
                    alert.setMessage("You Must Enter Your Vehicle Number");
                    alert.show();
                    error++;
                }
                if (error == 0) {
                    if (StrCompanyName.equals("Chhipa")) {
                        StrCompanyPhone = "1020";
                    } else if (StrCompanyName.equals("Edhi")) {
                        StrCompanyPhone = "115";
                    } else if (StrCompanyName.equals("Aman")) {
                        StrCompanyPhone = "1021";
                    } else if (StrCompanyName.equals("KKF")) {
                        StrCompanyPhone = "124";
                    } else if (StrCompanyName.equals("Rescue")) {
                        StrCompanyPhone = "1122";
                    }
                    ConnectivityManager manager = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
                    Boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
                    Boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
                    if (!isWifi && !is3g) {
                        networkdialog.show();
                    } else {
                        try {
                            returndata = new BackgroundDriverRegistration()
                                    .execute(StrFullName, StrCompanyName, StrCompanyPhone, OriginalPhoneNumber, StrVehicleName, StrVehicleNo)
                                    .get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        String[] str = returndata.split(",");
                        if (str[0].equals("done")) {
                            Intent locationServiceIntent = new Intent(DriverExtraInformation.this, LocationRecieverService.class);
                            locationServiceIntent.putExtra("phoneNumber", OriginalPhoneNumber);
                            locationServiceIntent.putExtra("type", "driver");
                            startService(locationServiceIntent);

                            Intent intent = new Intent(DriverExtraInformation.this, DriverMapActivity.class);
                            intent.putExtra("phoneNumber", OriginalPhoneNumber);
                            startActivity(intent);

                            DriverExtraInformation.this.finish();

                        } else if (str[0].equals("error")) {
                            Toast.makeText(DriverExtraInformation.this, "You have not inserted Correct Data", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

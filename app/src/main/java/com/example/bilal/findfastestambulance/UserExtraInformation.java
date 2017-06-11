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
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.concurrent.ExecutionException;

public class UserExtraInformation extends AppCompatActivity {

    private EditText fullname;
    private ImageButton go_nowBtn;
    private String strFullName,returndata,OriginalPhoneNumber;
    private AlertDialog.Builder networkdialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_extra_information);
        OriginalPhoneNumber = getIntent().getStringExtra("phoneNumber");

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
//                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        fullname = (EditText)findViewById(R.id.FullNametxt);
        go_nowBtn = (ImageButton)findViewById(R.id.GoNowBtn);

        onClick_GoNow();
    }
    private void onClick_GoNow()
    {
        go_nowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strFullName = fullname.getText().toString();
                Log.d("billal -> ",strFullName);

                int error=0;
                AlertDialog.Builder alert = new AlertDialog.Builder(UserExtraInformation.this);
                alert.setTitle("Error");

                if (strFullName.equals(""))
                {
                    alert.setMessage("You Must Enter Your Name");
                    alert.show();
                    error++;
                }
                if (error == 0)
                {
                    ConnectivityManager manager = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
                    Boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
                    Boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
                    if (!isWifi && !is3g) {
                        networkdialog = new AlertDialog.Builder(UserExtraInformation.this)
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
                    } else {
                        try {
                            returndata = new BackgroundUserRegistration()
                                    .execute(strFullName, OriginalPhoneNumber)
                                    .get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        if (returndata.equals("done")) {
                            Intent locationServiceIntent = new Intent(UserExtraInformation.this, LocationRecieverService.class);
                            locationServiceIntent.putExtra("phoneNumber", OriginalPhoneNumber);
                            locationServiceIntent.putExtra("type", "user");
                            startService(locationServiceIntent);

                            Intent intent = new Intent(UserExtraInformation.this, UserMapActivity.class);
                            intent.putExtra("phoneNumber", OriginalPhoneNumber);
                            startActivity(intent);

                            UserExtraInformation.this.finish();

                        } else if (returndata.equals("error")) {
                            Toast.makeText(UserExtraInformation.this, "You have not inserted Correct Data", Toast.LENGTH_LONG).show();
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

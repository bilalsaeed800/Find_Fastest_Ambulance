package com.example.bilal.findfastestambulance;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {

    private ImageButton BtnSignin,BtnSignup;
    private TextView TermsAndPolicy;
    int RESULT_LOCATION_UPDATE = 12311;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BtnSignin = (ImageButton)findViewById(R.id.Signinbtn);
        BtnSignup = (ImageButton)findViewById(R.id.Signupbtn);
        TermsAndPolicy = (TextView)findViewById(R.id.TermAndPolicyTextView);

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
//                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);
        onClick();
    }
    private void onClick()
    {
        BtnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SigninActivity.class));
            }
        });
        BtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SignupActivity.class));
            }
        });

        TermsAndPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,UserMapActivity.class));
            }
        });

    }
    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onResume() {
        turnOnLocation();
        super.onResume();
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

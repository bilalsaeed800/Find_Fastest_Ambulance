package com.example.bilal.findfastestambulance;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class SignupActivity extends AppCompatActivity {

    private EditText PhoneNumberEditText;
    private Intent Validateintent;
    private String OriginalPhoneNumber;
    private ImageButton BtnGo;
    private AlertDialog.Builder alertDialogMakeConfirmation,alertDialogGetError;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        PhoneNumberEditText = (EditText)findViewById(R.id.PhoneNumberEdittext);
        BtnGo = (ImageButton)findViewById(R.id.btngo);
        radioGroup = (RadioGroup)findViewById(R.id.groupRadio);
        Validateintent  = new Intent(SignupActivity.this,ValidatePhoneNumber.class);
//        relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayout);

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
//                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        alertDialogMakeConfirmation = new AlertDialog.Builder(this);
        alertDialogMakeConfirmation.setTitle("Confirmation");
        alertDialogMakeConfirmation.setIcon(R.drawable.ic_warning_black_24dp);
        alertDialogMakeConfirmation.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(Validateintent);
            }
        });
        alertDialogMakeConfirmation.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialogGetError = new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Phone Number is not valid please enter a valid Phone Number")
                .setIcon(R.drawable.ic_error_black_24dp)
//                .setIcon(RESULT_OK)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        onClick();
    }
    private void onClick()
    {
        BtnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OriginalPhoneNumber = "+92"+PhoneNumberEditText.getText().toString();
                if (PhoneNumberEditText.getText().length()<10)
                {
                    alertDialogGetError.show();
                }
                else {
                    int whichid = radioGroup.getCheckedRadioButtonId();
                    radioButton = (RadioButton)findViewById(whichid);
                    Log.d("billal ->","radio button text "+radioButton.getText());
                    Validateintent.putExtra("phoneNumber", OriginalPhoneNumber);
                    Validateintent.putExtra("type",radioButton.getText());
                    alertDialogMakeConfirmation.setMessage("Are you sure " + OriginalPhoneNumber + " is your Number");
                    alertDialogMakeConfirmation.show();
                }
            }
        });
//        relativeLayout.set;
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

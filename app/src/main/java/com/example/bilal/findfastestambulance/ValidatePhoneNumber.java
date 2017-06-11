package com.example.bilal.findfastestambulance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.w3c.dom.Text;

import java.util.Random;

public class ValidatePhoneNumber extends AppCompatActivity {

    private  AlertDialog.Builder alertDialog;
    private AlertDialog dialog;
    private String OriginalPhoneNumber,type;
    private EditText input;
    private String codeStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_phone_number);

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
//                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    ValidatePhoneNumber.this.finish();
                }
                return false;
            }
        });
        View myView = getLayoutInflater().inflate(R.layout.dialog_form,null);
        final EditText txtInput = (EditText)myView.findViewById(R.id.txtinput);
        Button btnOk = (Button)myView.findViewById(R.id.btnOk);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtInput.getText().length()==4)
                {
                    if (txtInput.getText().toString().contains( codeStr ))
                    {
                        Log.d("billal -> code matched","done");

                        dialog.dismiss();
                        if (type.contains("Driver"))
                        {
                            Intent i = new Intent(ValidatePhoneNumber.this,DriverExtraInformation.class);
                            i.putExtra("phoneNumber",OriginalPhoneNumber);
                            startActivity(i);
                        }
                        else if (type.contains("User"))
                        {
                            Intent i = new Intent(ValidatePhoneNumber.this,UserExtraInformation.class);
                            i.putExtra("phoneNumber",OriginalPhoneNumber);
                            startActivity(i);
                        }

                    }
                    else
                    {
                        Toast.makeText(ValidatePhoneNumber.this,"Code does not matched",Toast.LENGTH_LONG).show();
                        Text_To_Speech txt_speech = new Text_To_Speech(ValidatePhoneNumber.this,"Code does not matched");
                    }
                }
                else
                {
                    Toast.makeText(ValidatePhoneNumber.this,"Invalid Code Please Enter Again",Toast.LENGTH_LONG).show();
                    Text_To_Speech txt_speech = new Text_To_Speech(ValidatePhoneNumber.this,"Invalid Code Please Enter Again");
                }
            }
        });
        alertDialog.setView(myView);

        OriginalPhoneNumber = getIntent().getStringExtra("phoneNumber");
        type = getIntent().getStringExtra("type");
        Log.d("billal -> phonenumber ",OriginalPhoneNumber+type);
        onClick();
    }
    private void onClick()
    {
        Random rand = new Random();
        int code = 1100+rand.nextInt(8899);
        codeStr = String.valueOf(code);

        Log.d("billal -> code is",codeStr);
        if(MakeSMS(OriginalPhoneNumber,codeStr))
        {
            dialog = alertDialog.create();
            dialog.show();
        }
        else
        {
            Log.d("billal ->","code don't sent");
            AlertDialog.Builder errorInMessaging = new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Message don't Send")
                        .setIcon(R.drawable.ic_error_black_24dp)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
//        Log.d("billal -> back"," pressed");
        this.finish();
    }

    private boolean MakeSMS(String phonenumber,String messege) {
        try {
            Toast.makeText(this, "sending...", Toast.LENGTH_LONG).show();
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phonenumber, null, "Validation Code: "+messege, null, null);
            Toast.makeText(this, "sent ", Toast.LENGTH_LONG).show();
            return true;
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
        return false;
    }
    /*private boolean MakeSMS()
    {
        TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String number = tMgr.getLine1Number();
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendSMS



        return true;
    }*/
}

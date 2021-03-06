package com.example.bilal.findfastestambulance;

import android.os.AsyncTask;
import android.speech.tts.Voice;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

/**
 * Created by Bilal on 4/28/2017.
 */

public class BackgroundUserRegistration extends AsyncTask<String,Void,String> {
    String result;
    @Override
    protected String doInBackground(String... params) {
        try
        {
            HttpURLConnection httpURLConnection = new HttpConnection(new URL_genrator().getRegistrationUserUrl()).returnconnection();
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter buff = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

            String post_data = URLEncoder.encode("fullname","UTF-8")+"="+URLEncoder.encode(params[0],"UTF-8")+"&"+
                    URLEncoder.encode("personalphonenumber","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8");

            buff.write(post_data);
            buff.flush();
            buff.close();

            result = new InputReturn(httpURLConnection).returnString();
            Log.d("billal -> ",result);
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}

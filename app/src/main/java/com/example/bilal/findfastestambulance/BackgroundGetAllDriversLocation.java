package com.example.bilal.findfastestambulance;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

/**
 * Created by Bilal on 5/2/2017.
 */

public class BackgroundGetAllDriversLocation extends AsyncTask<String,Void,String> {
    @Override
    protected String doInBackground(String... params) {
            HttpURLConnection httpURLConnection = new HttpConnection(new URL_genrator().getShowFullUserMapUrl()).returnconnection();
//            OutputStream outputStream = httpURLConnection.getOutputStream();
//            BufferedWriter buff = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            String result = new InputReturn(httpURLConnection).returnString();
            Log.d("billal -> ",result);
            return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
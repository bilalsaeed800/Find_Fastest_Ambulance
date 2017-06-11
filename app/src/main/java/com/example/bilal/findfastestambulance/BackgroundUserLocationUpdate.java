package com.example.bilal.findfastestambulance;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

/**
 * Created by Bilal on 4/28/2017.
 */

public class BackgroundUserLocationUpdate extends AsyncTask<String,Void,Void> {
    String result = null;

    @Override
    protected Void doInBackground(String... params) {
        try {
            HttpURLConnection httpURLConnection = new HttpConnection(new URL_genrator().getUserlocationInsertionUrl()).returnconnection();
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter buff = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            String post_data = URLEncoder.encode("personalphonenumber", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                    URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                    URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8");

            buff.write(post_data);
            buff.flush();
            buff.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

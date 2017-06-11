package com.example.bilal.findfastestambulance;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by bilal.saeed800 on 29-Dec-16.
 */
public class HttpConnection {

    private static String URL;

    public HttpConnection(String url)
    {
        URL = url;
    }
    public HttpURLConnection returnconnection()
    {
        try {
            java.net.URL url = new URL(URL);
            HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
            httpcon.setRequestMethod("POST");
            httpcon.setDoOutput(true);
            httpcon.setDoInput(true);
            return httpcon;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

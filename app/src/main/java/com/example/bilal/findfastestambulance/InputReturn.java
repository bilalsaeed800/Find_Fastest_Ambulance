package com.example.bilal.findfastestambulance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

/**
 * Created by bilal.saeed800 on 29-Dec-16.
 */
public class InputReturn {
    private static HttpURLConnection httpURLConnection;

    public InputReturn(HttpURLConnection http)
    {
        httpURLConnection = http;
    }
    public String returnString()
    {
        try {
            InputStream inputstream = httpURLConnection.getInputStream();
            BufferedReader buffwrite = new BufferedReader(new InputStreamReader(inputstream, "iso-8859-1"));
            String result = "", line = "";
            while ((line = buffwrite.readLine()) != null) {
                result += line;
            }
            buffwrite.close();
            inputstream.close();
            httpURLConnection.disconnect();
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

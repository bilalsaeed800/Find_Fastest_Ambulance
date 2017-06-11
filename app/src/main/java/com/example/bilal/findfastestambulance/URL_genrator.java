package com.example.bilal.findfastestambulance;

/**
 * Created by Bilal on 3/7/2017.
 */

public class URL_genrator {
    private String main_url = "";
//    private String id;
    public URL_genrator(){
//        main_url = "http://172.15.59.130:81/Hackathon/";                        //iba
//        main_url = "http://192.168.0.106:81/Hackathon/";                        //tplink home
//        main_url = "http://192.168.43.11:81/Hackathon/";                        //
//        main_url = "http://192.168.10.8:81/Hackathon/";                        //bilal home
        main_url = "http://k142178.000webhostapp.com/hackathon/";                       //host website
    }

    public String getRegistrationDriverUrl()
    {
        return main_url+"registerDriver.php";
    }
    public String getRegistrationUserUrl()
    {
        return main_url+"registerUser.php";
    }
    public String getDriverlocationInsertionUrl()
    {
        return main_url + "InsertDriverLocation.php";
    }
    public String getUserlocationInsertionUrl()
    {
        return main_url + "InsertUserLocation.php";
    }
    public String getShowFullUserMapUrl(){ return main_url+ "ShowFullMapWithJson.php";}
}

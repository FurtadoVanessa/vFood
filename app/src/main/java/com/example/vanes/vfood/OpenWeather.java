package com.example.vanes.vfood;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenWeather {

    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static String IMG_URL = "http://openweathermap.org/img/w/";
    private static String APPID = "{5d23add59dafcbe74cbf8ce8ba02cd67}";

    public String getOpenWeatherData(String location) {
        HttpURLConnection con = null;
        InputStream is = null;

        try {
            con = (HttpURLConnection) (new URL(BASE_URL + location + "&APPID=" + APPID)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null)
                buffer.append(line + "rn");

            is.close();
            con.disconnect();
            return buffer.toString();

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }

        return null;
    }
}

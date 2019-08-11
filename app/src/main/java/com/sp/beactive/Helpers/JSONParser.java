package com.sp.beactive.Helpers;

import android.os.AsyncTask;


import com.sp.beactive.testjsonactivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class JSONParser extends AsyncTask<Void, Void, Void> {
    private String data="";
    private String dataParsed="";
    private String singleParsed="";

    @Override
    protected Void doInBackground(Void... voids) {
        //This is Background Thread
        try {
            URL url = new URL("https://api.myjson.com/bins/15qvyl");
            //URL url = new URL("http://localhost/MAD-BeActive/index.php");
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            InputStream inputStream = httpsURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";

            while(line!=null){
                line=bufferedReader.readLine();
                data = data + line;
        }


        JSONArray JA = new JSONArray(data);
            for (int i=0; i<JA.length();i++){
                JSONObject jsonObject = JA.getJSONObject(i);
                //You could use List View
                //singleParsed =  "Geometry: "+jsonObject.get("geometry") +"\n";
                String name = jsonObject.getString("stadium");
                String lat = jsonObject.getString("lat");
                String lng = jsonObject.getString("lng");
                singleParsed = "Stadium: "+name+"\n"+
                                "Lat: "+lat+"\n"+
                                "Lng: "+lng+"\n";
                dataParsed = dataParsed+singleParsed+"\n";
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //This is UI Thread

        testjsonactivity.textView.setText(dataParsed);
    }
}

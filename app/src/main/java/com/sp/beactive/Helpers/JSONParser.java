package com.sp.beactive.Helpers;

import android.os.AsyncTask;


import com.sp.beactive.Homepage.Select_Stadium;

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
    public String name ="";
    public String lat ="";
    public String lng ="";

    @Override
    protected Void doInBackground(Void... voids) {
        //This is Background Thread

        try {
            URL url = new URL("https://api.myjson.com/bins/15qvyl");
            //URL url = new URL("https://192.168.1.252:8080/MAD_BeActive/index.php");
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

                name = jsonObject.getString("stadium");
                lat = jsonObject.getString("lat");
                lng = jsonObject.getString("lng");
                if (name.equals("Jurong West")) {
                    break;
                }
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

        Select_Stadium.txtname.setText(name);
        Select_Stadium.txtlat.setText(lat);
        Select_Stadium.txtlng.setText(lng);
    }
}

package com.example.nomorekickout_student;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class ServerManager extends AsyncTask<Pair<String,String>, String, Pair<String, String>>{

    String url;

    ServerManager(String url){
        this.url = url;
    }

    @Override
    abstract protected void onPostExecute(Pair<String, String> s);

    @Override
    protected final Pair<String, String> doInBackground(Pair<String, String>... pairs) {
        try {
            URL object = new URL(url);

            HttpURLConnection con = (HttpURLConnection) object.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            con.setRequestProperty("Accept","application/json");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setChunkedStreamingMode(0);

            JSONObject cred = new JSONObject();
            for (Pair<String, String> pair : pairs){
                cred.put(pair.first, pair.second);
            }
            Log.v("QueryJSON", cred.toString());

            con.connect();

            DataOutputStream localDataOutputStream = new DataOutputStream(con.getOutputStream());
            localDataOutputStream.writeBytes("json="+cred.toString());
            localDataOutputStream.flush();

            int status = con.getResponseCode();
            Log.v("Response", status+"");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String decodedString;
            StringBuilder stringBuilder = new StringBuilder();
            while ((decodedString = in.readLine()) != null) {
                stringBuilder.append(decodedString);
            }
            localDataOutputStream.close();
            in.close();
            con.disconnect();
            return Pair.create(pairs[0].first, stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

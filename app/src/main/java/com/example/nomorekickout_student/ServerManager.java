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
import java.net.URLEncoder;


public class ServerManager{
    String url;
    OnResult onResult;

    interface OnResult{
        public void handleResult(Pair<String, String> s);
    }

    public ServerManager(String url, OnResult onResult){
        this.url = url;
        this.onResult = onResult;
    }

    public void execute(Pair<String, String>... pairs){
        SendQuery query = new SendQuery();
        query.execute(pairs);
    }

    class SendQuery extends AsyncTask<Pair<String,String>, String, Pair<String, String>>{

        @Override
        protected void onPostExecute(Pair<String, String> s){
            onResult.handleResult(s);
        }

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
                    if(pair.first.equals("ID") || pair.first.equals("room") || pair.first.equals("alarm")
                     || pair.first.equals("noAlert") || pair.first.equals("latecnt") || pair.first.equals("RID")
                     || pair.first.equals("requestType")){
                        cred.put(pair.first, Integer.parseInt(pair.second));
                    }
                    else cred.put(pair.first, URLEncoder.encode(pair.second, "UTF-8"));
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

}

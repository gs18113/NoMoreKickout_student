package com.example.nomorekickout_student;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

public class BackgroundService extends Service {

    ServerManager serverManager;
    int ID;

    public BackgroundService() {
        serverManager = new ServerManager("http://34.84.59.141", new ServerManager.OnResult() {
            @Override
            public void handleResult(Pair<String, String> s) {
                try {
                    if (s.first.equals("getAlarm")) {
                        if (s.second.equals("1")) {
                            Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                } catch (NullPointerException e){

                }
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class WatchThread extends Thread{
        @Override
        public void run() {
            while(true){
                serverManager.execute(
                        Pair.create("qtype", "getAlarm"),
                        Pair.create("ID", ""+ID)
                );
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.ID = intent.getIntExtra("ID", 0);
        new WatchThread().start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

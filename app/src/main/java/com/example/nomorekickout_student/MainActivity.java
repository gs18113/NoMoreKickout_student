package com.example.nomorekickout_student;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView profile, statusView, commentView;
    Button settingsButton, infoChangeButton;
    Student me;

    static ServerManager serverManager;

    int requestRID;

    Activity thisActivity;

    Pair[] requestParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thisActivity = this;
        profile = findViewById(R.id.profile);
        statusView = findViewById(R.id.statusView);
        commentView = findViewById(R.id.commentView);
        settingsButton = findViewById(R.id.settingsButton);
        infoChangeButton = findViewById(R.id.infoChangeButton);
        me = new Student();

        updateViews();

        serverManager = new ServerManager("http://34.84.59.141", new ServerManager.OnResult() {
            @Override
            public void handleResult(Pair<String, String> s) {
                try {
                    if(s.first.equals("addRequest")){
                        requestRID = Integer.parseInt(s.second);
                        updatePreferences();
                        Toast toast = Toast.makeText(getApplicationContext(), "요청이 전송되었습니다.", Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }
                } catch (Exception e){
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            serverManager.execute(requestParams);
                        }
                    }, 4000);
                    Toast toast = Toast.makeText(getApplicationContext(), "요청 통신 오류. 재시도중...", Toast.LENGTH_SHORT);
                    toast.show();
                }

                try{
                    if(s.first.equals("getStudentInfo")){
                        Gson gson = new Gson();
                        if(s.second.equals("")) throw new IllegalArgumentException();
                        Student info = gson.fromJson(s.second, Student.class);
                        if(info == null) throw new NullPointerException();
                        me = info;
                        updatePreferences();
                        updateViews();
                        return;
                    }
                } catch(NullPointerException e){
                    e.printStackTrace();
                    Toast toast = Toast.makeText(getApplicationContext(), "학생정보 통신 오류. 재시도중...", Toast.LENGTH_SHORT);
                    toast.show();
                    //me = new Student();
                    //updateViews();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            serverManager.execute(
                                    Pair.create("qtype", "getStudentInfo"),
                                    Pair.create("ID", ""+me.getID())
                            );
                        }
                    }, 4000);
                } catch (IllegalArgumentException e){
                    me = new Student();
                    requestRID = 0;
                    updatePreferences();
                    updateViews();
                    Toast toast = Toast.makeText(getApplicationContext(), "아직 승인되지 않은 학생입니다.", Toast.LENGTH_SHORT);
                    toast.show();
                }

                try {
                    if(s.first.equals("viewRequestExists")){
                        if (s.second.equals("false")) {
                            Intent it = new Intent(thisActivity, InfoActivity.class);
                            it.putExtra("ID", me.getID());
                            it.putExtra("name", me.getName());
                            it.putExtra("building", me.getBuilding());
                            it.putExtra("room", me.getRoom());
                            startActivityForResult(it, 0);
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "아직 기존 요청이 처리되지 않았습니다. 요청이 처리될 때까지 기다려 주세요.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        return;
                    }
                } catch (Exception e){
                    Toast toast = Toast.makeText(getApplicationContext(), "통신 오류. 다시 시도해 주세요.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


        serverManager.execute(
                Pair.create("qtype", "getStudentInfo"),
                Pair.create("ID", ""+me.getID())
        );

        settingsButton.setOnClickListener(this);
        infoChangeButton.setOnClickListener(this);

        if(!BackgroundService.isRunning) {
            Intent it = new Intent(getApplicationContext(), BackgroundService.class);
            it.putExtra("ID", me.getID());
            startService(it);
        }

    }

    private void updateViews(){
        SharedPreferences pref = getSharedPreferences("profile", MODE_PRIVATE);
        me.setID(pref.getInt("ID", 0));
        me.setName(pref.getString("name", ""));
        me.setBuilding(pref.getString("building", "우정2관"));
        me.setRoom(pref.getInt("room" , 0));
        me.setLatecnt(pref.getInt("latecnt" , 0));

        profile.setText("학번: "+me.getID()+"\n이름: "+me.getName()+"\n건물: "+me.getBuilding()+"\n호실: "+me.getRoom()+"호");
        statusView.setText("Current status: 점호불참 "+me.getLatecnt()+"회");
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.infoChangeButton) {
            serverManager.execute(
                Pair.create("qtype", "viewRequestExists"),
                Pair.create("RID", ""+requestRID)
            );

        }
        else if(view.getId() == R.id.settingsButton){
            Intent it = new Intent(this, SettingsActivity.class);
            startActivityForResult(it, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            requestParams = new Pair[]{
                    Pair.create("qtype", "addRequest"),
                    Pair.create("ID", "" + data.getIntExtra("ID", 0)),
                    Pair.create("building", data.getStringExtra("building")),
                    Pair.create("room", "" + data.getIntExtra("room", 0)),
                    Pair.create("name", data.getStringExtra("name")),
                    Pair.create("noAlert", "" + data.getIntExtra("alertSetting", 0)),
                    Pair.create("requestType", "" + (me.getName().equals("") ? 0 : 1))
            };
            serverManager.execute(requestParams);
            me.ID = data.getIntExtra("ID", 0);
            me.name = data.getStringExtra("name");
            me.building = data.getStringExtra("building");
            me.room = data.getIntExtra("room", 0);
            updatePreferences();
            updateViews();
        }
    }

    private void updatePreferences(){
        SharedPreferences pref = getSharedPreferences("profile", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("ID", me.getID());
        editor.putString("name", me.getName());
        editor.putString("building", me.getBuilding());
        editor.putInt("room", me.getRoom());
        editor.putInt("RID", requestRID);
        editor.apply();
        Toast toast = Toast.makeText(this, "변경 사항 저장", Toast.LENGTH_LONG);
        toast.show();
    }
}

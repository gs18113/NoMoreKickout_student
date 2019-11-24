package com.example.nomorekickout_student;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener{

    String alertType, musicName;
    int volumeVal;
    MediaPlayer mediaPlayer;

    LinearLayout okView;
    TextView currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        okView = findViewById(R.id.okView);
        currentTime = findViewById(R.id.currentTime);

        new Thread(){
            @Override
            public void run() {
                while(true) {
                    Date nowTime = Calendar.getInstance().getTime();
                    currentTime.setText(nowTime.getTime() + "");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        okView.setOnClickListener(this);

        SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
        alertType = pref.getString("alertType", "ring");
        volumeVal = pref.getInt("volume", 30);
        musicName = pref.getString("musicName", "기본");
        if(alertType.equals("ring")||alertType.equals("both")) {
            if (musicName.equals("기본")) {
                mediaPlayer = MediaPlayer.create(this, R.raw.pirate);
                mediaPlayer.setLooping(false);
                mediaPlayer.start();
            } else {
                try {
                    Uri myUri = Uri.parse(musicName);
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    mediaPlayer.setDataSource(getApplicationContext(), myUri);
                    mediaPlayer.prepare();
                    mediaPlayer.setLooping(false);
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onClick(View view) {
        mediaPlayer.stop();
        finish();
    }
}

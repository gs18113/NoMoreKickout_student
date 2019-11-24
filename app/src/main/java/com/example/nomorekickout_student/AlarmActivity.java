package com.example.nomorekickout_student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener{

    String alertType, musicName;
    int volumeVal;
    MediaPlayer mediaPlayer;

    LinearLayout okView;
    TextView currentTime;

    static class MHandler extends Handler{
        TextView currTime;
        public MHandler(TextView currTime) {
            super();
            this.currTime = currTime;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle bundle = msg.getData();
            String time = bundle.getString("time");
            currTime.setText(time);
            super.handleMessage(msg);
        }
    }

    MHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        okView = findViewById(R.id.okView);
        currentTime = findViewById(R.id.currentTime);

        mHandler = new MHandler(currentTime);

        new Thread(){
            @Override
            public void run() {
                while(true) {
                    Date nowTime = Calendar.getInstance().getTime();
                    SimpleDateFormat formatter = new SimpleDateFormat ( "HH:mm:ss", Locale.KOREA );
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("time", formatter.format(nowTime)+"");
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
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
                Log.v("asdf", "asfasd");
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
        if(mediaPlayer != null) mediaPlayer.stop();
        finish();
    }

    @Override
    public void onBackPressed() {
        if(mediaPlayer != null) mediaPlayer.stop();
        finish();
    }
}

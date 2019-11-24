package com.example.nomorekickout_student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
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
    Vibrator vibrator;

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
                Log.v("asdf", "asfasd");
            } else {
                    Uri myUri = Uri.parse(musicName);
                    mediaPlayer = MediaPlayer.create(this, myUri);
            }
            //float log1=1.f - (float)(Math.log(100-volumeVal)/Math.log(100));
            //mediaPlayer.setVolume(log1, log1);
            mediaPlayer.setVolume(((float)volumeVal)/100, ((float)volumeVal)/100);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        }
        if(alertType.equals("vibrate")||alertType.equals("both")){
            vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {500, 500};
            vibrator.vibrate(pattern, 0);
        }

    }

    @Override
    public void onClick(View view) {
        if(mediaPlayer != null) mediaPlayer.stop();
        if(vibrator != null) vibrator.cancel();
        finish();
    }

    @Override
    public void onBackPressed() {
        if(mediaPlayer != null) mediaPlayer.stop();
        if(vibrator != null) vibrator.cancel();
        finish();
    }
}

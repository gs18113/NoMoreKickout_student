package com.example.nomorekickout_student;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    RadioGroup alertGroup;
    RadioButton alertVibrate, alertRing, alertBoth;
    TextView musicNameView;
    Button submitButton, cancelButton, selectMusic;
    SeekBar volume;
    int volumeVal;
    String alertType, musicName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        alertGroup = findViewById(R.id.alertGroup);
        alertVibrate = findViewById(R.id.alertVibrate);
        alertRing = findViewById(R.id.alertRing);
        alertBoth = findViewById(R.id.alertBoth);
        musicNameView = findViewById(R.id.musicName);
        submitButton = findViewById(R.id.submitButton);
        cancelButton = findViewById(R.id.cancelButton);
        selectMusic = findViewById(R.id.selectMusic);
        volume = findViewById(R.id.volume);

        submitButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        selectMusic.setOnClickListener(this);
        volume.setOnSeekBarChangeListener(this);

        SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
        alertType = pref.getString("alertType", "");
        volumeVal = pref.getInt("volume", 30);
        musicName = pref.getString("musicName", "");

        if(alertType.equals("vibrate")){
            alertVibrate.setChecked(true);
        } else if(alertType.equals("ring")){
            alertRing.setChecked(true);
        } else if(alertType.equals("both")){
            alertBoth.setChecked(true);
        }

        volume.setProgress(volumeVal);

        if(musicName.equals("")) {
            musicNameView.setText("기본");
        } else{
            musicNameView.setText(musicName);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.submitButton){
            if(alertGroup.getCheckedRadioButtonId()==-1){
                Toast toast = Toast.makeText(this, "알람 유형(진동/음악)을 선책하세요.", Toast.LENGTH_SHORT);
                toast.show();
            }
            else {
                SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();

                int radioId = alertGroup.getCheckedRadioButtonId();
                if(radioId == R.id.alertVibrate) editor.putString("alertType", "vibrate");
                else if(radioId == R.id.alertRing) editor.putString("alertType", "ring");
                else if(radioId == R.id.alertBoth) editor.putString("alertType", "both");

                editor.putInt("volume", volume.getProgress());
                editor.putString("musicName", musicNameView.getText().toString());
                editor.apply();
                setResult(RESULT_OK);
                finish();
            }
        }
        else if(view.getId() == R.id.cancelButton){
            setResult(RESULT_CANCELED);
            finish();
        }
        else if(view.getId() == R.id.selectMusic){
            final int MyVersion = Build.VERSION.SDK_INT;
            if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                if (!checkIfAlreadyhavePermission()) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                    return;
                }
            }
            Intent intent=new Intent(Intent.ACTION_PICK);
            intent.setType(android.provider.MediaStore.Audio.Media.CONTENT_TYPE);
            startActivityForResult(intent, 1);
        }
    }

    private boolean checkIfAlreadyhavePermission(){
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 1){
            musicName = data.getData().toString();
            musicNameView.setText(musicName);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        volumeVal = i;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}

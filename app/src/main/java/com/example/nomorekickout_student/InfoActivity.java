package com.example.nomorekickout_student;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener{

    EditText editID, editName, editBuilding, editRoom;
    Button submitButton, cancelButton;

    RadioGroup alertSetting;
    RadioButton yesAlert, noAlert;

    boolean isFirst = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        editID = findViewById(R.id.editID);
        editName = findViewById(R.id.editName);
        editBuilding = findViewById(R.id.editBuilding);
        editRoom = findViewById(R.id.editRoom);
        submitButton = findViewById(R.id.submitButton);
        cancelButton = findViewById(R.id.cancelButton);
        alertSetting = findViewById(R.id.alertSetting);
        yesAlert = findViewById(R.id.yesAlert);
        noAlert = findViewById(R.id.noAlert);

        submitButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        Intent it = getIntent();
        editID.setText(""+it.getIntExtra("ID", 0));
        editName.setText(it.getStringExtra("name"));
        editBuilding.setText(it.getStringExtra("building"));
        editRoom.setText(""+it.getIntExtra("room", 0));

        if(it.getStringExtra("alertSetting") != null && it.getStringExtra("alertSetting").equals("no")){
            noAlert.setChecked(true);
        }
        else{
            yesAlert.setChecked(true);
        }

        if(it.getIntExtra("ID", 0) == 0) isFirst = true;
        if(!isFirst){
            editID.setEnabled(false);
            editName.setEnabled(false);
        }
    }

    private void sendDataAndFinish(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("알림");
        alert.setMessage("확인을 누르면 담당 사감선생님들께 승인 요청이 전송됩니다.\n 요청을 전송하시겠습니까?");
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent it = getIntent();
                it.putExtra("ID", Integer.parseInt(editID.getText().toString()));
                it.putExtra("name", editName.getText().toString().trim());
                it.putExtra("building", editBuilding.getText().toString().trim());
                it.putExtra("room", Integer.parseInt(editRoom.getText().toString()));
                setResult(RESULT_OK, it);
                finish();
            }
        });
        alert.setNegativeButton("취소", null);
        alert.show();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.submitButton) {
            if (isFirst){
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("알림");
                alert.setMessage("이름과 학번은 처음 설정한 후에는 바꿀 수 없습니다. 이대로 진행하시겠습니까?");
                alert.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendDataAndFinish();
                    }
                });
                alert.setNegativeButton("아니오", null);
                alert.show();
            }
            else{
                sendDataAndFinish();
            }
        }
        if(view.getId() == R.id.cancelButton){
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}

package com.baijiayun.videoplayerui.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baijiayun.playback.util.PBUtils;
import com.baijiayun.videoplayer.ui.activity.PBRoomActivity;
import com.baijiayun.videoplayer.ui.playback.PBRoomUI;
import com.baijiayun.videoplayer.ui.utils.ConstantUtil;

/**
 * 回放启动页
 */
public class PlaybackLauncherActivity extends AppCompatActivity {

    private EditText roomIdEt;
    private EditText tokenEt;
    private EditText sessionIdEt;
    private Button enterRoomBtn;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback_launcher);

        roomIdEt = findViewById(R.id.room_id_et);
        tokenEt = findViewById(R.id.room_token_et);
        sessionIdEt = findViewById(R.id.session_id_et);
        enterRoomBtn = findViewById(R.id.start_playback_btn);

        recoverStatus();
        initListener();
    }

    private void initListener(){
        enterRoomBtn.setOnClickListener(v -> {
            String roomId = roomIdEt.getText().toString();
            String token = tokenEt.getText().toString();
            String sessionId = sessionIdEt.getText().toString();
            if(TextUtils.isEmpty(roomId)){
                Toast.makeText(PlaybackLauncherActivity.this, "roomId必须填写", Toast.LENGTH_LONG).show();
                return;
            }
            if(TextUtils.isEmpty(token)){
                token = "test12345678";
            }
            if(TextUtils.isEmpty(sessionIdEt.getText())){
                sessionId = "-1";
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("roomId", roomId);
            editor.putString("token", token);
            editor.putString("sessionId", sessionId).apply();
            PBRoomUI.enterPBRoom(PlaybackLauncherActivity.this, roomId, token, sessionId, null);
        });
    }

    private void recoverStatus(){
        sharedPreferences = getSharedPreferences("playback_launcher_sp", MODE_PRIVATE);
        String roomId = sharedPreferences.getString("roomId", "0");
        String token = sharedPreferences.getString("token", "test12345678");
        String sessionId = sharedPreferences.getString("sessionId", "-1");
        roomIdEt.setText(roomId);
        tokenEt.setText(token);
        sessionIdEt.setText(sessionId);
    }
}

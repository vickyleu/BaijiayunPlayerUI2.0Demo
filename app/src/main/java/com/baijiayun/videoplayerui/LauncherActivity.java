package com.baijiayun.videoplayerui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baijiayun.videoplayer.PlayerConstants;
import com.baijiayun.videoplayer.ui.component.ComponentManager;
import com.baijiayun.videoplayer.ui.component.ControllerComponent;
import com.baijiayun.videoplayer.ui.component.LoadingComponent;
import com.baijiayun.videoplayer.ui.event.UIEventKey;

public class LauncherActivity extends AppCompatActivity {

    private final String VIDEO_ID = "videoId";
    private final String TOKEN = "token";
    private final String ENVIRONMENT = "environment";

    private Button playBtn;
    private EditText videoIdEt;
    private EditText videoTokenEt;
    private RadioButton testRadion, betaRadion, productRadio;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        playBtn = findViewById(R.id.play_btn);
        videoIdEt = findViewById(R.id.videoId_et);
        videoTokenEt = findViewById(R.id.video_token_et);
        testRadion = findViewById(R.id.rg_env_test);
        betaRadion = findViewById(R.id.rg_env_beta);
        productRadio = findViewById(R.id.rg_env_product);
        recoverStatus();
        initListener();
        //initComponentManager();
    }

    private void recoverStatus(){
        sharedPreferences = getSharedPreferences("launcher_sp", MODE_PRIVATE);
        String videoId = sharedPreferences.getString(VIDEO_ID, "0");
        String token = sharedPreferences.getString(TOKEN, "test12345678");
        videoIdEt.setText(videoId);
        videoTokenEt.setText(token);
        //默认正式服
        int env = sharedPreferences.getInt(ENVIRONMENT, 2);
        PlayerConstants.DEPLOY_TYPE = env;
        if(env == 0){
            testRadion.setChecked(true);
        } else if(env == 1){
            betaRadion.setChecked(true);
        } else{
            productRadio.setChecked(true);
        }
    }

    private void initListener(){
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoId = videoIdEt.getText().toString();
                String videoToken = videoTokenEt.getText().toString();
                if(TextUtils.isEmpty(videoId) || TextUtils.isEmpty(videoToken)){
                    Toast.makeText(LauncherActivity.this, "视频Id和token不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(VIDEO_ID, videoId);
                editor.putString(TOKEN, videoToken);
                editor.apply();
                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                intent.putExtra(VIDEO_ID, Long.parseLong(videoId));
                intent.putExtra(TOKEN, videoToken);
                startActivity(intent);
            }
        });

        ((RadioGroup) findViewById(R.id.rg_env)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rg_env_product) {
                    PlayerConstants.DEPLOY_TYPE = 2;
                    sharedPreferences.edit().putInt(ENVIRONMENT, 2).apply();
                } else if (checkedId == R.id.rg_env_beta) {
                    PlayerConstants.DEPLOY_TYPE = 1;
                    sharedPreferences.edit().putInt(ENVIRONMENT, 1).apply();
                } else {
                    PlayerConstants.DEPLOY_TYPE = 0;
                    sharedPreferences.edit().putInt(ENVIRONMENT, 0).apply();
                }
            }
        });
    }

    private void initComponentManager(){
        ComponentManager.get().release();
        ComponentManager.get().addComponent(UIEventKey.KEY_LOADING_COMPONENT, new LoadingComponent(LauncherActivity.this));
        ComponentManager.get().addComponent(UIEventKey.KEY_CONTROLLER_COMPONENT, new ControllerComponent(LauncherActivity.this));
    }
}

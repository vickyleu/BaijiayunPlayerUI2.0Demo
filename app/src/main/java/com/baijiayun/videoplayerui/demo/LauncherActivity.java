package com.baijiayun.videoplayerui.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baijiayun.playback.context.LPConstants;

public class LauncherActivity extends AppCompatActivity {

    private final String ENVIRONMENT = "environment";

    private Button videoLauncherBtn;
    private Button playbackLauncherBtn;
    private RadioButton testRadion, betaRadion, productRadio;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        testRadion = findViewById(R.id.rg_env_test);
        betaRadion = findViewById(R.id.rg_env_beta);
        productRadio = findViewById(R.id.rg_env_product);
        videoLauncherBtn = findViewById(R.id.video_launcher_btn);
        playbackLauncherBtn = findViewById(R.id.playback_launcher_btn);
        recoverStatus();
        initListener();
    }

    private void recoverStatus(){
        sharedPreferences = getSharedPreferences("launcher_sp", MODE_PRIVATE);
        //默认正式服
        int env = sharedPreferences.getInt(ENVIRONMENT, 2);
        if(env == 0){
            PlayerConstants.DEPLOY_TYPE = LPConstants.LPDeployType.Test;
            testRadion.setChecked(true);
        } else if(env == 1){
            PlayerConstants.DEPLOY_TYPE = LPConstants.LPDeployType.Beta;
            betaRadion.setChecked(true);
        } else{
            PlayerConstants.DEPLOY_TYPE = LPConstants.LPDeployType.Product;
            productRadio.setChecked(true);
        }
        videoLauncherBtn.setOnClickListener(v -> {
            startActivity(new Intent(LauncherActivity.this, VideoLauncherActivity.class));
        });
        playbackLauncherBtn.setOnClickListener(v -> {
            startActivity(new Intent(LauncherActivity.this, PlaybackLauncherActivity.class));
        });
    }

    private void initListener(){
        ((RadioGroup) findViewById(R.id.rg_env)).setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rg_env_product) {
                PlayerConstants.DEPLOY_TYPE = LPConstants.LPDeployType.Product;
                sharedPreferences.edit().putInt(ENVIRONMENT, 2).apply();
            } else if (checkedId == R.id.rg_env_beta) {
                PlayerConstants.DEPLOY_TYPE = LPConstants.LPDeployType.Beta;
                sharedPreferences.edit().putInt(ENVIRONMENT, 1).apply();
            } else {
                PlayerConstants.DEPLOY_TYPE = LPConstants.LPDeployType.Test;
                sharedPreferences.edit().putInt(ENVIRONMENT, 0).apply();
            }
        });

    }
}

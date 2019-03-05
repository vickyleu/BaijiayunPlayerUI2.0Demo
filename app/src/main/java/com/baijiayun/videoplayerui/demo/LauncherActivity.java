package com.baijiayun.videoplayerui.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baijiayun.BJYPlayerSDK;
import com.baijiayun.playback.context.PBConstants;

public class LauncherActivity extends AppCompatActivity {

    private static final String ENVIRONMENT = "environment";
    private static final String CUSTOM_DOMAIN = "custom_domain";

    private Button videoLauncherBtn;
    private Button playbackLauncherBtn;
    private RadioButton testRadio, betaRadio, productRadio;
    SharedPreferences sharedPreferences;
    EditText customDomainEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        testRadio = findViewById(R.id.rg_env_test);
        betaRadio = findViewById(R.id.rg_env_beta);
        productRadio = findViewById(R.id.rg_env_product);
        videoLauncherBtn = findViewById(R.id.video_launcher_btn);
        playbackLauncherBtn = findViewById(R.id.playback_launcher_btn);
        customDomainEt = findViewById(R.id.custom_domain_et);
        recoverStatus();
        initListener();
    }

    private void recoverStatus() {
        sharedPreferences = getSharedPreferences("launcher_sp", MODE_PRIVATE);
        //默认正式服
        int env = sharedPreferences.getInt(ENVIRONMENT, 2);
        BJYPlayerSDK.CUSTOM_DOMAIN = sharedPreferences.getString(CUSTOM_DOMAIN, "");
        if (env == 0) {
            BJYPlayerSDK.DEPLOY_TYPE = PBConstants.LPDeployType.Test;
            testRadio.setChecked(true);
        } else if (env == 1) {
            BJYPlayerSDK.DEPLOY_TYPE = PBConstants.LPDeployType.Beta;
            betaRadio.setChecked(true);
        } else {
            BJYPlayerSDK.DEPLOY_TYPE = PBConstants.LPDeployType.Product;
            productRadio.setChecked(true);
        }
        videoLauncherBtn.setOnClickListener(v -> {
            BJYPlayerSDK.CUSTOM_DOMAIN = customDomainEt.getText().toString();
            sharedPreferences.edit().putString(CUSTOM_DOMAIN, customDomainEt.getText().toString()).apply();
            startActivity(new Intent(LauncherActivity.this, VideoLauncherActivity.class));
        });
        playbackLauncherBtn.setOnClickListener(v -> {
            BJYPlayerSDK.CUSTOM_DOMAIN = customDomainEt.getText().toString();
            sharedPreferences.edit().putString(CUSTOM_DOMAIN, customDomainEt.getText().toString()).apply();
            startActivity(new Intent(LauncherActivity.this, PlaybackLauncherActivity.class));
        });
    }

    private void initListener() {
        ((RadioGroup) findViewById(R.id.rg_env)).setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rg_env_product) {
                BJYPlayerSDK.DEPLOY_TYPE = PBConstants.LPDeployType.Product;
                sharedPreferences.edit().putInt(ENVIRONMENT, 2).apply();
            } else if (checkedId == R.id.rg_env_beta) {
                BJYPlayerSDK.DEPLOY_TYPE = PBConstants.LPDeployType.Beta;
                sharedPreferences.edit().putInt(ENVIRONMENT, 1).apply();
            } else {
                BJYPlayerSDK.DEPLOY_TYPE = PBConstants.LPDeployType.Test;
                sharedPreferences.edit().putInt(ENVIRONMENT, 0).apply();
            }
        });

    }
}

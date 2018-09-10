package com.baijiayun.videoplayerui.demo;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baijiahulian.common.permission.AppPermissions;
import com.baijiayun.constant.PlayerConstants;
import com.baijiayun.playback.context.LPConstants;
import com.baijiayun.videoplayer.log.BJLog;
import com.baijiayun.videoplayer.ui.component.ComponentManager;
import com.baijiayun.videoplayer.ui.component.ControllerComponent;
import com.baijiayun.videoplayer.ui.component.LoadingComponent;
import com.baijiayun.videoplayer.ui.event.UIEventKey;

import java.io.File;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class LauncherActivity extends AppCompatActivity {

    private final String VIDEO_ID = "videoId";
    private final String TOKEN = "token";
    private final String ENVIRONMENT = "environment";

    private Button playBtn;
    private Button offlinePlayBtn;
    private EditText videoIdEt;
    private EditText videoTokenEt;
    private EditText videoPathEt;
    private RadioButton testRadion, betaRadion, productRadio;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        playBtn = findViewById(R.id.play_btn);
        offlinePlayBtn = findViewById(R.id.offline_play_btn);
        videoIdEt = findViewById(R.id.videoId_et);
        videoTokenEt = findViewById(R.id.video_token_et);
        videoPathEt = findViewById(R.id.video_path_et);
        testRadion = findViewById(R.id.rg_env_test);
        betaRadion = findViewById(R.id.rg_env_beta);
        productRadio = findViewById(R.id.rg_env_product);
        recoverStatus();
        initListener();
        //initComponentManager();

        BJLog.LOG_OPEN = true;
    }

    private void recoverStatus(){
        sharedPreferences = getSharedPreferences("launcher_sp", MODE_PRIVATE);
        String videoId = sharedPreferences.getString(VIDEO_ID, "0");
        String token = sharedPreferences.getString(TOKEN, "test12345678");
        videoIdEt.setText(videoId);
        videoTokenEt.setText(token);
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
        videoPathEt.setText(sharedPreferences.getString("videoPath", ""));
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
                intent.putExtra("isOffline", false);
                startActivity(intent);
            }
        });

        ((RadioGroup) findViewById(R.id.rg_env)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
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
            }
        });

        offlinePlayBtn.setOnClickListener(v -> AppPermissions.newPermissions(LauncherActivity.this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                            String videoPath = videoPathEt.getText().toString();
                            File file = new File(videoPath);
                            if (file.exists()) {
                                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                                intent.putExtra("videoPath", videoPath);
                                intent.putExtra("isOffline", true);
                                startActivity(intent);
                                sharedPreferences.edit().putString("videoPath", videoPath).apply();
                            } else {
                                Toast.makeText(LauncherActivity.this, videoPath + "不存在的", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LauncherActivity.this, "找不到存储卡！", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LauncherActivity.this, "没有获取读写sd卡权限", Toast.LENGTH_SHORT).show();
                    }
                }));
    }


    private void initComponentManager(){
        ComponentManager.get().release();
        ComponentManager.get().addComponent(UIEventKey.KEY_LOADING_COMPONENT, new LoadingComponent(LauncherActivity.this));
        ComponentManager.get().addComponent(UIEventKey.KEY_CONTROLLER_COMPONENT, new ControllerComponent(LauncherActivity.this));
    }
}

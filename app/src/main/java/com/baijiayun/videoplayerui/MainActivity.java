package com.baijiayun.videoplayerui;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.baijiayun.videoplayer.PlayerConstants;
import com.baijiayun.videoplayer.VideoPlayerFactory;
import com.baijiayun.videoplayer.event.BundlePool;
import com.baijiayun.videoplayer.ui.widget.BJYVideoView;
import com.baijiayun.videoplayer.ui.event.UIEventKey;
import com.baijiayun.videoplayer.ui.listener.IComponentEventListener;
import com.baijiayun.videoplayer.util.Utils;

public class MainActivity extends AppCompatActivity {

    private BJYVideoView videoView;
    private boolean isLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.bjyvideoview);
        videoView.initPlayer(new VideoPlayerFactory.Builder()
                //后台暂停播放
                .setSupportBackgroundAudio(false)
                //开启记忆播放
                .setSupportBreakPointPlay(true, this)
                //绑定activity生命周期
                .setLifecycle(getLifecycle())
        );

        videoView.setComponentEventListener(new IComponentEventListener() {
            @Override
            public void onReceiverEvent(int eventCode, Bundle bundle) {
                switch (eventCode) {
                    case UIEventKey.CUSTOM_CODE_REQUEST_BACK:
                        if (isLandscape) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        } else {
                            finish();
                        }
                        break;
                    case UIEventKey.CUSTOM_CODE_REQUEST_TOGGLE_SCREEN:
                        setRequestedOrientation(isLandscape ?
                                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
                                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                    default:
                        break;
                }
            }
        });
        videoView.setupOnlineVideoWithId(getIntent().getLongExtra("videoId", 0L), getIntent().getStringExtra("token"));
        videoView.play();
    }

    @Override
    public void onBackPressed() {
        if (isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandscape = true;
            updateVideo(true);
        } else {
            isLandscape = false;
            updateVideo(false);
        }
    }

    private void updateVideo(boolean landscape) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) videoView.getLayoutParams();
        if (landscape) {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            layoutParams.width = Utils.getScreenWidthPixels(this);
            layoutParams.height = layoutParams.width * 9 / 16;
        }
        videoView.setLayoutParams(layoutParams);
        videoView.sendCustomEvent(UIEventKey.CUSTOM_CODE_REQUEST_TOGGLE_SCREEN, BundlePool.obtain(landscape));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.onDestroy();
    }
}

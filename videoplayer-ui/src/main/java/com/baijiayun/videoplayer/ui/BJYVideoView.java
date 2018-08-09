package com.baijiayun.videoplayer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.baijiayun.videoplayer.BJYPlayerView;
import com.baijiayun.videoplayer.BJYVideoPlayer;
import com.baijiayun.videoplayer.VideoPlayerFactory;
import com.baijiayun.videoplayer.event.BundlePool;
import com.baijiayun.videoplayer.event.EventKey;
import com.baijiayun.videoplayer.event.OnPlayerEventListener;
import com.baijiayun.videoplayer.listeners.OnBufferedUpdateListener;
import com.baijiayun.videoplayer.listeners.OnPlayerErrorListener;
import com.baijiayun.videoplayer.listeners.OnPlayerStatusChangeListener;
import com.baijiayun.videoplayer.listeners.OnPlayingTimeChangeListener;
import com.baijiayun.videoplayer.player.PlayerStatus;
import com.baijiayun.videoplayer.player.error.PlayerError;
import com.baijiayun.videoplayer.ui.listener.IComponentEventListener;

/**
 * Created by yongjiaming on 2018/8/6
 * <p>
 * 带ui的播放器组件
 */

public class BJYVideoView extends FrameLayout {

    private BJYVideoPlayer bjyVideoPlayer;
    private BJYPlayerView bjyPlayerView;
    private ComponentContainer componentContainer;
    private IComponentEventListener componentEventListener;

    public BJYVideoView(@NonNull Context context) {
        this(context, null);
    }

    public BJYVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BJYVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        bjyPlayerView = new BJYPlayerView(context);
        addView(bjyPlayerView);

        componentContainer = new ComponentContainer(context);
        addView(componentContainer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        componentContainer.setOnComponentEventListener(internalComponentEventListener);
    }

    public void initPlayer(VideoPlayerFactory.Builder builder) {
        bjyVideoPlayer = builder.build();

        bjyVideoPlayer.bindPlayerView(bjyPlayerView);

        bjyVideoPlayer.setOnPlayerEventListener(new OnPlayerEventListener() {
            @Override
            public void onPlayerEvent(int eventCode, Bundle bundle) {
                componentContainer.dispatchPlayEvent(eventCode, bundle);
            }
        });

        bjyVideoPlayer.setOnPlayerErrorListener(new OnPlayerErrorListener() {
            @Override
            public void onError(PlayerError error) {
                Bundle bundle = BundlePool.obtain();
                bundle.putString("message", error.getMessage());
                componentContainer.dispatchErrorEvent(error.getCode(), bundle);
            }
        });

        bjyVideoPlayer.setOnPlayingTimeChangeListener(new OnPlayingTimeChangeListener() {
            @Override
            public void onPlayingTimeChange(int currentTime, int duration) {
                Bundle bundle = BundlePool.obtain();
                bundle.putInt(UIEventKey.KEY_INT_CURRENT_TIME, currentTime);
                bundle.putInt(UIEventKey.KEY_INT_TOTAL_TIME, duration);
                componentContainer.dispatchPlayEvent(OnPlayerEventListener.PLAYER_EVENT_ON_TIMER_UPDATE, bundle);
            }
        });

        bjyVideoPlayer.setOnBufferUpdateListener(new OnBufferedUpdateListener() {
            @Override
            public void onBufferedPercentageChange(int bufferedPercentage) {
                Bundle bundle = BundlePool.obtain();
                bundle.putInt(UIEventKey.KEY_INT_BUFFER_PERCENT, bufferedPercentage);
                componentContainer.dispatchPlayEvent(OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_UPDATE, bundle);
            }
        });

        bjyVideoPlayer.setOnPlayerStatusChangeListener(new OnPlayerStatusChangeListener() {
            @Override
            public void onStatusChange(PlayerStatus status) {
                Bundle bundle = BundlePool.obtain();
                bundle.putSerializable(UIEventKey.KEY_PLAYER_STATUS_CHANGE, status);
                componentContainer.dispatchPlayEvent(OnPlayerEventListener.PLAYER_EVENT_ON_STATUS_CHANGE, bundle);
            }
        });
    }

    private IComponentEventListener internalComponentEventListener = new IComponentEventListener() {
        @Override
        public void onReceiverEvent(int eventCode, Bundle bundle) {
            switch (eventCode) {
                case UIEventKey.CUSTOM_CODE_REQUEST_PAUSE:
                    pause();
                    break;
                case UIEventKey.CUSTOM_CODE_REQUEST_PLAY:
                    play();
                    break;
                case UIEventKey.CUSTOM_CODE_REQUEST_SEEK:
                    int seekToPosition = bundle.getInt(EventKey.INT_DATA);
                    seek(seekToPosition);
                    break;
            }
            if (componentEventListener != null) {
                componentEventListener.onReceiverEvent(eventCode, bundle);
            }
        }
    };

    public void setComponentEventListener(IComponentEventListener componentEventListener) {
        this.componentEventListener = componentEventListener;
    }

    public void setupOnlineVideoWithId(long videoId, String token) {
        bjyVideoPlayer.setupOnlineVideoWithId(videoId, token);
    }

    public void play(){
        bjyVideoPlayer.play();
    }

    public void pause() {
        bjyVideoPlayer.pause();
    }

    public void seek(int time) {
        bjyVideoPlayer.seek(time);
    }

    public void sendCustomEvent(int eventCode, Bundle bundle) {
        componentContainer.dispatchCustomEvent(eventCode, bundle);
    }

    public void onDestroy(){
       bjyVideoPlayer.release();
       componentEventListener = null;
       componentContainer.destroy();
    }
}

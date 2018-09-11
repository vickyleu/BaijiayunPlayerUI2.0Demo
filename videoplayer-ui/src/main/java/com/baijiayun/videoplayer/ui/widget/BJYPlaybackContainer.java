package com.baijiayun.videoplayer.ui.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.baijiayun.constant.VideoDefinition;
import com.baijiayun.playback.PBRoom;
import com.baijiayun.playback.mocklive.OnPlayerListener;
import com.baijiayun.videoplayer.BJYVideoPlayer;
import com.baijiayun.videoplayer.bean.BJYVideoInfo;
import com.baijiayun.videoplayer.event.BundlePool;
import com.baijiayun.videoplayer.event.EventKey;
import com.baijiayun.videoplayer.event.OnPlayerEventListener;
import com.baijiayun.videoplayer.player.PlayerStatus;
import com.baijiayun.videoplayer.player.error.PlayerError;
import com.baijiayun.videoplayer.ui.event.UIEventKey;
import com.baijiayun.videoplayer.ui.listener.IComponentEventListener;
import com.baijiayun.videoplayer.ui.listener.PlayerStateGetter;

/**
 * Created by yongjiaming on 2018/9/10 20:00
 */
public class BJYPlaybackContainer extends FrameLayout implements PlayerStateGetter{

    private ComponentContainer componentContainer;
    private IComponentEventListener componentEventListener;
    private BJYVideoPlayer videoPlayer;
    private FrameLayout pptOrVideoContainer;

    public BJYPlaybackContainer(@NonNull Context context) {
        this(context, null);
    }

    public BJYPlaybackContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BJYPlaybackContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        pptOrVideoContainer = new FrameLayout(context);
        addView(pptOrVideoContainer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        componentContainer = new ComponentContainer(context);
        componentContainer.setStateGetter(this);
        addView(componentContainer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        componentContainer.setOnComponentEventListener(internalComponentEventListener);
    }

    /**
     * 绑定PBRoom和BJYVideoPlayer,实现视频状态监听和视频控制
     * @param pbRoom
     * @param videoPlayer
     */
    public void attachPBRoom(PBRoom pbRoom, BJYVideoPlayer videoPlayer){
        pbRoom.setOnPlayerListener(new OnPlayerListener() {
            @Override
            public void onBufferingStart() {
                componentContainer.dispatchPlayEvent(UIEventKey.PLAYER_CODE_BUFFERING_START, null);
            }

            @Override
            public void onBufferingEnd() {
                componentContainer.dispatchPlayEvent(UIEventKey.PLAYER_CODE_BUFFERING_END, null);
            }

            @Override
            public void onError(PlayerError error) {
                Bundle bundle = BundlePool.obtain();
                bundle.putString(EventKey.STRING_DATA, error.getMessage());
                componentContainer.dispatchErrorEvent(error.getCode(), bundle);
            }

            @Override
            public void onStatusChange(PlayerStatus status) {
                Bundle bundle = BundlePool.obtain(status);
                componentContainer.dispatchPlayEvent(OnPlayerEventListener.PLAYER_EVENT_ON_STATUS_CHANGE, bundle);
            }

            @Override
            public void onPlayingTimeChange(int currentTime, int duration) {
                //只通知到controller component
                Bundle bundle = BundlePool.obtainPrivate(UIEventKey.KEY_CONTROLLER_COMPONENT, currentTime);
                componentContainer.dispatchPlayEvent(OnPlayerEventListener.PLAYER_EVENT_ON_TIMER_UPDATE, bundle);
            }
        });
        this.videoPlayer = videoPlayer;
    }

    /**
     * 动态添加ppt
     * @param view
     * @param params
     */
    public void addPPTView(View view, FrameLayout.LayoutParams params){
        pptOrVideoContainer.removeAllViews();
        pptOrVideoContainer.addView(view, params);
    }

    private IComponentEventListener internalComponentEventListener = (eventCode, bundle) -> {
        switch (eventCode) {
            case UIEventKey.CUSTOM_CODE_REQUEST_PAUSE:
                videoPlayer.pause();
                break;
            case UIEventKey.CUSTOM_CODE_REQUEST_REPLAY:
                //bjyVideoPlayer.rePlay();
                break;
            case UIEventKey.CUSTOM_CODE_REQUEST_SEEK:
                int seekToPosition = bundle.getInt(EventKey.INT_DATA);
                videoPlayer.seek(seekToPosition);
                break;
            case UIEventKey.CUSTOM_CODE_REQUEST_SET_RATE:
                videoPlayer.setPlayRate(bundle.getFloat(EventKey.FLOAT_DATA));
                break;
            case UIEventKey.CUSTOM_CODE_REQUEST_SET_DEFINITION:
                videoPlayer.changeDefinition((VideoDefinition) bundle.getSerializable(EventKey.SERIALIZABLE_DATA));
                break;
            case UIEventKey.CUSTOM_CODE_REQUEST_PLAY:
//                enablePlayWithMobileNetwork = true;
//                //视频未初始化则请求视频地址
//                if (videoPlayer.getVideoInfo().getVideoId() == 0) {
//                    setupOnlineVideoWithId(videoId, token, encrypted);
//                } else {
//                    play();
//                }
                videoPlayer.play();
                break;
        }
        if (componentEventListener != null) {
            componentEventListener.onReceiverEvent(eventCode, bundle);
        }
    };

    public void setComponentEventListener(IComponentEventListener componentEventListener) {
        this.componentEventListener = componentEventListener;
    }

    /**
     * 供外部发射自定义事件到各个component
     *
     * @param eventCode
     * @param bundle
     */
    public void sendCustomEvent(int eventCode, Bundle bundle) {
        componentContainer.dispatchCustomEvent(eventCode, bundle);
    }

    @Override
    public PlayerStatus getPlayerStatus() {
        return videoPlayer.getPlayerStatus();
    }

    @Override
    public int getCurrentPosition() {
        return videoPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return videoPlayer.getDuration();
    }

    @Override
    public int getBufferPercentage() {
        return videoPlayer.getBufferPercentage();
    }

    @Override
    public float getPlayRate() {
        return videoPlayer.getPlayRate();
    }

    @Nullable
    @Override
    public BJYVideoInfo getVideoInfo() {
        return videoPlayer.getVideoInfo();
    }
}

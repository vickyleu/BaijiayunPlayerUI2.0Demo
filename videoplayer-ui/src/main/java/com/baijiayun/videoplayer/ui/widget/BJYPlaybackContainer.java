package com.baijiayun.videoplayer.ui.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baijiayun.playback.PBRoom;
import com.baijiayun.playback.mocklive.OnPlayerListener;
import com.baijiayun.videoplayer.BJYVideoPlayer;
import com.baijiayun.videoplayer.event.BundlePool;
import com.baijiayun.videoplayer.event.EventKey;
import com.baijiayun.videoplayer.event.OnPlayerEventListener;
import com.baijiayun.videoplayer.player.PlayerStatus;
import com.baijiayun.videoplayer.player.error.PlayerError;
import com.baijiayun.videoplayer.ui.component.ComponentManager;
import com.baijiayun.videoplayer.ui.event.UIEventKey;

/**
 * Created by yongjiaming on 2018/9/10 20:00
 */
public class BJYPlaybackContainer extends BaseVideoView {

    private FrameLayout pptOrVideoContainer;

    public BJYPlaybackContainer(@NonNull Context context) {
        this(context, null);
    }

    public BJYPlaybackContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BJYPlaybackContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super.init(context, attributeSet, defStyleAttr);
        pptOrVideoContainer = new FrameLayout(context);
        addView(pptOrVideoContainer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ComponentManager componentManager = new ComponentManager(context);
        //使用没有loading的component组合
        componentManager.generatePBComponentGroup(context);
        componentContainer = new ComponentContainer(context);
        componentContainer.init(this, componentManager);
        addView(componentContainer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        componentContainer.setOnComponentEventListener(internalComponentEventListener);

        if (useDefaultNetworkListener) {
            registerNetChangeReceiver();
        }
    }

    /**
     * 绑定PBRoom和BJYVideoPlayer,实现视频状态监听和视频控制
     *
     * @param pbRoom
     * @param videoPlayer
     */
    public void attachPBRoom(PBRoom pbRoom, BJYVideoPlayer videoPlayer) {
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
        bjyVideoPlayer = videoPlayer;
    }

    @Override
    protected void requestPlayAction() {
        super.requestPlayAction();
        //房间信息未初始化成功
        if (getVideoInfo() == null || getVideoInfo().getVideoId() == 0) {
            Toast.makeText(getContext(), "房间信息未初始化成功，请重新进入", Toast.LENGTH_LONG).show();
        } else {
            play();
        }
    }

    /**
     * 动态添加ppt
     *
     * @param view
     * @param params
     */
    public void addPPTView(View view, FrameLayout.LayoutParams params) {
        pptOrVideoContainer.removeAllViews();
        pptOrVideoContainer.addView(view, params);
    }

    public void setGestureEnable(boolean enable) {
        componentContainer.setGestureEnable(enable);
    }
}

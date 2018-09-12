package com.baijiayun.videoplayer.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.baijiayun.constant.VideoDefinition;
import com.baijiayun.download.DownloadModel;
import com.baijiayun.glide.Glide;
import com.baijiayun.videoplayer.BJYVideoPlayer;
import com.baijiayun.videoplayer.event.BundlePool;
import com.baijiayun.videoplayer.event.EventKey;
import com.baijiayun.videoplayer.event.OnPlayerEventListener;
import com.baijiayun.videoplayer.listeners.OnBufferingListener;
import com.baijiayun.videoplayer.log.BJLog;
import com.baijiayun.videoplayer.player.PlayerStatus;
import com.baijiayun.videoplayer.render.AspectRatio;
import com.baijiayun.videoplayer.render.IRender;
import com.baijiayun.videoplayer.ui.R;
import com.baijiayun.videoplayer.ui.component.ComponentManager;
import com.baijiayun.videoplayer.ui.event.UIEventKey;
import com.baijiayun.videoplayer.ui.utils.NetworkUtils;
import com.baijiayun.videoplayer.widget.BJYPlayerView;

/**
 * Created by yongjiaming on 2018/8/6
 * <p>
 * 带ui的播放器组件
 */

public class BJYVideoView extends BaseVideoView {

    private static final String TAG = "BJYVideoView";

    private BJYPlayerView bjyPlayerView;
    private long videoId;
    private String token;
    private boolean encrypted;
    private ImageView audioCoverIv;
    private int mAspectRatio = AspectRatio.AspectRatio_16_9.ordinal();
    private int mRenderType = IRender.RENDER_TYPE_SURFACE_VIEW;

    public BJYVideoView(@NonNull Context context) {
        this(context, null);
    }

    public BJYVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BJYVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BJYVideoView(@NonNull Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BJVideoView, 0, 0);
        if (a.hasValue(R.styleable.BJVideoView_aspect_ratio)) {
            mAspectRatio = a.getInt(R.styleable.BJVideoView_aspect_ratio, AspectRatio.AspectRatio_16_9.ordinal());
        }
        if (a.hasValue(R.styleable.BJVideoView_render_type)) {
            mRenderType = a.getInt(R.styleable.BJVideoView_render_type, IRender.RENDER_TYPE_SURFACE_VIEW);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mRenderType = IRender.RENDER_TYPE_SURFACE_VIEW;
            }
        }
        a.recycle();
    }


    @Override
    protected void init(Context context, AttributeSet attrs, int defStyleAttr) {
        bjyPlayerView = new BJYPlayerView(context);
        addView(bjyPlayerView);

        audioCoverIv = new ImageView(context);
        audioCoverIv.setScaleType(ImageView.ScaleType.FIT_XY);
        audioCoverIv.setVisibility(View.GONE);
        audioCoverIv.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(audioCoverIv);

        componentContainer = new ComponentContainer(context);
        componentContainer.init(this, new ComponentManager(context));
        componentContainer.setOnComponentEventListener(internalComponentEventListener);
        addView(componentContainer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 初始化播放器
     *
     * @param videoPlayer
     */
    public void initPlayer(BJYVideoPlayer videoPlayer) {
        bjyVideoPlayer = videoPlayer;
        bjyVideoPlayer.bindPlayerView(bjyPlayerView);

        //初始化videoplayer之后才能设置宽高比
        bjyPlayerView.setAspectRatio(AspectRatio.values()[mAspectRatio]);
        bjyPlayerView.setRenderType(mRenderType);

        bjyVideoPlayer.setOnPlayerErrorListener(error -> {
            Bundle bundle = BundlePool.obtain();
            bundle.putString(EventKey.STRING_DATA, error.getMessage());
            componentContainer.dispatchErrorEvent(error.getCode(), bundle);
        });

        bjyVideoPlayer.setOnPlayingTimeChangeListener((currentTime, duration) -> {
            //只通知到controller component
            Bundle bundle = BundlePool.obtainPrivate(UIEventKey.KEY_CONTROLLER_COMPONENT, currentTime);
            componentContainer.dispatchPlayEvent(OnPlayerEventListener.PLAYER_EVENT_ON_TIMER_UPDATE, bundle);
        });

        bjyVideoPlayer.setOnBufferUpdateListener(bufferedPercentage -> {
            //只通知到controller component
            Bundle bundle = BundlePool.obtainPrivate(UIEventKey.KEY_CONTROLLER_COMPONENT, bufferedPercentage);
            componentContainer.dispatchPlayEvent(OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_UPDATE, bundle);
        });

        bjyVideoPlayer.setOnPlayerStatusChangeListener(status -> {
            if (status == PlayerStatus.STATE_PREPARED) {
                updateAudioCoverStatus(bjyVideoPlayer.getVideoInfo() != null && bjyVideoPlayer.getVideoInfo().getDefinition() == VideoDefinition.Audio);
            }
            Bundle bundle = BundlePool.obtain(status);
            componentContainer.dispatchPlayEvent(OnPlayerEventListener.PLAYER_EVENT_ON_STATUS_CHANGE, bundle);
        });

        bjyVideoPlayer.setOnBufferingListener(new OnBufferingListener() {
            @Override
            public void onBufferingStart() {
                BJLog.d("bjy", "onBufferingStart invoke");
                componentContainer.dispatchPlayEvent(UIEventKey.PLAYER_CODE_BUFFERING_START, null);
            }

            @Override
            public void onBufferingEnd() {
                BJLog.d("bjy", "onBufferingEnd invoke");
                componentContainer.dispatchPlayEvent(UIEventKey.PLAYER_CODE_BUFFERING_END, null);
            }
        });
    }

    @Override
    protected void requestPlayAction() {
        super.requestPlayAction();
        //视频未初始化成功则请求视频地址
        if (getVideoInfo() == null || getVideoInfo().getVideoId() == 0) {
            setupOnlineVideoWithId(videoId, token, encrypted);
        } else {
            play();
        }
    }

    /**
     * 设置播放百家云在线视频
     *
     * @param videoId 视频id
     * @param token   需要集成方后端调用百家云后端的API获取
     */
    public void setupOnlineVideoWithId(long videoId, String token) {
        setupOnlineVideoWithId(videoId, token, false);
    }

    /**
     * 设置播放百家云在线视频
     *
     * @param videoId   视频id
     * @param token     需要集成方后端调用百家云后端的API获取
     * @param encrypted 是否加密
     */
    public void setupOnlineVideoWithId(long videoId, String token, boolean encrypted) {
        this.videoId = videoId;
        this.token = token;
        this.encrypted = encrypted;
        if (useDefaultNetworkListener) {
            registerNetChangeReceiver();
        }
        if (!enablePlayWithMobileNetwork && NetworkUtils.isMobile(NetworkUtils.getNetworkState(getContext()))) {
            sendCustomEvent(UIEventKey.CUSTOM_CODE_NETWORK_CHANGE_TO_MOBILE, null);
        } else {
            bjyVideoPlayer.setupOnlineVideoWithId(videoId, token, encrypted);
        }
    }

    /**
     * 设置播放本地文件路径
     *
     * @param path 视频文件绝对路径
     */
    public void setupLocalVideoWithFilePath(String path) {
        bjyVideoPlayer.setupLocalVideoWithFilePath(path);
    }

    /**
     * 设置播放百家云下载的本地视频
     *
     * @param downloadModel 百家云下载的model
     */
    public void setupLocalVideoWithDownloadModel(DownloadModel downloadModel) {
        bjyVideoPlayer.setupLocalVideoWithDownloadModel(downloadModel);
    }


    /**
     * 使用surfaceview播放视频
     */
    public void setRenderWithSurfaceView() {
        bjyPlayerView.setRenderType(IRender.RENDER_TYPE_SURFACE_VIEW);
    }

    /**
     * 使用textureview播放视频
     */
    public void setRenderWithTextureView() {
        bjyPlayerView.setRenderType(IRender.RENDER_TYPE_TEXTURE_VIEW);
    }

    /**
     * 更新纯音频占位图状态
     */
    private void updateAudioCoverStatus(boolean isAudio) {
        if (isAudio) {
            audioCoverIv.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(PlayerConstants.AUDIO_ON_PICTURE)
                    .into(audioCoverIv);
        } else {
            audioCoverIv.setVisibility(View.GONE);
        }
    }
}

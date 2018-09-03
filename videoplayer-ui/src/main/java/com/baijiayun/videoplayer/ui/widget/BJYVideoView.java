package com.baijiayun.videoplayer.ui.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.baijiayun.constant.VideoDefinition;
import com.baijiayun.download.DownloadModel;
import com.baijiayun.videoplayer.BJYVideoPlayer;
import com.baijiayun.videoplayer.VideoPlayerFactory;
import com.baijiayun.videoplayer.bean.BJYVideoInfo;
import com.baijiayun.videoplayer.event.BundlePool;
import com.baijiayun.videoplayer.event.EventKey;
import com.baijiayun.videoplayer.event.OnPlayerEventListener;
import com.baijiayun.videoplayer.listeners.OnBufferedUpdateListener;
import com.baijiayun.videoplayer.listeners.OnBufferingListener;
import com.baijiayun.videoplayer.listeners.OnPlayerErrorListener;
import com.baijiayun.videoplayer.listeners.OnPlayerStatusChangeListener;
import com.baijiayun.videoplayer.listeners.OnPlayingTimeChangeListener;
import com.baijiayun.videoplayer.log.BJLog;
import com.baijiayun.videoplayer.player.PlayerStatus;
import com.baijiayun.videoplayer.player.error.PlayerError;
import com.baijiayun.videoplayer.render.IRender;
import com.baijiayun.videoplayer.ui.event.UIEventKey;
import com.baijiayun.videoplayer.ui.listener.IComponentEventListener;
import com.baijiayun.videoplayer.ui.listener.PlayerStateGetter;
import com.baijiayun.videoplayer.ui.utils.NetworkUtils;
import com.baijiayun.videoplayer.widget.BJYPlayerView;

/**
 * Created by yongjiaming on 2018/8/6
 * <p>
 * 带ui的播放器组件
 */

public class BJYVideoView extends FrameLayout implements PlayerStateGetter{

    private static final String TAG = "BJYVideoView";

    private BJYVideoPlayer bjyVideoPlayer;
    private BJYPlayerView bjyPlayerView;
    private ComponentContainer componentContainer;
    private IComponentEventListener componentEventListener;
    //默认检测网络变化
    private boolean useDefaultNetworkListener = true;
    //默认不允许移动网络播放
    private boolean enablePlayWithMobileNetwork = false;
    private NetChangeBroadcastReceiver mBroadcastReceiver;
    private long videoId;
    private String token;
    private boolean encrypted;

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
        componentContainer.setStateGetter(this);
        componentContainer.setOnComponentEventListener(internalComponentEventListener);
    }

    /**
     * 初始化播放器
     *
     * @param builder
     */
    public void initPlayer(VideoPlayerFactory.Builder builder) {
        bjyVideoPlayer = builder.build();
        bjyVideoPlayer.bindPlayerView(bjyPlayerView);

        bjyVideoPlayer.setOnPlayerErrorListener(new OnPlayerErrorListener() {
            @Override
            public void onError(PlayerError error) {
                Bundle bundle = BundlePool.obtain();
                bundle.putString(EventKey.STRING_DATA, error.getMessage());
                componentContainer.dispatchErrorEvent(error.getCode(), bundle);
            }
        });

        bjyVideoPlayer.setOnPlayingTimeChangeListener(new OnPlayingTimeChangeListener() {
            @Override
            public void onPlayingTimeChange(int currentTime, int duration) {
                //只通知到controller component
                Bundle bundle = BundlePool.obtainPrivate(UIEventKey.KEY_CONTROLLER_COMPONENT, currentTime);
                componentContainer.dispatchPlayEvent(OnPlayerEventListener.PLAYER_EVENT_ON_TIMER_UPDATE, bundle);
            }
        });

        bjyVideoPlayer.setOnBufferUpdateListener(new OnBufferedUpdateListener() {
            @Override
            public void onBufferedPercentageChange(int bufferedPercentage) {
                //只通知到controller component
                Bundle bundle = BundlePool.obtainPrivate(UIEventKey.KEY_CONTROLLER_COMPONENT, bufferedPercentage);
                componentContainer.dispatchPlayEvent(OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_UPDATE, bundle);
            }
        });

        bjyVideoPlayer.setOnPlayerStatusChangeListener(new OnPlayerStatusChangeListener() {
            @Override
            public void onStatusChange(PlayerStatus status) {
                Bundle bundle = BundlePool.obtain(status);
                componentContainer.dispatchPlayEvent(OnPlayerEventListener.PLAYER_EVENT_ON_STATUS_CHANGE, bundle);
            }
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

    private IComponentEventListener internalComponentEventListener = new IComponentEventListener() {
        @Override
        public void onReceiverEvent(int eventCode, Bundle bundle) {
            switch (eventCode) {
                case UIEventKey.CUSTOM_CODE_REQUEST_PAUSE:
                    pause();
                    break;
                case UIEventKey.CUSTOM_CODE_REQUEST_REPLAY:
                    bjyVideoPlayer.rePlay();
                    break;
                case UIEventKey.CUSTOM_CODE_REQUEST_SEEK:
                    int seekToPosition = bundle.getInt(EventKey.INT_DATA);
                    seek(seekToPosition);
                    break;
                case UIEventKey.CUSTOM_CODE_REQUEST_SET_RATE:
                    setPlayRate(bundle.getFloat(EventKey.FLOAT_DATA));
                    break;
                case UIEventKey.CUSTOM_CODE_REQUEST_SET_DEFINITION:
                    changeDefinition((VideoDefinition) bundle.getSerializable(EventKey.SERIALIZABLE_DATA));
                    break;
                case UIEventKey.CUSTOM_CODE_REQUEST_PLAY:
                    enablePlayWithMobileNetwork = true;
                    //视频未初始化则请求视频地址
                    if(bjyVideoPlayer.getVideoInfo().getVideoId() == 0){
                        setupOnlineVideoWithId(videoId, token, encrypted);
                    } else{
                        play();
                    }
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

    /**
     * 供外部发射自定义事件到各个component
     *
     * @param eventCode
     * @param bundle
     */
    public void sendCustomEvent(int eventCode, Bundle bundle) {
        componentContainer.dispatchCustomEvent(eventCode, bundle);
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
        if(!enablePlayWithMobileNetwork && NetworkUtils.isMobile(NetworkUtils.getNetworkState(getContext()))){
            sendCustomEvent(UIEventKey.CUSTOM_CODE_NETWORK_CHANGE_TO_MOBILE, null);
        } else{
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
     * 开始播放
     */
    public void play() {
        bjyVideoPlayer.play();
    }

    /**
     * 从startOffset开始播放
     *
     * @param startOffset
     */
    public void play(int startOffset) {
        bjyVideoPlayer.play(startOffset);
    }

    /**
     * 暂停播放
     */
    public void pause() {
        bjyVideoPlayer.pause();
    }

    /**
     * 快进/快退到指定时间
     *
     * @param time
     */
    public void seek(int time) {
        bjyVideoPlayer.seek(time);
    }

    /**
     * 倍速播放[0.5 ~ 2.0]倍
     *
     * @param playRate 倍率
     */
    public void setPlayRate(float playRate) {
        bjyVideoPlayer.setPlayRate(playRate);
    }

    /**
     * 获取当前播放器的状态
     *
     * @return
     */
    @Override
    public PlayerStatus getPlayerStatus() {
        return bjyVideoPlayer.getPlayerStatus();
    }

    /**
     * 获取当前播放进度
     *
     * @return
     */
    @Override
    public int getCurrentPosition() {
        return bjyVideoPlayer.getCurrentPosition();
    }

    /**
     * 获取总时长
     *
     * @return
     */
    @Override
    public int getDuration() {
        return bjyVideoPlayer.getDuration();
    }

    /**
     * 获取视频信息
     *
     * @return
     */
    @Nullable
    @Override
    public BJYVideoInfo getVideoInfo() {
        return bjyVideoPlayer.getVideoInfo();
    }

    /**
     * 获取缓冲百分比
     *
     * @return
     */
    @Override
    public int getBufferPercentage() {
        return bjyVideoPlayer.getBufferPercentage();
    }

    /**
     * 获取播放倍速
     *
     * @return
     */
    @Override
    public float getPlayRate() {
        return bjyVideoPlayer.getPlayRate();
    }

    /**
     * 改变清晰度
     * 播放的时候调用，如果没有对应的清晰度不做处理，播本地文件不生效
     *
     * @param definition 清晰度
     * @return true切换清晰度成功  false切换清晰度失败
     */
    public void changeDefinition(VideoDefinition definition) {
        bjyVideoPlayer.changeDefinition(definition);
    }

    /**
     * 设置第三方用户信息，用于统计
     *
     * @param userName     第三方用户名
     * @param userIdentity 第三方用户标识
     */
    public void setUserInfo(String userName, String userIdentity) {
        bjyVideoPlayer.setUserInfo(userName, userIdentity);
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
     * 是否监听网络变化
     * @param enable true 监听
     */
    public void useDefaultNetworkListener(boolean enable){
        useDefaultNetworkListener = enable;
    }

    private void registerNetChangeReceiver() {
        if(mBroadcastReceiver == null){
            unregisterNetChangeReceiver();
            mBroadcastReceiver = new NetChangeBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            getContext().registerReceiver(mBroadcastReceiver, intentFilter);
        }
    }

    private void unregisterNetChangeReceiver() {
        if (mBroadcastReceiver != null) {
            getContext().unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }
    }

    /**
     * 回收资源
     */
    public void onDestroy() {
        bjyVideoPlayer.release();
        componentEventListener = null;
        componentContainer.destroy();
        unregisterNetChangeReceiver();
    }

    class NetChangeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                int netState = NetworkUtils.getNetworkState(context);
                if (!enablePlayWithMobileNetwork && NetworkUtils.isMobile(netState)) {
                    bjyVideoPlayer.pause();
                    componentContainer.dispatchCustomEvent(UIEventKey.CUSTOM_CODE_NETWORK_CHANGE_TO_MOBILE, null);
                }
                if(!NetworkUtils.isNetConnected(context)){
                    bjyVideoPlayer.pause();
                    componentContainer.dispatchCustomEvent(UIEventKey.CUSTOM_CODE_NETWORK_DISCONNETCT, null);
                }
//                if(NetworkUtils.isWifiConnected(context)){
//                    bjyVideoPlayer.play();
//                    componentContainer.dispatchCustomEvent(UIEventKey.CUSTOM_CODE_NETWORK_CHANGE_TO_WIFI, null);
//                }
            }
        }
    }
}

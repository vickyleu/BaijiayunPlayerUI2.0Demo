package com.baijiayun.videoplayer.ui.component;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baijiayun.videoplayer.event.OnPlayerEventListener;
import com.baijiayun.videoplayer.ui.event.UIEventKey;
import com.baijiayun.videoplayerui.R;

/**
 * Created by yongjiaming on 2018/8/7
 */

public class LoadingComponent extends BaseComponent {

    private TextView loadingTipTv;

    public LoadingComponent(Context context) {
        super(context);
    }

    @Override
    protected View onCreateComponentView(Context context) {
        return View.inflate(context, R.layout.layout_loading_component, null);
    }

    @Override
    protected void onInitView() {
        loadingTipTv = findViewById(R.id.loading_tips_tv);
    }

    @Override
    protected void setKey() {
        super.key = UIEventKey.KEY_LOADING_COMPONENT;
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode){
            case OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_START:
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:
            case OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_START:
            case OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_TO:
                setLoadingState(true);
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START:
            case OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_END:
            case OnPlayerEventListener.PLAYER_EVENT_ON_STOP:
            case OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_ERROR:
            case OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE:
                setLoadingState(false);
                break;
        }
    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {
        setLoadingState(false);
    }

    private void setLoadingState(boolean show) {
        setComponentVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void setTips(String tips){
        loadingTipTv.setText(tips);
    }
}

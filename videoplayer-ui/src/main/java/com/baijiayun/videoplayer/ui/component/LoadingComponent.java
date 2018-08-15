package com.baijiayun.videoplayer.ui.component;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baijiayun.videoplayer.event.EventKey;
import com.baijiayun.videoplayer.event.OnPlayerEventListener;
import com.baijiayun.videoplayer.player.PlayerStatus;
import com.baijiayun.videoplayer.ui.R;
import com.baijiayun.videoplayer.ui.event.UIEventKey;

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
            case OnPlayerEventListener.PLAYER_EVENT_ON_STATUS_CHANGE:
                PlayerStatus playerStatus = (PlayerStatus) bundle.getSerializable(EventKey.SERIALIZABLE_DATA);
                if(playerStatus == null){
                    return;
                }
                switch (playerStatus) {
                    case STATE_INITIALIZED:
                        setLoadingState(true);
                        break;
                    case STATE_ERROR:
                    case STATE_PAUSED:
                    case STATE_STARTED:
                    case STATE_STOPPED:
                    case STATE_PLAYBACK_COMPLETED:
                        setLoadingState(false);
                        break;
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE:
                setLoadingState(false);
                break;
        }
    }

    @Override
    public void onComponentEvent(int eventCode, Bundle bundle) {
        super.onComponentEvent(eventCode, bundle);
        switch (eventCode){
            case UIEventKey.CUSTOM_CODE_REQUEST_SEEK:
                setLoadingState(true);
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

    private void setTips(String tips) {
        loadingTipTv.setText(tips);
    }
}

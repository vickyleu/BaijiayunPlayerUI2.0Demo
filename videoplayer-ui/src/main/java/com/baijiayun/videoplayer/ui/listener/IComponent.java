package com.baijiayun.videoplayer.ui.listener;

import android.os.Bundle;
import android.view.View;

import com.baijiayun.videoplayer.listeners.PlayerStateGetter;

/**
 * Created by yongjiaming on 2018/8/7
 */

public interface IComponent {

    void onComponentEvent(int eventCode, Bundle bundle);

    void onPlayerEvent(int eventCode, Bundle bundle);

    void onErrorEvent(int eventCode, Bundle bundle);

    void onCustomEvent(int eventCode, Bundle bundle);

    void setComponentEventListener(IComponentEventListener componentEventListener);

    void setComponentVisibility(int visibility);

    void bindStateGetter(PlayerStateGetter stateGetter);

    PlayerStateGetter getStateGetter();

    View getView();

    String getKey();
}

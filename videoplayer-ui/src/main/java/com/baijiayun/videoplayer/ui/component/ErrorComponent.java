package com.baijiayun.videoplayer.ui.component;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.baijiayun.videoplayer.ui.UIEventKey;

/**
 * Created by yongjiaming on 2018/8/7
 */

public class ErrorComponent extends BaseComponent{

    public ErrorComponent(Context context) {
        super(context);
    }

    @Override
    protected View onCreateComponentView(Context context) {
        return null;
    }

    @Override
    protected void onInitView() {

    }

    @Override
    protected void setKey() {
        key = UIEventKey.KEY_ERROR_COMPONENT;
    }

    @Override
    public void onViewAttachedToWindow(View v) {

    }

    @Override
    public void onViewDetachedFromWindow(View v) {

    }

    @Override
    public void onComponentEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onCustomEvent(int eventCode, Bundle bundle) {

    }
}

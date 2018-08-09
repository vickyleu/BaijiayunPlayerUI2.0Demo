package com.baijiayun.videoplayer.ui.component;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.baijiayun.videoplayer.ui.UIEventKey;
import com.baijiayun.videoplayer.ui.listener.IComponent;
import com.baijiayun.videoplayer.ui.listener.IComponentEventListener;

/**
 * Created by yongjiaming on 2018/8/7
 */

public abstract class BaseComponent implements IComponent, View.OnAttachStateChangeListener{

    private View view;
    protected String key;
    private IComponentEventListener componentEventListener;

    public BaseComponent(Context context){
        view = onCreateComponentView(context);
        onInitView();
        view.addOnAttachStateChangeListener(this);
    }

    @Override
    public void setComponentVisibility(int visibility) {
        view.setVisibility(visibility);
    }

    @Override
    public View getView() {
        return view;
    }

    protected abstract View onCreateComponentView(Context context);

    protected abstract void onInitView();

    protected final <T extends View> T findViewById(int id){
        return view.findViewById(id);
    }

    //发射当前组件的事件
    protected void notifyComponentEvent(int eventCode, Bundle bundle){
        if(componentEventListener != null){
            componentEventListener.onReceiverEvent(eventCode, bundle);
        }
    }

    @Override
    public void setComponentEventListener(IComponentEventListener componentEventListener) {
        this.componentEventListener = componentEventListener;
    }


    public final void requestPause(Bundle bundle) {
        notifyComponentEvent(UIEventKey.CUSTOM_CODE_REQUEST_PAUSE, bundle);
    }

    public final void requestPlay(Bundle bundle) {
        notifyComponentEvent(UIEventKey.CUSTOM_CODE_REQUEST_PLAY, bundle);
    }

    public final void requestSeek(Bundle bundle) {
        notifyComponentEvent(UIEventKey.CUSTOM_CODE_REQUEST_SEEK, bundle);
    }

    public final void requestStop(Bundle bundle) {
        notifyComponentEvent(UIEventKey.CUSTOM_CODE_REQUEST_STOP, bundle);
    }

    protected abstract void setKey();

    @Override
    public String getKey(){
        return key;
    }
}

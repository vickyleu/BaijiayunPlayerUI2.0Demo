package com.baijiayun.videoplayer.ui;

import android.os.Bundle;

import com.baijiayun.videoplayer.event.OnPlayerEventListener;
import com.baijiayun.videoplayer.ui.component.BaseComponent;
import com.baijiayun.videoplayer.ui.listener.IComponent;
import com.baijiayun.videoplayer.ui.listener.IComponentChangeListener;

/**
 * Created by yongjiaming on 2018/8/7
 * 事件分发器
 */

public class EventDispatcher {

    private ComponentManager componentManager;

    public EventDispatcher(ComponentManager manager){
        this.componentManager = manager;
    }

    public void dispatchPlayEvent(final int eventCode, final Bundle bundle){
        componentManager.forEach(new IComponentChangeListener.OnLoopListener() {
            @Override
            public void onEach(IComponent component) {
                component.onPlayerEvent(eventCode, bundle);
            }
        });
        recycleBundle(bundle);
    }

    public void dispatchPlayEvent(IComponentChangeListener.Filter filter, final int eventCode, final Bundle bundle){
        componentManager.forEach(filter, new IComponentChangeListener.OnLoopListener() {
            @Override
            public void onEach(IComponent component) {
                component.onPlayerEvent(eventCode, bundle);
            }
        });
        recycleBundle(bundle);
    }

    public void dispatchErrorEvent(final int eventCode, final Bundle bundle){
        componentManager.forEach(new IComponentChangeListener.OnLoopListener() {
            @Override
            public void onEach(IComponent component) {
                component.onErrorEvent(eventCode, bundle);
            }
        });
        recycleBundle(bundle);
    }

    public void dispatchErrorEvent(IComponentChangeListener.Filter filter, final int eventCode, final Bundle bundle){
        componentManager.forEach(filter, new IComponentChangeListener.OnLoopListener() {
            @Override
            public void onEach(IComponent component) {
                component.onErrorEvent(eventCode, bundle);
            }
        });
        recycleBundle(bundle);
    }

    public void dispatchComponentEvent(final int eventCode, final Bundle bundle){
        componentManager.forEach(new IComponentChangeListener.OnLoopListener() {
            @Override
            public void onEach(IComponent component) {
                component.onComponentEvent(eventCode, bundle);
            }
        });
        recycleBundle(bundle);
    }

    public void dispatchComponentEvent(IComponentChangeListener.Filter filter, final int eventCode, final Bundle bundle){
        componentManager.forEach(filter, new IComponentChangeListener.OnLoopListener() {
            @Override
            public void onEach(IComponent component) {
                component.onComponentEvent(eventCode, bundle);
            }
        });
        recycleBundle(bundle);
    }

    public void dispatchCustomEvent(final int eventCode, final Bundle bundle){
        componentManager.forEach(new IComponentChangeListener.OnLoopListener() {
            @Override
            public void onEach(IComponent component) {
                component.onCustomEvent(eventCode, bundle);
            }
        });
    }

    public void dispatchCustomEvent(IComponentChangeListener.Filter filter, final int eventCode, final Bundle bundle){
        componentManager.forEach(filter, new IComponentChangeListener.OnLoopListener() {
            @Override
            public void onEach(IComponent component) {
                component.onCustomEvent(eventCode, bundle);
            }
        });
    }

    private void recycleBundle(Bundle bundle){
        if(bundle!=null)
            bundle.clear();
    }
}

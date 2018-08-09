package com.baijiayun.videoplayer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.baijiayun.videoplayer.ui.listener.IComponent;
import com.baijiayun.videoplayer.ui.listener.IComponentChangeListener;
import com.baijiayun.videoplayer.ui.listener.IComponentEventListener;

/**
 * Created by yongjiaming on 2018/8/7
 * 组件容器类
 */

public class ComponentContainer extends FrameLayout {

    private ComponentManager componentManager;
    private IComponentEventListener onComponentEventListener;
    private IComponentEventListener internalComponentEventListener =
            new IComponentEventListener() {
                @Override
                public void onReceiverEvent(int eventCode, Bundle bundle) {
                    //通知外部监听
                    if(onComponentEventListener!=null){
                        onComponentEventListener.onReceiverEvent(eventCode, bundle);
                    }
                    //通知其它component
                    if (eventDispatcher != null) {
                        eventDispatcher.dispatchComponentEvent(eventCode, bundle);
                    }
                }
            };
    private EventDispatcher eventDispatcher;

    public ComponentContainer(@NonNull Context context) {
        this(context, null);
    }

    public ComponentContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ComponentContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (componentManager == null) {
            componentManager = ComponentManager.get();
            componentManager.generateDefaultComponentList(context);
        }

        eventDispatcher = new EventDispatcher(componentManager);
        componentManager.forEach(new IComponentChangeListener.OnLoopListener() {
            @Override
            public void onEach(IComponent component) {
                addComponent(component);
            }
        });
    }

    private void addComponent(IComponent component) {
        addView(component.getView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        component.setComponentEventListener(internalComponentEventListener);
    }

    private void removeComponent(IComponent component) {
        removeView(component.getView());
        component.setComponentEventListener(null);
    }

    public void setOnComponentEventListener(IComponentEventListener componentEventListener) {
        onComponentEventListener = componentEventListener;
    }

    public final void dispatchPlayEvent(int eventCode, Bundle bundle) {
        if (eventDispatcher != null) {
            eventDispatcher.dispatchPlayEvent(eventCode, bundle);
        }
    }

    public final void dispatchPlayEvent(IComponentChangeListener.Filter filter, int eventCode, Bundle bundle){
        if(eventDispatcher != null){
            eventDispatcher.dispatchPlayEvent(filter, eventCode, bundle);
        }
    }

    public final void dispatchErrorEvent(int eventCode, Bundle bundle) {
        if (eventDispatcher != null) {
            eventDispatcher.dispatchErrorEvent(eventCode, bundle);
        }
    }

    public final void dispatchErrorEvent(IComponentChangeListener.Filter filter, int eventCode, Bundle bundle) {
        if (eventDispatcher != null) {
            eventDispatcher.dispatchErrorEvent(filter, eventCode, bundle);
        }
    }

    public final void dispatchCustomEvent(int eventCode, Bundle bundle){
        if (eventDispatcher != null) {
            eventDispatcher.dispatchCustomEvent(eventCode, bundle);
        }
    }

    public final void dispatchCustomEvent(IComponentChangeListener.Filter filter, int eventCode, Bundle bundle){
        if (eventDispatcher != null) {
            eventDispatcher.dispatchCustomEvent(filter, eventCode, bundle);
        }
    }

    public void destroy(){
        onComponentEventListener = null;
        internalComponentEventListener = null;
        eventDispatcher = null;
        componentManager.forEach(new IComponentChangeListener.OnLoopListener() {
            @Override
            public void onEach(IComponent component) {
                removeComponent(component);
            }
        });
    }
}

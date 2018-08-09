package com.baijiayun.videoplayer.ui;

import android.content.Context;

import com.baijiayun.videoplayer.ui.component.BaseComponent;
import com.baijiayun.videoplayer.ui.component.ControllerComponent;
import com.baijiayun.videoplayer.ui.component.LoadingComponent;
import com.baijiayun.videoplayer.ui.listener.IComponent;
import com.baijiayun.videoplayer.ui.listener.IComponentChangeListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by yongjiaming on 2018/8/7
 */

public class ComponentManager {
    //component 集合
    private Map<String, IComponent> componentMap;
    private List<IComponentChangeListener> componentChangeListeners;
    private static ComponentManager instance;

    private ComponentManager(){
        componentMap = new ConcurrentHashMap<>();
        componentChangeListeners = new CopyOnWriteArrayList<>();
    }

    public static ComponentManager get(){
        if(instance == null){
            synchronized (ComponentManager.class){
                if(instance == null){
                    instance = new ComponentManager();
                }
            }
        }
        return instance;
    }


    public void addComponent(String key, BaseComponent component){
        componentMap.put(key, component);
        for(IComponentChangeListener changeListener : componentChangeListeners){
            changeListener.onComponentAdd(component);
        }
    }

    public void removeComponent(String key){
        IComponent removedComponent = componentMap.remove(key);
        for(IComponentChangeListener changeListener : componentChangeListeners){
            changeListener.onComponentRemove(removedComponent);
        }
    }

    public void addOnComponentChangeListener(IComponentChangeListener componentChangeListener){
        if(componentChangeListener != null && !componentChangeListeners.contains(componentChangeListener)){
            componentChangeListeners.add(componentChangeListener);
        }
    }

    public void removeOnComponentChangeListener(IComponentChangeListener componentChangeListener){
        componentChangeListeners.remove(componentChangeListener);
    }


    public void forEach(IComponentChangeListener.OnLoopListener loopListener){
        for(IComponent component : componentMap.values()){
            loopListener.onEach(component);
        }
    }

    public void forEach(IComponentChangeListener.Filter filter, IComponentChangeListener.OnLoopListener loopListener){
        for(IComponent component : componentMap.values()){
            if(filter == null || filter.filter(component)){
                loopListener.onEach(component);
            }
        }
    }

    /**
     * 默认组合的组件
     * @param context
     */
    public void generateDefaultComponentList(Context context){
        componentMap.clear();
        componentChangeListeners.clear();
        addComponent(UIEventKey.KEY_LOADING_COMPONENT, new LoadingComponent(context));
        addComponent(UIEventKey.KEY_CONTROLLER_COMPONENT, new ControllerComponent(context));
    }
}

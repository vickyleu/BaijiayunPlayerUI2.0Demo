package com.baijiayun.videoplayer.ui.listener;

/**
 * Created by yongjiaming on 2018/8/7
 */

public interface IComponentChangeListener {

    void onComponentAdd(IComponent component);

    void onComponentRemove(IComponent component);

    interface OnLoopListener{
        void onEach(IComponent component);
    }

    interface Filter{
        boolean filter(IComponent component);
    }
}

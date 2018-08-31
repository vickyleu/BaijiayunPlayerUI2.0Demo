package com.baijiayun.videoplayerui;

import android.app.Application;

import com.baijiayun.videoplayer.util.Utils;


/**
 * Created by wangkangfei on 17/5/13.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}

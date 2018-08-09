package com.baijiayun.videoplayer.ui;

/**
 * Created by yongjiaming on 2018/8/7
 */

public class UIEventKey {

    public static final String KEY_LOADING_COMPONENT = "loading_component";
    public static final String KEY_CONTROLLER_COMPONENT = "controller_component";
    public static final String KEY_GESTURE_COVER = "gesture_cover";
    public static final String KEY_COMPLETE_COVER = "complete_cover";
    public static final String KEY_ERROR_COMPONENT = "error_component";
    public static final String KEY_CLOSE_COVER = "close_cover";

    public static final String KEY_INT_CURRENT_TIME = "current_time";
    public static final String KEY_INT_TOTAL_TIME = "total_time";
    public static final String KEY_INT_BUFFER_PERCENT = "buffer_percent";
    public static final String KEY_PLAYER_STATUS_CHANGE = "status_change";


    /***************Custom Code*******************/
    //controller status change
    public static final int CUSTOM_CODE_CONTROLLER_STATUS_CHANGE = -80001;
    public static final int CUSTOM_CODE_REQUEST_SEEK = -80002;
    public static final int CUSTOM_CODE_REQUEST_PAUSE = - 80003;
    public static final int CUSTOM_CODE_REQUEST_PLAY = -80004;
    public static final int CUSTOM_CODE_REQUEST_STOP = -80005;
    public static final int CUSTOM_CODE_REQUEST_TOGGLE_SCREEN = -80006;
    public static final int CUSTOM_CODE_REQUEST_BACK = -80007;
}

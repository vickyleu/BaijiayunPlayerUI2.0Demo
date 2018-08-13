package com.baijiayun.videoplayer.ui;

import android.content.Context;

import com.baijiayun.constant.VideoDefinition;
import com.baijiayun.videoplayerui.R;

/**
 * Created by yongjiaming on 2018/8/13 14:29
 */
public class Utils {

    /**
     * 由VideoDefinition获取清晰度字符串
     */
    public static String getDefinitionInString(Context context, VideoDefinition def) {
        String[] defArray = context.getResources().getStringArray(R.array.bjy_player_definition);
        switch (def) {
            case Audio:
                return defArray[0];
            case SD:
                return defArray[1];
            case HD:
                return defArray[2];
            case SHD:
                return defArray[3];
            case _720P:
                return defArray[4];
            case _1080P:
                return defArray[5];
            default:
                return defArray[2];
        }
    }

}

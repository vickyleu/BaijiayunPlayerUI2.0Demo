package com.baijiayun.videoplayer.ui.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import com.baijiayun.constant.VideoDefinition;
import com.baijiayun.videoplayer.ui.R;

/**
 * Created by yongjiaming on 2018/8/13 14:29
 */
public class Utils {

    private static float mScreenDensity = 0.0f;

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


    /**
     * 获取 density
     *
     * @param context 上下文
     * @return density
     */
    public static float getScreenDensity(Context context) {
        if (mScreenDensity == 0.0f) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            mScreenDensity = dm.density;
        }
        return mScreenDensity;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


}

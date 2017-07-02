package com.rayzhang.android.rzscan.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by Ray on 17/1/13.
 */
public class DisplayUtil {

    public static int screenWidthPx;
    public static int screenhightPx;
    public static float density;
    public static int densityDPI;
    public static float screenWidthDip;
    public static float screenHightDip;

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
        return px;
    }
}

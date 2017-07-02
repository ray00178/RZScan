package com.rayzhang.android.rzscan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;

import com.google.zxing.BarcodeFormat;
import com.rayzhang.android.rzscan.impl.AnalyzeCallback;
import com.rayzhang.android.rzscan.utils.DisplayUtil;
import com.rayzhang.android.rzscan.utils.ScanUtil;

/**
 * Created by Ray on 2017/6/29.
 * 調用入口class
 */

public class RZScan {
    private static final String TAG = RZScan.class.getSimpleName();
    protected static final String RZSCAN_APP_NAME = "RZSCAN_APP_NAME";
    protected static final String RZSCAN_SHOW_STATUSBAR = "RZSCAN_SHOW_STATUSBAR";
    protected static final String RZSCAN_STATUSBAR_COLOR = "RZSCAN_STATUSBAR_COLOR";
    protected static final String RZSCAN_TOOLBAR_TITLE = "RZSCAN_TOOLBAR_TITLE";
    protected static final String RZSCAN_TOOLBAR_TITLE_COLOR = "RZSCAN_TOOLBAR_TITLE_COLOR";
    protected static final String RZSCAN_TOOLBAR_COLOR = "RZSCAN_TOOLBAR_COLOR";
    protected static final String RZSCAN_DIALOG_ICON = "RZSCAN_DIALOG_ICON";
    protected static final String RZSCAN_BORDER_COLOR = "RZSCAN_BORDER_COLOR";
    protected static final String RZSCAN_BORDER_WIDTH = "RZSCAN_BORDER_WIDTH";
    protected static final String RZSCAN_BORDER_LENGTH = "RZSCAN_BORDER_LENGTH";
    protected static final String RZSCAN_DESCRIPTION_COLOR = "RZSCAN_DESCRIPTION_COLOR";
    protected static final String RZSCAN_DESCRIPTION_SIZE = "RZSCAN_DESCRIPTION_SIZE";
    protected static final String RZSCAN_DESCRIPTION_OFFSET = "RZSCAN_DESCRIPTION_OFFSET";
    protected static final String RZSCAN_FRAME_SIZE = "RZSCAN_FRAME_SIZE";

    private Intent rzIntent;
    private Bundle rzBundle;

    public static RZScan ofAppName(@NonNull String appName) {
        return new RZScan(appName);
    }

    private RZScan(String appName) {
        rzBundle = new Bundle();
        rzBundle.putString(RZSCAN_APP_NAME, appName);
    }

    public RZScan showStatusBar(boolean isShow) {
        rzBundle.putBoolean(RZSCAN_SHOW_STATUSBAR, isShow);
        return this;
    }

    public RZScan setStatusBarColor(@ColorInt int statusBarColor) {
        rzBundle.putInt(RZSCAN_STATUSBAR_COLOR, statusBarColor);
        return this;
    }

    public RZScan setToolBarTitle(String title) {
        rzBundle.putString(RZSCAN_TOOLBAR_TITLE, title);
        return this;
    }

    public RZScan setToolBarTitleColor(@ColorInt int titleColor) {
        rzBundle.putInt(RZSCAN_TOOLBAR_TITLE_COLOR, titleColor);
        return this;
    }

    public RZScan setToolBarColor(@ColorInt int toolBarColor) {
        rzBundle.putInt(RZSCAN_TOOLBAR_COLOR, toolBarColor);
        return this;
    }

    public RZScan setDialogIcon(@DrawableRes int resID) {
        rzBundle.putInt(RZSCAN_DIALOG_ICON, resID);
        return this;
    }

    public RZScan setScanBorderColor(@ColorInt int color) {
        rzBundle.putInt(RZSCAN_BORDER_COLOR, color);
        return this;
    }

    public RZScan setScanBorderWidth(int width) {
        rzBundle.putInt(RZSCAN_BORDER_WIDTH, width);
        return this;
    }

    public RZScan setScanBorderLength(int length) {
        rzBundle.putInt(RZSCAN_BORDER_LENGTH, length);
        return this;
    }

    public RZScan setDescriptionColor(@ColorInt int color) {
        rzBundle.putInt(RZSCAN_DESCRIPTION_COLOR, color);
        return this;
    }

    public RZScan setDescriptionSize(float size) {
        rzBundle.putFloat(RZSCAN_DESCRIPTION_SIZE, size);
        return this;
    }

    public RZScan setDescriptionOffSetTop(float offSet) {
        rzBundle.putFloat(RZSCAN_DESCRIPTION_OFFSET, offSet);
        return this;
    }

    public RZScan setScanFrameSize(@NonNull int[] widthAndheight) {
        if (widthAndheight.length < 2) widthAndheight = new int[]{250, 250};
        rzBundle.putIntArray(RZSCAN_FRAME_SIZE, widthAndheight);
        return this;
    }

    public void start(@NonNull Activity activity, @NonNull int requestCode) {
        initDisplayOpinion(activity.getApplicationContext());
        rzIntent = new Intent(activity, CaptureActivity.class);
        rzIntent.putExtras(rzBundle);
        activity.startActivityForResult(rzIntent, requestCode);
    }

    public void start(@NonNull android.app.Fragment fragment, @NonNull int requestCode) {
        initDisplayOpinion(fragment.getActivity().getApplicationContext());
        rzIntent = new Intent(fragment.getActivity(), CaptureActivity.class);
        rzIntent.putExtras(rzBundle);
        fragment.startActivityForResult(rzIntent, requestCode);
    }

    public void start(@NonNull Fragment fragment, @NonNull int requestCode) {
        initDisplayOpinion(fragment.getActivity().getApplicationContext());
        rzIntent = new Intent(fragment.getActivity(), CaptureActivity.class);
        rzIntent.putExtras(rzBundle);
        fragment.startActivityForResult(rzIntent, requestCode);
    }

    public static String getResult(Intent data) {
        Bundle bundle = data.getExtras();
        return bundle.getString(ScanUtil.RESULT_STRING);
    }

    public static Bitmap ENCODE(@NonNull String text, int w, int h, Bitmap logo, @NonNull BarcodeFormat type) {
        return ScanUtil.encode(text, w, h, logo, type);
    }

    public static void DECODE(String filePath, AnalyzeCallback analyzeCallback) {
        ScanUtil.decode(filePath, analyzeCallback);
    }

    private void initDisplayOpinion(Context context) {
        if (context == null) {
            return;
        }
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        DisplayUtil.density = metrics.density;
        DisplayUtil.densityDPI = metrics.densityDpi;
        DisplayUtil.screenWidthPx = metrics.widthPixels;
        DisplayUtil.screenhightPx = metrics.heightPixels;
        DisplayUtil.screenWidthDip = DisplayUtil.px2dp(context, metrics.widthPixels);
        DisplayUtil.screenHightDip = DisplayUtil.px2dp(context, metrics.heightPixels);
    }
}

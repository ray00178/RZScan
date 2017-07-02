package com.rayzhang.android.rzscan.impl;

import android.graphics.Bitmap;

/**
 * Created by Ray on 2017/7/2.
 *
 * 解析二維碼結果
 */

public interface AnalyzeCallback {
    void onAnalyzeSuccess(Bitmap mBitmap, String result);

    void onAnalyzeFailed();
}

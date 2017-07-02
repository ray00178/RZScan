package com.rayzhang.android.rzscan.camera;

import android.graphics.Bitmap;

import com.google.zxing.LuminanceSource;

/**
 * Created by Ray on 17/1/13.
 * 自定義解析Bitmap LuminanceSource
 */
public class BitmapLuminanceSource extends LuminanceSource {

    private byte bitmapPixels[];

    public BitmapLuminanceSource(Bitmap bitmap) {
        super(bitmap.getWidth(), bitmap.getHeight());

        // 首先，要取得該圖片的像素數組內容
        int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
        this.bitmapPixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(data, 0, getWidth(), 0, 0, getWidth(), getHeight());

        // 將int數組轉換為byte數組，也就是取像素值中藍色值部分作為辨析內容
        for (int i = 0; i < data.length; i++) {
            this.bitmapPixels[i] = (byte) data[i];
        }
    }

    @Override
    public byte[] getMatrix() {
        // 返回我們生成好的像素數據
        return bitmapPixels;
    }

    @Override
    public byte[] getRow(int y, byte[] row) {
        // 這裡要得到指定行的像素數據
        System.arraycopy(bitmapPixels, y * getWidth(), row, 0, getWidth());
        return row;
    }
}

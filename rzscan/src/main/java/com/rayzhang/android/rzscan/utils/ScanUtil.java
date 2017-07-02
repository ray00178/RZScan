package com.rayzhang.android.rzscan.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.rayzhang.android.rzscan.camera.BitmapLuminanceSource;
import com.rayzhang.android.rzscan.camera.CameraManager;
import com.rayzhang.android.rzscan.decoding.DecodeFormatManager;
import com.rayzhang.android.rzscan.impl.AnalyzeCallback;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Created by aaron on 16/7/27.
 * 二維碼掃描工具類
 */
public class ScanUtil {

    public static final String RESULT_TYPE = "result_type";
    public static final String RESULT_STRING = "result_string";
    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_FAILED = 2;

    /**
     * 解析二維碼圖片工具類
     */
    public static void decode(String filePath, AnalyzeCallback analyzeCallback) {

        /**
         * 首先判斷圖片的大小,若圖片過大,則執行圖片的裁剪操作,防止OOM
         */
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 先獲取原大小
        options.inJustDecodeBounds = true;
        Bitmap mBitmap = BitmapFactory.decodeFile(filePath, options);
        // 獲取新的大小
        options.inJustDecodeBounds = false;

        int sampleSize = (int) (options.outHeight / (float) 400);

        if (sampleSize <= 0) sampleSize = 1;
        options.inSampleSize = sampleSize;
        mBitmap = BitmapFactory.decodeFile(filePath, options);

        MultiFormatReader multiFormatReader = new MultiFormatReader();

        // 解碼的參數
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(2);
        // 可以解析的編碼類型
        Vector<BarcodeFormat> decodeFormats = new Vector<BarcodeFormat>();
        if (decodeFormats.isEmpty()) {
            decodeFormats = new Vector<>();
            // 這裡設置可掃描的類型，我這裡選擇了都支持
            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        // 設置繼續的字符編碼格式為UTF8
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
        // 設置解析配置參數
        multiFormatReader.setHints(hints);

        // 開始對圖像資源解碼
        Result rawResult = null;
        try {
            rawResult = multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(new BitmapLuminanceSource(mBitmap))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (rawResult != null) {
            if (analyzeCallback != null) {
                analyzeCallback.onAnalyzeSuccess(mBitmap, rawResult.getText());
            }
        } else {
            if (analyzeCallback != null) {
                analyzeCallback.onAnalyzeFailed();
            }
        }
    }

    /**
     * 生成二維碼圖片
     */
    public static Bitmap encode(String text, int w, int h, Bitmap logo, BarcodeFormat type) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        try {
            Bitmap scaleLogo = getScaleLogo(logo, w, h);

            int offsetX = w / 2;
            int offsetY = h / 2;

            int scaleWidth = 0;
            int scaleHeight = 0;
            if (scaleLogo != null) {
                scaleWidth = scaleLogo.getWidth();
                scaleHeight = scaleLogo.getHeight();
                offsetX = (w - scaleWidth) / 2;
                offsetY = (h - scaleHeight) / 2;
            }
            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF8");
            // 容錯級別
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            // 設置空白邊距的寬度
            hints.put(EncodeHintType.MARGIN, 0);

            //BitMatrix bitMatrix = new QRCodeWriter().encode(text, type, w, h, hints);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, type, w, h, hints);
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (x >= offsetX && x < offsetX + scaleWidth && y >= offsetY && y < offsetY + scaleHeight) {
                        int pixel = scaleLogo.getPixel(x - offsetX, y - offsetY);
                        if (pixel == 0) {
                            if (bitMatrix.get(x, y)) {
                                pixel = 0xff000000;
                            } else {
                                pixel = 0xffffffff;
                            }
                        }
                        pixels[y * w + x] = pixel;
                    } else {
                        if (bitMatrix.get(x, y)) {
                            pixels[y * w + x] = 0xff000000;
                        } else {
                            pixels[y * w + x] = 0xffffffff;
                        }
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap getScaleLogo(Bitmap logo, int w, int h) {
        if (logo == null) return null;
        Matrix matrix = new Matrix();
        float scaleFactor = Math.min(w * 1.0f / 4.5f / logo.getWidth(), h * 1.0f / 4.5f / logo.getHeight());
        matrix.postScale(scaleFactor, scaleFactor);
        return Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), matrix, true);
    }


    /**
     * 是否開啟閃光燈
     */
    public static void isLightEnable(boolean isEnable) {
        Camera camera = CameraManager.get().getCamera();
        if (camera != null) {
            Camera.Parameters parameter = camera.getParameters();
            if (isEnable) {
                parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            } else {
                parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            camera.setParameters(parameter);
        }
    }
}

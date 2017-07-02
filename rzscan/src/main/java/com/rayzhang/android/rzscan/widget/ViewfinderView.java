/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rayzhang.android.rzscan.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.rayzhang.android.rzscan.R;
import com.rayzhang.android.rzscan.camera.CameraManager;
import com.rayzhang.android.rzscan.utils.DisplayUtil;

/**
 * 自定義View,掃描功能
 */
public final class ViewfinderView extends View {
    private static final String TAG = ViewfinderView.class.getSimpleName();
    private static final int DEFAULT_BORDER_COLOR = Color.argb(255, 255, 255, 0);
    private static final int DEFAULT_BORDER_LENGTH = 65;
    private static final int DEFAULT_BORDER_WIDTH = 15;
    private static final float DEFAULT_DESCRIPTION_SIZE = 10f;
    private static final int DEFAULT_DESCRIPTION_COLOR = Color.argb(255, 128, 128, 128);
    private float descriptionOffSet;
    private String descriptionText;

    private final Paint paint;
    private final Paint textPaint;
    private final int maskColor;
    // 掃描框邊角顏色
    private int innercornercolor;
    // 掃描框邊角長度
    private int innercornerlength;
    // 掃描框邊角寬度
    private int innercornerwidth;

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        maskColor = Color.argb(200, 0, 0, 0);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setDither(true);
        textPaint.setTextSize(DisplayUtil.sp2px(context, DEFAULT_DESCRIPTION_SIZE));
        textPaint.setColor(DEFAULT_DESCRIPTION_COLOR);
        descriptionOffSet = DisplayUtil.dp2px(context, 20);
        descriptionText = getResources().getString(R.string.rzscan_description);

        initInnerRect(context, attrs);
    }

    /**
     * 初始化内部框的大小
     *
     * @param context
     * @param attrs
     */
    private void initInnerRect(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.innerrect);

        // 掃描框距離頂部
        float innerMarginTop = ta.getDimension(R.styleable.innerrect_inner_margintop, -1);
        if (innerMarginTop != -1) {
            CameraManager.FRAME_MARGINTOP = (int) innerMarginTop;
        }

        // 掃描框的寬度
        CameraManager.FRAME_WIDTH = (int) ta.getDimension(R.styleable.innerrect_inner_width, DisplayUtil.dp2px(context, 250));
        // 掃描框的高度
        CameraManager.FRAME_HEIGHT = (int) ta.getDimension(R.styleable.innerrect_inner_height, DisplayUtil.dp2px(context, 250));
        // 掃描框邊角顏色
        innercornercolor = ta.getColor(R.styleable.innerrect_inner_corner_color, DEFAULT_BORDER_COLOR);
        // 掃描框邊角長度
        innercornerlength = (int) ta.getDimension(R.styleable.innerrect_inner_corner_length, DEFAULT_BORDER_LENGTH);
        // 掃描框邊角寬度
        innercornerwidth = (int) ta.getDimension(R.styleable.innerrect_inner_corner_width, DEFAULT_BORDER_WIDTH);

        ta.recycle();
    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect frame = CameraManager.get().getFramingRect();
        if (frame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        paint.setColor(maskColor);
        paint.setStyle(Paint.Style.FILL);
        // 畫半透明背景
        // Draw top
        canvas.drawRect(0, 0, width, frame.top, paint);
        // Draw left
        canvas.drawRect(0, frame.top, frame.left, frame.bottom, paint);
        // Draw right
        canvas.drawRect(frame.right, frame.top, width, frame.bottom, paint);
        // Draw bottom
        canvas.drawRect(0, frame.bottom, width, height, paint);
        // 畫掃描邊框
        drawFrameBounds(canvas, frame);
        // 畫敘述文字
        drawDescription(canvas, frame);
    }

    /**
     * 繪製取景框邊框
     *
     * @param canvas
     * @param frame
     */
    private void drawFrameBounds(Canvas canvas, Rect frame) {
        paint.setColor(innercornercolor);

        int corWidth = innercornerwidth;
        int corLength = innercornerlength;

        // 左上角
        canvas.drawRect(frame.left, frame.top, frame.left + corWidth, frame.top + corLength, paint);
        canvas.drawRect(frame.left, frame.top, frame.left + corLength, frame.top + corWidth, paint);
        // 右上角
        canvas.drawRect(frame.right - corWidth, frame.top, frame.right, frame.top + corLength, paint);
        canvas.drawRect(frame.right - corLength, frame.top, frame.right, frame.top + corWidth, paint);
        // 左下角
        canvas.drawRect(frame.left, frame.bottom - corLength, frame.left + corWidth, frame.bottom, paint);
        canvas.drawRect(frame.left, frame.bottom - corWidth, frame.left + corLength, frame.bottom, paint);
        // 右下角
        canvas.drawRect(frame.right - corWidth, frame.bottom - corLength, frame.right, frame.bottom, paint);
        canvas.drawRect(frame.right - corLength, frame.bottom - corWidth, frame.right, frame.bottom, paint);
    }

    private void drawDescription(Canvas canvas, Rect frame) {
        if (TextUtils.isEmpty(descriptionText)) return;
        Paint.FontMetricsInt fm = textPaint.getFontMetricsInt();
        int ascent = frame.bottom + fm.ascent;
        int descent = frame.bottom + fm.descent;
        // 計算文字高度
        int height = descent - ascent;
        // 計算文字寬度
        float textWidth = textPaint.measureText(descriptionText);

        float drawX = (getWidth() - textWidth) / 2;
        float drawY = frame.bottom + height + descriptionOffSet;
        canvas.drawText(descriptionText, drawX, drawY, textPaint);
    }


    public void drawViewfinder() {
        invalidate();
    }

    public void setInnerRectColor(@ColorInt int color) {
        innercornercolor = color;
    }

    public void setInnerRectWidth(int width) {
        innercornerwidth = width;
    }

    public void setInnerRectLength(int length) {
        innercornerlength = length;
    }

    public void setDescriptionColor(@ColorInt int color) {
        textPaint.setColor(color);
    }

    public void setDescriptionSize(float size) {
        textPaint.setTextSize(DisplayUtil.sp2px(getContext(), size));
    }

    public void setDescriptionOffSetTop(float offset) {
        descriptionOffSet = DisplayUtil.dp2px(getContext(), offset);
    }

    /**
     * 掃描框的大小
     *
     * @param width
     * @param height
     */
    public void setScanFrameSize(int width, int height) {
        CameraManager.FRAME_WIDTH = DisplayUtil.dp2px(getContext(), width);
        CameraManager.FRAME_HEIGHT = DisplayUtil.dp2px(getContext(), height);
    }
}

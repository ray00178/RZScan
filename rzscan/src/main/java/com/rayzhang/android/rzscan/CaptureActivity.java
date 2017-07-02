package com.rayzhang.android.rzscan;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.rayzhang.android.rzscan.impl.AnalyzeCallback;
import com.rayzhang.android.rzscan.utils.ScanUtil;

import java.util.Locale;

/**
 * 默認的二維碼掃描Activity
 */
public class CaptureActivity extends AppCompatActivity implements AnalyzeCallback {
    private static final String TAG = CaptureActivity.class.getSimpleName();
    private static final int STATUSBAR_COLOR = Color.argb(255, 0, 0, 0);
    private static final int TOOLBAR_COLOR = Color.argb(0, 0, 0, 0);
    private static final String TOOLBAR_TITLE = "RZZxingScan";

    private String appName;
    private int borderColor = -1, borderWidth = -1, borderLength = -1, descriptionColor = -1, dialogIcon = 0;
    private float descriptionOffSet = -1f, descriptionSize = -1;
    private int[] frameSize;
    private boolean isOpen = false;

    private static final int PERMISSION_REQUEST_CAMERA = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        Bundle bundle = getIntent().getExtras();
        appName = bundle.getString(RZScan.RZSCAN_APP_NAME, "");
        boolean isShowStatusBar = bundle.getBoolean(RZScan.RZSCAN_SHOW_STATUSBAR, true);
        int statusBarColor = bundle.getInt(RZScan.RZSCAN_STATUSBAR_COLOR, STATUSBAR_COLOR);
        int toolBarColor = bundle.getInt(RZScan.RZSCAN_TOOLBAR_COLOR, TOOLBAR_COLOR);
        String toolBarTitle = bundle.getString(RZScan.RZSCAN_TOOLBAR_TITLE, TOOLBAR_TITLE);
        dialogIcon = bundle.getInt(RZScan.RZSCAN_DIALOG_ICON, R.drawable.ic_rzscan_dialog_denied_30_3dp);
        borderColor = bundle.getInt(RZScan.RZSCAN_BORDER_COLOR, -1);
        borderWidth = bundle.getInt(RZScan.RZSCAN_BORDER_WIDTH, -1);
        borderLength = bundle.getInt(RZScan.RZSCAN_BORDER_LENGTH, -1);
        descriptionColor = bundle.getInt(RZScan.RZSCAN_DESCRIPTION_COLOR, -1);
        descriptionOffSet = bundle.getFloat(RZScan.RZSCAN_DESCRIPTION_OFFSET, -1);
        descriptionSize = bundle.getFloat(RZScan.RZSCAN_DESCRIPTION_SIZE, -1);
        frameSize = bundle.getIntArray(RZScan.RZSCAN_FRAME_SIZE);

        if (!isShowStatusBar) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

        if (isShowStatusBar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(statusBarColor);
                window.setNavigationBarColor(Color.BLACK);
            }
        }

        Toolbar mToolBar = (Toolbar) findViewById(R.id.mToolBar);
        mToolBar.setTitle(toolBarTitle);
        mToolBar.setTitleTextColor(Color.WHITE);
        mToolBar.setBackgroundColor(toolBarColor);
        mToolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        mToolBar.getMenu().add(0, 0, 1, "Flashlight")
                .setIcon(R.drawable.ic_flash_off_2_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        mToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 0:
                        isOpen = !isOpen;
                        ScanUtil.isLightEnable(isOpen);
                        item.setIcon(isOpen ? R.drawable.ic_flash_on_2_24dp : R.drawable.ic_flash_off_2_24dp);
                        return true;
                    default:
                        return false;
                }
            }
        });
        requestCamera();
    }

    private void requestCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    // 第一次被使用者拒絕後，這邊做些解釋的動作
                    showDescriptionDialog();
                } else {
                    // 第一次詢問
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                }
            } else {
                startScanCode();
            }
        } else {
            startScanCode();
        }
    }

    private void startScanCode() {
        Bundle bundle = new Bundle();
        bundle.putInt(RZScan.RZSCAN_BORDER_COLOR, borderColor);
        bundle.putInt(RZScan.RZSCAN_BORDER_WIDTH, borderWidth);
        bundle.putInt(RZScan.RZSCAN_BORDER_LENGTH, borderLength);
        bundle.putInt(RZScan.RZSCAN_BORDER_LENGTH, descriptionColor);
        bundle.putFloat(RZScan.RZSCAN_DESCRIPTION_OFFSET, descriptionOffSet);
        bundle.putFloat(RZScan.RZSCAN_DESCRIPTION_SIZE, descriptionSize);
        bundle.putIntArray(RZScan.RZSCAN_FRAME_SIZE, frameSize);
        CaptureFragment CaptureFragment = new CaptureFragment();
        CaptureFragment.setArguments(bundle);
        CaptureFragment.setAnalyzeCallback(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_zxing_container, CaptureFragment).commitAllowingStateLoss();
    }

    private void showDescriptionDialog() {
        String title = getResources().getString(R.string.rzscan_dia_title);
        String msg = getResources().getString(R.string.rzscan_dia_description);
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setIcon(dialogIcon)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.rzscan_dia_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 再一次請求
                        ActivityCompat.requestPermissions(CaptureActivity.this, new String[]{Manifest.permission.CAMERA},
                                PERMISSION_REQUEST_CAMERA);
                    }
                })
                .setNegativeButton(R.string.rzscan_dia_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        CaptureActivity.this.setResult(RESULT_CANCELED);
                        CaptureActivity.this.finish();
                    }
                })
                .show();
    }

    /**
     * 權限拒絕已達2次 or 權限已被永久拒絕
     */
    private void showIsDenideDialog() {
        String title = getResources().getString(R.string.rzscan_dia_title_denied);
        String msg = String.format(Locale.TAIWAN, getResources().getString(R.string.rzscan_dia_description_denied), appName);
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setIcon(dialogIcon)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.rzscan_dia_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        CaptureActivity.this.setResult(RESULT_CANCELED);
                        CaptureActivity.this.finish();
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int permissionResult = grantResults[0];
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA:
                if (permissionResult == PackageManager.PERMISSION_GRANTED) {
                    startScanCode();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        showDescriptionDialog();
                    } else {
                        showIsDenideDialog();
                    }
                }
                break;
        }
    }

    /**
     * 二維碼解析回調函數
     */
    @Override
    public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(ScanUtil.RESULT_TYPE, ScanUtil.RESULT_SUCCESS);
        bundle.putString(ScanUtil.RESULT_STRING, result);
        resultIntent.putExtras(bundle);
        CaptureActivity.this.setResult(RESULT_OK, resultIntent);
        CaptureActivity.this.finish();
    }

    @Override
    public void onAnalyzeFailed() {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(ScanUtil.RESULT_TYPE, ScanUtil.RESULT_FAILED);
        bundle.putString(ScanUtil.RESULT_STRING, "");
        resultIntent.putExtras(bundle);
        CaptureActivity.this.setResult(RESULT_OK, resultIntent);
        CaptureActivity.this.finish();
    }
}
package com.rayzhang.android.rzscan;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.rayzhang.android.rzscan.camera.CameraManager;
import com.rayzhang.android.rzscan.decoding.CaptureActivityHandler;
import com.rayzhang.android.rzscan.decoding.InactivityTimer;
import com.rayzhang.android.rzscan.impl.AnalyzeCallback;
import com.rayzhang.android.rzscan.widget.ViewfinderView;

import java.io.IOException;
import java.util.Vector;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

/**
 * 自定義實現的掃描Fragment
 */
public class CaptureFragment extends Fragment implements SurfaceHolder.Callback {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private SurfaceHolder surfaceHolder;
    private AnalyzeCallback analyzeCallback;
    private Camera camera;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CameraManager.init(getActivity().getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this.getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        int borderColor = bundle.getInt(RZScan.RZSCAN_BORDER_COLOR, -1);
        int borderWidth = bundle.getInt(RZScan.RZSCAN_BORDER_WIDTH, -1);
        int borderLength = bundle.getInt(RZScan.RZSCAN_BORDER_LENGTH, -1);
        int descriptionColor = bundle.getInt(RZScan.RZSCAN_DESCRIPTION_COLOR, -1);
        float descriptionSize = bundle.getFloat(RZScan.RZSCAN_DESCRIPTION_SIZE, -1);
        float descriptionOffSet = bundle.getFloat(RZScan.RZSCAN_DESCRIPTION_OFFSET, -1);
        int[] frameSize = bundle.getIntArray(RZScan.RZSCAN_FRAME_SIZE);

        View view = inflater.inflate(R.layout.fragment_capture, container, false);
        viewfinderView = (ViewfinderView) view.findViewById(R.id.viewfinder_view);
        if (borderColor != -1) {
            viewfinderView.setInnerRectColor(borderColor);
        }
        if (borderWidth != -1) {
            viewfinderView.setInnerRectWidth(borderWidth);
        }
        if (borderLength != -1) {
            viewfinderView.setInnerRectLength(borderLength);
        }
        if (descriptionColor != -1) {
            viewfinderView.setDescriptionColor(descriptionColor);
        }
        if (descriptionSize != -1) {
            viewfinderView.setDescriptionSize(descriptionSize);
        }
        if (descriptionOffSet != -1) {
            viewfinderView.setDescriptionOffSetTop(descriptionOffSet);
        }
        if (frameSize != null) {
            viewfinderView.setScanFrameSize(frameSize[0], frameSize[1]);
        }

        SurfaceView surfaceView = (SurfaceView) view.findViewById(R.id.preview_view);
        surfaceHolder = surfaceView.getHolder();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();

        if (result == null || TextUtils.isEmpty(result.getText())) {
            if (analyzeCallback != null) {
                analyzeCallback.onAnalyzeFailed();
            }
        } else {
            if (analyzeCallback != null) {
                analyzeCallback.onAnalyzeSuccess(barcode, result.getText());
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            camera = CameraManager.get().getCamera();
        } catch (Exception e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet, viewfinderView);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
        if (camera != null) {
            if (CameraManager.get().isPreviewing()) {
                if (!CameraManager.get().isUseOneShotPreviewCallback()) {
                    camera.setPreviewCallback(null);
                }
                camera.stopPreview();
                CameraManager.get().getPreviewCallback().setHandler(null, 0);
                CameraManager.get().getAutoFocusCallback().setHandler(null, 0);
                CameraManager.get().setPreviewing(false);
            }
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    public AnalyzeCallback getAnalyzeCallback() {
        return analyzeCallback;
    }

    public void setAnalyzeCallback(AnalyzeCallback analyzeCallback) {
        this.analyzeCallback = analyzeCallback;
    }
}

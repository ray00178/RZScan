package com.rayzhang.rayzhangscan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.rayzhang.android.rzalbum.RZAlbum;
import com.rayzhang.android.rzscan.RZScan;
import com.rayzhang.android.rzscan.impl.AnalyzeCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ImageView mImgCode1, mImgCode2;
    private TextView mTextView;
    private static final int REQUEST_SCAN = 1111;
    private static final int REQUEST_ALBUM = 1112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.mTextView);
        mImgCode1 = (ImageView) findViewById(R.id.mImgCode1);
        mImgCode2 = (ImageView) findViewById(R.id.mImgCode2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Scan");
        menu.add(0, 1, 1, "Encode");
        menu.add(0, 2, 2, "Decode");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                RZScan.ofAppName(getResources().getString(R.string.app_name))
                        .start(this, REQUEST_SCAN);
                return true;
            case 1:
                // No logo
                mImgCode1.setImageBitmap(RZScan.ENCODE("87217582", 400, 200, null, BarcodeFormat.EAN_8));

                /* Have logo
                 * AZTEC, CODABAR, CODE_39, CODE_93, CODE_128, DATA_MATRIX, EAN_8, EAN_13,
                 * ITF, PDF_417, QR_CODE, UPC_A, UPC_E
                 */
                Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_bar_code_30dp);
                mImgCode2.setImageBitmap(RZScan.ENCODE("87217582", 400, 200, logo, BarcodeFormat.EAN_8));
                return true;
            case 2:
                RZAlbum.ofAppName(getResources().getString(R.string.app_name))
                        .setLimitCount(1)
                        .showCamera(false)
                        .start(this, REQUEST_ALBUM);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SCAN:
                    String result = RZScan.getResult(data);
                    mTextView.setText("Result：" + result);
                    break;
                case REQUEST_ALBUM:
                    List<String> paths = RZAlbum.parseResult(data);
                    RZScan.DECODE(paths.get(0), new AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            mTextView.setText("Result：" + result);
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            mTextView.setText("Result：Sorry! can' decode.");
                        }
                    });
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }
}

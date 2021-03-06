package it.matteolobello.palazzovenezia.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;

import java.io.File;
import java.util.Locale;

import it.matteolobello.palazzovenezia.R;
import it.matteolobello.palazzovenezia.data.asset.AssetFileReader;
import it.matteolobello.palazzovenezia.data.bundle.BundleKeys;
import it.matteolobello.palazzovenezia.data.model.Painting;
import it.matteolobello.palazzovenezia.data.qrcode.QRCodeScanning;
import it.matteolobello.palazzovenezia.util.SystemBarsUtil;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    public static final int REQUEST_CODE = 505;

    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

        SystemBarsUtil.goImmersive(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public void handleResult(Result result) {
        String id = result.getText();

        if (!QRCodeScanning.isQRCodeValid(this, id)) {
            Toast.makeText(getApplicationContext(), R.string.qr_code_not_valid, Toast.LENGTH_SHORT).show();

            return;
        }

        stopCamera();

        Painting painting = new Painting();

        try {
            String language = Locale.getDefault().getLanguage();

            painting.setId(id);

            String descriptionFilePath = getAssets().list(id + "/" + "txt" + "/" + language)[0];
            File descriptionFile = new File(descriptionFilePath);
            String description = AssetFileReader.readAssetsFile(this,
                    id + "/" + "txt" + "/" + language + "/" + descriptionFilePath);

            painting.setName(descriptionFile.getName().replace("_", " "));
            painting.setDescription(description);

            String audioFileName = getAssets().list(id + "/" + "mp3" + "/" + language)[0];
            painting.setAudioPath(id + "/" + "mp3" + "/" + language + "/" + audioFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent();
        intent.putExtra(BundleKeys.EXTRA_PAINTING, painting);
        setResult(Activity.RESULT_OK, intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();

        startCamera();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void startCamera() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mScannerView != null) {
                    try {
                        mScannerView.setResultHandler(QRCodeActivity.this);
                        mScannerView.startCamera();
                    } catch (Exception ignored) {
                    }
                }
            }
        }).start();
    }

    private void stopCamera() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mScannerView != null) {
                    try {
                        mScannerView.stopCamera();
                    } catch (Exception ignored) {
                    }
                }
            }
        }).start();
    }
}

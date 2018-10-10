package com.example.exattraining;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final String TAG = QrScannerActivity.class.getName();
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        scannerView = findViewById(R.id.scanner_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        String code = result.getText();
        String codeFormat = result.getBarcodeFormat().toString();
        String msg = String.format(
                "Code: %s, Format: %s",
                code,
                codeFormat
        );
        Log.i(TAG, msg);

        // If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);
    }
}

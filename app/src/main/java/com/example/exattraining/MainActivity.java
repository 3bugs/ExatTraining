package com.example.exattraining;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String API_BASE_URL = "http://www.promlert.com/2fellows/ExatTraining/api.php/";
    private static final String API_DO_ATTEND_EVENT = API_BASE_URL + "doAttendEvent";

    TextView pointsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pointsTextView = findViewById(R.id.points_text_view);

        Button scanQrButton = findViewById(R.id.scan_qr_button);
        scanQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checkCameraPermission();
                testOkHttp();
            }
        });
    }

    private void testOkHttp() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_DO_ATTEND_EVENT + "?event_code=0001")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();
                Log.i(TAG, responseString);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(responseString);
                            int resultCode = jsonObject.getInt("result_code");
                            String resultText = jsonObject.getString("result_text");
                            if (resultCode == 0) { // success
                                JSONObject resultData = jsonObject.getJSONObject("result_data");
                                int currentPoints = resultData.getInt("current_points");
                                pointsTextView.setText(String.valueOf(currentPoints));

                            } else { // failed
                                String msg = String.format(
                                        Locale.getDefault(),
                                        "%d: %s",
                                        resultCode, resultText
                                );
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Error!")
                                        .setMessage(msg)
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION
            );
        } else { // Permission granted.
            startQrCodeScanner();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    startQrCodeScanner();
                } else {
                    // permission denied

                    String msg = "แอพไม่สามารถทำงานต่อได้ เนื่องจากไม่ได้รับอนุญาตให้เข้าถึงกล้อง";
                    Toast.makeText(
                            MainActivity.this,
                            msg,
                            Toast.LENGTH_LONG
                    ).show();

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Alert!")
                            .setMessage(msg)
                            .setPositiveButton("OK", null)
                            .show();

                }
            }
        }
    }

    private void startQrCodeScanner() {
        Intent intent = new Intent(this, QrScannerActivity.class);
        startActivity(intent);
    }
}

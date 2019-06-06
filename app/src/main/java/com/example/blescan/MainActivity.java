package com.example.blescan;

import android.Manifest;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1313;

    private Button startScanButton;
    private BleScanner scanner;
    private RecyclerView mRecyclerView;
    private ScanAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<ScanResult> scanResultList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        BleClient bleClient = BleClient.getSharedApplication();

        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ScanAdapter(this, scanResultList);

        mRecyclerView = findViewById(R.id.recyclerView);
        startScanButton = findViewById(R.id.startScanButton);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
        }

        mAdapter.setOnItemClickListener(new ScanAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }
        });

        scanner = new BleScanner(scanCallback);
        startScanButton.setOnClickListener(v -> {
//            bleClient.disconnect();
//            bleClient.close();
            scanResultList.clear();
            mAdapter.notifyDataSetChanged();
            scanner.startScan();
            Handler scanStopHandler = new Handler();
            scanStopHandler.postDelayed(() -> scanner.stopScan(), BleScanner.DEFAULT_SCAN_TIME);
        });

        if (ContextCompat.checkSelfPermission(getApplicationContext(), // Проверка
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            askForLocationPermissions();
        } else {
            Log.i("DEBUG_PERMISSSIONS", "Already granted");
        }
    }

    private ScanCallback scanCallback = new ScanCallback() { //Скан колбек
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            for (ScanResult s : scanResultList) {
                if (s.getDevice().getAddress().equals(result.getDevice().getAddress())) {
                    return;
                }
            }

            scanResultList.add(result);
            mAdapter.notifyItemChanged(scanResultList.size() - 1);

            long rxTimestampMillis = System.currentTimeMillis() -
                    SystemClock.elapsedRealtime() +
                    result.getTimestampNanos() / 1000000;
            Date rxDate = new Date(rxTimestampMillis);
            String sDate = new SimpleDateFormat("SSS").format(rxDate);
//            "Scan: " + result.getDevice().getName() + " " + result.getDevice().getAddress() + " " + result.getRssi()
//                    + "  " + result.getDevice().getBluetoothClass() + " " +
//                    result.getDevice().getBluetoothClass().getDeviceClass() + " " +

            Log.i("CNCT", String.valueOf(result.getScanRecord()));
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.i("DEBUG_SCAN", "Error " + errorCode);
        }
    };

    private void askForLocationPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Определение локации")
                    .setMessage("Для корректной работы приложения необходим доступ к данным геолокации")
                    .setPositiveButton("Разрешить", (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS))
                    .setNegativeButton("Не сейчас", (dialog, which) -> {
//                                        //Do nothing
                    })
                    .show();

        } else {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { //Когда уже получили.
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
    }

}
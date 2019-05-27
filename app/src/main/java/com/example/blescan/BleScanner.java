package com.example.blescan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanSettings;

/*package*/ class BleScanner {

    public static final int DEFAULT_SCAN_TIME = 5000;
    private ScanCallback scanCallback;
    private BluetoothLeScanner scanner;

    /*package*/ BleScanner(ScanCallback scanCallback) {
        this.scanCallback = scanCallback;
    }

    /*package*/ void startScan() {
        scanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        scanner.startScan(null, scanSettings, scanCallback);
    }

    /*package*/ void stopScan() {
        if (scanner != null) {
            scanner.stopScan(scanCallback);
        }
    }
}

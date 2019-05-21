package com.example.blescan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

/*package*/ class BleScanner {

    private ScanCallback scanCallback;
    private BluetoothLeScanner scanner;

    /*package*/ BleScanner(ScanCallback scanCallback) {
        this.scanCallback = scanCallback;
    }

    /*package*/ void startScan() {
        scanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();

        ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        scanner.startScan(null, scanSettings, scanCallback); //
    }

    /*package*/ void stopScan() {
        if (scanner != null) {
            scanner.stopScan(scanCallback);
        }
    }
}

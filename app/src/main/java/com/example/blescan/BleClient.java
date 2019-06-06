package com.example.blescan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BleClient {

//    private static final UUID DATA_SERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
//    private static final UUID RX_CHAR = UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb");

    private Context context;
    private BluetoothGatt gattClient;
    private BleStatusInterface bleStatusInterface;

    List<UUID> characteristicUUIDsList = new ArrayList<>();
    ArrayList<String> servch = new ArrayList<>();

//    private BleClient(BleStatusInterface bleStatusInterface) {
//        this.bleStatusInterface = bleStatusInterface;
//    }
//    private static BleClient _app;
//    public static BleClient getSharedApplication(BleStatusInterface bleStatusInterface)
//    {
//        if (_app == null)
//            _app = new BleClient(bleStatusInterface);
//        return _app;
//    }

    public BleClient(Context context, BleStatusInterface bleStatusInterface) {
        this.context = context;
        this.bleStatusInterface = bleStatusInterface;
    }

//    public void read(BluetoothGattCharacteristic CharUUID){
//        gattClient.readCharacteristic(CharUUID);
//    }

//    public void close(){
//        if (gattClient!=null)
//        gattClient.close();
//    }

    public void connect(String adress) {
        Log.i("CNCT", "Connect " + adress);
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(adress);
        gattClient = device.connectGatt(context, false, gattCallback);
    }


    public void disconnect() {
        if (gattClient != null)
            gattClient.disconnect();
    }

    public BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            gattClient = gatt;

            Log.i("CNCT", "Status " + status + " " + newState);

            if (newState == BluetoothGatt.STATE_CONNECTED) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    gatt.discoverServices();

                    bleStatusInterface.connectStatus("Connected");
                    Log.i("CNCT", "Connected " + status);

                } else {


                    Log.i("CNCT", "Connected " + status);

                }

            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                gatt.close();

                Log.i("CNCT", "Disconnected " + status);
                bleStatusInterface.connectStatus("Disconnected");
                servch.clear();
                characteristicUUIDsList.clear();

            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            gattClient = gatt;
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> servicesList = gatt.getServices();
                for (int i = 0; i < servicesList.size(); i++) {
                    BluetoothGattService bluetoothGattService = servicesList.get(i);
                    List<BluetoothGattCharacteristic> bluetoothGattCharacteristicList = bluetoothGattService.getCharacteristics();

                    servch.add("*Service " + bluetoothGattService.getUuid().toString() + " *Characteristics");

                    for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattCharacteristicList) {
                        characteristicUUIDsList.add(bluetoothGattCharacteristic.getUuid());

                        servch.add(String.valueOf(bluetoothGattCharacteristic.getUuid()));

                    }
                    for (int j = 0; j < characteristicUUIDsList.size(); j++) {
                        BluetoothGattCharacteristic rxChar;
                        rxChar = bluetoothGattService.getCharacteristic(UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb"));
                        if (rxChar != null) {
                            gattClient.readCharacteristic(rxChar);
//                            gattClient.disconnect();
                        }
                    }
                }
            }

            for (int k = 0; k < characteristicUUIDsList.size(); k++) {
                Log.i("characteristicUUIDsList", String.valueOf(characteristicUUIDsList.get(k)) + " " + characteristicUUIDsList.size());
            }

            String listString = "";
            for (String s : servch) {
                listString += s + " ";
            }
            Log.i("CNCT", listString.replace(" ", "\n"));
            if (listString != "") {
                bleStatusInterface.connectStatus(listString.replace(" ", "\n")
                        .replace("*Service", "* Service")
                        .replace("*Characteristics", "* Characteristics"));
            }

//            BluetoothGattCharacteristic txChar;
//            BluetoothGattCharacteristic rxChar;
//            if (dataService != null) {
//                txChar = dataService.getCharacteristic(TX_CHAR);//
//                gatt.writeCharacteristic(txChar);
//                rxChar = dataService.getCharacteristic(RX_CHAR); //Запрос
//                gattClient.readCharacteristic(rxChar); // Получаем характеристики

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) { // Что делаем с данными characteristic
            gattClient = gatt;
            byte[] value = characteristic.getValue();
            StringBuilder sb = new StringBuilder();
            for (byte b : value) {
                sb.append(String.format("%02X", b));
            }

            Log.i("CNCT", status + "11 " + characteristic.getValue() + " " + sb + " " + value.length + " " + characteristic.getUuid());

            bleStatusInterface.connectStatus(characteristic.getUuid() + ": " + sb);
        }


        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            gattClient = gatt;
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("CNCT", "success ");
//                gatt.disconnect();
            } else {
                Log.i("CNCT", "fail ");
            }
        }
    };

    public List<BluetoothDevice> getConnectedDevice() {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        List<BluetoothDevice> devices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
        return devices;
    }
}

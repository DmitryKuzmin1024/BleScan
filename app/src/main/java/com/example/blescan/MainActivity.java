package com.example.blescan;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    boolean rrr = false;

    boolean isUSD = false;



    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1313;

    private Button startScanButton;

    Set<String> scanResultList = new HashSet<>();
    ArrayList<ExampleItem> exampleList = new ArrayList<>();

    private BleScanner scanner;

    BleClient bleClient;

//*Ресайкл

    private RecyclerView mRecyclerView;
    private ExampleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public TextView textView3;

    public static Button mButton1;
    public static Button mButton2;
    public static LinearLayout linearLayout1;

//*Ресайкл

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bleClient = new BleClient(getApplicationContext(), bleStatusInterface);

        //*Ресайкл

        mRecyclerView = findViewById(R.id.recyclerView);
        //mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter(exampleList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);





        mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int position) {

                exampleList.get(position);

                textView3 = mRecyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.textview3);


                mButton1 = mRecyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.button1);
                mButton2 = mRecyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.button2);

                linearLayout1 = mRecyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.linearLayout1);






                mButton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        bleClient.connect(String.valueOf(scanResultList.toArray()[position])
                                .split(";")[1]
                                .replace("Address: ","")
                                .replaceAll(" ",""));


//                        if (!isUSD) {
//                            bleClient.connect(String.valueOf(
//                                    scanResultList.toArray()[position]).split(";")[1].replace("Address: ","").replaceAll(" ",""));
//                            mButton1.setText("CONNECTING");
//                            if (rrr = true){
//                                mButton1.setText("DISCONNECT");
//                            }
//                            isUSD = true;
//                        } else {
//                            bleClient.disconnect();
//                            mButton1.setText("CONNECT");
//                            isUSD = false;
//                        }

//                        final Handler handler1 = new Handler(); // Таймер
//                        handler1.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                List<BluetoothDevice> sas = bleClient.getConnectedDevice();
//
//                                Log.i("sas", String.valueOf(sas.get(0)));
//
//                            }
//                        }, 6000);

                    }
                });

                mButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bleClient.disconnect();
                    }
                });

                if (textView3.getVisibility() == View.GONE) {
                    textView3.setVisibility(View.VISIBLE);
                  } else {
                    textView3.setVisibility(View.GONE);
                }

                if (linearLayout1.getVisibility() == View.GONE) {
                    linearLayout1.setVisibility(View.VISIBLE);
                } else {
                    linearLayout1.setVisibility(View.GONE);
                }
            }
        });



        startScanButton = findViewById(R.id.startScanButton);

        startScanButton.setOnClickListener(new View.OnClickListener() {  //startScanButton

            @Override
            public void onClick(View v) {

                scanResultList.clear(); // Очищаю лист
                exampleList.clear();
                mRecyclerView.removeAllViewsInLayout();
                mAdapter.notifyDataSetChanged();// Очищаю и обновляю ресайкл
                scanner.startScan();

                Toast toast = Toast.makeText(getApplicationContext(), // Toast
                        "5 seconds...", Toast.LENGTH_SHORT);
                toast.show();

                final Handler handler = new Handler(); // Таймер
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        scanner.stopScan(); // Останавливаю скан

                        //*Ресайкл

                        for (int i = 0; i < scanResultList.size(); i++){
                           exampleList.add(new ExampleItem(

                                   String.valueOf(scanResultList.toArray()[i]).split(";")[0],

                                   String.valueOf(scanResultList.toArray()[i]).split(";")[1],

                                   String.valueOf(
                                           scanResultList.toArray()[i]).split(";")[2]
                                           .replace("0","DEVICE_TYPE_UNKNOWN")
                                           .replace("1","DEVICE_TYPE_CLASSIC")
                                           .replace("2","DEVICE_TYPE_LE")
                                           .replace("3","DEVICE_TYPE_DUAL")
                                           + "\n" +String.valueOf(scanResultList.toArray()[i]).split(";")[3]

                                   ));
                        } // Добавляю в ресайкл мой лист

                        mAdapter.notifyDataSetChanged(); // Снова обновляю

                        //*Ресайкл

                        Log.i("CNCT", String.valueOf(scanResultList));

                    }
                }, 5000);

                final Handler handler1 = new Handler(); // Таймер
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < exampleList.size(); i++){
                            Log.i("ScanResult312", String.valueOf(exampleList.get(i).getmText1()));

                        }

                    }
                }, 6000);
            }
        });

        scanner = new BleScanner(scanCallback);

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

    public void startfff(){

    }

    private BleStatusInterface bleStatusInterface = new BleStatusInterface() { //Интерфейс для сообщения со статусом.
        @Override
        public void connectStatus(String s) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {



                    if (s.equals("GATT_SUCCESS")||s.equals("Disconnected")) {

                        List<BluetoothDevice> sas = bleClient.getConnectedDevice();
                        for (int i = 0; i < exampleList.size(); i++) {
                            String a1 = exampleList.get(i).getmText2().split(" ")[1];
                            String a11 = exampleList.get(i).getmText2();
                            Log.i("a11",a1+" "+a11);

                            if (sas.size() != 0) {
                                for (int j = 0; j < sas.size(); j++) {
                                    String a2 = String.valueOf(sas.get(j));
                                    Log.i("a11",a1+" "+a11+" "+a2);

                                    if (a1.equals(a2)) {
                                        Log.i("helen", exampleList.get(i).getmText2() + sas.get(j) + "true");
                                        exampleList.get(i).setmText2();
                                        mAdapter.notifyItemChanged(i);
                                    }else if ((!a1.equals(a2))&& (a11.replaceAll(" Connected","").length()<a11.length())){
                                        exampleList.get(i).clearText2();
                                        mAdapter.notifyItemChanged(i);
                                    }
                                }
                            }else if ((sas.size() == 0) && (a11.replaceAll(" Connected","").length()<a11.length())){
                                exampleList.get(i).clearText2();
                                mAdapter.notifyItemChanged(i);
                            }
                        }
                    }





                    if (s.equals("GATT_SUCCESS")){
                        rrr = true;
                    }

                    if (s.equals("Disconnected")){
                        rrr = false;
                    }

                    Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private ScanCallback scanCallback = new ScanCallback() { //Скан калбек
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (result.getDevice().getName()==null) {
                scanResultList.add("No name device" + ";Address: " + result.getDevice().getAddress() + ";Device type: "+ result.getDevice().getType()+ ";Hash code: "+ result.getDevice().hashCode());// добавляю в лист
            } else {
                scanResultList.add("Name: " + result.getDevice().getName() + ";Address: " + result.getDevice().getAddress()+ ";Device type: " + result.getDevice().getType()+ ";Hash code: "+ result.getDevice().hashCode());
            }

            Log.i("SCANING", String.valueOf(scanResultList));

            if (result.getDevice().getName()==null) {
                Log.i("Debag_", "Null");
            } else {
                Log.i("Debag_", String.valueOf(result.getDevice().getName()));
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.i("DEBUG_SCAN", "Error " + errorCode);
        }
    };

    private void askForLocationPermissions() { // Запрос

        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("Определение локации")
                    .setMessage("Для корректной работы приложения необходим доступ к данным геолокации")
                    .setPositiveButton("Разрешить", (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS))
                    .setNegativeButton("Не сейчас", (dialog, which) -> {
//                                        //Do nothing
                    })
                    .show();

            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { //Когда уже получили.
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    bleClient = new BleClient(getApplicationContext());
                   // scanner.startScan();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }

    }
}
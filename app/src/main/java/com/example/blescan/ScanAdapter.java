package com.example.blescan;

import android.app.Activity;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ScanAdapter extends RecyclerView.Adapter<ScanAdapter.ExampleViewHolder> {

    private OnItemClickListener mListener;
    private List<ScanResult> scanSet;
    private Context context;
    boolean isUSD = false;





    public ScanAdapter(Activity context, List<ScanResult> results) {
        scanSet = results;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView1;
        TextView mTextView2;
        TextView mTextView3;



//        public static Button mButton1;

        public ExampleViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);

            Button connectBtn = itemView.findViewById(R.id.button1);
            mTextView1 = itemView.findViewById(R.id.textview1);
            mTextView2 = itemView.findViewById(R.id.textview2);
            mTextView3 = itemView.findViewById(R.id.textview3);

            BleStatusInterface bleStatusInterface = new BleStatusInterface() {
                @Override
                public void connectStatus(String s) {

                    if (s.equals("GATT_SUCCESS")) {
                        ( (MainActivity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                connectBtn.setText("Connected");
                            }
                        });


                    }
                    if (s.equals("Disconnected")){
                        ( (MainActivity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                connectBtn.setText("CONNECT");
                            }
                        });

                    }

                }
            };

//            mButton1 = itemView.findViewById(R.id.button1);

            connectBtn.setOnClickListener(v -> {
//                ScanResult result = ScanAdapter.this.scanSet.get(getAdapterPosition());
                BleClient bleClient = new BleClient(context, bleStatusInterface);
                bleClient.connect(ScanAdapter.this.scanSet.get(getAdapterPosition()).getDevice().getAddress());

            });

            itemView.setOnClickListener(v -> {
                if (connectBtn.getVisibility() == View.GONE)
                    connectBtn.setVisibility(View.VISIBLE);
                else if (connectBtn.getVisibility() == View.VISIBLE)
                    connectBtn.setVisibility(View.GONE);

            });

        }

    }


    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.example_item, viewGroup, false);
        return new ExampleViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int i) {
        ScanResult result = scanSet.get(i);
        holder.mTextView1.setText(result.getDevice().getName() + "\n" + result.getDevice().getAddress());
//        ExampleItem currentItem = scanSet.get(i);
////        ExampleViewHolder.mImageView.setImageResource(currentItem.getmImageResource());
//        ExampleViewHolder.mTextView1.setText(currentItem.getmText1());
//        ExampleViewHolder.mTextView2.setText(currentItem.getmText2());
//        ExampleViewHolder.mTextView3.setText(currentItem.getmText3());
    }

    @Override
    public int getItemCount() {
        return scanSet.size();
    }


}

package com.example.blescan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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

        TextView mNameText;
        TextView mDbmView;
        TextView mAddressText;
        TextView mCharText;
        TextView mTypeText;
        Button connectBtn;
        LinearLayout infoLayout1;
        //        MaterialCardView mCards;
        TableLayout tablelayout;

        public ExampleViewHolder(View itemView, OnItemClickListener listener) {

            super(itemView);

            connectBtn = itemView.findViewById(R.id.button1);
            mNameText = itemView.findViewById(R.id.nameTextView);
            mDbmView = itemView.findViewById(R.id.DbmView);
            mAddressText = itemView.findViewById(R.id.addressTextView);
            mCharText = itemView.findViewById(R.id.charTextView);
            mTypeText = itemView.findViewById(R.id.typeTextView);
            infoLayout1 = itemView.findViewById(R.id.infoLayout);
//            mCards = itemView.findViewById(R.id.mCards);
            tablelayout = itemView.findViewById(R.id.tablelayout);

            BleStatusInterface bleStatusInterface = new BleStatusInterface() {
                @Override
                public void connectStatus(String s) {
                    if (s.contains(" ms")) {
                        Log.i("msms", s);
                    }
                    if (s.equals("GATT_SUCCESS") || (s.equals("Disconnected"))) {
                        ((MainActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, s, Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                    if (s.contains("Characteristics")) {
                        ((MainActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCharText.setText(s.substring(0, s.length() - 1));
                                mCharText.setVisibility(View.VISIBLE);

//                                String[]sub = s.split("\n");
//                                TableRow  tv[] = new TableRow[s.length()];
//                                for (int i = 0; i < sub.length; i++) {
//                                    tv[i] = new TableRow (context);
//                                    tv[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                                            ViewGroup.LayoutParams.WRAP_CONTENT));
//                                    TextView textView = new TextView(context);
//                                    textView.setText(sub[i]);
//                                    tv[i].addView(textView);
//                                    tablelayout.addView(tv[i]);
//                                }

                            }
                        });
                    }
                    if (s.equals("GATT_SUCCESS")) {
                        ((MainActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                connectBtn.setText("Disconnect");
                                mAddressText.setText(mAddressText.getText() + " - Connected");
                                mAddressText.setTextColor(Color.parseColor("#b541f4"));
                                isUSD = true;
                            }
                        });
                    }
                    if (s.equals("Disconnected")) {
                        ((MainActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                connectBtn.setText("CONNECT");
                                mAddressText.setText(mAddressText.getText().toString().replace(" - Connected", ""));
                                mAddressText.setTextColor(Color.BLACK);
                                isUSD = false;
                                mCharText.setText("");
                                mCharText.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            };

            BleClient bleClient = new BleClient(context, bleStatusInterface);

//            BleClient bleClient = BleClient.getSharedApplication(bleStatusInterface);

            connectBtn.setOnClickListener(v -> {
                if (!isUSD) {
                    bleClient.connect(ScanAdapter.this.scanSet.get(getAdapterPosition()).getDevice().getAddress());
                    connectBtn.setText("Connecting");
                }
                if (isUSD) {
                    bleClient.disconnect();
                }
            });

//            StateListAnimator stateListAnimator = AnimatorInflater
//                    .loadStateListAnimator(context, R.drawable.animat);
//            mCards.setStateListAnimator(stateListAnimator);
//            mCards.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    v.setClickable(true);
//
//                }
//            });

            itemView.setOnClickListener(v -> {

                if (infoLayout1.getVisibility() == View.GONE) {
                    v.animate()
                            .translationZ(8)
                            .setDuration(350)
                            .setInterpolator(new FastOutSlowInInterpolator())
                            .start();
                    infoLayout1.setVisibility(View.VISIBLE);
                } else if (infoLayout1.getVisibility() == View.VISIBLE) {
                    infoLayout1.setVisibility(View.GONE);
                    v.animate()
                            .translationZ(1)
                            .setDuration(350)
                            .setInterpolator(new FastOutSlowInInterpolator())
                            .start();
                }

            });

        }

    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.example_item, viewGroup, false);
        return new ExampleViewHolder(v, mListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int i) {
        ScanResult result = scanSet.get(i);
        holder.mNameText.setText((result.getDevice().getName() == null) ? "NO NAME" : "Name " + result.getDevice().getName());
        holder.mDbmView.setText(String.valueOf(result.getRssi()) + " dBm");
        holder.mAddressText.setText("Adress " + result.getDevice().getAddress());
        holder.mTypeText.setText("Device type " + ((result.getDevice().getType() == 0) ? "unknown" :
                (result.getDevice().getType() == 1) ? "classic" :
                        (result.getDevice().getType() == 2) ? "LE-only" :
                                (result.getDevice().getType() == 3) ? "dual" :
                                        "--"));
    }

    @Override
    public int getItemCount() {
        return scanSet.size();
    }

}

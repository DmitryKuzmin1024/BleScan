package com.example.blescan;

import android.app.Application;
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

public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    private ArrayList<ExampleItem> mExampleList;



    public static class ExampleViewHolder extends RecyclerView.ViewHolder {

        public static TextView mTextView1;
        public static TextView mTextView2;
        public static TextView mTextView3;


//        public static Button mButton1;

        public ExampleViewHolder(View itemView, OnItemClickListener listener){
            super(itemView);

            mTextView1 = itemView.findViewById(R.id.textview1);
            mTextView2 = itemView.findViewById(R.id.textview2);
            mTextView3 = itemView.findViewById(R.id.textview3);

//            mButton1 = itemView.findViewById(R.id.button1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }

    }

    public ExampleAdapter(ArrayList<ExampleItem> exampleList){
        mExampleList = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.example_item, viewGroup, false);
        return new ExampleViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder exampleViewHolder, int i) {
        ExampleItem currentItem = mExampleList.get(i);
//        ExampleViewHolder.mImageView.setImageResource(currentItem.getmImageResource());
        ExampleViewHolder.mTextView1.setText(currentItem.getmText1());
        ExampleViewHolder.mTextView2.setText(currentItem.getmText2());
        ExampleViewHolder.mTextView3.setText(currentItem.getmText3());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }




}

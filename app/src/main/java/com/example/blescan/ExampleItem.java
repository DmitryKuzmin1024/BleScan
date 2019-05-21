package com.example.blescan;

import android.widget.TextView;

public class ExampleItem {

    private String mText1;
    private String mText2;
    private String mText3;

    public ExampleItem(String text1, String text2, String text3){

//        mImageResource = imageResource;

        mText1 = text1;
        mText2 = text2;
        mText3 = text3;

    }

   // public void changeInfo

    public String getmText1(){
        return mText1;
    }
    public String getmText2(){
        return mText2;
    }
    public String getmText3(){
        return mText3;
    }

    public void setmText2(){
        mText2 = mText2.replaceAll(" Connected","");
        mText2 = mText2+" Connected";
    }

    public void clearText2(){
        mText2 = mText2.replaceAll(" Connected","");
    }



}

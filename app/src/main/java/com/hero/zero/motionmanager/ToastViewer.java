package com.hero.zero.motionmanager;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by shadk on 06.12.2015.
 */
public class ToastViewer implements Runnable {
    private Context mContext;
    private String mText;
    private int mToastTimeout;

    public ToastViewer(Context context, String text, int lengthShort) {
        mText = text;
        mContext = context;
        mToastTimeout = lengthShort;
    }


    @Override
    public void run() {
        Toast.makeText(mContext, mText, mToastTimeout).show();
    }

}

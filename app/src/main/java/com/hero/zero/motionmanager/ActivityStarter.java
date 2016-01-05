package com.hero.zero.motionmanager;

/**
 * Created by shadk on 06.12.2015.
 */

import android.content.Context;
import android.content.Intent;

public class ActivityStarter implements Runnable{
    private Motion mFirstMotion = null;
    private Motion mSecondMotion  = null;
    private Context mContext = null;

    public ActivityStarter(Context context, Motion first, Motion second) {
        mFirstMotion = first;
        mSecondMotion = second;
        mContext = context;
    }

    public void startCompareActivity() {
        if (mFirstMotion != null && mSecondMotion != null) {
            Intent intent = new Intent(mContext, CompareActivity.class);
            intent.putExtra(Common.EXTRA_FIRST_COMPARING_MOTION, mFirstMotion);
            intent.putExtra(Common.EXTRA_SECOND_COMPARING_MOTION, mSecondMotion);
            mContext.startActivity(intent);
        }
    }

    @Override
    public void run() {
        startCompareActivity();
    }
}

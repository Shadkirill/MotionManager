package com.hero.zero.motionmanager;

import java.util.ArrayList;

/**
 * Created by shadk on 06.12.2015.
 */
public class MotionRecognizer implements Runnable {
    private ArrayList<Motion> mMotions = null;
    private Motion mMotionToRecognize = null;
    private INotifiable mNotifiable = null;
    private IActivityStartable mActivityStartable = null;

    public MotionRecognizer(ArrayList<Motion> motions, Motion motionToRecognize, INotifiable notifiable, IActivityStartable activityStartable) {
        mMotions = motions;
        mMotionToRecognize = motionToRecognize;
        mNotifiable = notifiable;
        mActivityStartable = activityStartable;
    }

    private void recognizeMotion() {
        mNotifiable.showText("Recognizing..." + mMotionToRecognize.size());
        double minDTW = Double.MAX_VALUE;
        Motion minDTWMotion = null;
        for (int i = 0; i < mMotions.size(); ++i) {
            double DTW  = DWTAlgorithm.findDistance(mMotionToRecognize, mMotions.get(i));
            if (DTW < minDTW) {
                minDTW = DTW;
                minDTWMotion = mMotions.get(i);
            }
        }
        if (minDTWMotion != null) {
            mNotifiable .showText("Recognized! Your motion is " + minDTWMotion);
            mActivityStartable.startActivity(mMotionToRecognize, minDTWMotion);
        }
    }

    @Override
    public void run() {
        recognizeMotion();
    }
}

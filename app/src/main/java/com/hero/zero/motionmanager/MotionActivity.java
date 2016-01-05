package com.hero.zero.motionmanager;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by shadk on 06.12.2015.
 */
public class MotionActivity extends Activity {
    private static final int GRAPHIC_TYPE_X = R.id.graph_x;
    private static final int GRAPHIC_TYPE_Y = R.id.graph_y;
    private static final int GRAPHIC_TYPE_Z = R.id.graph_z;

    private Motion mMotion = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.motion_activity);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mMotion =  this.getIntent().getParcelableExtra(Common.EXTRA_MOTION);
        setupGraphic(GRAPHIC_TYPE_X);
        setupGraphic(GRAPHIC_TYPE_Y);
        setupGraphic(GRAPHIC_TYPE_Z);
    }


    private void setupGraphic(int graphicType) {
        GraphView graph = (GraphView) findViewById(graphicType);
        DataPoint[] dataPoints = new DataPoint[mMotion.size()];
        long time = 0;
        mMotion.begin();
        do {
            try {
                switch (graphicType) {
                    case R.id.graph_x: {
                        dataPoints[mMotion.getPosition()] = new DataPoint(time += 10, mMotion.getXCord());
                        break;
                    }
                    case R.id.graph_y: {
                        dataPoints[mMotion.getPosition()] = new DataPoint(time += 10, mMotion.getYCord());
                        break;
                    }
                    case R.id.graph_z: {
                        dataPoints[mMotion.getPosition()] = new DataPoint(time += 10, mMotion.getZCord());
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (mMotion.next());
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
        graph.addSeries(series);
    }
}


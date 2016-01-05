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
public class CompareActivity extends Activity {
    private static final int GRAPHIC_TYPE_X_COMPARE = R.id.graph_compare_x;
    private static final int GRAPHIC_TYPE_Y_COMPARE = R.id.graph_compare_y;
    private static final int GRAPHIC_TYPE_Z_COMPARE = R.id.graph_compare_z;

    private Motion mFirstMotion = null;
    private Motion mSecondMotion = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.comparable_activity);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mFirstMotion =  this.getIntent().getParcelableExtra(Common.EXTRA_FIRST_COMPARING_MOTION);
        mSecondMotion =  this.getIntent().getParcelableExtra(Common.EXTRA_SECOND_COMPARING_MOTION);
        setupGraphic(GRAPHIC_TYPE_X_COMPARE);
        setupGraphic(GRAPHIC_TYPE_Y_COMPARE);
        setupGraphic(GRAPHIC_TYPE_Z_COMPARE);
    }


    private void setupGraphic(int graphicType) {
        GraphView graph = (GraphView) findViewById(graphicType);
        int size = Math.min(mFirstMotion.size(), mSecondMotion.size());
        DataPoint[] firstMotionDataPoints = new DataPoint[size];
        DataPoint[] secondMotionDataPoints = new DataPoint[size];
        long time = 0;
        mFirstMotion.begin();
        mSecondMotion.begin();
        do {
            try {
                switch (graphicType) {
                    case R.id.graph_compare_x: {
                        firstMotionDataPoints[mFirstMotion.getPosition()] = new DataPoint(time += 10, mFirstMotion.getXCord());
                        secondMotionDataPoints[mSecondMotion.getPosition()] = new DataPoint(time += 10, mSecondMotion.getXCord());
                        break;
                    }
                    case R.id.graph_compare_y: {
                        firstMotionDataPoints[mFirstMotion.getPosition()] = new DataPoint(time += 10, mFirstMotion.getYCord());
                        secondMotionDataPoints[mSecondMotion.getPosition()] = new DataPoint(time += 10, mSecondMotion.getYCord());
                        break;
                    }
                    case R.id.graph_compare_z: {
                        firstMotionDataPoints[mFirstMotion.getPosition()] = new DataPoint(time += 10, mFirstMotion.getZCord());
                        secondMotionDataPoints[mSecondMotion.getPosition()] = new DataPoint(time += 10, mSecondMotion.getZCord());
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            time += 10;
        } while (mFirstMotion.next() && mSecondMotion.next());
        LineGraphSeries<DataPoint> firstMotionGraphSeries = new LineGraphSeries<DataPoint>(firstMotionDataPoints);
        LineGraphSeries<DataPoint> secondMotionGraphSeries = new LineGraphSeries<DataPoint>(secondMotionDataPoints);

        graph.addSeries(firstMotionGraphSeries);
        graph.addSeries(secondMotionGraphSeries);
        String graphName = null;
        switch (graphicType) {
            case R.id.graph_compare_x: {
                graphName = new String("X_AXIS");
                break;
            }
            case R.id.graph_compare_y: {
                graphName = new String("Y_AXIS");
                break;
            }
            case R.id.graph_compare_z: {
                graphName = new String("Z_AXIS");
                break;
            }
        }
        graph.setTitle(graphName);
    }

}

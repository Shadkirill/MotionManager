package com.hero.zero.motionmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by shadk on 06.12.2015.
 */
public class MainActivity extends Activity implements SensorEventListener, INotifiable, IActivityStartable {

    private static final String PREFS_MOTIONS_SIZE = "PREFS_MOTIONS_SIZE";
    private static final String PREFS_MOTION = "PREFS_MOTION";
    private Button mRecordNewMotionButton = null;
    private TextView mXAngleTextView = null;
    private TextView mYAngleTextView = null;
    private TextView mZAngleTextView = null;
    private ListView mMotionsListView = null;

    private Gson mGson = null;


    private int mSelectedListItemPosition = -1;

    private ArrayAdapter mListAdapter = null;

    private float mLastXCord = 0;
    private float mLastYCord = 0;
    private float mLastZCord = 0;

    private Motion mCurrMotion = null;
    private ArrayList mMotions = null;

    private static SensorManager mSensorManager = null;
    private static Sensor mAccelerometerSensor = null;

    private Timer mTimer = null;
    private TimerTask mTimerTask = null;

    private ExecutorService mThreadPool = null;

    private boolean isRecording = false;

    private void startRecording() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mCurrMotion = new Motion();
        // re-schedule timer here
        // otherwise, IllegalStateException of
        // "TimerTask is scheduled already"
        // will be thrown
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mCurrMotion.addPoint(mLastXCord, mLastYCord, mLastZCord);
            }
        };
        mTimer.schedule(mTimerTask, 0, Common.SENSOR_CHECK_DELAY);
        isRecording = true;

        showText("Recording");
    }

    private void stopRecording() {
        isRecording = false;
        if (mTimer != null) {
            mTimer.cancel();
        }
        mXAngleTextView.setText("X");
        mYAngleTextView.setText("Y");
        mZAngleTextView.setText("Z");
        showSaveDialog();
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter the name of new motion");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mCurrMotion != null) {

                    mCurrMotion.setName(input.getText().toString());

                    mMotions.add(mCurrMotion);
                    mListAdapter.notifyDataSetChanged();
                }
            }
        });

        builder.setNeutralButton("Recognize", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mCurrMotion != null) {
                    MotionRecognizer recognizer = new MotionRecognizer(mMotions, mCurrMotion, MainActivity.this, MainActivity.this);
                    if (mThreadPool != null) {
                        mThreadPool.submit(recognizer);
                    }
                    mCurrMotion = null;
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCurrMotion = null;
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void setupUI() {
        setContentView(R.layout.motion_list_activity);
        mXAngleTextView = (TextView)findViewById(R.id.text_view_x_angle);
        mYAngleTextView = (TextView)findViewById(R.id.text_view_y_angle);
        mZAngleTextView = (TextView)findViewById(R.id.text_view_z_angle);


        mListAdapter = new ArrayAdapter<Motion>(this, android.R.layout.simple_list_item_1, mMotions);
        mMotionsListView = ((ListView)findViewById(R.id.list_view_motions));
        mMotionsListView.setAdapter(mListAdapter);
        registerForContextMenu(mMotionsListView);

        mRecordNewMotionButton = (Button)findViewById(R.id.button_record_new_motion);
        mRecordNewMotionButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        startRecording();
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        stopRecording();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void setupAccelerometer() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); // Получаем менеджер сенсоров
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // Получаем датчик положения
        mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMotions = new ArrayList <Motion> ();
        loadFromPreferences();
        mThreadPool = Executors.newCachedThreadPool();


        setupUI();
        setupAccelerometer();
    }

    @Override
    protected void onPause() {
        savePrefereces();
        super.onPause();
    }

    private void savePrefereces() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs == null) {
            return;
        }
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putInt(PREFS_MOTIONS_SIZE, mMotions.size());
        for (int i = 0; i < mMotions.size(); ++i) {
            Gson gson = new Gson();
            String json = gson.toJson(mMotions.get(i));
            prefsEditor.putString(PREFS_MOTION + String.valueOf(i), json);
        }

        prefsEditor.commit();
    }

    private void loadFromPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs == null) {
            return;
        }
        int motionsSize = prefs.getInt(PREFS_MOTIONS_SIZE, -1);
        if (motionsSize == -1) {
            return;
        }
        for (int i = 0; i < motionsSize; ++i) {
            if (mGson == null) {
                mGson = new Gson();
            }
            Motion newMotion = mGson.fromJson(prefs.getString(PREFS_MOTION + String.valueOf(i), null), Motion.class);
            if (newMotion != null) {
                mMotions.add(newMotion);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (isRecording) {
            mLastXCord = event.values[0]; //x
            mLastYCord = event.values[1]; //y
            mLastZCord = event.values[2]; //z

            mXAngleTextView.setText(String.valueOf(event.values[0]));
            mYAngleTextView.setText(String.valueOf(event.values[1]));
            mZAngleTextView.setText(String.valueOf(event.values[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void showText(String text) {
        runOnUiThread(new ToastViewer(this, text, Toast.LENGTH_SHORT));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Show");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        mSelectedListItemPosition = info.position;
        if(item.getTitle() == "Show") {
            Intent intent = new Intent(this, MotionActivity.class);
            intent.putExtra(Common.EXTRA_MOTION, (Motion) mMotions.get(mSelectedListItemPosition));
            startActivity(intent);
        }
        if(item.getTitle() == "Delete") {
            mMotions.remove(mSelectedListItemPosition);
            mListAdapter.notifyDataSetChanged();
        }
        return true;
    }


    @Override
    public void startActivity(Motion firstMotion, Motion secondMotion) {
        runOnUiThread(new ActivityStarter(this, firstMotion, secondMotion ));
    }
}

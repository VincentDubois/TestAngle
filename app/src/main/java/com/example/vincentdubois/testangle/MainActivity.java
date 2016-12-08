package com.example.vincentdubois.testangle;


// Source : http://stackoverflow.com/questions/20339942/get-device-angle-by-using-getorientation-function
// http://www.techrepublic.com/article/pro-tip-use-android-sensors-to-detect-orientation-changes/

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    Sensor mRotationSensor;

    float[] orientation = new float[3];

    private static final int SENSOR_DELAY = 500 * 1000; // 500ms
    private static final int FROM_RADS_TO_DEGS = -57;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            mSensorManager = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
            mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY);
        } catch (Exception e) {
            Toast.makeText(this, "Hardware compatibility issue", Toast.LENGTH_LONG).show();
        }

        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
                .getDefaultDisplay();

        int orientation = display.getRotation();

        if (orientation == Surface.ROTATION_180) {
            Toast.makeText(this, "180", Toast.LENGTH_LONG).show();
        }


    }

    protected void onResume() {
        super.onResume();
//        mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mRotationSensor) {
            if (event.values.length > 4) {
                float[] truncatedRotationVector = new float[4];
                System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
                update(truncatedRotationVector);
            } else {
                update(event.values);
            }
        }
    }

    private void update(float[] vectors) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
        int worldAxisX = SensorManager.AXIS_X;
        int worldAxisZ = SensorManager.AXIS_Z;
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);

        update();

    }


    private void update() {
        ((TextView)findViewById(R.id.textViewX)).setText("X "+(orientation[0]* FROM_RADS_TO_DEGS));
        ((TextView)findViewById(R.id.textViewY)).setText("Y "+(orientation[1]* FROM_RADS_TO_DEGS));
        ((TextView)findViewById(R.id.textViewZ)).setText("Z "+(orientation[2]* FROM_RADS_TO_DEGS));
    }
}

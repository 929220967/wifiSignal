package com.example.wifisignal2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class SensorText extends AppCompatActivity implements SensorEventListener{
private SensorManager sensorManager;
    private Sensor accelSensor;
    private Sensor orientation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_text);
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);//获得系统服务
        accelSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        orientation=sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                Log.i("Sensor","x: "+event.values[0]+",y: "+event.values[1]+",z: "+event.values[2]);
                break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    protected void onResume() {
        super.onResume();
       sensorManager.registerListener(this,accelSensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}

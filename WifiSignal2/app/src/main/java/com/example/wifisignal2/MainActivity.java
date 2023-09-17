package com.example.wifisignal2;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.util.Log;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;

import java.sql.Date;
import java.text.SimpleDateFormat;

//主程序，采集wifi信号和安卓传感器信号
public class MainActivity extends AppCompatActivity implements SensorEventListener {
    MainActivity activity;
    private WifiManager mainWifi;
    private WifiAdmin mWifiAdmin;
    private WifiAdmin mWifiAdmin1;
    private List<ScanResult> list;
    private List<ScanResult> list1;
    Map<String, String> store = new HashMap<String, String>();
    Map<String, String> store1 = new HashMap<String, String>();
    Map<String, String> ssidFrequency = new HashMap<String, String>();
    Button start;
    Button end;
    Button search;
    Button stop;
    Button stop_play;
    EditText editText1;
    EditText editText2;
    EditText editText3;
    TextView orientation;
    String x;
    String y;
    boolean isStop = false;
    boolean offline_stop = false;
    boolean online_stop = false;
    int i = 0;
    int m = 0;
    SharedPreferences sharedPreferences;
    int num_sample;
    int delay;
    int delaySensor;
    int interval;
    String offline_dir;
    String online_dir;
    int information;
    long startTime = 0;
    long startTime_online = 0;
    long sensorStartTime;
    private Vibrator vibrator;
    private SoundPool sp;
    private float volume;
    private int soundId;
    private MediaPlayer mediaPlayer;
    private MediaPlayer preparePlayer;
    private Boolean isVibrate;
    private Boolean isRing;
    private static final String TAGA = "test";
    private TextView accelerometerView;
    private TextView orientationView;
    int ii = 0, mm = 0;
    File destDir;
    private boolean isstart = false;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mLinearAccelerometer;
    private Sensor mMagnetometer;
    private Sensor mGyroscope;
    private Sensor mRotationVector;
    private Sensor mGravity;
    private float[] mOrientation = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private float[] mGyroscopeData = new float[3];
    private float[] mLinearAccelData = new float[3];
    private float[] mLastGravityData = new float[3];
    private boolean mLastGravitySet = false;
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mR1 = new float[9];
    private File rootFile;
    private File sensorFile;
    private File orientationFile;
    private File accelFile;
    private File gyroFile;
    private File linearAccelFile;
    private File magneticFile;
    private File RVorientaionFile;
    private File GorientationFile;
    private File GravityFile;
    private File quatFile;
    float timestamp = 0;
    WakeLock wakeLock = null;
    String tempData;

    String accelData = "";
    String lineAccelData = "";
    String gravityData = "";
    String magnData = "";
    String gyroData = "";
    String rvOrienData = "";
    String gOrienData = "";
    String orienData = "";
    String quatData = "";


    //测试222
    //boolean logOneTime=false;
    boolean startScanWifi = true;
    long lastTime = 0;
    HandlerThread handlerThread;
    Handler backgroundHandler;
    long startScanTime = 0;
    long wifiUpdateTime;
    long diffWifiTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        iniBackgroudThread();
        start = (Button) findViewById(R.id.button1);
        start.setOnClickListener(new StartClickListener());
        end = (Button) findViewById(R.id.button2);
        end.setOnClickListener(new EndClickListener());
        stop_play = (Button) findViewById(R.id.button5);
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        orientation = (TextView) findViewById(R.id.orientation);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //Log.v(TAGA, "测试");
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundId = sp.load(getApplicationContext(), R.raw.lightning_strike_plus_thunder, 1);
        AudioManager audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actualVolume / maxVolume;
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.huanyinbaohe);
        preparePlayer = MediaPlayer.create(getApplicationContext(), R.raw.ready1096);
        delaySensor = preparePlayer.getDuration();
        accelerometerView = (TextView) this.findViewById(R.id.accelerometerView);
        orientationView = (TextView) this.findViewById(R.id.orientationView);
        //获取感应器管理器
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLinearAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        rootFile = new File(getExternalFilesDir(null).getPath() + "/IndoorLocation");
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, MainActivity.class.getName());
        wakeLock.acquire();
        //测试
        mWifiAdmin = new WifiAdmin(this);
    }

    public void createSensorFile(String fileName) {
        String basePath = rootFile.getAbsolutePath() + File.separator + offline_dir + File.separator + "sensorData" + File.separator + fileName;
        sensorFile = new File(basePath);
        if (!sensorFile.exists()) {
            sensorFile.mkdirs();
        }
        orientationFile = new File(sensorFile.getAbsolutePath() + File.separator + "Orientation.txt");
        if (!orientationFile.exists()) {
            try {
                orientationFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        magneticFile = new File(sensorFile.getAbsolutePath() + File.separator + "Magnetic.txt");
        if (!magneticFile.exists()) {
            try {
                magneticFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        accelFile = new File(sensorFile.getAbsolutePath() + File.separator + "Accelerometer.txt");
        if (!accelFile.exists()) {
            try {
                accelFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        gyroFile = new File(sensorFile.getAbsolutePath() + File.separator + "Gyroscope.txt");
        if (!gyroFile.exists()) {
            try {
                gyroFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        linearAccelFile = new File(sensorFile.getAbsolutePath() + File.separator + "LinearAccel.txt");
        if (!linearAccelFile.exists()) {
            try {
                linearAccelFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        RVorientaionFile = new File(sensorFile.getAbsolutePath() + File.separator + "RVorientation.txt");
        if (!RVorientaionFile.exists()) {
            try {
                RVorientaionFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        quatFile = new File(sensorFile.getAbsolutePath() + File.separator + "Quaternion.txt");
        if (!quatFile.exists()) {
            try {
                quatFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        GorientationFile = new File(sensorFile.getAbsolutePath() + File.separator + "Gorientation.txt");
        if (!GorientationFile.exists()) {
            try {
                GorientationFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        GravityFile = new File(sensorFile.getAbsolutePath() + File.separator + "Gravity.txt");
        if (!GravityFile.exists()) {
            try {
                GravityFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void stopPlay(View v) {
        mediaPlayer.stop();
        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            Log.v(TAGA, e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.v(TAGA, e.getMessage());
            e.printStackTrace();
        }
    }

    Handler handler = new Handler();
    //要用handler来处理多线程可以使用runnable接口，这里先定义该接口
    //线程中运行该接口的run函数
    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {//WIFI_STATE_CHANGED_ACTION   SCAN_RESULTS_AVAILABLE_ACTION
                if (isstart) { //&& wifiStartTime>=startTime
                    wifiUpdateTime = System.currentTimeMillis();
                    list = mWifiAdmin.getWifiList();
                    diffWifiTime = wifiUpdateTime - startScanTime;
                    mWifiAdmin.startScan(activity);
                    startScanTime = System.currentTimeMillis();
                    Log.i("MainActivity", "扫描间隔 :" + diffWifiTime + ",wifi个数:" + list.size());//scan interval
                    backgroundHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            saveWifiResult();
                        }
                    });
                }
            }
        }
    };

    public void saveWifiResult() {
        if (diffWifiTime > 5000) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(MainActivity.this).setTitle("提示")
                            .setMessage("wifi更新不稳定，采集程序退出，请重新采集")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();
                }
            });
        } else {
            try {
                //扫描wifi信号以及进行预处理
                //Log.i("MainActivity", "num of wifi: " + list.size());//num of wifi
                String file_name = "x" + editText1.getText().toString() + "y" + editText2.getText().toString() + editText3.getText().toString();
                File file2 = new File(rootFile.getAbsolutePath() + File.separator + offline_dir + File.separator + "wifi" + File.separator + file_name);
                if (!file2.exists()) {
                    file2.mkdirs();
                }
                File file1 = new File(rootFile.getAbsolutePath() + File.separator + offline_dir + File.separator + "wifi" + File.separator + file_name + File.separator + i + ".txt");
                if (!file1.exists()) {
                    try {
                        file1.createNewFile();
                        FileOutputStream fos1;
                        fos1 = new FileOutputStream(file1);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM dd HH:mm:ss:SSS");
                        Date curDate = new Date(wifiUpdateTime);
                        String strtime = formatter.format(curDate);
                        StringBuilder stringBuilder = new StringBuilder();
                        String a = "x    " + editText1.getText().toString() + "    y    " + editText2.getText().toString() + "    " + strtime + "    ";
                        stringBuilder.append(a);
                        for (ScanResult value : list) {
                            stringBuilder.append("    " + value.BSSID + "    " + value.level);//value.SSID + "  " + value.level
                        }
                        fos1.write(stringBuilder.toString().getBytes());
                        fos1.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                i++;
                Log.i("MainActivity", "wifi保存完成");
            } catch (Exception e) {

            }
        }
    }

    public void iniBackgroudThread() {
        handlerThread = new HandlerThread("background");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
    }

    Runnable update_thread = new Runnable() {
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            if (seconds < num_sample & offline_stop == false) {
                handler.postDelayed(update_thread, interval);
            } else {
                isstart = false;
                handler.removeCallbacks(update_thread);
                start.setEnabled(true);
                i = 0;    //延时1s后又将线程加入到线程队列中
                if (isVibrate) {
                    vibrator.vibrate(500); //手机震动500ms
                }
                //sp.play(soundId, volume,volume, 1, 0, 1f);//第一个参数为id，id即为放入到soundPool中的顺序，比如现在collide.wav是第一个，因此它的id就是1。第二个和第三个参数为左右声道的音量控制。第四个参数为优先级，由于只有这一个声音，因此优先级在这里并不重要。第五个参数为是否循环播放，0为不循环，-1为循环。最后一个参数为播放比率，从0.5到2，一般为1，表示正常播放。
                if (isRing) {
                    //int status=  mediaPlayer.getCurrentPosition();
                    Log.v(TAGA, "start开始");
                    mediaPlayer.start();
                    Log.v(TAGA, "start结束");
                }
            }
        }
    };

    private class StartClickListener implements OnClickListener {
        public void onClick(View v) {
            // TODO Auto-generated method stub
            //将线程接口立刻送到线程队列中
            // handler.post(update_thread);
            String sensorFileName = "x" + editText1.getText().toString() + "y" + editText2.getText().toString() + editText3.getText().toString();
            createSensorFile(sensorFileName);
            startTime = System.currentTimeMillis();
            startTime = startTime + delay;
            //startScanWifi=true;
            backgroundHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startScanTime = System.currentTimeMillis();//上次扫描时间
                    mWifiAdmin.startScan(activity);
                }
            }, delay);
            //handler.postDelayed(update_thread, delay);
            if (delay > 0) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //vibrator.vibrate(500);
                        Log.i("MainActivity提示音播放开始播放时间", "BroadcastReceiver");
                        preparePlayer.start();
                    }
                }, delay);
            }
            start.setEnabled(false);
            offline_stop = false;
            isstart = true;
        }
    }

    private class EndClickListener implements OnClickListener {
        public void onClick(View v) {
            // TODO Auto-generated method stub
            //将接口从线程队列中移除
            //startScanWifi=false;
            //handler.removeCallbacks(update_thread);
            start.setEnabled(true);
            i = 0;
            offline_stop = true;
            isstart = false;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2click();
        }
        return super.onKeyDown(keyCode, event);
    }

    private static Boolean isExit = false;

    private void exitBy2click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true;
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    isExit = false;
                }
            }, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * 获取菜单点击响应，跳转到另一页面
     *
     * @param item The menu item that was selected.
     *
     * @return boolean 是否响应
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        int actionSettingId = R.id.action_setting;
        if (item.getItemId() == actionSettingId) {
            Intent intent = new Intent(this, SetActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        sharedPreferences = getSharedPreferences("datas", MODE_PRIVATE);
        num_sample = sharedPreferences.getInt("num_sample", 300);
        delay = sharedPreferences.getInt("delay", 0);
        interval = sharedPreferences.getInt("interval", 200);
        offline_dir = sharedPreferences.getString("offline_dir", "sampling");
        isVibrate = sharedPreferences.getBoolean("vibrate", false);
        isRing = sharedPreferences.getBoolean("ring", false);
        //online_dir=sharedPreferences.getString("online_dir", "locating");
        mLastAccelerometerSet = false;
        mLastMagnetometerSet = false;
        mLastGravitySet = false;
        mSensorManager.registerListener(this, mAccelerometer, 20000);//samplingPeriodUs is 20000 microseconds;
        mSensorManager.registerListener(this, mMagnetometer, 20000);
        mSensorManager.registerListener(this, mGyroscope, 20000);
        mSensorManager.registerListener(this, mLinearAccelerometer, 20000);
        mSensorManager.registerListener(this, mGravity, 20000);
        mSensorManager.registerListener(this, mRotationVector, 20000);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        //测试
        registerReceiver(myReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        //测试
        //unregisterReceiver(myReceiver);
        super.onPause();
    }


    @SuppressLint("NewApi")
    @Override
    public void onSensorChanged(final SensorEvent event) {
        // TODO Auto-generated method stub
        sensorStartTime = System.currentTimeMillis();
        if (isstart == true && sensorStartTime >= startTime + delaySensor) {
            if (event.sensor == mRotationVector) {
                float[] rOrientation = new float[3];
                float[] rMat = new float[9];
                float[] roVector = event.values.clone();
                //输出通过四元数计算得到的方向txt文件
                System.out.print("vector of size" + roVector.length);
                SensorManager.getRotationMatrixFromVector(rMat, roVector);
                SensorManager.getOrientation(rMat, rOrientation);
                long time = event.timestamp;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM dd HH:mm:ss:SSS");
                Date curDate = new Date(System.currentTimeMillis());
                String str = formatter.format(curDate);
                rvOrienData = rvOrienData + str + "\t" + time + "\t" + Math.toDegrees(rOrientation[0]) + "\t" + Math.toDegrees(rOrientation[1])
                        + "\t" + Math.toDegrees(rOrientation[2]) + "\t";
                //输出四元数txt文件
                float[] quaternion = new float[4];
                SensorManager.getQuaternionFromVector(quaternion, roVector);
                quatData = quatData + str + "\t" + time + "\t" + quaternion[0] + "\t" + quaternion[1]
                        + "\t" + quaternion[2] + "\t" + quaternion[3] + "\t";
                backgroundHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        FileOutputStream fos;
                        try {
                            fos = new FileOutputStream(RVorientaionFile, true);
                            fos.write(rvOrienData.getBytes());
                            fos.close();
                            rvOrienData = "";
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        FileOutputStream fos2;
                        try {
                            fos2 = new FileOutputStream(quatFile, true);
                            fos2.write(quatData.getBytes());
                            fos2.close();
                            quatData = "";
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            } else if (event.sensor == mGravity) {
                System.arraycopy(event.values, 0, mLastGravityData, 0, event.values.length);
                mLastGravitySet = true;
                long time = event.timestamp;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM dd HH:mm:ss:SSS");
                Date curDate = new Date(System.currentTimeMillis());
                String str = formatter.format(curDate);
                gravityData = gravityData + str + "\t" + time + "\t" + mLastGravityData[0] + "\t" + mLastGravityData[1]
                        + "\t" + mLastGravityData[2] + "\t";
                backgroundHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        FileOutputStream fos;
                        try {
                            fos = new FileOutputStream(GravityFile, true);
                            fos.write(gravityData.getBytes());
                            fos.close();
                            gravityData = "";
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                });
            } else if (event.sensor == mAccelerometer) {
                System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
                mLastAccelerometerSet = true;
                long time = event.timestamp;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM dd HH:mm:ss:SSS");
                Date curDate = new Date(System.currentTimeMillis());
                String str = formatter.format(curDate);
                accelData = accelData + str + "\t" + time + "\t" + mLastAccelerometer[0] + "\t" + mLastAccelerometer[1]
                        + "\t" + mLastAccelerometer[2] + "\t";
                backgroundHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        FileOutputStream fos;
                        try {
                            fos = new FileOutputStream(accelFile, true);
                            fos.write(accelData.getBytes());
                            fos.close();
                            accelData = "";
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            } else if (event.sensor == mMagnetometer) {
                System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
                mLastMagnetometerSet = true;
                long time = event.timestamp;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM dd HH:mm:ss:SSS");
                Date curDate = new Date(System.currentTimeMillis());
                String str = formatter.format(curDate);
                magnData = magnData + str + "\t" + time + "\t" + mLastMagnetometer[0] + "\t" + mLastMagnetometer[1] + "\t" + mLastMagnetometer[2] + "\t";
                FileOutputStream fos1;
                try {
                    fos1 = new FileOutputStream(magneticFile, true);
                    fos1.write(magnData.getBytes());
                    fos1.close();
                    magnData = "";
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (event.sensor == mGyroscope) {
                System.arraycopy(event.values, 0, mGyroscopeData, 0, event.values.length);
                long time = event.timestamp;
                float EPSILON = 0.000000001f;
                float[] deltaRotationMatrix = new float[9];
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM dd HH:mm:ss:SSS");
                Date curDate = new Date(System.currentTimeMillis());
                String str = formatter.format(curDate);
                float NS2S = 1.0f / 1000000000.0f;
                float[] deltaRotationVector = new float[4];
                if (timestamp != 0) {
                    final float dT = (event.timestamp - timestamp) * NS2S;
                    // Axis of the rotation sample, not normalized yet.
                    float axisX = mGyroscopeData[0];
                    float axisY = mGyroscopeData[1];
                    float axisZ = mGyroscopeData[2];
                    float omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
                    // Normalize the rotation vector if it's big enough to get the axis
                    if (omegaMagnitude > EPSILON) {
                        axisX /= omegaMagnitude;
                        axisY /= omegaMagnitude;
                        axisZ /= omegaMagnitude;
                    }
                    float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                    float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
                    float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
                    deltaRotationVector[0] = sinThetaOverTwo * axisX;
                    deltaRotationVector[1] = sinThetaOverTwo * axisY;
                    deltaRotationVector[2] = sinThetaOverTwo * axisZ;
                    deltaRotationVector[3] = cosThetaOverTwo;
                    SensorManager.getRotationMatrixFromVector(
                            deltaRotationMatrix,
                            deltaRotationVector);
                    gyroData = gyroData + str + "\t" + time + "\t\t" + mGyroscopeData[0] + "\t" + mGyroscopeData[1]
                            + "\t" + mGyroscopeData[2] + "\t" + deltaRotationMatrix[0] + "\t" + deltaRotationMatrix[1] + "\t" + deltaRotationMatrix[2]
                            + "\t" + deltaRotationMatrix[3] + "\t" + deltaRotationMatrix[4] + "\t" + deltaRotationMatrix[5]
                            + "\t" + deltaRotationMatrix[6] + "\t" + deltaRotationMatrix[7]
                            + "\t" + deltaRotationMatrix[8] + "\t";
                }
                timestamp = event.timestamp;
                backgroundHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        FileOutputStream fos;
                        try {
                            fos = new FileOutputStream(gyroFile, true);
                            fos.write(gyroData.getBytes());
                            fos.close();
                            gyroData = "";
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            } else if (event.sensor == mLinearAccelerometer) {
                System.arraycopy(event.values, 0, mLinearAccelData, 0, event.values.length);
                long time = event.timestamp;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM dd HH:mm:ss:SSS");
                Date curDate = new Date(System.currentTimeMillis());
                String str = formatter.format(curDate);
                lineAccelData = lineAccelData + str + "\t" + time + "\t" + mLinearAccelData[0] + "\t" + mLinearAccelData[1]
                        + "\t" + mLinearAccelData[2] + "\t";
                backgroundHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        FileOutputStream fos;
                        try {
                            fos = new FileOutputStream(linearAccelFile, true);
                            fos.write(lineAccelData.getBytes());
                            fos.close();
                            lineAccelData = "";
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            }
            //加速度时间间隔和方向时间间隔
            if (mLastMagnetometerSet && mLastGravitySet) {
                mLastGravitySet = false;
                mLastMagnetometerSet = false;
                //加速度+磁场==》方向
                float[] aOrientation = new float[3];
                SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
                SensorManager.getOrientation(mR, aOrientation);
                long time = event.timestamp;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM dd HH:mm:ss:SSS");
                Date curDate = new Date(System.currentTimeMillis());
                String str = formatter.format(curDate);
                orienData = orienData + str + "\t" + time + "\t" + Math.toDegrees(aOrientation[0]) + "\t" + Math.toDegrees(aOrientation[1])
                        + "\t" + Math.toDegrees(aOrientation[2]) + "\t";
                //重力+磁场++》方向
                float[] gOrientation = new float[3];
                SensorManager.getRotationMatrix(mR1, null, mLastGravityData, mLastMagnetometer);
                SensorManager.getOrientation(mR1, gOrientation);
                gOrienData = gOrienData + str + "\t" + time + "\t" + Math.toDegrees(gOrientation[0]) + "\t" + Math.toDegrees(gOrientation[1])
                        + "\t" + Math.toDegrees(gOrientation[2]) + "\t";
                backgroundHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        FileOutputStream fos;
                        try {
                            fos = new FileOutputStream(orientationFile, true);
                            fos.write(orienData.getBytes());
                            fos.close();
                            orienData = "";
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        FileOutputStream fos1;
                        try {
                            fos1 = new FileOutputStream(GorientationFile, true);
                            fos1.write(gOrienData.getBytes());
                            fos1.close();
                            gOrienData = "";
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        long millis = System.currentTimeMillis() - startTime;
        int seconds = (int) (millis / 1000);
        if (isstart && seconds > num_sample) {
            isstart = false;
            start.setEnabled(true);
            i = 0;    //延时1s后又将线程加入到线程队列中
            if (isVibrate) {
                vibrator.vibrate(500); //手机震动500ms
            }
            if (isRing) {
                Log.v(TAGA, "start开始");
                mediaPlayer.start();
                Log.v(TAGA, "start结束");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        mSensorManager.unregisterListener(this);
        //测试
        unregisterReceiver(myReceiver);
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
        super.onDestroy();
    }
}
package com.zz.eat.demoeat;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static android.content.Context.SENSOR_SERVICE;

public class EatSensorManager {


    /**
     * 检测的时间间隔
     */
    static final int UPDATE_INTERVAL = 120;
    /**
     * 上一次检测的时间
     */
    long mLastUpdateTime;
    /**
     * 上一次检测时，加速度在x、y、z方向上的分量，用于和当前加速度比较求差。
     */
    float mLastX, mLastY, mLastZ;

    /**
     * 摇晃检测阈值，决定了对摇晃的敏感程度，越小越敏感。
     */
    public int shakeThreshold = 2500;

    /**
     * 接受摇晃的间隔
     * */
    static final int SHAKE_INTERVAL = 1000;
    private static volatile EatSensorManager instance;
    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    private SensorEventListener mSensorEventListener;
    long mLastShakeTime;

    private EatSensorManager (){}
    public static EatSensorManager getInstance(){
        if (instance == null) {
            synchronized (EatSensorManager.class) {
                if (instance == null) {
                    instance = new EatSensorManager();
                }
            }
        }
        return instance;
    }

    public void init(Context mContext, final OnShakeCallBack onShakeCallBack) {
        mSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);

        if (mSensorManager != null) {

            mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    long currentTime = System.currentTimeMillis();
                    long diffTime = currentTime - mLastUpdateTime;
                    if (diffTime < UPDATE_INTERVAL) {
                        return;
                    }
                    mLastUpdateTime = currentTime;
                    float x = sensorEvent.values[0];
                    float y = sensorEvent.values[1];
                    float z = sensorEvent.values[2];
                    float deltaX = x - mLastX;
                    float deltaY = y - mLastY;
                    float deltaZ = z - mLastZ;
                    mLastX = x;
                    mLastY = y;
                    mLastZ = z;
                    float delta = (float) (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / diffTime * 10000);
                    // 当加速度的差值大于指定的阈值，认为这是一个摇晃
                    if (delta > shakeThreshold) {
                        long shakeCurrentTime = System.currentTimeMillis();
                        long shakeDiffTime = shakeCurrentTime - mLastShakeTime;
                        if (shakeDiffTime < SHAKE_INTERVAL) {
                            return;
                        }
                        mLastShakeTime = currentTime;
                        if(onShakeCallBack!=null){
                            onShakeCallBack.onShake();
                        }
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };
            if (mAccelerometerSensor != null) {
                mSensorManager.registerListener(mSensorEventListener, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    public void release() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mSensorEventListener);
            mSensorManager = null;
            mAccelerometerSensor = null;
            mSensorEventListener = null;
        }
    }

    public interface OnShakeCallBack{
        void onShake();
    }

}

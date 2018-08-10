package com.zz.eat.demoeat;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private List<String> stringName;
    String chooseName ;
    int choosePositon;
    Button btnChoose;
    TextView tvShopName;
    private final static String TAG = "MainActivity";
    private static boolean runing = false;

    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnChoose = findViewById(R.id.btn_begin);
        tvShopName = findViewById(R.id.tv_address);
        stringName =  Arrays.asList(getResources().getStringArray(R.array.eat_address));

        getChooseName();

        tvShopName.setText("今日推荐:" + chooseName);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (runing) {
                    handler.removeCallbacksAndMessages(null);
                } else  {
                    handler.post(choose);
                    handler.postDelayed(stopChoose,2000);
                }
                runing = !runing;
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        EatSensorManager.getInstance().init(MainActivity.this, new EatSensorManager.OnShakeCallBack() {
            @Override
            public void onShake() {
                handler.removeCallbacksAndMessages(null);
                getChooseName();
                tvShopName.setText("摇到的餐厅是:" + chooseName);

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        EatSensorManager.getInstance().release();
        handler.removeCallbacksAndMessages(null);
    }

    private Runnable choose = new Runnable() {
        @Override
        public void run() {
            getChooseName();
            tvShopName.setText("帮您选择的餐馆是:" + chooseName);
            handler.postDelayed(choose,50);
        }
    };

    private Runnable stopChoose = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacksAndMessages(null);
            runing = !runing;

        }
    };

    private void getChooseName() {
        if (stringName != null && stringName.size() >0) {
            Random random = new Random();
            choosePositon = random.nextInt(stringName.size());
            Log.e(TAG,"产生的随机数是" + choosePositon);
            chooseName = stringName.get(choosePositon);
        }
    }
}

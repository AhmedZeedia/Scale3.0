package com.example.scale30;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.starmicronics.starmgsio.ConnectionInfo;
import com.starmicronics.starmgsio.ConnectionInfo.InterfaceType;
import com.starmicronics.starmgsio.Scale;
import com.starmicronics.starmgsio.ScaleCallback;
import com.starmicronics.starmgsio.ScaleData;
import com.starmicronics.starmgsio.ScaleData.DataType;
import com.starmicronics.starmgsio.StarDeviceManager;
import com.starmicronics.starmgsio.ScaleSetting;


import java.util.Locale;

public class Main2Activity extends AppCompatActivity {

    public static final String IDENTIFIER_BUNDLE_KEY     = "IDENTIFIER_BUNDLE_KEY";
    public static final String INTERFACE_TYPE_BUNDLE_KEY = "INTERFACE_TYPE_BUNDLE_KEY";

    private TextView mWeightTextView;
    private TextView mStateTextView;
    private Scale mScale;
    private DataType tare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main2);


        mWeightTextView = findViewById(R.id.WeightTextView);
        mStateTextView  = findViewById(R.id.StateTextView);

        Button zeroAdj = findViewById(R.id.zeroAdju);
        zeroAdj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mScale == null){
                    return;
                }
                mScale.updateSetting(ScaleSetting.ZeroPointAdjustment);

            /*   tare.getDataType(ScaleData.DataType.TARE);
                tare.getDeclaringClass();*/
            }
        });

      /*  Button tare1 = findViewById(R.id.tare);
        tare1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mScale == null){
                    return;
                }
                mScale.updateSetting(DataType.TARE);
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mScale == null) {
            String identifier = getIntent().getStringExtra(IDENTIFIER_BUNDLE_KEY);

            ConnectionInfo.InterfaceType interfaceType = ConnectionInfo.InterfaceType.valueOf(getIntent()
                    .getStringExtra(INTERFACE_TYPE_BUNDLE_KEY));

            StarDeviceManager starDeviceManager = new StarDeviceManager(Main2Activity.this);

            ConnectionInfo connectionInfo;

            switch (interfaceType) {
                default:
                case BLE:
                    connectionInfo = new ConnectionInfo.Builder()
                            .setBleInfo(identifier)
                            .build();
                    break;
                case USB:
                    connectionInfo = new ConnectionInfo.Builder()
                            .setUsbInfo(identifier)
                            .setBaudRate(1200)
                            .build();
                    break;
            }

            mScale = starDeviceManager.createScale(connectionInfo);

            mScale.connect(mScaleCallback);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mScale != null) {
            mScale.disconnect();

        }

    }
    private final ScaleCallback mScaleCallback= new ScaleCallback() {
        @Override
        public void onReadScaleData(Scale scale, ScaleData scaleData) {
            super.onReadScaleData(scale, scaleData);

            if (scaleData.getStatus()== ScaleData.Status.ERROR){

                mWeightTextView.setText("0 [INVALID]");
                mStateTextView.setText("Status: ERROR");
            } else {
                String weight = String.format(Locale.US, "%."
                        + scaleData.getNumberOfDecimalPlaces() + "f", scaleData.getWeight());
                String unit = scaleData.getUnit().toString();
                String weightStr = weight + " [" + unit + "]";

                mWeightTextView.setText(weightStr);
                String statusStr =
                        "Status: " + scaleData.getStatus() + "\n";

                mStateTextView.setText(statusStr);
            }

        }
    };

}

package com.example.cekke.activity_recognition_original;

/**
 * Created by cekke on 17/05/2017.
 */

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class Fragment_2recorder extends Fragment {

    private static TextView feature1, feature2, feature3, feature4, feature5;
    private static TextView activityTextView;
    private static TextView tvRecordTime;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_2recorder, container, false);

        feature1 = (TextView) rootView.findViewById(R.id.tv_feature1);
        feature2 = (TextView) rootView.findViewById(R.id.tv_feature2);
        feature3 = (TextView) rootView.findViewById(R.id.tv_feature3);
        feature4 = (TextView) rootView.findViewById(R.id.tv_feature4);
        feature5 = (TextView) rootView.findViewById(R.id.tv_feature5);
        activityTextView = (TextView) rootView.findViewById(R.id.et_Activity);
        activityTextView.setText("Activity : None");
        activityTextView.setEnabled(false);
        tvRecordTime = (TextView) rootView.findViewById(R.id.tv_f2_recordtime);


        ImageButton buttonStart =(ImageButton) rootView.findViewById(R.id.ib_startRec);
        buttonStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //create looper for check status and generate start allarm...
                MainActivity.nRip=0;
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                MainActivity.creatorStatusLooper();
                MainActivity.startRepeatingTask();
                if (MainActivity.initNewSession()) {
                    MainActivity.startBandRecording();
                }
                MainActivity.activityLoop();

            }
        });

        ImageButton buttonPrint =(ImageButton) rootView.findViewById(R.id.ib_printvett);
        buttonPrint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                MainActivity.staticCalculatorObj.printArrays();
            }
        });

        return rootView;
    }



    public static void setDataFromMainClass(String StdDevX, String XmeansModule, String YmeansModule, String ZmeansModule, String StdDevXYZ, String seconlayer )
    {
        feature1.setText(StdDevX);
        feature2.setText(XmeansModule);
        feature3.setText(YmeansModule);
        feature4.setText(ZmeansModule);
        feature5.setText(StdDevXYZ);
        activityTextView.setText(seconlayer);
    }

    public static String getRecordTime()
    {
        return tvRecordTime.getText().toString();
    }

    public static void setRecordTime(String Time)
    {
        tvRecordTime.setText(Time);
    }



}

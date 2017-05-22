package com.example.cekke.activity_recognition_original;

/**
 * Created by cekke on 17/05/2017.
 */

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class Fragment_2recorder extends Fragment {

    private TextView feature1, feature2, feature3, feature4, feature5;
    private TextView activityTextView;
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
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                MainActivity.creatorStatusLooper();
                MainActivity.startRepeatingTask();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Start Recording after 6 seconds (waiting for clean data)
                        MainActivity.contData=0;
                        MainActivity.folder=MainActivity.getStandardFolderName().replace("data", MainActivity.getDate());
                        MainActivity.started = true;

                        if (MainActivity.initNewSession()) {
                            MainActivity.startBandRecording();
                        }
                    }
                }, 6000);
            }
        });

        ImageButton buttonPrint =(ImageButton) rootView.findViewById(R.id.ib_printvett);
        buttonPrint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String Xstring="";
                String Ystring="";
                String Zstring="";
                for (int i=0;i<100;i++){
                    Xstring+= MainActivity.valueListX[i] + " ," ;
                    Ystring+= MainActivity.valueListY[i] + " ," ;
                    Zstring+= MainActivity.valueListZ[i] + " ," ;
                }

                Log.i("X", Xstring);
                Log.i("Y", Ystring);
                Log.i("Z", Zstring);
            }
        });

        return rootView;
    }



    public void getDataFromMainClass()
    {
        feature1.setText(String.valueOf(MainActivity.stdDevX));
        feature2.setText(String.valueOf(MainActivity.XmeansModule));
        feature3.setText(String.valueOf(MainActivity.YmeansModule));
        feature4.setText(String.valueOf(MainActivity.ZmeansModule));
        feature5.setText(String.valueOf(MainActivity.stdDevXYZ));
        activityTextView.setText(MainActivity.currentActivity);
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

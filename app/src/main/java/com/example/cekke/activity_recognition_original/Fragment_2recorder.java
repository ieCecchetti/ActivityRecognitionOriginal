package com.example.cekke.activity_recognition_original;

/**
 * Created by cekke on 17/05/2017.
 */

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.annotation.MainThread;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_2recorder extends Fragment {

    private static TextView feature1, feature2, feature3, feature4, feature5;
    private static TextView activityTextView;
    private static TextView tvRecordTime;
    private static ImageButton buttonStart;
    private boolean isstop=true;




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


        buttonStart =(ImageButton) rootView.findViewById(R.id.ib_startRec);
        buttonStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //create looper for check status and generate start allarm...
                if (isstop){
                    MainActivity.nRip=0;
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                    MainActivity.folder=MainActivity.getStandardFolderName().replace("data", MainActivity.projectUtils.getDate());
                    MainActivity.creatorStatusLooper();
                    MainActivity.startRepeatingTask();
                    MainActivity.initNewSession();
                    MainActivity.activityLoop();
                    isstop=false;
                    switchActionAnimation();
                }else{
                    MainActivity.stopRepeatingTask();
                    Toast.makeText(MainActivity.getContext(),"Process aborted, impossible to calculate the activity on process",
                            Toast.LENGTH_LONG).show();
                    isstop=true;
                    switchActionAnimation();
                    MainActivity.staticCalculatorObj.restartTime();

                }


            }
        });

        ImageButton buttonPrint =(ImageButton) rootView.findViewById(R.id.ib_printvett);
        buttonPrint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //MainActivity.staticCalculatorObj.printArrays();
                MainActivity.printNearestBeacon();
            }
        });

        return rootView;
    }

    private void switchActionAnimation()
    {
        if(isstop==true)
        {
            buttonStart.setImageResource(R.drawable.buttonstart);

        }else{
            buttonStart.setImageResource(R.drawable.stopses);
        }
    }

    public static void stopRecord()
    {
        buttonStart.setImageResource(R.drawable.buttonstart);
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

    public static void setRecordTime(String Time)
    {
        tvRecordTime.setText(Time);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            if (MainActivity.staticCalculatorObj.isStarted())
            {
                setRecordTime(MainActivity.staticCalculatorObj.popTime());
            }
        }
    }


}

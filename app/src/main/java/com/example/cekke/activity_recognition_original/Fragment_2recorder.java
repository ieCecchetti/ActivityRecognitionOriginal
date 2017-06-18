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

    private static TextView feature1, feature2, feature3, feature4, feature5, feature6;
    private static TextView feature7, feature8, feature9, feature10, feature11,feature12;
    private static TextView activityTextView;
    private static TextView tvRecordTime;
    private static ImageButton buttonStart;
    private static boolean isstop=true;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_2recorder, container, false);
        //firstLayer features
        feature1 = (TextView) rootView.findViewById(R.id.tv_feature1);
        feature2 = (TextView) rootView.findViewById(R.id.tv_feature2);
        feature3 = (TextView) rootView.findViewById(R.id.tv_feature3);
        feature4 = (TextView) rootView.findViewById(R.id.tv_feature4);
        feature5 = (TextView) rootView.findViewById(R.id.tv_feature5);
        feature6 = (TextView) rootView.findViewById(R.id.tv_feature6);
        //secondLayer features
        feature7 = (TextView) rootView.findViewById(R.id.tv_feature7);
        feature8 = (TextView) rootView.findViewById(R.id.tv_feature8);
        feature9 = (TextView) rootView.findViewById(R.id.tv_feature9);
        feature10 = (TextView) rootView.findViewById(R.id.tv_feature10);
        feature11 = (TextView) rootView.findViewById(R.id.tv_feature11);
        feature12 = (TextView) rootView.findViewById(R.id.tv_feature12);

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



    public static void setDataFromMainClass1L(String StdDevXPhone, String XmeansModulePhone, String YmeansModulePhone, String ZmeansModulePhone, String StdDevXYZPhone, String ZenergyPhone, String firstLayer )
    {
        feature1.setText(MainActivity.projectUtils.round(Double.parseDouble(StdDevXPhone),4));
        feature2.setText(MainActivity.projectUtils.round(Double.parseDouble(XmeansModulePhone),4));
        feature3.setText(MainActivity.projectUtils.round(Double.parseDouble(YmeansModulePhone),4));
        feature4.setText(MainActivity.projectUtils.round(Double.parseDouble(ZmeansModulePhone),4));
        feature5.setText(MainActivity.projectUtils.round(Double.parseDouble(StdDevXYZPhone),4));
        feature6.setText(MainActivity.projectUtils.round(Double.parseDouble(ZenergyPhone),4));
        activityTextView.setText(firstLayer);
    }

    public static void setDataFromMainClass2L(String xMeanBand, String zMeanBand, String ZdevStdBand, String XdevStdBand, String XenergyBand, String ZenergyBand, String secondlayer )
    {
        feature7.setText(MainActivity.projectUtils.round(Double.parseDouble(xMeanBand),4));
        feature8.setText(MainActivity.projectUtils.round(Double.parseDouble(zMeanBand),4));
        feature9.setText(MainActivity.projectUtils.round(Double.parseDouble(ZdevStdBand),4));
        feature10.setText(MainActivity.projectUtils.round(Double.parseDouble(XenergyBand),4));
        feature11.setText(MainActivity.projectUtils.round(Double.parseDouble(ZenergyBand),4));
        feature12.setText(MainActivity.projectUtils.round(Double.parseDouble(XdevStdBand),4));
        activityTextView.setText(secondlayer);
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

package com.example.cekke.activity_recognition_original;

/**
 * Created by cekke on 17/05/2017.
 */

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Fragment_3status extends Fragment {

    private static TextView tvPhoneStatus, tvBeaconStatus, tvBandStatus, tvRecordTime;
    private String TimeRecording;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_3status, container, false);

        tvRecordTime = (TextView) rootView.findViewById(R.id.tv_f3_recordtime);
        tvPhoneStatus = (TextView) rootView.findViewById(R.id.tv_f3_phonerecorder_value);
        tvBeaconStatus = (TextView) rootView.findViewById(R.id.tv_f3_beaconrecorder_value);
        tvBandStatus = (TextView) rootView.findViewById(R.id.tv_f3_bandrecorder_value);

        return rootView;
    }

    public static void printListenerStatus(){
        if (MainActivity.phoneStatus)
        {
            tvPhoneStatus.setTextColor(Color.GREEN);
            tvPhoneStatus.setText("Recording");
        }else{
            tvPhoneStatus.setTextColor(Color.RED);
            tvPhoneStatus.setText("Not Recording");
        }

        if (MainActivity.bandStatus)
        {
            tvBandStatus.setTextColor(Color.GREEN);
            tvBandStatus.setText("Recording");
        }else{
            tvBandStatus.setTextColor(Color.RED);
            tvBandStatus.setText("Not Recording");
        }

        if (MainActivity.beaconStatus)
        {
            tvBeaconStatus.setTextColor(Color.GREEN);
            tvBeaconStatus.setText("Recording");
        }else{
            tvBeaconStatus.setTextColor(Color.RED);
            tvBeaconStatus.setText("Not Recording");
        }
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

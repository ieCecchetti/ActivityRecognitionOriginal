package com.example.cekke.activity_recognition_original;

import android.bluetooth.BluetoothAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by cekke on 22/05/2017.
 */

public final class Utils {

    //remember to insert PERMISSION on manifest before using
    //<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    //<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    public void saveInFile(String path, String folder, String fileName, String savingText)
    {
        FileOutputStream fileOut=null;
        File root = new File(path+ "/" + folder, fileName);
        if (!root.exists()) {
            root.mkdirs(); // this will create folder.
        }

        OutputStreamWriter outStreamWriter = null;
        File out = new File(root, fileName + ".txt");  // file path to save

        if (out.exists() == false) {
            try {
                out.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fileOut = new FileOutputStream(out, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        outStreamWriter = new OutputStreamWriter(fileOut);

        try {
            outStreamWriter.append(savingText);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outStreamWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String TimeIncr(String Time)
    {
        String[] separated = Time.split(":");
        int min = Integer.parseInt(separated[0]);
        int sec = Integer.parseInt(separated[1].split(" ")[0]);
        sec++;
        if(sec==60)
        {
            sec=0;
            min++;
        }
        String newTime=min+":"+sec+" min";
        return newTime;
    }

    public static String getDate(){
        DateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
        String date=dfDate.format(Calendar.getInstance().getTime());
        DateFormat dfTime = new SimpleDateFormat("HH:mm:ss");
        String time = dfTime.format(Calendar.getInstance().getTime());
        return date + "_" + time;
    }


    //remember to insert PERMISSION on manifest before using
    //<uses-permission android:name="android.permission.BLUETOOTH" />
    public boolean checkBluetoothStatus()
    {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            return false;
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                // Bluetooth is not enable
                return false;
            }
            return true;
        }
    }

}

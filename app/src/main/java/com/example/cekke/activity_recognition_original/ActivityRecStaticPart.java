package com.example.cekke.activity_recognition_original;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;

import com.microsoft.band.sensors.BandAccelerometerEvent;

import net.sf.javaml.utils.MathUtils;

/**
 * Created by cekke on 22/05/2017.
 */

public final class ActivityRecStaticPart {
    private static String activityList;
    private static double[] valueListX = new double[150];
    private static double[] valueListY = new double[150];
    private static double[] valueListZ = new double[150];
    public static double[] valueListXBand = new double[300];
    public static double[] valueListYBand = new double[300];
    public static double[] valueListZBand = new double[300];
    private static boolean started=false;
    private static boolean startedSLayer=false;
    private static int contDataPhone=0;
    private static int contDataBand=0;
    private static double stdDevX;
    private static double XmeansModule;
    private static double YmeansModule;
    private static double ZmeansModule;
    private static double stdDevXYZ;
    private static String firstLayerActivity="Activity : None";
    private static String secondLayerActivity="Activity : None";
    private static boolean ReadyFL=false;
    private static boolean ReadySL=false;

    public void addActivity(String activity)
    {
        activityList+=(activity+" ,");
    }

    public String getActivityList()
    {
        return activityList;
    }

    public void setActivityList(String activitylist)
    {
        activityList=activitylist;
    }

    public void resetActivityList()
    {
        activityList="";
    }

    public boolean addPhoneData(SensorEvent event)
    {
        int eType = event.sensor.getType();

        if(eType == Sensor.TYPE_ACCELEROMETER)
        {
            if(contDataPhone!=149){  //3sec di finestra
                contDataPhone++;
                valueListX[contDataPhone]=(event.values[0]);
                valueListY[contDataPhone]=(event.values[1]);
                valueListZ[contDataPhone]=(event.values[2]);
            }else{
                contDataPhone=0;
                ReadyFL=true;
                started=false;
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100);
            }
        }
        return true;
    }

    public void addBandData(BandAccelerometerEvent bandAccelerometerEvent)
    {
        valueListXBand[contDataBand]=Double.parseDouble(String.valueOf(bandAccelerometerEvent.getAccelerationX()*9.8));
        valueListYBand[contDataBand]=Double.parseDouble(String.valueOf(bandAccelerometerEvent.getAccelerationY()*9.8));
        valueListZBand[contDataBand]=Double.parseDouble(String.valueOf(bandAccelerometerEvent.getAccelerationZ()*9.8));
        contDataBand++;
    }

    public boolean isStarted()
    {
        return started;
    }

    public boolean isStartedSLayer()
    {
        return startedSLayer;
    }

    public void StartRecFirstLayer()
    {
        started=true;
    }

    public void StopRecFirstLayer()
    {
        started=false;
        ReadyFL=true;
    }

    public void StartRecSecondLayer()
    {
        startedSLayer=true;
    }

    public void StopRecSecondLayer()
    {
        startedSLayer=false;
        ReadySL=true;
    }


    private void calcFirstLayerActivity(){
        ReadyFL=false;
        filterData(150);
        double Xmeans= MathUtils.arithmicMean(valueListX);
        double Ymeans=MathUtils.arithmicMean(valueListY);
        double Zmeans= MathUtils.arithmicMean(valueListZ);

        stdDevX= Math.sqrt(variance(valueListX,Xmeans,valueListY.length));
        //feature1.setText(String.valueOf(stdDevX));

        XmeansModule=Math.abs(Xmeans);
        //feature2.setText(String.valueOf(XmeansModule));
        YmeansModule=Math.abs(Ymeans);
        //feature3.setText(String.valueOf(YmeansModule));
        ZmeansModule=Math.abs(Zmeans);
        //feature4.setText(String.valueOf(ZmeansModule));

        stdDevXYZ = Math.sqrt(variance(valueListX,Xmeans,valueListX.length)+
                variance(valueListY,Ymeans,valueListY.length)+
                variance(valueListZ,Zmeans,valueListZ.length));
        //feature5.setText(String.valueOf(stdDevXYZ));
        Log.i("cont", String.valueOf(valueListZ.length));



        //posizione?
        if (YmeansModule>8.8)
        {
            //posizione eretta
            if(stdDevXYZ>1.2  && stdDevX>0.7)
            {
                //walking
                firstLayerActivity="Activity : WALKING";
            }else{
                firstLayerActivity="Activity : STANDING";
            }
        }else{
            //sitting
            if ((XmeansModule>1.3) || (stdDevX)>0.32) //se la stdDev è alta solitamente siamo in sitting
            {
                if(stdDevX<0.1) // se stdDev<0.1 si raggiunge solitamente solo da lying
                {
                    //caso limite
                    firstLayerActivity="Activity : LYING";

                }else{
                    firstLayerActivity="Activity : SITTING";
                }

            }
            else{
                firstLayerActivity="Activity : LYING";
            }
            //lying sul lato
            if (ZmeansModule<8.5){
                if(Ymeans>1)
                {
                    firstLayerActivity="Activity : SITTING";
                }else{
                    firstLayerActivity="Activity : LYING";
                }
            }
        }


    }

    private void calcSecondLayerActivity(){

        ReadySL=false;
        secondLayerActivity=firstLayerActivity;
    }

    private void filterData(int nData){
        Double sumX;
        Double sumY;
        Double sumZ;
        for (int i=2;i<nData-2;i++)  //pre 96
        {
            sumX=valueListX[i-2];
            sumX+=valueListX[i-1];
            sumX+=valueListX[i];
            sumX+=valueListX[i+1];
            sumX+=valueListX[i+2];

            sumY=valueListY[i-2];
            sumY+=valueListY[i-1];
            sumY+=valueListY[i];
            sumY+=valueListY[i+1];
            sumY+=valueListY[i+2];

            sumZ=valueListZ[i-2];
            sumZ+=valueListZ[i-1];
            sumZ+=valueListZ[i];
            sumZ+=valueListZ[i+1];
            sumZ+=valueListZ[i+2];
        }

    }

    private double variance(double[] array,double mean, int nElem)
    {
        double discrepancy=0;
        double sum=0;

        for (int i=0; i<nElem;i++)
        {
            discrepancy=0;
            discrepancy= array[i]-mean;
            sum+= (discrepancy*discrepancy);
        }
        return sum/(array.length-1);
    }

    public String getFirstLayerActivity()
    {
        calcFirstLayerActivity();
        return firstLayerActivity;
    }

    public void setFirstLayerActivity(String activity)
    {
        firstLayerActivity= activity;
    }

    public String getSecondLayerActivity()
    {
        calcSecondLayerActivity();
        return secondLayerActivity;
    }

    public void setSecondLayerActivity(String activity)
    {
        secondLayerActivity= activity;
    }

    public boolean isReadyFL()
    {
        return ReadyFL;
    }

    public boolean isReadySL()
    {
        return ReadySL;
    }

    public void flushFLData()
    {
        started=false;
        ReadyFL=false;
        for(int i=0;i<150;i++)
        {
            valueListX[i]=0.0;
            valueListY[i]=0.0;
            valueListZ[i]=0.0;
        }
        contDataPhone=0;
    }
    public void flushSLData()
    {
        startedSLayer=false;
        ReadySL=false;
        for(int i=0;i<150;i++)
        {
            valueListXBand[i]=0.0;
            valueListYBand[i]=0.0;
            valueListZBand[i]=0.0;
        }
        contDataBand=0;
    }

    public int getSensPhoneCount()
    {
        return contDataPhone;
    }

    public int getSensBandCount()
    {
        return contDataBand;
    }

    public void printArrays()
    {
        String Xstring="";
        String Ystring="";
        String Zstring="";
        Log.i("band count", String.valueOf(getSensBandCount()));
        Log.i("band count", String.valueOf(getSensBandCount()));
        for (int i=0;i<MainActivity.staticCalculatorObj.getSensPhoneCount();i++){
            Xstring+= valueListX[i] + " ," ;
            Ystring+= valueListY[i] + " ," ;
            Zstring+= valueListZ[i] + " ," ;
        }

        Log.i("X", Xstring);
        Log.i("Y", Ystring);
        Log.i("Z", Zstring);

        Xstring="";
        Ystring="";
        Zstring="";
        for (int i=0;i<MainActivity.staticCalculatorObj.getSensBandCount();i++){
            Xstring+= valueListX[i] + " ," ;
            Ystring+= valueListY[i] + " ," ;
            Zstring+= valueListZ[i] + " ," ;
        }

        Log.i("X", Xstring);
        Log.i("Y", Ystring);
        Log.i("Z", Zstring);
    }

    public double getStdDevX()
    {
        return stdDevX;
    }

    public double getXmeansModule()
    {
        return XmeansModule;
    }

    public double getYmeansModule()
    {
        return YmeansModule;
    }

    public double getZmeansModule()
    {
        return ZmeansModule;
    }

    public double getStdDevXYZ()
    {
        return stdDevXYZ;
    }

}

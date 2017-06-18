package com.example.cekke.activity_recognition_original;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;

import com.microsoft.band.sensors.BandAccelerometerEvent;

import net.sf.javaml.utils.MathUtils;

import org.jtransforms.fft.DoubleFFT_1D;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cekke on 22/05/2017.
 */

public final class ActivityRecStaticPart {
    private static double[] valueListX = new double[150];
    private static double[] valueListY = new double[150];
    private static double[] valueListZ = new double[150];
    public static double[] valueListXBand = new double[190];
    public static double[] valueListYBand = new double[190];
    public static double[] valueListZBand = new double[190];
    private static List<String> activityList;
    private static List<String> dateList;
    private static List<String> featuresList;
    private static boolean started=false;
    private static boolean startedSLayer=false;
    private static int contDataPhone=0;
    private static int contDataBand=0;
    private static double stdDevXPhone;
    private static double XmeansModulePhone;
    private static double YmeansModulePhone;
    private static double ZmeansModulePhone;
    private static double ZEnergyPhone;
    private static double stdDevXYZPhone;
    private static String firstLayerActivity="Activity : None";
    private static double XmeansBand;
    private static double YmeansBand;
    private static double ZmeansBand;
    private static double XEnergyBand;
    private static double ZEnergyBand;
    private static double stdDevXBand;
    private static double stdDevZBand;
    private static String secondLayerActivity="Activity : None";
    private static boolean ReadyFL=false;
    private static boolean ReadySL=false;
    private static String currentTime;

    public ActivityRecStaticPart()
    {
        activityList= new ArrayList<String>();
        dateList= new ArrayList<String>();
        featuresList= new ArrayList<String>();
        valueListXBand[0]=0;
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
            }
        }
        return true;
    }

    public void addBandData(BandAccelerometerEvent bandAccelerometerEvent)
    {
        if (contDataBand<190)
        {
            valueListXBand[contDataBand]=Double.parseDouble(String.valueOf(bandAccelerometerEvent.getAccelerationX()*9.8));
            valueListYBand[contDataBand]=Double.parseDouble(String.valueOf(bandAccelerometerEvent.getAccelerationY()*9.8));
            valueListZBand[contDataBand]=Double.parseDouble(String.valueOf(bandAccelerometerEvent.getAccelerationZ()*9.8));
            contDataBand++;
        }
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
        //filterPhoneData(150);
        double Xmeans= MathUtils.arithmicMean(valueListX);
        double Ymeans= MathUtils.arithmicMean(valueListY);
        double Zmeans= MathUtils.arithmicMean(valueListZ);
        ZEnergyPhone=energy(valueListZ);

        stdDevXPhone= Math.sqrt(variance(valueListX,Xmeans,valueListY.length));

        XmeansModulePhone=Math.abs(Xmeans);
        YmeansModulePhone=Math.abs(Ymeans);
        ZmeansModulePhone=Math.abs(Zmeans);

        stdDevXYZPhone = Math.sqrt(variance(valueListX,Xmeans,valueListX.length)+
                variance(valueListY,Ymeans,valueListY.length)+
                variance(valueListZ,Zmeans,valueListZ.length));

        //posizione?
        if (YmeansModulePhone>8.8)
        {
            //posizione eretta
            if(stdDevXYZPhone>1.2  && stdDevXPhone>0.7)
            {
                //walking
                firstLayerActivity="Activity : WALKING";
            }else{
                firstLayerActivity="Activity : STANDING";
            }
        }else{
            //impossible to recognise... we need band to do it right
            firstLayerActivity="Activity : SITTING";
        }
    }

    private void calcSecondLayerActivity(){

        ReadySL=false;

        //feature calc
        XmeansBand= MathUtils.arithmicMean(valueListXBand);
        YmeansBand= MathUtils.arithmicMean(valueListYBand);
        ZmeansBand= MathUtils.arithmicMean(valueListZBand);
        stdDevXBand= Math.sqrt(variance(valueListXBand,XmeansBand,valueListX.length));
        stdDevZBand= Math.sqrt(variance(valueListZBand,ZmeansBand,valueListZBand.length));
        XEnergyBand= energy(valueListXBand);
        ZEnergyBand= energy(valueListZBand);

        String room=BeaconIdToRoom(MainActivity.NearestBeaconId);
        room="livingroom";

        if(firstLayerActivity.equals("Activity : SITTING"))
        {
            firstLayerActivity= LyingOrSittingVerify(firstLayerActivity,room);
        }

        //three selection
        if (!room.equals("none")){
            switch (firstLayerActivity)
            {
                case "Activity : LYING":
                    switch (room)
                    {
                        case "livingroom":
                            secondLayerActivity="Activity : LYING SOFA'";
                            break;
                        case "bedroom":
                            secondLayerActivity="Activity : LYING BED";
                            break;
                        case "kitchen":
                            secondLayerActivity="Activity : SITTING";
                            break;
                        default:
                            secondLayerActivity=firstLayerActivity;
                            break;
                    }
                    break;
                case "Activity : STANDING":
                    switch (room)
                    {
                        case "bathroom":
                            if (YmeansBand<-3){
                                secondLayerActivity=SweepOrVacum("Activity : STANDING");
                            }
                            else if (YmeansBand>-3){
                                secondLayerActivity=BrushOrHair("Activity : STANDING");
                            }
                            break;
                        case "livingroom":
                            secondLayerActivity=SweepOrVacum("Activity : STANDING");
                            break;
                        case "bedroom":
                            secondLayerActivity=SweepOrVacum("Activity : STANDING");
                            break;
                        case "kitchen":
                            secondLayerActivity=SweepOrVacum("Activity : STANDING");
                            break;
                    }
                    break;
                case "Activity : SITTING":
                    switch (room)
                    {
                        case "livingroom":
                            secondLayerActivity=TypeEatOrDrink("Activity : SIT.SOFA'");
                            break;
                        case "bedroom":
                            secondLayerActivity=TypeEatOrDrink("Activity : SITTING BED");
                            break;
                        case "kitchen":
                            secondLayerActivity=TypeEatOrDrink("Activity : SITTING");
                            break;
                    }
                    break;
                case "Activity : WALKING":
                    switch (room)
                    {
                        case "bathroom":
                            secondLayerActivity=SweepOrVacum("Activity : WALKING");
                            break;
                        case "livingroom":
                            secondLayerActivity=SweepOrVacum("Activity : WALKING");
                            break;
                        case "bedroom":
                            secondLayerActivity=SweepOrVacum("Activity : WALKING");
                            break;
                        case "kitchen":
                            secondLayerActivity=SweepOrVacum("Activity : WALKING");
                            break;
                    }
                    break;
            }
        }else{
            secondLayerActivity=firstLayerActivity;
        }
    }

    private String LyingOrSittingVerify(String FL, String rom){
        String res=FL;
        //verify first layer (sitting or lying)
        if (XmeansBand>1){
            res= "Activity : SITTING";
        }else{
            //Log.i("devstdx","value :"+stdDevXBand);
            if(stdDevXBand>0.02)
            {
                res= "Activity : SITTING";
            }else{
                res= "Activity : LYING";
            }
        }
        return res;
    }

    private String SweepOrVacum(String deambulante)
    {
        String res = deambulante;
        if (res.equals("Activity : STANDING"))
        {
            if (XmeansBand > -5 && XmeansBand < 2)
            {
                res =  "Activity : SWEEPING";
            }else if(XmeansBand > 4 && XmeansBand < 10){
                if (XmeansBand > 8 && XmeansBand < 10){
                    if(stdDevXBand < 0.15)
                    {
                        res =  deambulante;
                    }else{
                        res =  "Activity : VACUUMING";
                    }
                }else
                {
                    res =  "Activity : VACUUMING";
                }
            }
        }else if(res.equals("Activity : WALKING"))
        {
            if (stdDevXPhone>1.5)
            {
                res =  deambulante;
            }else{
                if (XmeansBand > -5 && XmeansBand < 2)
                {
                    res =  "Activity : SWEEPING";
                }else if(XmeansBand > 4 && XmeansBand < 10){
                    res =  "Activity : VACUUMING";
                }
            }
        }

        return res;
    }

    private String BrushOrHair(String deambulante)
    {
        String res = deambulante;
        if (ZEnergyBand > 11200){
            res =  "Activity : BRUSHING";
        }else if(ZEnergyBand < 11200 && ZEnergyBand > 1900)
        {
            res =  "Activity : HAIRSTYLE";
        }else{
            res =  deambulante;
        }
        return res;
    }

    private String TypeEatOrDrink(String deambulante)
    {
        String res = deambulante;
        //drink or eating
        if (ZmeansBand > 7){
            if(getXmeansBand()<0)  //stdDevZBand>0.1
            {
                res =  "Activity : TYPING";
            }else{
                res =  deambulante;
            }
        }else{
            if(stdDevZBand>1.7)
            {
                res =  "Activity : EATING";
            }else{
                res =  "Activity : DRINKING";
            }
        }
        return res;
    }

    private void filterPhoneData(int nData){
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

    private void filterBandData(int nData){
        Double sumX;
        Double sumY;
        Double sumZ;
        for (int i=2;i<nData-2;i++)  //pre 96
        {
            sumX=valueListXBand[i-2];
            sumX+=valueListXBand[i-1];
            sumX+=valueListXBand[i];
            sumX+=valueListXBand[i+1];
            sumX+=valueListXBand[i+2];

            sumY=valueListYBand[i-2];
            sumY+=valueListYBand[i-1];
            sumY+=valueListYBand[i];
            sumY+=valueListYBand[i+1];
            sumY+=valueListYBand[i+2];

            sumZ=valueListZBand[i-2];
            sumZ+=valueListZBand[i-1];
            sumZ+=valueListZBand[i];
            sumZ+=valueListZBand[i+1];
            sumZ+=valueListZBand[i+2];
        }

    }

    private double Mean(double[] input)
    {
        double sum=0.0;
        for (int i=0; i<input.length; i++)
        {
            sum+= input[i];
        }
        return sum/input.length;
    }

    private double energy(double[] input)
    {
        DoubleFFT_1D fftDo = new DoubleFFT_1D(input.length);
        double[] fft = new double[input.length * 2];
        double[] fftvalsModule = new double[input.length];
        System.arraycopy(input, 0, fft, 0, input.length);
        fftDo.realForwardFull(fft);

        //System.out.println("--------------------------fft module values");
        int k=0;
        for (int i=0;i<input.length; i++){
            fftvalsModule[i]=(fft[k]*fft[k])+(fft[k+1]*fft[k+1]);
            k=k+2;
        }
        double sum=0;
        for (double d: fftvalsModule){
            //System.out.println(d);
            sum+=d;
        }
        double energy= sum/input.length;

        //System.out.println("----->energy:"+ MainActivity.projectUtils.round(energy,4) +" <-------");
        //System.out.println("--------------------------get back to normal val");
        //fftDo.complexInverse(fft,true);

        return energy;
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

    public void flushAllData()
    {
        flushFLData();
        flushSLData();
        featuresList.clear();
        activityList.clear();
        dateList.clear();
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
        for (int i=0;i<contDataBand;i++){
            Xstring+= valueListXBand[i] + " ," ;
            Ystring+= valueListYBand[i] + " ," ;
            Zstring+= valueListZBand[i] + " ," ;
        }

        Log.i("X", Xstring);
        Log.i("Y", Ystring);
        Log.i("Z", Zstring);
    }

    public double getStdDevX()
    {
        return stdDevXPhone;
    }

    public double getXmeansModule()
    {
        return XmeansModulePhone;
    }

    public double getYmeansModule()
    {
        return YmeansModulePhone;
    }

    public double getZmeansModule()
    {
        return ZmeansModulePhone;
    }

    public double getStdDevXYZ()
    {
        return stdDevXYZPhone;
    }

    public double getEnergyZ()
    {
        return ZEnergyPhone;
    }

    public double getXmeansBand()
    {
        return XmeansBand;
    }

    public double getZmeansBand()
    {
        return ZmeansBand;
    }

    public double getStdDevZBand()
    {
        return stdDevZBand;
    }

    public double getStdDevXBand()
    {
        return stdDevXBand;
    }

    public double getXEnergyBand()
    {
        return XEnergyBand;
    }

    public double getZEnergyBand()
    {
        return ZEnergyBand;
    }

    public  void setActivityData(List<String> vettActivity, List<String> vettData ,List<String> featurevett){
        activityList.clear();
        dateList.clear();
        featuresList.clear();
        activityList=vettActivity;
        dateList=vettData;
        featuresList=featurevett;
    }

    public List<String> getActivityList(){
        return activityList;
    }

    public List<String> getDateList(){
        return dateList;
    }

    public List<String> getFeaturesList(){
        return featuresList;
    }

    public void pushTime(String time)
    {
        currentTime= time;
    }

    public String popTime()
    {
        return currentTime;
    }

    public void restartTime()
    {
        currentTime= "00:00 min";
    }

    public String BeaconIdToRoom(String id)
    {
        switch(id)
        {
            case "24543":
                return "bathroom";

            case "44943":
                return "kitchen";

            case "66435":
                return "livingroom";

            case "8679":
                return "bedroom";

            default:
                return "none";
        }
    }

    public static String getFeaturAtId(int Id)
    {
        return featuresList.get(Id);
    }


}

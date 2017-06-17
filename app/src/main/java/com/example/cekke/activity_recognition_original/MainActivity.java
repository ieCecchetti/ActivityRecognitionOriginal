package com.example.cekke.activity_recognition_original;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.BandGsrEvent;
import com.microsoft.band.sensors.BandGsrEventListener;
import com.microsoft.band.sensors.BandGyroscopeEvent;
import com.microsoft.band.sensors.BandGyroscopeEventListener;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.BandRRIntervalEvent;
import com.microsoft.band.sensors.BandRRIntervalEventListener;
import com.microsoft.band.sensors.BandSkinTemperatureEvent;
import com.microsoft.band.sensors.BandSkinTemperatureEventListener;
import com.microsoft.band.sensors.GsrSampleRate;
import com.microsoft.band.sensors.HeartRateConsentListener;
import com.microsoft.band.sensors.SampleRate;
import net.sf.javaml.utils.MathUtils;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements SensorEventListener,BeaconConsumer {

    //-----------------------------------------------------------settings option
    public static boolean phoneRec=true;
    public static boolean bandRec=true;
    public static boolean beaconRec=true;
    public static String FolderNameStandard="ActivityRec_data";

    //-----------------------------------------------------------notification option
    Notification.Builder notification;
    private static final int uniqueId = 45612;
    NotificationManager nm;

    //-----------------------------------------------------------fragments
    private Fragment_1intro fragIntro= new Fragment_1intro();
    private Fragment_2recorder fragRecorder= new Fragment_2recorder();
    private Fragment_3status fragStatus= new Fragment_3status();
    private Fragment_4show fragShow= new Fragment_4show();
    public static Utils projectUtils= new Utils();
    public static ActivityRecStaticPart staticCalculatorObj= new ActivityRecStaticPart();

    //-----------------------------------------------------------global variables
    private Sensor accellerometer;
    private Sensor gyroscope;
    private SensorManager SM;
    private SensorManager SM1;
    private boolean exit=false;
    public static String firstLayer;
    public static String secondLayer;
    public static int nRip=0;
    public static int maxRip=6;
    private static List<String> activityList;
    private static List<String> dateList;
    private static List<String> featureList;

    private static Context context;
    private static Activity activity;

    //-----------------------------------------------------------band variables

    public static FileOutputStream HRos=null;
    public static FileOutputStream GSRos=null;
    public static FileOutputStream RRos=null;
    public static FileOutputStream STos=null;
    public static FileOutputStream Accos=null;
    public static FileOutputStream Gyros=null;
    public static FileOutputStream Taccos=null;

    public static File HRlog;
    public static File GSRlog;
    public static File RRlog;
    public static File STlog;
    public static File Acclog;
    public static File Gyrolog;

    //-----------------------------------------------------------beacons variables
    protected final String TAG = "RangingActivity";
    private BeaconManager beaconManager;
    private String beaconsDesc;
    public static String NearestBeaconId="";
    public static double NearestBeaconDistance=5.0;

    //-----------------------------------------------------------folder and general path
    public static String appFolder="ActivityRecognitionOfficial";
    public static String path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    public static String folder="ActivityRec_data";
    public static final String folderBand="Band";
    public static final String folderBeacons="Beacons";
    public static final String folderPhoneData="Phone";

    //-----------------------------------------------------------define the time for checking devices
    public static boolean phoneStatus=false;
    public static boolean bandStatus=false;
    public static boolean beaconStatus=false;
    public static boolean recorderWorkingStatus=false;
    public static int m_interval = 1000; // 1 seconds by default, can be changed later
    public static Handler m_handler;
    private static BandClient client = null;

    //-----------------------------------------------------------define the starting folder
    public static EditText eTdirectory;

    //-----------------------------------------------------------intro
    private long time=0;

    //-----------------------------------------------------------status checker
    private static boolean stateHros;
    private static boolean stateGSRos;
    private static boolean stateRRos;
    private static boolean stateSTos;
    private static boolean stateAccos;
    private static boolean stateGyros;
    private static boolean stateTaccos;
    private static boolean checker;






    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        MainActivity.context= getApplicationContext();
        activity= this;
        //settings reset code
        //PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().clear().apply();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        phoneRec=prefs.getBoolean("example_switch_phone",true);
        beaconRec=prefs.getBoolean("example_switch_beacons",true);
        bandRec=prefs.getBoolean("example_switch_band",true);

        //notification init code
        notification = new Notification.Builder(this);
        notification.setAutoCancel(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //fragment2_Record variable init
        SM=(SensorManager)getSystemService(SENSOR_SERVICE);
        accellerometer= SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //SM.registerListener(this, accellerometer,SensorManager.SENSOR_DELAY_NORMAL); //0ms
        SM.registerListener(this, accellerometer,SensorManager.SENSOR_DELAY_GAME); //20ms ->50data per second
        //SM.registerListener(this, accellerometer,SensorManager.SENSOR_DELAY_UI); //60ms
        //SM.registerListener(this, accellerometer,SensorManager.SENSOR_DELAY_FASTEST); //200ms
        SM1=(SensorManager)getSystemService(SENSOR_SERVICE);
        gyroscope= SM1.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //SM1.registerListener(this, accellerometer,SensorManager.SENSOR_DELAY_NORMAL); //0ms
        SM1.registerListener(this, gyroscope,SensorManager.SENSOR_DELAY_GAME); //20ms
        //SM.registerListener(this, accellerometer,SensorManager.SENSOR_DELAY_UI); //60ms
        //SM.registerListener(this, accellerometer,SensorManager.SENSOR_DELAY_FASTEST); //200ms

        activityList= new ArrayList<String>();
        dateList= new ArrayList<String>();
        featureList= new ArrayList<String>();


        //--------------------------------------------------------------------------BEACONS SETTINGS
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        /*
            m - matching byte sequence for this beacon type to parse (exactly one required)

            s - ServiceUuid for this beacon type to parse (optional, only for Gatt-based beacons)

            i - identifier (at least one required, multiple allowed)

            p - power calibration field (exactly one required)

            d - data field (optional, multiple allowed)

            x - extra layout. Signifies that the layout is secondary to a primary layout with the same matching byte sequence (or ServiceUuid). Extra layouts do not require power or identifier fields and create Beacon objects without identifiers.

            Example of a parser string for AltBeacon:

            "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"
         */
        beaconManager.bind(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Intent = new Intent(view.getContext(), SettingsActivity.class);
                view.getContext().startActivity(Intent);
            }
        });

        startBandRecording();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //startRepeatingTask();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SM.unregisterListener(this);
        SM1.unregisterListener(this);
        //stopRepeatingTask();
    }

    @Override
    public void onBackPressed() {
        if (exit)
        {
            this.finish();
        }else{
            exit=true;
            Toast.makeText(getBaseContext(),"Press return to shut application!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        beaconManager.unbind(this);
        if (client != null) {
            try {
                client.disconnect().await();
                HRos.close();
                GSRos.close();
                RRos.close();
                STos.close();
                Accos.close();
                Gyros.close();
                Taccos.close();
            } catch (InterruptedException e) {
                // Do nothing as this is happening during destroy
            } catch (BandException e) {
                // Do nothing as this is happening during destroy
            } catch (IOException e) {

            }
        }
        nm.cancel(uniqueId);
        super.onDestroy();
    }

    final WeakReference<Activity> reference = new WeakReference<Activity>(this);


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    //Fragment_1intro fragIntro= new Fragment_1intro();
                    return fragIntro;
                case 1:
                    //Fragment_2recorder fragRecorder= new Fragment_2recorder();
                    return fragRecorder;
                case 2:
                    //Fragment_3status fragShow= new Fragment_4show();
                    return fragStatus;
                case 3:
                    //Fragment_4show fragShow= new Fragment_4show();
                    return fragShow;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "INTRO";
                case 1:
                    return "RECORD";
                case 2:
                    return "STATUS";
                case 3:
                    return "HISTORY";

            }
            return null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (staticCalculatorObj.isStarted() && phoneRec)
        {
            phoneStatus=staticCalculatorObj.addPhoneData(event);
            savePhoneSens(event);
        }
        if(staticCalculatorObj.isReadyFL())
        {
            firstLayer=staticCalculatorObj.getFirstLayerActivity();
        }
        if(staticCalculatorObj.isReadySL())
        {
            secondLayer=staticCalculatorObj.getSecondLayerActivity();
            //stopRepeatingTask();
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static void getRecordersFromSettings(boolean phone, boolean beacon, boolean band)
    {
        phoneRec= phone;
        beaconRec=beacon;
        bandRec=band;
    }
    public static String getStandardFolderName()
    {
        return FolderNameStandard;
    }

    public static void setStandardFolderName(String name)
    {
        FolderNameStandard=name;
    }

    @Override
    protected void onUserLeaveHint()
    {
        super.onUserLeaveHint();

        notification.setSmallIcon(R.drawable.activityrec);
        notification.setTicker(getResources().getString(R.string.main_notification_ticker));
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle(getResources().getString(R.string.main_notification_title));
        notification.setContentText(getResources().getString(R.string.main_notification_content));

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent= PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(notificationPendingIntent);

        //Build notification and issues it
        nm= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueId, notification.build());
    }

    public static Context getContext(){
        return MainActivity.context;
    }

    public static Context getActivity(){
        return activity;
    }

    public static void RecorderWorkingStatusON()
    {
        recorderWorkingStatus=true;
    }

    public static void RecorderWorkingStatusOFF()
    {
        recorderWorkingStatus=false;
    }

    public static boolean initNewSession() {

        RecorderWorkingStatusON();
        if(!projectUtils.checkBluetoothStatus()){
            Toast.makeText(getContext(),
                    "Bluetooth disabled, impossible to connect with beacons and band!",Toast.LENGTH_LONG).show();
        }
        beaconInit();
        staticCalculatorObj.flushAllData();
        path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        File file = new File(path, appFolder);
        if (!file.exists()) {
            try {
                file.mkdir();
            } catch (SecurityException e) {
            }
        }
        path+= "/"+appFolder;

        file = new File(path, folder);
        if (!file.exists()) {
            try {
                file.mkdir();
            } catch (SecurityException e) {
            }
        }

        if(bandRec)
        {
            file = new File(path+"/"+ folder, folderBand);

            if (!file.exists()) {
                try {
                    file.mkdir();
                } catch (SecurityException e) {
                }
            }

            HRlog = new File(file,"HRlog.txt");
            GSRlog = new File(file,"GSRlog.txt");
            RRlog = new File(file,"RRlog.txt");
            STlog = new File(file,"STlog.txt");
            Acclog = new File(file,"Acclog.txt");
            Gyrolog = new File(file,"Gyrolog.txt");



            try {
                HRlog.createNewFile();
                GSRlog.createNewFile();
                RRlog.createNewFile();
                STlog.createNewFile();
                Acclog.createNewFile();
                Gyrolog.createNewFile();
            } catch (IOException e) {
                Log.d("App",String.format("Errore: "+e));
            }
            try {
                HRos = new FileOutputStream(HRlog);
                GSRos = new FileOutputStream(GSRlog);
                RRos = new FileOutputStream(RRlog);
                STos = new FileOutputStream(STlog);
                Accos = new FileOutputStream(Acclog);
                Gyros = new FileOutputStream(Gyrolog);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    static Runnable m_statusChecker = new Runnable()
    {
        @Override
        public void run() {
            String Time = Utils.TimeIncr(staticCalculatorObj.popTime().substring(0,4));
            staticCalculatorObj.pushTime(Time);
            Fragment_2recorder.setRecordTime(Time);
            bandStatus= checkAllBandStatus();
            beaconStatus= projectUtils.checkBluetoothStatus(); //beacon research start automatically with Bluetooth service start
            Fragment_3status.printListenerStatus(phoneStatus, beaconStatus, bandStatus);
            refreshListenerStatus(); //this function can change value of m_interval.
            m_handler.postDelayed(m_statusChecker, m_interval);
        }
    };

    public static void startRepeatingTask()
    {
        m_statusChecker.run();
    }

    public static void stopRepeatingTask()
    {
        RecorderWorkingStatusOFF();
        m_handler.removeCallbacks(m_statusChecker);
        Fragment_2recorder.stopRecord();
    }



    public static void creatorStatusLooper()
    {
        //DEFINING THE TREAD FOR CHECKING DEVICE STATUS
        m_handler = new Handler();
        staticCalculatorObj.restartTime();
    }

    public static void refreshListenerStatus(){
        phoneStatus=false;
        bandStatus=false;
        beaconStatus=false;
        resetAllBandStatus();
    }

    public static boolean checkAllBandStatus()
    {
        //Log.i("CHECK",stateHros+"+"+stateRRos+"+"+stateSTos+"+"+stateAccos+"+"+stateGSRos+"+"+stateGyros);
        //return stateHros&&stateRRos&&stateSTos&&stateAccos&&stateGSRos&&stateGyros;
        return stateAccos&&stateGyros;

    }

    public static void resetAllBandStatus(){
        stateGSRos=false;
        stateAccos=false;
        stateRRos=false;
        stateGyros=false;
        stateHros=false;
    }

    private void savePhoneSens(SensorEvent event)
    {
        String dati = null;
        String sensType="sensor";
        int eType = event.sensor.getType();
        if (eType == Sensor.TYPE_ACCELEROMETER) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
            Calendar c = Calendar.getInstance();
            dati = String.valueOf(event.values[0]) +
                    " " + String.valueOf(event.values[1]) +
                    " " + String.valueOf(event.values[2]) +
                    " " + sdf.format(c.getTime()) + ";\n";
            sensType = "Accelerometer";
        } else if (eType == Sensor.TYPE_GYROSCOPE) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
            Calendar c = Calendar.getInstance();
            dati = String.valueOf(event.values[0]) +
                    " " + String.valueOf(event.values[1]) +
                    " " + String.valueOf(event.values[2]) +
                    " " + sdf.format(c.getTime()) + ";\n";
            sensType = "Gyroscope";
        }
        File root = new File(path+ "/" + folder, folderPhoneData);
        if (!root.exists()) {
            root.mkdirs(); // this will create folder.
        }


        OutputStreamWriter outStreamWriter = null;
        File out = new File(root, sensType + ".txt");  // file path to save

        if (out.exists() == false) {
            try {
                out.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Taccos = new FileOutputStream(out, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        outStreamWriter = new OutputStreamWriter(Taccos);

        try {
            outStreamWriter.append(dati);
            phoneStatus = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outStreamWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void abortRecording(){
        staticCalculatorObj.flushAllData();
        activityList.clear();
        staticCalculatorObj.StopRecFirstLayer();
        staticCalculatorObj.StopRecSecondLayer();
        nRip=maxRip;
    }

    public static void activityLoop()
    {
        if (!recorderWorkingStatus)
        {
            abortRecording();
            return;
        }
        if(nRip!=maxRip)
        {
            Handler PhoneBandWindow = new Handler();
            PhoneBandWindow.postDelayed(new Runnable() {
                public void run() {
                    // Start Recording after 4 seconds (waiting for clean data)
                    //record band and phone for the first layer of activity
                    staticCalculatorObj.flushFLData();
                    staticCalculatorObj.flushSLData();
                    staticCalculatorObj.StartRecFirstLayer();
                    staticCalculatorObj.StartRecSecondLayer();
                }
            }, 4000);
            Handler BandWindow = new Handler();
            BandWindow.postDelayed(new Runnable() {
                public void run() {
                    // Start Recording for the second layer
                    firstLayer = staticCalculatorObj.getFirstLayerActivity();
                    secondLayer = staticCalculatorObj.getSecondLayerActivity();
                    Fragment_2recorder.setDataFromMainClass1L(String.valueOf(staticCalculatorObj.getStdDevX()), String.valueOf(staticCalculatorObj.getXmeansModule()), String.valueOf(staticCalculatorObj.getYmeansModule()),
                            String.valueOf(staticCalculatorObj.getZmeansModule()), String.valueOf(staticCalculatorObj.getStdDevXYZ()), String.valueOf(staticCalculatorObj.getEnergyZ()), firstLayer);
                    Fragment_2recorder.setDataFromMainClass2L(String.valueOf(staticCalculatorObj.getXmeansBand()), String.valueOf(staticCalculatorObj.getZmeansBand()), String.valueOf(staticCalculatorObj.getStdDevZBand()),
                            String.valueOf(staticCalculatorObj.getXEnergyBand()),String.valueOf(staticCalculatorObj.getZEnergyBand()), secondLayer);
                    staticCalculatorObj.StopRecSecondLayer();
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100);
                    toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
                    Calendar c = Calendar.getInstance();
                    projectUtils.saveInFile(path, folder, "ActivityList", (staticCalculatorObj.getSecondLayerActivity().split(":")[1]+" "+sdf.format(c.getTime()) + ";\n"));
                    activityList.add(secondLayer.split(":")[1]);
                    featureList.add("Xmean: "+staticCalculatorObj.getXmeansBand()+"\nZmean: "+staticCalculatorObj.getZmeansBand()+"\nZdevStd: "+
                                    staticCalculatorObj.getStdDevZBand()+"\nXenergy: "+staticCalculatorObj.getXEnergyBand()+"\nZenergy: "+staticCalculatorObj.getZEnergyBand()
                                    +"\n1Lyaer: "+firstLayer.split(":")[1]);
                    dateList.add(projectUtils.getDate());
                    activityLoop();
                }
            }, 10000);
            nRip++;
        }else{
            stopRepeatingTask();
            staticCalculatorObj.setActivityData(activityList, dateList, featureList);
            //Fragment_4show.printListView(activityList, dateList);
        }

    }


    //---------------------------------------------------------------------------------------------// band procedure

    private static BandGsrEventListener mGsrEventListener = new BandGsrEventListener() {
        @Override
        public void onBandGsrChanged(BandGsrEvent event) {
            if ((event != null)&&staticCalculatorObj.isStartedSLayer()&& bandRec) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
                Calendar c = Calendar.getInstance();
                String string = String.format(event.getResistance()+" "+sdf.format(c.getTime())+";\n\r");
                try {
                    stateGSRos=true;
                    GSRos.write(string.getBytes());
                } catch (IOException e) {
                    Log.i("bandError","Attenzione:scrittura fallita (GSR)");
                }
            }
        }
    };

    private static BandRRIntervalEventListener mRRIntervalEventListener = new BandRRIntervalEventListener() {
        @Override
        public void onBandRRIntervalChanged(final BandRRIntervalEvent event) {
            if ((event != null)&&staticCalculatorObj.isStartedSLayer()&& bandRec) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
                Calendar c = Calendar.getInstance();
                String string = String.format(event.getInterval()+" "+sdf.format(c.getTime())+";\n\r");
                try {
                    stateRRos=true;
                    RRos.write(string.getBytes());
                } catch (IOException e) {
                    Log.i("bandError","Attenzione:scrittura fallita (RR)");
                }
            }
        }
    };

    private static BandSkinTemperatureEventListener mSkinTemperatureEventListener = new BandSkinTemperatureEventListener() {
        @Override
        public void onBandSkinTemperatureChanged(BandSkinTemperatureEvent bandSkinTemperatureEvent) {
            if ((bandSkinTemperatureEvent != null)&&staticCalculatorObj.isStartedSLayer()&& bandRec) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
                Calendar c = Calendar.getInstance();
                String string = String.format(bandSkinTemperatureEvent.getTemperature()+" "+sdf.format(c.getTime())+";\n\r");
                try {
                    stateSTos=true;
                    STos.write(string.getBytes());
                } catch (IOException e) {
                    Log.i("bandError","Attenzione:scrittura fallita (ST)");
                }
            }
        }
    };
    private static BandAccelerometerEventListener accelerometerEventListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(BandAccelerometerEvent bandAccelerometerEvent) {
            if((bandAccelerometerEvent != null)&&staticCalculatorObj.isStartedSLayer()&& bandRec){
                //Log.i("mylog","bandAcc registra" +staticCalculatorObj.isStartedSLayer()+ " e "+ bandRec);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
                Calendar c = Calendar.getInstance();
                String string = String.valueOf(bandAccelerometerEvent.getAccelerationX()*9.8)+" "+
                        String.valueOf(bandAccelerometerEvent.getAccelerationY()*9.8)+" "+
                        String.valueOf(bandAccelerometerEvent.getAccelerationZ()*9.8)+ " "+
                        sdf.format(c.getTime())+";\n\r";
                try {
                    stateAccos=true;
                    Accos.write(string.getBytes());
                } catch (IOException e) {
                    Log.i("bandError","Attenzione:scrittura fallita (Acc)");
                }

                staticCalculatorObj.addBandData(bandAccelerometerEvent);
            }
        }

    };

    private static BandGyroscopeEventListener gyroscopeEventListener = new BandGyroscopeEventListener() {
        @Override
        public void onBandGyroscopeChanged(BandGyroscopeEvent bandGyroscopeEvent) {

            if((gyroscopeEventListener!= null)&&staticCalculatorObj.isStartedSLayer()&& bandRec){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
                Calendar c = Calendar.getInstance();
                String string = String.valueOf(bandGyroscopeEvent.getAngularVelocityX())+" "+
                        String.valueOf(bandGyroscopeEvent.getAngularVelocityY())+" "+
                        String.valueOf(bandGyroscopeEvent.getAngularVelocityZ())+ " "+
                        sdf.format(c.getTime())+";\n\r";
                try {
                    stateGyros=true;
                    Gyros.write(string.getBytes());
                } catch (IOException e) {
                    Log.i("bandError","Attenzione:scrittura fallita (Acc)");
                }
            }
        }
    };

    private BandHeartRateEventListener mHeartRateEventListener = new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(final BandHeartRateEvent event) {
            if ((event != null)&&staticCalculatorObj.isStartedSLayer()&& bandRec) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
                Calendar c = Calendar.getInstance();
                String string = String.format(event.getHeartRate()+" "+sdf.format(c.getTime())+";\n\r");
                try {
                    stateHros=true;
                    HRos.write(string.getBytes());
                } catch (IOException e) {
                    Log.i("bandError","Attenzione:scrittura fallita (HR)");
                }
            }
        }
    };

    private class GsrSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    Log.i("bandError","Band is connected.\n");
                    client.getSensorManager().registerGsrEventListener(mGsrEventListener, GsrSampleRate.MS200);
                } else {
                    Log.i("bandError","Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                Log.i("bandError",exceptionMessage);

            } catch (Exception e) {
                Log.i("bandError",e.getMessage());
            }
            return null;
        }
    }

    private class SkinTemperatureSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    Log.i("bandError","Band is connected.\n");
                    client.getSensorManager().registerSkinTemperatureEventListener(mSkinTemperatureEventListener);
                } else {
                    Log.i("bandError","Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                Log.i("bandError",exceptionMessage);

            } catch (Exception e) {
                Log.i("bandError",e.getMessage());
            }
            return null;
        }
    }

    private class RRIntervalSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    int hardwareVersion = Integer.parseInt(client.getHardwareVersion().await());
                    if (hardwareVersion >= 20) {
                        if (client.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                            client.getSensorManager().registerRRIntervalEventListener(mRRIntervalEventListener);
                        } else {
                            Log.i("bandError","You have not given this application consent to access heart rate data yet."
                                    + " Please press the Heart Rate Consent button.\n");
                        }
                    } else {
                        Log.i("bandError","The RR Interval sensor is not supported with your Band version. Microsoft Band 2 is required.\n");
                    }
                } else {
                    Log.i("bandError","Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                Log.i("bandError",exceptionMessage);

            } catch (Exception e) {
                Log.i("bandError",e.getMessage());
            }
            return null;
        }
    }

    private class HeartRateSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    if (client.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                        client.getSensorManager().registerHeartRateEventListener(mHeartRateEventListener);
                    } else {
                        Log.i("bandError","You have not given this application consent to access heart rate data yet."
                                + " Please press the Heart Rate Consent button.\n");
                    }
                } else {
                    Log.i("bandError","Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                Log.i("bandError",exceptionMessage);

            } catch (Exception e) {
                Log.i("bandError",e.getMessage());
            }
            return null;
        }
    }
    private class AccelerometerSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    Log.i("bandError","Band is connected.\n");
                    client.getSensorManager().registerAccelerometerEventListener(accelerometerEventListener, SampleRate.MS32);
                } else {
                    Log.i("bandError","Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                Log.i("bandError",exceptionMessage);

            } catch (Exception e) {
                Log.i("bandError",e.getMessage());
            }
            return null;
        }
    }

    private class GyroscopeSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    Log.i("bandError","Band is connected.\n");
                    client.getSensorManager().registerGyroscopeEventListener(gyroscopeEventListener, SampleRate.MS32);
                } else {
                    Log.i("bandError","Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                Log.i("bandError",exceptionMessage);

            } catch (Exception e) {
                Log.i("bandError",e.getMessage());
            }
            return null;
        }
    }

    private class HeartRateConsentTask extends AsyncTask<WeakReference<Activity>, Void, Void> {
        @Override
        protected Void doInBackground(WeakReference<Activity>... params) {
            Log.d("Lucia",String.format("Sto chiedendo il consenso"));
            try {
                if (getConnectedBandClient()) {

                    if (params[0].get() != null) {
                        client.getSensorManager().requestHeartRateConsent(params[0].get(), new HeartRateConsentListener() {
                            @Override
                            public void userAccepted(boolean consentGiven) {
                            }
                        });
                    }
                } else {
                    Log.i("bandError","Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                Log.i("bandError",exceptionMessage);

            } catch (Exception e) {
                Log.i("bandError",e.getMessage());
            }
            return null;
        }
    }

    private  boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                Log.i("bandError","Band isn't paired with your phone.\n");
                return false;
            }
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }
        Log.i("bandError","Band is connecting...\n");
        return ConnectionState.CONNECTED == client.connect().await();
    }

    public void startBandRecording()
    {
        new HeartRateSubscriptionTask().execute();
        new GsrSubscriptionTask().execute();
        new RRIntervalSubscriptionTask().execute();
        new SkinTemperatureSubscriptionTask().execute();
        new AccelerometerSubscriptionTask().execute();
        new GyroscopeSubscriptionTask().execute();
        //new HeartRateConsentTask().execute(reference);
        Log.i("mylog","bandTask creati");
    }

    @Override
    public void onBeaconServiceConnect() {

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if ((beacons.size() > 0)&&staticCalculatorObj.isStarted() && beaconRec) {
                    //stamp for the number of beacons in region
                    beaconStatus = true;

                    for (Beacon beacon : beacons) {
                        beaconsDesc = beacon.getId2() +
                                " " + beacon.getId3() +
                                " " + beacon.getRssi() + " ";

                        if(NearestBeaconDistance>beacon.getDistance())
                        {
                            NearestBeaconDistance=beacon.getDistance();
                            NearestBeaconId=beacon.getId2().toString();
                        }

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
                        Calendar c = Calendar.getInstance();
                        beaconsDesc += sdf.format(c.getTime()) + ";\n";
                        File root = new File(path+ "/" + folder, folderBeacons);
                        if (!root.exists()) {
                            root.mkdirs(); // this will create folder.
                        }
                        OutputStreamWriter outStreamWriter = null;
                        File out = new File(root, "Beacons.txt");  // file path to save

                        if (out.exists() == false) {
                            try {
                                out.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            Taccos = new FileOutputStream(out, true);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        outStreamWriter = new OutputStreamWriter(Taccos);

                        try {
                            outStreamWriter.append(beaconsDesc);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            outStreamWriter.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }

    }

    public static void printNearestBeacon(){
        if (!NearestBeaconId.equals(""))
        {
            Toast.makeText(getContext(), "Nearest Beacon is :"+staticCalculatorObj.BeaconIdToRoom(NearestBeaconId), Toast.LENGTH_SHORT).show();
            //Log.i("beacon","nearest is "+NearestBeaconId);
        }
        Toast.makeText(getContext(), "No beacon altready registered!", Toast.LENGTH_SHORT).show();
    }

    private static void beaconInit()
    {
        NearestBeaconId="";
        NearestBeaconDistance=10;
    }





}

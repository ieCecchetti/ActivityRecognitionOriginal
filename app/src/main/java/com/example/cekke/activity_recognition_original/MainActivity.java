package com.example.cekke.activity_recognition_original;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import net.sf.javaml.utils.MathUtils;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //-----------------------------------------------------------settings option
    public static boolean phoneRec=true;
    public static boolean bandRec=true;
    public static boolean beaconRec=true;
    public static String FolderNameStandard="ActivityRec_data";

    //-----------------------------------------------------------notification option
    Notification.Builder notification;
    private static final int uniqueId = 45612;

    //-----------------------------------------------------------fragments
    private Fragment_1intro fragIntro= new Fragment_1intro();
    private Fragment_2recorder fragRecorder= new Fragment_2recorder();
    private Fragment_3status fragStatus= new Fragment_3status();
    private Fragment_4show fragShow= new Fragment_4show();

    //-----------------------------------------------------------global variables
    private Sensor accellerometer;
    private SensorManager SM;
    public static double[] valueListX = new double[100];
    public static double[] valueListY = new double[100];
    public static double[] valueListZ = new double[100];
    public static boolean started=false;
    public static int contData=0;
    public static double stdDevX;
    public static double XmeansModule;
    public static double YmeansModule;
    public static double ZmeansModule;
    public static double stdDevXYZ;


    public static String currentActivity="Activity : None";




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

        //settings reset code
        //PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().clear().apply();

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
        SM.registerListener(this, accellerometer,SensorManager.SENSOR_DELAY_GAME); //20ms
        //SM.registerListener(this, accellerometer,SensorManager.SENSOR_DELAY_UI); //60ms
        //SM.registerListener(this, accellerometer,SensorManager.SENSOR_DELAY_FASTEST); //200ms


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Intent = new Intent(view.getContext(), SettingsActivity.class);
                view.getContext().startActivity(Intent);
            }
        });



    }


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

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(started)
        {
            if(contData!=99){
                contData++;

                valueListX[contData]=(event.values[0]);
                valueListY[contData]=(event.values[1]);
                valueListZ[contData]=(event.values[2]);


            }else{
                contData=0;
                started=false;
                //Toast.makeText(MainActivity.this,"Record has finished!",Toast.LENGTH_SHORT).show();
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100);
                //Toast.makeText(getContext() ,"Recording has stopped!",Toast.LENGTH_SHORT).show();
                processData();
                fragRecorder.getDataFromMainClass();
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
                    return "RECORDER";
                case 2:
                    return "STATUS";
                case 3:
                    return "HISTORY";

            }
            return null;
        }
    }

    public static void getRecordersFromSettings(boolean phone, boolean beacon, boolean band)
    {
        phoneRec= phone;
        beaconRec=beacon;
        bandRec=band;
    }
    public static void getStandardFolderName(String name)
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
        NotificationManager nm= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueId, notification.build());
    }

    private void processData(){
        filterData();
        double Xmeans=MathUtils.arithmicMean(valueListX);
        double Ymeans=MathUtils.arithmicMean(valueListY);
        double Zmeans= MathUtils.arithmicMean(valueListZ);

        stdDevX= Math.sqrt(variance(valueListX,Xmeans,100));
        //feature1.setText(String.valueOf(stdDevX));

        XmeansModule=Math.abs(Xmeans);
        //feature2.setText(String.valueOf(XmeansModule));
        YmeansModule=Math.abs(Ymeans);
        //feature3.setText(String.valueOf(YmeansModule));
        ZmeansModule=Math.abs(Zmeans);
        //feature4.setText(String.valueOf(ZmeansModule));

        stdDevXYZ = Math.sqrt(variance(valueListX,Xmeans,100)+
                variance(valueListX,Xmeans,100)+
                variance(valueListX,Xmeans,100));
        //feature5.setText(String.valueOf(stdDevXYZ));



        //posizione?
        if (YmeansModule>8.8)
        {
            //posizione eretta
            if(stdDevXYZ>1.2  && stdDevX>0.7)
            {
                //walking
                currentActivity="Activity : WALKING";
            }else{
                currentActivity="Activity : STANDING";
            }
        }else{
            //sitting
            if ((XmeansModule>1.3) || (stdDevX)>0.32) //se la stdDev è alta solitamente siamo in sitting
            {
                if(stdDevX<0.1) // se stdDev<0.1 si raggiunge solitamente solo da lying
                {
                    //caso limite
                    currentActivity="Activity : LYING";

                }else{
                    currentActivity="Activity : SITTING";
                }

            }
            else{
                currentActivity="Activity : LYING";
            }
            //lying sul lato
            if (ZmeansModule<8.5){
                if(Ymeans>1)
                {
                    currentActivity="Activity : SITTING";
                }else{
                    currentActivity="Activity : LYING";
                }
            }
        }



    }

    private void filterData(){
        Double sumX;
        Double sumY;
        Double sumZ;
        for (int i=4;i<96;i++)
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
        return sum/(100-1);
    }
}

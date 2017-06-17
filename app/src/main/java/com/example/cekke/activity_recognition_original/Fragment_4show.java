package com.example.cekke.activity_recognition_original;

/**
 * Created by cekke on 17/05/2017.
 */

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.zip.Inflater;

public class Fragment_4show extends Fragment {
    private static TextView Id,Activity,DateTime;
    private static TableLayout tableActivity;
    private static TableRow tableActivityRow;
    private static Context context ;
    private static Activity activity ;
    private ImageButton ib_search;
    private static int CurrentNum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_4show, container, false);

        tableActivity= (TableLayout) rootView.findViewById(R.id.tableresult);
        tableActivity.setColumnStretchable(0,true);
        tableActivity.setColumnStretchable(1,true);
        tableActivity.setColumnStretchable(2,true);
        ib_search= (ImageButton) rootView.findViewById(R.id.iv_buttonSearch);
        ib_search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.getContext(),"Opening a other ActivityList",Toast.LENGTH_SHORT).show();
            }
        });

        context = getContext();

        return rootView;
    }

    private static void addActivityLog(int id, String action, String datetime)
    {
        tableActivityRow = new TableRow(context);
        tableActivityRow.setId(id);
        tableActivityRow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                rowClick(v);
            }
        });
        Id = new TextView(context);
        Activity = new TextView(context);
        DateTime = new TextView(context);
        Id.setText(String.valueOf(id));
        Activity.setText(action);
        DateTime.setText(datetime);
        Id.setGravity(Gravity.CENTER);
        Id.setPadding(50,50,50,50);
        Id.setBackgroundColor(Color.parseColor("#E8EAF6"));
        Activity.setGravity(Gravity.CENTER);
        Activity.setPadding(50,50,50,50);
        Activity.setBackgroundColor(Color.parseColor("#E8EAF6"));
        DateTime.setGravity(Gravity.CENTER);
        DateTime.setPadding(50,50,50,50);
        DateTime.setBackgroundColor(Color.parseColor("#E8EAF6"));
        tableActivityRow.addView(Id);
        tableActivityRow.addView(Activity);
        tableActivityRow.addView(DateTime);
        tableActivity.addView(tableActivityRow);
    }

    public void printListView(List<String> vettActivity, List<String> vettData, List<String> vettFeatures)
    {
        if(vettActivity.size()!= 0)
        {
            refreshFragment();
            for(int i=0; i<vettActivity.size(); i++)
            {
                addActivityLog(i, vettActivity.get(i), vettData.get(i));
            }
            CurrentNum=vettActivity.size();
        }
    }

    public void refreshFragment()
    {
        if (CurrentNum!=0)
        {
            for(int i=0; i<CurrentNum;i++)
            {
                tableActivity.removeView(getView().findViewById(i));
            }
        }

    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            printListView(MainActivity.staticCalculatorObj.getActivityList(),MainActivity.staticCalculatorObj.getDateList(), MainActivity.staticCalculatorObj.getFeaturesList());
        }
    }

    public static void rowClick(View view) {
        Toast.makeText(MainActivity.getContext(),MainActivity.staticCalculatorObj.getFeaturAtId(view.getId()), Toast.LENGTH_SHORT).show();
    }




}

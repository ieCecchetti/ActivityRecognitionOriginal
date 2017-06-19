package com.example.cekke.activity_recognition_original;

/**
 * Created by cekke on 17/05/2017.
 */

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment_1intro extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_1intro, container, false);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapse_toolbar);
        collapsingToolbarLayout.setTitle("ACTIVITY PRECOG");

        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(rootView.getContext(),R.color.colorPrimaryDark));

        return rootView;
    }
}

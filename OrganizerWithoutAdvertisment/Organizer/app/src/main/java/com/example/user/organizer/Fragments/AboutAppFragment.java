package com.example.user.organizer.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.organizer.R;


public class AboutAppFragment extends Fragment {




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);


        View rootView =
                inflater.inflate(R.layout.fragment_about, container, false);
        FloatingActionButton fab = (FloatingActionButton)rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        //.setAction("Action", null).show();

            }
        });

        return rootView;
    }






}

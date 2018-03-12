package first.project.nikzhebindev.organizer.TabsFragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.Toast;

import first.project.nikzhebindev.organizer.R;

import static android.content.Context.MODE_PRIVATE;


public class Tab1Fragment extends Fragment{

    private static final String TAG = "Tab1Fragment";





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.theme1_fragment, container, false);


        final ImageButton imageButtonDefTheme = view.findViewById(R.id.imageButtonDefTheme);
        final ImageButton imageButtonDefThemeNight = view.findViewById(R.id.imageButtonDefThemeNight);
        final ImageButton imageButtonNightTheme = view.findViewById(R.id.imageButtonNightTheme);
        final ImageButton imageButtonTTheme = view.findViewById(R.id.imageButtonTTheme);


        imageButtonDefTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation anim = android.view.animation.AnimationUtils.loadAnimation(imageButtonDefTheme.getContext(),  R.anim.press_float_btn);
                imageButtonDefTheme.startAnimation(anim);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("THEME", "Default");
                        ed.apply();

                        ed.putString("ThemeWasChanged", "YES");
                        ed.apply();

                        getActivity().finish();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

            }
        });
        imageButtonDefTheme.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Animation anim = android.view.animation.AnimationUtils.loadAnimation(imageButtonDefTheme.getContext(),  R.anim.btn_theme_lonk_click);
                imageButtonDefTheme.startAnimation(anim);
                return false;
            }
        });



        imageButtonDefThemeNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("THEME", "DefaultThemeN");
                ed.apply();

                ed.putString("ThemeWasChanged", "YES");
                ed.apply();

                getActivity().finish();
            }
        });


        imageButtonNightTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("THEME", "NightTheme");
                ed.apply();

                ed.putString("ThemeWasChanged", "YES");
                ed.apply();

                getActivity().finish();
            }
        });


        imageButtonTTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("THEME", "TTheme");
                ed.apply();

                ed.putString("ThemeWasChanged", "YES");
                ed.apply();

                getActivity().finish();
            }
        });



        return view;
    }







}

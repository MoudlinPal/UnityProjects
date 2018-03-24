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

import first.project.nikzhebindev.organizer.R;



public class TabIndigoGreenPurpleFragment extends Fragment{

    private static final String TAG = "TabIndigoGreenPurpleFragment";





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.theme_indigo_green_purple_fragment, container, false);


        final ImageButton imageButtonIndigo = view.findViewById(R.id.imageButtonIndigo);
        final ImageButton imageButtonIndigoNight = view.findViewById(R.id.imageButtonIndigoNight);

        final ImageButton imageButtonGreen = view.findViewById(R.id.imageButtonGreen);
        final ImageButton imageButtonGreenNight = view.findViewById(R.id.imageButtonGreenNight);

        final ImageButton imageButtonPurple = view.findViewById(R.id.imageButtonPurple);
        final ImageButton imageButtonPurpleNight = view.findViewById(R.id.imageButtonPurpleNight);





        imageButtonIndigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation anim = android.view.animation.AnimationUtils.loadAnimation(imageButtonIndigo.getContext(),  R.anim.btn_theme_click);
                imageButtonIndigo.startAnimation(anim);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        imageButtonIndigo.setAlpha(0.0f);

                        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("THEME", "IndigoTheme");
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


        imageButtonIndigoNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation anim = android.view.animation.AnimationUtils.loadAnimation(imageButtonIndigoNight.getContext(),  R.anim.btn_theme_click);
                imageButtonIndigoNight.startAnimation(anim);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        imageButtonIndigoNight.setAlpha(0.0f);

                        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("THEME", "IndigoThemeN");
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











        imageButtonGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation anim = android.view.animation.AnimationUtils.loadAnimation(imageButtonGreen.getContext(),  R.anim.btn_theme_click);
                imageButtonGreen.startAnimation(anim);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        imageButtonGreen.setAlpha(0.0f);

                        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("THEME", "GreenTheme");
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


        imageButtonGreenNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation anim = android.view.animation.AnimationUtils.loadAnimation(imageButtonGreenNight.getContext(),  R.anim.btn_theme_click);
                imageButtonGreenNight.startAnimation(anim);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        imageButtonGreenNight.setAlpha(0.0f);

                        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("THEME", "GreenThemeN");
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

















        imageButtonPurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation anim = android.view.animation.AnimationUtils.loadAnimation(imageButtonPurple.getContext(),  R.anim.btn_theme_click);
                imageButtonPurple.startAnimation(anim);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        imageButtonPurple.setAlpha(0.0f);

                        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("THEME", "PurpleTheme");
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


        imageButtonPurpleNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation anim = android.view.animation.AnimationUtils.loadAnimation(imageButtonPurpleNight.getContext(),  R.anim.btn_theme_click);
                imageButtonPurpleNight.startAnimation(anim);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        imageButtonPurpleNight.setAlpha(0.0f);



                        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("THEME", "PurpleThemeN");
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




        return view;
    }







}

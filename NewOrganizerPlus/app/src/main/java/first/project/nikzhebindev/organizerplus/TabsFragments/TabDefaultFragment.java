package first.project.nikzhebindev.organizerplus.TabsFragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import first.project.nikzhebindev.organizerplus.Fragments.ThemesFragment;
import first.project.nikzhebindev.organizerplus.R;


public class TabDefaultFragment extends Fragment {



    private static final String TAG = "TabDefaultFragment";






    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.theme_default_fragment, container, false);


        final ImageButton imageButtonDefTheme = view.findViewById(R.id.imageButtonDefTheme);
        final ImageButton imageButtonDefThemeNight = view.findViewById(R.id.imageButtonDefThemeNight);
        final ImageButton imageButtonNightTheme = view.findViewById(R.id.imageButtonNightTheme);
        final ImageButton imageButtonTTheme = view.findViewById(R.id.imageButtonTTheme);






        /** //////////////////////////////////   Premium   ////////////////////////////////// */


        final ImageButton imageButtonDeepTheme = view.findViewById(R.id.imageButtonDeepTheme);

        imageButtonDeepTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Animation anim = android.view.animation.AnimationUtils.loadAnimation(imageButtonDeepTheme.getContext(), R.anim.btn_theme_click);
                    imageButtonDeepTheme.startAnimation(anim);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            imageButtonDeepTheme.setAlpha(0.0f);

                            SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            SharedPreferences.Editor ed = sPref.edit();
                            ed.putString("THEME", "LeoTheme");
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


        imageButtonTTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    Animation anim = android.view.animation.AnimationUtils.loadAnimation(imageButtonTTheme.getContext(), R.anim.btn_theme_click);
                    imageButtonTTheme.startAnimation(anim);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            imageButtonTTheme.setAlpha(0.0f);

                            SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            SharedPreferences.Editor ed = sPref.edit();
                            ed.putString("THEME", "TTheme");
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


        /** //////////////////////////////////   Premium   ////////////////////////////////// */







        imageButtonDefTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Animation anim = android.view.animation.AnimationUtils.loadAnimation(imageButtonDefTheme.getContext(), R.anim.btn_theme_click);
                    imageButtonDefTheme.startAnimation(anim);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            imageButtonDefTheme.setAlpha(0.0f);

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



        imageButtonDefThemeNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Animation anim = android.view.animation.AnimationUtils.loadAnimation(imageButtonDefThemeNight.getContext(), R.anim.btn_theme_click);
                    imageButtonDefThemeNight.startAnimation(anim);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            imageButtonDefThemeNight.setAlpha(0.0f);

                            SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            SharedPreferences.Editor ed = sPref.edit();
                            ed.putString("THEME", "DefaultThemeN");
                            ed.apply();

                            ed.putString("ThemeWasChanged", "YES");
                            ed.apply();

                            getActivity().finish();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

        }});


        imageButtonNightTheme.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){

                Animation anim = android.view.animation.AnimationUtils.loadAnimation(imageButtonNightTheme.getContext(), R.anim.btn_theme_click);
                imageButtonNightTheme.startAnimation(anim);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        imageButtonNightTheme.setAlpha(0.0f);

                        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("THEME", "NightTheme");
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
            }
        );



        return view;
    }









}

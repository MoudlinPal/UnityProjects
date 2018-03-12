package first.project.nikzhebindev.organizer.TabsFragments;

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

import first.project.nikzhebindev.organizer.Fragments.ThemesFragment;
import first.project.nikzhebindev.organizer.R;


public class TabRandomFragment extends Fragment {

    private static final String TAG = "TabRandomFragment";





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.theme_random_fragment, container, false);



        final ImageButton imageRandomTheme = view.findViewById(R.id.imageRandomTheme);




        /** //////////////////////////////////   Premium   ////////////////////////////////// */

        imageRandomTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isOnline()) {
                    Animation anim = android.view.animation.AnimationUtils.loadAnimation(imageRandomTheme.getContext(), R.anim.btn_theme_click);
                    imageRandomTheme.startAnimation(anim);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            imageRandomTheme.setAlpha(0.0f);

                            SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            SharedPreferences.Editor ed = sPref.edit();
                            ed.putString("AdVideoTheme", "RandomAd");
                            ed.apply();

                            ((ThemesFragment) getActivity()).loadRewardedVideoAd();

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                } else {
                    Animation anim = android.view.animation.AnimationUtils.loadAnimation(imageRandomTheme.getContext(), R.anim.shake);
                    imageRandomTheme.startAnimation(anim);
                    Toast.makeText(getActivity(), "Check your Internet connection!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        /** //////////////////////////////////   Premium   ////////////////////////////////// */





        return view;
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        try { netInfo = cm.getActiveNetworkInfo();} catch (Exception e) {
            //
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}

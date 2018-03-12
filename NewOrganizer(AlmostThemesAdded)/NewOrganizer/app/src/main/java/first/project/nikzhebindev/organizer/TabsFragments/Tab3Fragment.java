package first.project.nikzhebindev.organizer.TabsFragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import first.project.nikzhebindev.organizer.R;


public class Tab3Fragment extends Fragment{

    private static final String TAG = "Tab3Fragment";





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.theme3_fragment, container, false);


        ImageButton imageButtonGreen = view.findViewById(R.id.imageButtonGreen);
        ImageButton imageButtonGreenNight = view.findViewById(R.id.imageButtonGreenNight);


        imageButtonGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("THEME", "GreenTheme");
                ed.apply();

                ed.putString("ThemeWasChanged", "YES");
                ed.apply();

                getActivity().finish();
            }
        });


        imageButtonGreenNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("THEME", "GreenThemeN");
                ed.apply();

                ed.putString("ThemeWasChanged", "YES");
                ed.apply();

                getActivity().finish();
            }
        });


        return view;
    }







}

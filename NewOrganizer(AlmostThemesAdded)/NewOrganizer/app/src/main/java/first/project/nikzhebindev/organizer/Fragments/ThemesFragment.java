package first.project.nikzhebindev.organizer.Fragments;


import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import first.project.nikzhebindev.organizer.R;
import first.project.nikzhebindev.organizer.TabsFragments.SectionsPageAdapter;
import first.project.nikzhebindev.organizer.TabsFragments.Tab1Fragment;
import first.project.nikzhebindev.organizer.TabsFragments.Tab2Fragment;
import first.project.nikzhebindev.organizer.TabsFragments.Tab3Fragment;
import first.project.nikzhebindev.organizer.TabsFragments.Tab4Fragment;

public class ThemesFragment extends AppCompatActivity {

    private static final String TAG = "ThemesFragment";


    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes_fragment);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }



    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new Tab1Fragment(), "Default");
        adapter.addFragment(new Tab2Fragment(), "Indigo");
        adapter.addFragment(new Tab3Fragment(), "Green");
        adapter.addFragment(new Tab4Fragment(), "Purple");

        viewPager.setAdapter(adapter);
    }






}

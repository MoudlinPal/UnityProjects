package first.project.nikzhebindev.organizerplus.Fragments;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import first.project.nikzhebindev.organizerplus.R;
import first.project.nikzhebindev.organizerplus.TabsFragments.SectionsPageAdapter;
import first.project.nikzhebindev.organizerplus.TabsFragments.TabDefaultFragment;
import first.project.nikzhebindev.organizerplus.TabsFragments.TabIndigoGreenPurpleFragment;
import first.project.nikzhebindev.organizerplus.TabsFragments.TabRandomFragment;

public class ThemesFragment extends AppCompatActivity {


    /** /////////////////////////// Advertisement /////////////////////////// */
    //private RewardedVideoAd mRewardedVideoAd;
    /** /////////////////////////// Advertisement /////////////////////////// */



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


        /** /////////////////////////// Advertisement /////////////////////////// */
        // Sample AdMob app ID: ca-app-pub-5033052294993457~5980065515
        //MobileAds.initialize(this, "ca-app-pub-5033052294993457~5980065515");
        /** /////////////////////////// Advertisement /////////////////////////// */


        /** /////////////////////////// Advertisement /////////////////////////// */

        // Use an activity context to get the rewarded video instance.
        //mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        //mRewardedVideoAd.setRewardedVideoAdListener(this);

        //loadRewardedVideoAd();

        /** /////////////////////////// Advertisement /////////////////////////// */


    }





    /** /////////////////////////// Advertisement /////////////////////////// */
    /*public void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(getString(R.string.reward_video_ad),
                new AdRequest.Builder().build());
    }*/
    /** /////////////////////////// Advertisement /////////////////////////// */






    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new TabDefaultFragment(), "Default");
        adapter.addFragment(new TabIndigoGreenPurpleFragment(), "Indigo - Green - Purple");
        adapter.addFragment(new TabRandomFragment(), "Random Theme");

        viewPager.setAdapter(adapter);
    }








}

package first.project.nikzhebindev.organizer.Fragments;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import first.project.nikzhebindev.organizer.R;
import first.project.nikzhebindev.organizer.TabsFragments.SectionsPageAdapter;
import first.project.nikzhebindev.organizer.TabsFragments.TabDefaultFragment;
import first.project.nikzhebindev.organizer.TabsFragments.TabIndigoGreenPurpleFragment;
import first.project.nikzhebindev.organizer.TabsFragments.TabRandomFragment;

public class ThemesFragment extends AppCompatActivity implements RewardedVideoAdListener {


    /** /////////////////////////// Advertisement /////////////////////////// */
    private RewardedVideoAd mRewardedVideoAd;
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
        MobileAds.initialize(this, "ca-app-pub-5033052294993457~5980065515");
        /** /////////////////////////// Advertisement /////////////////////////// */


        /** /////////////////////////// Advertisement /////////////////////////// */

        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        //loadRewardedVideoAd();

        /** /////////////////////////// Advertisement /////////////////////////// */


    }





    /** /////////////////////////// Advertisement /////////////////////////// */
    public void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(getString(R.string.reward_video_ad),
                new AdRequest.Builder().build());
    }
    /** /////////////////////////// Advertisement /////////////////////////// */






    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new TabDefaultFragment(), "Default");
        adapter.addFragment(new TabIndigoGreenPurpleFragment(), "Indigo - Green - Purple");
        adapter.addFragment(new TabRandomFragment(), "Random Theme");

        viewPager.setAdapter(adapter);
    }










    @Override
    public void onRewardedVideoAdLoaded() {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

        recreate();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {


        SharedPreferences sPrefAd = PreferenceManager.getDefaultSharedPreferences(this);
        String AdVideoTheme = sPrefAd.getString("AdVideoTheme", "");

        if(AdVideoTheme.compareTo("LeoAd") == 0) {
            SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("THEME", "LeoTheme");
            ed.apply();

            ed.putString("ThemeWasChanged", "YES");
            ed.apply();

            // recover ad
            ed.putString("AdVideoTheme", "");
            ed.apply();

            finish();
        }


        //////////////////////////////////////////////////////////////////////////////////////////////

        else if(AdVideoTheme.compareTo("RandomAd") == 0) {
            SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("THEME", "RandomTheme");
            ed.apply();

            ed.putString("ThemeWasChanged", "YES");
            ed.apply();

            // recover ad
            ed.putString("AdVideoTheme", "");
            ed.apply();

            finish();
        }

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

        //finish();
        // Cancel set theme

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

        // Cancel set theme

    }

    @Override
    public void onRewardedVideoCompleted() {

    }
}

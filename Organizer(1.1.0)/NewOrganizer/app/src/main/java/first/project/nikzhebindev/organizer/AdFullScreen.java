package first.project.nikzhebindev.organizer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class AdFullScreen extends AppCompatActivity {


    private InterstitialAd mInterstitialAd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_ad_full_screen);

/*
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded(){
                if(mInterstitialAd.isLoaded()){
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                finish();
            }
        });
*/

    }
}

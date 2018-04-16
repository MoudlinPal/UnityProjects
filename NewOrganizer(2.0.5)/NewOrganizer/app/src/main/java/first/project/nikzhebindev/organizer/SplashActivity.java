package first.project.nikzhebindev.organizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.widget.ImageView;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //ImageView appIcon = findViewById(R.id.imageView_app_icon);
        //Animation anim = android.view.animation.AnimationUtils.loadAnimation(appIcon.getContext(),  R.anim.slide_to_down);
        //appIcon.startAnimation(anim);

        //ImageView nameIcon = findViewById(R.id.imageView_app_name);
        //Animation anim2 = android.view.animation.AnimationUtils.loadAnimation(appIcon.getContext(),  R.anim.slide_to_up);
        //nameIcon.startAnimation(anim2);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(
                        SplashActivity.this, MainMenu.class));
                finish();
            }
        }, 1000);


    }
}

package mudit.com.smartapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import static mudit.com.smartapp.R.id.imgView;

public class SplashScreen extends AppCompatActivity {
    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Typeface typeface=Typeface.createFromAsset(getAssets(),"fonts/Billabong.ttf");
        ((TextView)findViewById(R.id.tvFamilybrations)).setTypeface(typeface);
        imgView=(ImageView)findViewById(R.id.imgView);
//        Glide.with(MainActivity.this).load("android.resource://mudit.com.familybrations/drawable/splash").asGif().into(imgView);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imgView,1);
        Glide.with(SplashScreen.this).load(R.raw.splash).into(imageViewTarget);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Fire Intent
                Intent intent=new Intent(SplashScreen.this,MainActivity.class);
                startActivity(intent);
            }
        },4500);
    }
}

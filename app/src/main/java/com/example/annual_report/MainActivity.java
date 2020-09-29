package com.example.annual_report;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 4500;
    ImageView imageView;
    TextView textView;
    Animation top,bottom;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        imageView = findViewById(R.id.imageview);
        textView = findViewById(R.id.textview);

        top = AnimationUtils.loadAnimation(this,R.anim.top);
        bottom = AnimationUtils.loadAnimation(this,R.anim.bottom);

        imageView.setAnimation(top);
        textView.setAnimation(bottom);

        preferences = getSharedPreferences("login", Context.MODE_PRIVATE);


        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run() {
                if (preferences.contains("isUserLogin")) {
                    Intent intent = new Intent(MainActivity.this, navigation.class);
                    intent.putExtra("doremonuser",preferences.getString("ShrEmail","NoUserfound"));
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(MainActivity.this, login.class);
                    startActivity(intent);
                }
                finish();
            }
        },SPLASH_SCREEN);
    }

}


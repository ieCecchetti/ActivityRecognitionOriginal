package com.example.cekke.activity_recognition_original;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class IntroActivity extends AppCompatActivity {

    //intro animation
    private static int SHOW_TIME=3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_intro);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent startApp = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(startApp);
                finish();
            }
        },SHOW_TIME);

    }
}

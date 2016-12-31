package com.example.android.helloworld;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void eatCookie(View view) {
        ImageView androidCookieImageView = (ImageView) findViewById(R.id.android_cookie_image_view);
        androidCookieImageView.setImageResource(R.drawable.after_cookie);

        TextView statusTextView = (TextView) findViewById(R.id.status_text_view);
        statusTextView.setText("I'm so full");
    }
}

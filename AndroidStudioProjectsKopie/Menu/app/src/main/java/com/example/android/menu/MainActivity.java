package com.example.android.menu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void printToLogs(View view) {
        String msg = "My menu app.";
        TextView textView1 = (TextView)findViewById(R.id.menu_item_1);
        TextView textView2 = (TextView)findViewById(R.id.menu_item_2);
        TextView textView3 = (TextView)findViewById(R.id.menu_item_3);
        Log.v(msg,textView1.getText().toString());
        Log.v(msg,textView2.getText().toString());
        Log.v(msg,textView3.getText().toString());
    }
}

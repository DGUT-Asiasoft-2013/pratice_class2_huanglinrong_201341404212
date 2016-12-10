package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/12/7.
 */

public class FeedsShowActivity extends Activity {
    TextView textView;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        text=getIntent().getStringExtra("text");
        setContentView(R.layout.activity_feed_show);
        textView= (TextView) findViewById(R.id.text);
        textView.setText(text);
    }
}

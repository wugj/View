package com.jt.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jt.view.R;
import com.jt.view.receiver.MyAppWidgetProvider;

public class AppWidetProviderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendBroadcast(new Intent(MyAppWidgetProvider.CLICK_ACTION));
    }
}

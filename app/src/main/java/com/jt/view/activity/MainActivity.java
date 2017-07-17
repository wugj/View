package com.jt.view.activity;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.jt.view.R;

public class MainActivity extends AppCompatActivity {

    private WindowManager.LayoutParams mLayoutParams;
    private Button mButton;
    private WindowManager mWindowManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View textView = findViewById(R.id.btn_transition_drawable);
        TransitionDrawable drawable = (TransitionDrawable) textView.getBackground();
        drawable.startTransition(5000);

        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,10);
        }else {
            showButtonWindow();
        }
    }

    private void showButtonWindow() {
        mWindowManager = getWindowManager();
        mButton = new Button(this);
        mButton.setText("按钮");
        mLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR, 0, PixelFormat.TRANSLUCENT);
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mLayoutParams.x = 300;
        mLayoutParams.y = 100;

        mWindowManager.addView(mButton, mLayoutParams);

        mButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        mLayoutParams.x = (int) event.getRawX();
                        mLayoutParams.y = (int) event.getRawY();
                        mWindowManager.updateViewLayout(mButton, mLayoutParams);
                        break;
                }
                return false;
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindowManager.removeViewImmediate(mButton);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    showButtonWindow();
                }
            }
        }
    }
}

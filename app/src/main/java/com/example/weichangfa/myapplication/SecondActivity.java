package com.example.weichangfa.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.logging.Logger;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        com.orhanobut.logger.Logger.d("SecondActivity onNewIntent");
        super.onNewIntent(intent);
    }
}

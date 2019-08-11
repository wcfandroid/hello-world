package com.example.weichangfa.myapplication;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;

/**
 * Created by weichangfa on 2017/1/8.
 */

public class MyApplication extends Application {
    Handler handler=new Handler();
    @Override
    public void onCreate() {
        super.onCreate();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startFirstActivity();
//            }
//        },10*1000);
    }
    public void startFirstActivity(){
        Intent intent=new Intent(this,FirstActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

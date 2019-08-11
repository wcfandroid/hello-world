package com.example.weichangfa.myapplication;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.Button;

import com.bumptech.glide.Glide;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class FirstActivity extends AppCompatActivity {
    Handler mHandle=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        ExecutorService executorService=Executors.newCachedThreadPool();

        mHandle.postDelayed(new Runnable() {
            @Override
            public void run() {
                com.orhanobut.logger.Logger.d("1111111主线程");

//                for (int i = 0;ß i <100000 ; i++) {
//                    i=i*2;
//                    try {
//                                        Thread.sleep(10000);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                    }
//                    com.orhanobut.logger.Logger.d("i========="+i);
//                }
            }
        },1000);
        Button buttonFisst= (Button) findViewById(R.id.button_first);
        GrilFriend grilFriend=new GrilFriend();
        grilFriend.goHomeWithYou(true);

        buttonFisst.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Looper.myLooper() == Looper.getMainLooper()) { // UI主线程
                    com.orhanobut.logger.Logger.d("1111111主线程");
                } else { // 非UI主线程
                    com.orhanobut.logger.Logger.d("1111111子线程");
                }


                        while(true){
                                    try {
                                        Thread.sleep(1000000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                    }
                    //com.orhanobut.logger.Logger.d("FirstActivity onNewIntent");
                }
            }
        });
    }
    public void firstClick(View view){
        startActivity(new Intent(FirstActivity.this,SecondActivity.class));

    }
    class  GrilFriend{
       void  goHomeWithYou(boolean flag){

       };
    }

    @Override
        protected void onNewIntent(Intent intent) {
            super.onNewIntent(intent);

    }
}

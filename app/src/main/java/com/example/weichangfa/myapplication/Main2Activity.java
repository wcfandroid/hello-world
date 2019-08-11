package com.example.weichangfa.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Main2Activity extends AppCompatActivity implements FristFragment.OnFragmentInteractionListener,ThirdFragment.OnFragmentInteractionListener,SecondFragment.OnFragmentInteractionListener

{

    private  FragmentManager fm;
    private FristFragment fristFragment;
    private SecondFragment secondFragment;
    private ThirdFragment thirdFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
         fm=getSupportFragmentManager();



        setDefaultFragment();
    }
    private void setDefaultFragment()
    {
        FragmentTransaction transaction = fm.beginTransaction();
        if(fristFragment==null){

         fristFragment = new FristFragment();
         transaction.add(R.id.content, fristFragment);
        }else{
            transaction.show(fristFragment);
            if(secondFragment!=null)
            transaction.hide(secondFragment);
            if(thirdFragment!=null)
            transaction.hide(thirdFragment);
        }
        transaction.commit();

    }

    public void firstFragment(View view){
        FragmentTransaction transaction = fm.beginTransaction();
        if(fristFragment==null){

            fristFragment = new FristFragment();
            transaction.add(R.id.content, fristFragment);
        }else{
            transaction.show(fristFragment);
            if(secondFragment!=null)
                transaction.hide(secondFragment);
            if(thirdFragment!=null)
                transaction.hide(thirdFragment);
        }
        transaction.commit();
    }
    public void secondFragment(View view){
        FragmentTransaction transaction = fm.beginTransaction();
        if(secondFragment==null){

            secondFragment = new SecondFragment();
            transaction.add(R.id.content, secondFragment);
        }else{
            transaction.show(secondFragment);
            if(fristFragment!=null)
                transaction.hide(fristFragment);
            if(thirdFragment!=null)
                transaction.hide(thirdFragment);
        }
        transaction.commit();

    }
    public void thirdFragment(View view){
        FragmentTransaction transaction = fm.beginTransaction();
        if(thirdFragment==null){

            thirdFragment = new ThirdFragment();
            transaction.add(R.id.content, thirdFragment);
        }else{
            transaction.show(thirdFragment);
            if(secondFragment!=null)
                transaction.hide(secondFragment);
            if(fristFragment!=null)
                transaction.hide(fristFragment);
        }
        transaction.commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

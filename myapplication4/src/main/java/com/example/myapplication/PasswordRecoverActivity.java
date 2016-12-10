package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;

import com.example.myapplication.fragment.PasswordRecoverStep1Fragment;
import com.example.myapplication.fragment.PasswordRecoverStep2Fragment;

/**
 * Created by Administrator on 2016/12/6.
 */

public class PasswordRecoverActivity extends Activity {
    PasswordRecoverStep1Fragment step1=new PasswordRecoverStep1Fragment();
    PasswordRecoverStep2Fragment step2=new PasswordRecoverStep2Fragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recover);
        step1.setOnGoNextListener(new PasswordRecoverStep1Fragment.OnGoNextListener() {
            @Override
            public void onGoNext() {
                goStep2();
            }
        });
        getFragmentManager().beginTransaction().replace(R.id.container,step1).commit();
    }
    void goStep2(){
        getFragmentManager().beginTransaction().replace(R.id.container,step2).addToBackStack(null).commit();
    }
}

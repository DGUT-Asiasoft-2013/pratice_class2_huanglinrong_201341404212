package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.fragment.inputcells.SimpleTextInputCellFragment;

/**
 * Created by Administrator on 2016/12/3.
 */

public class LoginActivity extends Activity {
    Button buttonRegister;
    SimpleTextInputCellFragment fragmentUsername;
    SimpleTextInputCellFragment fragmentPassword;

    //在构造时只能定义，不能在onCreate外面findViewById
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fragmentUsername = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.loginusername);
        fragmentPassword = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.loginpassword);
        buttonRegister = (Button) findViewById(R.id.register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegisterActivity();
            }
        });
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoginActivity();
            }
        });
        findViewById(R.id.btn_forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startForgotPassword();
            }
        });
    }

    void startForgotPassword(){
        Intent intent = new Intent(this, PasswordRecoverActivity.class);
        startActivity(intent);
    }

    void startRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        //finish();
    }
    void startLoginActivity() {
        Intent intent = new Intent(this, HelloWorldActivity.class);
        startActivity(intent);
        //finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        fragmentUsername.setLabelText("用户名：");
        fragmentUsername.setHintText("请输入用户名");
        fragmentPassword.setLabelText("密码：");
        fragmentPassword.setHintText("请输入密码");
        fragmentPassword.setIsPassword(true);

    }
}

package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.api.Server;
import com.example.myapplication.fragment.inputcells.SimpleTextInputCellFragment;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    void startForgotPassword() {
        Intent intent = new Intent(this, PasswordRecoverActivity.class);
        startActivity(intent);
    }

    void startRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        //finish();
    }

    void startLoginActivity() {
        //把账号密码post到服务器
        final String account = fragmentUsername.getText();
        String passwordHash = fragmentPassword.getText();
        //对登陆时输入的密码加密然后传输到服务器
        passwordHash=MD5.getMD5(passwordHash);
        OkHttpClient okHttpClient = Server.getSharedClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("account", account)
                .addFormDataPart("passwordHash", passwordHash)
                .build();
        Request requestLogin =Server.requestBuildWithApi("login")
                .method("post", null)
                .post(requestBody)//post上去的内容
                .build();

        //登陆中的提示信息的显示
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Login...");//设置内容
        progressDialog.setCancelable(false);//设置不能点击取消
        progressDialog.setCanceledOnTouchOutside(false);//设置一直在显示前面
        progressDialog.show();//设置显示

        okHttpClient.newCall(requestLogin).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        onFailure(call, e);
                    }
                });

            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                final String message = response.body().string();//把返回的response转换为String,用于下面判断
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    final User user = objectMapper.readValue(message, User.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();//登陆中提示信息取消显示
                            //更新方法后的内容
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("请求成功")
                                    .setMessage("Hello  " + user.getAccount())//response是返回的登陆信息
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(LoginActivity.this, HelloWorldActivity.class);
                                            startActivity(intent);
                                            //finish();
                                        }
                                    })
                                    .show();

                      /*  if (message.equals("")) {
                            LoginActivity.this.onFailure(call, "密码错误！");//密码错误跳转到失败提示方法
                        } else {
                            try {
                                LoginActivity.this.onResponse(call, message);
                            } catch (Exception e) {
                                e.printStackTrace();
                                LoginActivity.this.onFailure(call, e);//有问题跳转到失败提示方法
                            }
                        }*/
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();//登陆中提示信息取消显示
                            LoginActivity.this.onFailure(call, e);//有问题跳转到失败提示方法
                        }
                    });

                }
            }
        });


    }

    //请求成功时显示登陆信息和跳转页面
    void onResponse(Call call, String response) {
        new AlertDialog.Builder(this)
                .setTitle("请求成功")
                .setMessage(response)//response是返回的登陆信息
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //finish();
                    }
                })
                .show();
        Intent intent = new Intent(this, HelloWorldActivity.class);
        startActivity(intent);
        //finish();
    }

    //请求失败时显示的提示
    void onFailure(Call call, Exception e) {
        new AlertDialog.Builder(this)
                .setTitle("请求失败")
                .setMessage("未知错误 "+e.getLocalizedMessage())
                .setPositiveButton("确认", null)
                .show();

    }

/*    //密码错误提示信息方法
    void onFailure(Call call, String fail) {
        new AlertDialog.Builder(this)
                .setTitle("请求失败")
                .setMessage(fail)
                .setPositiveButton("确认", null)
                .show();

    }*/


    @Override
    protected void onResume() {
        super.onResume();
//设置布局控件的text
        fragmentUsername.setLabelText("用户名：");
        fragmentUsername.setHintText("请输入用户名");
        fragmentPassword.setLabelText("密码：");
        fragmentPassword.setHintText("请输入密码");
        fragmentPassword.setIsPassword(true);

    }
}

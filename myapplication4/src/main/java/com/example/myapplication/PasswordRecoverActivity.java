package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.myapplication.api.Server;
import com.example.myapplication.fragment.PasswordRecoverStep1Fragment;
import com.example.myapplication.fragment.PasswordRecoverStep2Fragment;
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
 * Created by Administrator on 2016/12/6.
 */

public class PasswordRecoverActivity extends Activity {
    PasswordRecoverStep1Fragment step1 = new PasswordRecoverStep1Fragment();
    PasswordRecoverStep2Fragment step2 = new PasswordRecoverStep2Fragment();

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
        getFragmentManager().beginTransaction().replace(R.id.container, step1).commit();
        step2.setOnGoPostListener(new PasswordRecoverStep2Fragment.OnGoPostListener() {
            @Override
            public void OnGoPost() {
                passwordRecover();
            }
        });

    }

    void goStep2() {
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.slide_in_right,//忘记密码的左右切换
                        R.animator.slide_out_left,
                        R.animator.slide_in_left,
                        R.animator.slide_out_right
                )
                .replace(R.id.container, step2)
                .addToBackStack(null)
                .commit();
    }

    //修改密码方法
    void passwordRecover() {

        String email = step1.getEmail();
        String passwordHash = step2.getPassword();
        String passwordagain = step2.getPasswordRepeat();
        if(passwordagain.equals("")||passwordHash.equals("")){
            new AlertDialog.Builder(PasswordRecoverActivity.this)
                    .setMessage("输入的密码不能为空!")
                    .setNegativeButton("确认", null)
                    .show();
            return;
        }
        if (!passwordHash.equals(passwordagain)) {
            new AlertDialog.Builder(PasswordRecoverActivity.this)
                    .setMessage("两次输入的密码不一致!")
                    .setNegativeButton("确认", null)
                    .show();
            return;
        }
        //对于修改密码时输入的密码进行加密，然后传输到服务器
        passwordHash=MD5.getMD5(passwordHash);
        OkHttpClient okHttpClient = Server.getSharedClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email", email)
                .addFormDataPart("passwordHash", passwordHash)
                .build();
        Request request = Server.requestBuildWithApi("passwordrecover")
                .method("post", null)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(PasswordRecoverActivity.this)
                                .setTitle("请求失败")
                                .setMessage(e.toString())
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                final Boolean succeed = new ObjectMapper().readValue(response.body().bytes(), Boolean.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (succeed) {
                            new AlertDialog.Builder(PasswordRecoverActivity.this)
                                    .setTitle("请求成功")
                                    .setMessage("修改密码成功！")//response是返回的登陆信息
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .show();
                        } else {
                            new AlertDialog.Builder(PasswordRecoverActivity.this)
                                    .setTitle("请求成功")
                                    .setMessage("修改密码失败！")//response是返回的登陆信息
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .show();
                        }
                    }
                });
            }
        });

    }
}

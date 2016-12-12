package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.api.Server;
import com.example.myapplication.fragment.inputcells.SimpleTextInputCellFragment;

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

public class RegisterActivity extends Activity {
    SimpleTextInputCellFragment fragInputCellAccount;
    SimpleTextInputCellFragment fragInputCellPassword;
    SimpleTextInputCellFragment fragInputCellPasswordRepeat;
    SimpleTextInputCellFragment fragInputCellAddress;
    SimpleTextInputCellFragment fragInputCellName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fragInputCellAccount = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_account);
        fragInputCellPassword = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_password);
        fragInputCellPasswordRepeat = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_password_repeat);
        fragInputCellAddress = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_email);
        fragInputCellName = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_name);
        findViewById(R.id.but_register_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
    }

    void submit() {
        String password = fragInputCellPassword.getText();
        String passwordRepeat = fragInputCellPasswordRepeat.getText();
        if (!password.equals(passwordRepeat)) {
            new AlertDialog.Builder(RegisterActivity.this)
                    .setMessage("两次输入的密码不一致!")
                    .setNegativeButton("确认", null)
                    .show();
            return;
        }
        String account = fragInputCellAccount.getText();
        String name = fragInputCellName.getText();
        String email = fragInputCellAddress.getText();
        //加密注册时密码
        password=MD5.getMD5(password);
        OkHttpClient okHttpClient =Server.getSharedClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("account", account)
                .addFormDataPart("passwordHash", password)
                .addFormDataPart("name", name)
                .addFormDataPart("email", email)
                .build();

        Request request = Server.requestBuildWithApi("register")
                .method("post", null)
                .post(requestBody)
                .build();

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Login...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        RegisterActivity.this.onFailure(call, e);
                    }
                });
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.dismiss();
                        try {
                            RegisterActivity.this.onResponse(call, response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    void onResponse(Call call, String response) {
        new AlertDialog.Builder(this)
                .setMessage("请求成功" + response)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();

    }

    void onFailure(Call call, IOException e) {
        new AlertDialog.Builder(this)
                .setTitle("请求失败")
                .setMessage(e.getLocalizedMessage())
                .setPositiveButton("确认", null)
                .show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        fragInputCellAccount.setLabelText("用户名：");
        fragInputCellAccount.setHintText("请输入用户名");
        fragInputCellPassword.setLabelText("密码：");
        fragInputCellPassword.setHintText("请输入密码");
        fragInputCellPassword.setIsPassword(true);
        fragInputCellPasswordRepeat.setLabelText("再次密码：");
        fragInputCellPasswordRepeat.setHintText("请再次输入密码");
        fragInputCellPasswordRepeat.setIsPassword(true);
        fragInputCellAddress.setLabelText("邮箱：");
        fragInputCellAddress.setHintText("请输入邮箱");
        fragInputCellName.setLabelText("请输入昵称：");
        fragInputCellName.setHintText("昵称");

    }
}

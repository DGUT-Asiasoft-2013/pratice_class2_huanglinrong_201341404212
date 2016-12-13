package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.myapplication.api.Server;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/12/7.
 */

public class InputEditActivity extends Activity {
    EditText editTitle, editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_edit);
        editTitle = (EditText) findViewById(R.id.title);
        editText = (EditText) findViewById(R.id.text);

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendContent();
            }
        });
    }

    void sendContent() {
        String text = editText.getText().toString();
        String title = editTitle.getText().toString();

        // check these value is not null
        if (text.equals("") || title.equals("")) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("发送不能为空")
                    .setNegativeButton("确定", null)
                    .show();
            return;
        }

        MultipartBody body = new MultipartBody.Builder()
                .addFormDataPart("title", title)
                .addFormDataPart("text", text)
                .build();

        Request request = Server.requestBuildWithApi("article")
                .post(body)
                .build();

        Server.getSharedClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                final String responseBody = arg1.body().string();

                runOnUiThread(new Runnable() {
                    public void run() {
                        InputEditActivity.this.onSucceed(responseBody);
                    }
                });
            }

            @Override
            public void onFailure(Call arg0, final IOException arg1) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        InputEditActivity.this.onFailure(arg1);
                    }
                });
            }
        });
    }

    void onSucceed(String text) {
        new AlertDialog.Builder(this).setMessage(text)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        overridePendingTransition(R.anim.none, R.anim.slide_out_top);
                    }
                }).show();
    }

    void onFailure(Exception e) {
        new AlertDialog.Builder(this).setNegativeButton("确定", null).setMessage(e.getMessage()).show();
    }
}

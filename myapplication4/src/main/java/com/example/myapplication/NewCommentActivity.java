package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.api.Server;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/12/14.
 */

public class NewCommentActivity extends Activity {
    EditText discussEdit;
    Button discussBtn;
    Article article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comment);
        article = (Article) getIntent().getSerializableExtra("article");
        discussEdit = (EditText) findViewById(R.id.edit_input_discuss);
        discussBtn = (Button) findViewById(R.id.btn_input_discuss);
        discussBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendContent();
            }
        });
    }

    void sendContent() {

        String text = discussEdit.getText().toString();
        if (text.equals("")) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("发送不能为空")
                    .setNegativeButton("确定", null)
                    .show();
            return;
        }
        MultipartBody body = new MultipartBody.Builder()
                .addFormDataPart("text", text)
                .build();
        Request request = Server.requestBuildWithApi("/article/" + article.getId() + "/comments")
                .post(body)
                .build();
        Server.getSharedClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(NewCommentActivity.this)
                                .setTitle("提示")
                                .setMessage("连接失败" + e.getMessage())
                                .setNegativeButton("OK", null)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new AlertDialog.Builder(NewCommentActivity.this)
                                    .setTitle("提示")
                                    .setMessage("发送成功！" + response.body().string())
                                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                            // overridePendingTransition(R.anim.none, R.anim.slide_out_top);
                                        }
                                    })
                                    .show();
                        } catch (Exception e) {
                            new AlertDialog.Builder(NewCommentActivity.this)
                                    .setTitle("提示")
                                    .setMessage("发送成功(catch)")
                                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                            // overridePendingTransition(R.anim.none, R.anim.slide_out_top);
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

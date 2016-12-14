package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.api.Server;
import com.example.myapplication.fragment.AvatarView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.myapplication.R.id.btn_discuss;

/**
 * Created by Administrator on 2016/12/7.
 */

public class FeedsShowActivity extends Activity {
    TextView textAccount;//article用户名
    TextView textView;//article详细内容
    Article article;//传递过来的article
    Button btnDiscuss;//评论按钮
    ListView discussList;//显示评论内容列表
    View loadMoreView;//加载按钮更多的布局
    TextView loadMoreBtn;//加载更多按钮

    List<Comment> data;//传递回来的信息
    int page;//页数


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_show);
        //获得加载更多的按钮视图
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        loadMoreView = layoutInflater.inflate(R.layout.feed_load_more_button, null);
        //获得加载更多按钮
        loadMoreBtn = (TextView) loadMoreView.findViewById(R.id.text);
        //
        //接受传送的信息
        article = (Article) getIntent().getSerializableExtra("article");
        //article作者
        textAccount = (TextView) findViewById(R.id.article_account);
        //article详情
        textView = (TextView) findViewById(R.id.article_text);
        //评论列表
        discussList = (ListView) findViewById(R.id.discuss_list);
        //把加载更多视图布置在评论列表最下面
        discussList.addFooterView(loadMoreView);
        //匹配评论列表接口
        discussList.setAdapter(baseAdapter);

        //评论按钮
        btnDiscuss = (Button) findViewById(btn_discuss);
        btnDiscuss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discuss(article);
            }
        });
        //加载更多按钮
        loadMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadmore();
            }
        });
    }

    //评论列表的设置接口
    BaseAdapter baseAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            //如果为空，证明没有数据，显示0行
            return data == null ? 0 : data.size();
        }

        @Override
        public Object getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View discussView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                discussView = inflater.inflate(R.layout.fragment_feed_listview, null);
            } else {
                discussView = view;
            }
            TextView nameText = (TextView) discussView.findViewById(R.id.name_textView);
            TextView detailsText = (TextView) discussView.findViewById(R.id.details_textView);
            TextView timeText = (TextView) discussView.findViewById(R.id.time_textView);
            AvatarView avatarView = (AvatarView) discussView.findViewById(R.id.image_avatar);
            Comment comment = data.get(i);
            //设置头像，如果没有头像的会在load中调用设置为默认
            avatarView.load(comment.getAuthor());
            nameText.setText(comment.getAuthor().getAccount());
            detailsText.setText(comment.getText());
            timeText.setText(new SimpleDateFormat("hh:mm:ss").format(comment.getCreateDate()));
            return discussView;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //调用方法从服务器获取信息
        load();
        //设置article的作者和详细内容
        textAccount.setText(article.getAuthor().getAccount() + ":");
        textView.setText(article.getText() + "...");
    }

    //从服务器获取内容
    void load() {
        Request request = Server.requestBuildWithApi("article/" + article.getId() + "/comments")
                .get()
                .build();
        Server.getSharedClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(FeedsShowActivity.this)
                                .setTitle("提示")
                                .setMessage("连接失败!" + e.getMessage())
                                .setNegativeButton("OK", null)
                                .show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final Page<Comment> data = new ObjectMapper().readValue(response.body().string(), new TypeReference<Page<Comment>>() {
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FeedsShowActivity.this.page = data.getNumber();//取出页数
                            FeedsShowActivity.this.data = data.getContent();//取出数据
                            baseAdapter.notifyDataSetInvalidated();//更新视图
                        }
                    });
                } catch (final Exception e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(FeedsShowActivity.this)
                                    .setTitle("提示")
                                    .setMessage("连接成功,但出现错误" + e.getMessage())
                                    .setNegativeButton("OK", null)
                                    .show();
                        }
                    });
                }
            }
        });
    }

    //跳转评论页面
    void discuss(Article article) {
        Intent intent = new Intent(FeedsShowActivity.this, NewCommentActivity.class);
        intent.putExtra("article", article);
        startActivity(intent);
    }

    //加载更多按钮事件
    void loadmore() {
        loadMoreBtn.setEnabled(false);
        loadMoreBtn.setText("载入中…");

        Request request = Server.requestBuildWithApi("article/" + article.getId() + "/comments/" + (page + 1)).get().build();
        Server.getSharedClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    public void run() {
                        loadMoreBtn.setEnabled(true);
                        loadMoreBtn.setText("加载更多");
                        try {
                            final Page<Comment> comment = new ObjectMapper().readValue(response.body().string(), new TypeReference<Page<Comment>>() {
                            });

                            if (comment.getNumber() > page) {
                                if (FeedsShowActivity.this.data == null) {
                                    FeedsShowActivity.this.data = comment.getContent();
                                } else {
                                    //加上要显示更多的
                                    FeedsShowActivity.this.data.addAll(comment.getContent());
                                }
                                page = comment.getNumber();
                                baseAdapter.notifyDataSetChanged();
                            }
                        } catch (final Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(FeedsShowActivity.this)
                                            .setTitle("提示")
                                            .setMessage(e.getMessage())
                                            .setNegativeButton("OK", null)
                                            .show();
                                }
                            });

                        }
                    }
                });
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        loadMoreBtn.setEnabled(true);
                        loadMoreBtn.setText("网络挂了...");
                    }
                });
            }
        });
    }
}

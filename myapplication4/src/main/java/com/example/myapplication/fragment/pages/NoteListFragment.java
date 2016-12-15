package com.example.myapplication.fragment.pages;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.Comment;
import com.example.myapplication.Page;
import com.example.myapplication.R;
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

/**
 * Created by Administrator on 2016/12/7.
 */

public class NoteListFragment extends Fragment {
    View view;//当前布局
    ListView noteListView;//消息结果列表
    List<Comment> data;//数据库获取的消息信息
    View loadmoreView;//加载更多视图
    TextView loadmoreBtn;//加载更多的TextView按钮
    int page = 0;//页数

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            //加载当前布局
            view = inflater.inflate(R.layout.fragment_page_note_list, null);
            //获取消息结果列表
            noteListView = (ListView) view.findViewById(R.id.note_listView);
            //获取加载更多视图和对应TextView
            loadmoreView = inflater.inflate(R.layout.feed_load_more_button, null);
            loadmoreBtn = (TextView) loadmoreView.findViewById(R.id.text);
            //加载视图到布局
            noteListView.setAdapter(baseAdapter);
            noteListView.addFooterView(loadmoreView);
            loadmoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadmoreBtnOnClickListener();
                }
            });
        }
        return view;
    }

    BaseAdapter baseAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
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
            if (view == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
                view = layoutInflater.inflate(R.layout.fragment_feed_listview, null);
            }
            //获取列表对应视图的控件
            TextView nameText = (TextView) view.findViewById(R.id.name_textView);
            TextView detailsText = (TextView) view.findViewById(R.id.details_textView);
            TextView timeText = (TextView) view.findViewById(R.id.time_textView);
            AvatarView avatarView = (AvatarView) view.findViewById(R.id.image_avatar);
            //设置对应的控件内容
            Comment comment = data.get(i);
            //设置头像，用户没有时会自动加载默认图片
            avatarView.load(comment.getAuthor());
            nameText.setText(comment.getAuthor().getAccount());
            detailsText.setText(comment.getText());
            timeText.setText(new SimpleDateFormat("hh:mm:ss").format(comment.getCreateDate()));
            return view;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        //数据库获取信息
        loadComment();
    }

    void loadComment() {
        Request request = Server.requestBuildWithApi("article/allComment")
                .get()
                .build();
        Server.getSharedClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("提示")
                                .setMessage("网络挂了...." + "\n" + e.getMessage())
                                .setNegativeButton("OK", null)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final Page<Comment> data = new ObjectMapper().readValue(response.body().string(), new TypeReference<Page<Comment>>() {
                            });
                            NoteListFragment.this.data = data.getContent();
                            NoteListFragment.this.page = data.getNumber();
                            baseAdapter.notifyDataSetInvalidated();//更新视图
                        } catch (final Exception e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle("提示")
                                            .setMessage("连接成功，但出现错误！" + "\n" + e.getMessage())
                                            .setNegativeButton("OK", null)
                                            .show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    //加载更多按钮方法
    void loadmoreBtnOnClickListener() {
        loadmoreView.setEnabled(false);
        loadmoreBtn.setText("载入中…");
        Request request = Server.requestBuildWithApi("article/allComment" +"?page="+(page + 1))
                .get()
                .build();
        Server.getSharedClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadmoreBtn.setEnabled(true);
                        loadmoreBtn.setText("网络挂了...");
                        new AlertDialog.Builder(getActivity())
                                .setTitle("提示")
                                .setMessage("网络挂了...." + "\n" + e.getMessage())
                                .setNegativeButton("OK", null)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadmoreBtn.setEnabled(true);
                        loadmoreBtn.setText("加载更多");
                        try {
                            final Page<Comment> article = new ObjectMapper().readValue(response.body().string(), new TypeReference<Page<Comment>>() {
                            });

                            if (article.getNumber() > page) {
                                if (NoteListFragment.this.data == null) {
                                    NoteListFragment.this.data = article.getContent();
                                } else {
                                    //加上要显示更多的
                                    NoteListFragment.this.data.addAll(article.getContent());
                                }
                                page = article.getNumber();
                                baseAdapter.notifyDataSetChanged();
                            }
                        } catch (final Exception e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle("提示")
                                            .setMessage("连接成功，但出现错误！" + "\n" + e.getMessage())
                                            .setNegativeButton("OK", null)
                                            .show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}

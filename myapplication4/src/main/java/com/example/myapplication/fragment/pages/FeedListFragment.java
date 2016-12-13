package com.example.myapplication.fragment.pages;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.Article;
import com.example.myapplication.FeedsShowActivity;
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

public class FeedListFragment extends Fragment {
    View view;
    ListView listView;
    List<Article> data;
    int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_page_feed_list, null);
            listView = (ListView) view.findViewById(R.id.list);
            listView.setAdapter(listAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    onItemClicked(i);
                }
            });

        }
        return view;
    }

    void onItemClicked(int i) {
        Intent intent = new Intent(getActivity(), FeedsShowActivity.class);
        intent.putExtra("text", data.get(i).getText());
        startActivity(intent);


    }

    BaseAdapter listAdapter = new BaseAdapter() {
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
            View viewFeed = null;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                viewFeed = inflater.inflate(R.layout.fragment_feed_listview, null);
            } else {
                viewFeed = view;
            }
            TextView nameText = (TextView) viewFeed.findViewById(R.id.name_textView);
            TextView detailsText = (TextView) viewFeed.findViewById(R.id.details_textView);
            TextView timeText = (TextView) viewFeed.findViewById(R.id.time_textView);
            AvatarView avatarView = (AvatarView) viewFeed.findViewById(R.id.image_avatar);
            Article article = data.get(i);
            if (article.getAuthorAvatar() != null) {
                avatarView.load(article.getAuthorAvatar());
            } else {
                //使用默认头像
                Resources res = getResources();
                Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.logo);
                avatarView.setBitmap(bmp);
            }
            nameText.setText(article.getAuthorName());
            detailsText.setText(article.getTitle() + ":" + article.getText());
            timeText.setText(new SimpleDateFormat("hh:mm:ss").format(article.getCreateDate()));

            return viewFeed;
        }

    };

    void reload() {
        Request request = Server.requestBuildWithApi("feeds")
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
                                .setMessage("连接失败!" + e.getMessage())
                                .setNegativeButton("OK", null)
                                .show();
                    }
                });

            }

            @Override
            public void onResponse(Call call,final Response response) throws IOException {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Page<Article> data = null;
                            try {
                                data = new ObjectMapper().readValue(response.body().string(), new TypeReference<Page<Article>>() {
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            FeedListFragment.this.page = data.getNumber();//取出页数
                            FeedListFragment.this.data = data.getContent();//取出数据
                            listAdapter.notifyDataSetInvalidated();//更新视图
                        }
                    });
                } catch (final Exception e) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("提示")
                                    .setMessage("连接成功" + e.getMessage())
                                    .setNegativeButton("OK", null)
                                    .show();
                        }
                    });
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //调用获得信息方法
        reload();
    }
}

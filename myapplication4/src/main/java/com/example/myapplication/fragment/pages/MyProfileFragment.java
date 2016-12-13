package com.example.myapplication.fragment.pages;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.User;
import com.example.myapplication.api.Server;
import com.example.myapplication.fragment.AvatarView;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.myapplication.R.id.avatar;

/**
 * Created by Administrator on 2016/12/7.
 */

public class MyProfileFragment extends Fragment {
    View view;
    TextView meText;
    ProgressBar progressBar;
    AvatarView meavatarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_page_my_profile, null);
        }
        meText = (TextView) view.findViewById(R.id.me_text);
        meavatarView = (AvatarView) view.findViewById(avatar);
        progressBar = (ProgressBar) view.findViewById(R.id.me_progress);
        return view;
    }

    @Override
    public void onResume() {
        meText.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        OkHttpClient okHttpClient = Server.getSharedClient();
        Request request = Server.requestBuildWithApi("me")
                .method("GET", null)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        meText.setVisibility(View.VISIBLE);
                        meText.setTextColor(Color.BLUE);
                        meText.setText("连接未知错误" + e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    final User user = objectMapper.readValue(response.body().string(), User.class);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            meavatarView.load(user.getAvatar());
                            meText.setVisibility(View.VISIBLE);
                            meText.setTextColor(Color.BLACK);
                            meText.setText("Hello  " + user.getAccount());

                        }
                    });

                } catch (Exception e) {
                    progressBar.setVisibility(View.GONE);
                    meText.setVisibility(View.VISIBLE);
                    meText.setTextColor(Color.BLUE);
                    meText.setText("连接成功，但有未知错误" + e.getMessage());
                }

            }
        });
        super.onResume();
    }
}

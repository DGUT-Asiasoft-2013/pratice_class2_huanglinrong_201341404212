package com.example.myapplication.fragment.pages;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.User;
import com.example.myapplication.api.Server;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/12/7.
 */

public class MyProfileFragment extends Fragment {
    View view;
    TextView meText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_page_my_profile, null);
        }
        meText = (TextView) view.findViewById(R.id.me_text);
        return view;
    }

    @Override
    public void onResume() {
        OkHttpClient okHttpClient = Server.getSharedClient();
        Request request = Server.requestBuildWithApi("me")
                .method("GET", null)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setView("出现错误！");
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
                            setView(user.getAccount());
                        }
                    });

                } catch (Exception e) {
                    setView("出现错误！");
                }

            }
        });
        super.onResume();
    }

    void setView(String account) {
        meText.setText(account);
    }
}

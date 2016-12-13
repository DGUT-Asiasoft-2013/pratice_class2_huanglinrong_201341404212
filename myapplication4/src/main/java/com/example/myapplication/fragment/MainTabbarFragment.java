package com.example.myapplication.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;

/**
 * Created by Administrator on 2016/12/7.
 */

public class MainTabbarFragment extends Fragment {
    View btnNew, tabFeeds, tabNotes, tabSearch, tabMe;
    View[] tabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_tabbar, null);
        btnNew = view.findViewById(R.id.btn_new);
        tabFeeds = view.findViewById(R.id.tab_feeds);
        tabNotes = view.findViewById(R.id.tab_notes);
        tabSearch = view.findViewById(R.id.tab_search);
        tabMe = view.findViewById(R.id.tab_me);

        tabs = new View[]{
                tabFeeds, tabNotes, tabSearch, tabMe
        };
        //四个图标按钮监听事件
        for (final View tab : tabs) {
            tab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTabClicked(tab);
                }
            });
        }

        //加号图标按钮监听事件
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGoNext();//跳转到发布信息输入框
            }
        });
        return view;
    }


    //在HellWorldActivity上调用方法跳转InputEditActivity
    public static interface OnBtnGoNextListener {
        void onBtnGoNext();
    }

    OnBtnGoNextListener onBtnGoNextListener;

    public void setOnBtnGoNextListener(OnBtnGoNextListener onBtnGoNextListener) {
        this.onBtnGoNextListener = onBtnGoNextListener;
    }

    void btnGoNext() {
        if ((onBtnGoNextListener != null)) {

            onBtnGoNextListener.onBtnGoNext();
        }
    }

    //四个按钮跳转事件
    public static interface OnTabSelectedListener {
        void onTabSelected(int index);
    }

    OnTabSelectedListener onTabSelectedListener;

    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        this.onTabSelectedListener = onTabSelectedListener;
    }

    public void setSelectedItem(int index) {
        if (index >= 0 && index < tabs.length) {
            onTabClicked(tabs[index]);
        }
    }

    public int getSelectedIndex() {
        for (int i = 0; i < tabs.length; i++) {
            if (tabs[i].isSelected()) return i;
        }
        return -1;
    }


    void onTabClicked(View tab) {
        int selectedIndex = -1;
        for (int i = 0; i < tabs.length; i++) {
            View otherTab = tabs[i];
            if (otherTab == tab) {
                otherTab.setSelected(true);
                selectedIndex = i;
            } else {
                otherTab.setSelected(false);
            }
        }
        if (onTabSelectedListener != null && selectedIndex >= 0) {
            onTabSelectedListener.onTabSelected(selectedIndex);
        }
    }
}

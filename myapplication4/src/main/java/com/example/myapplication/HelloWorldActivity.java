package com.example.myapplication;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.fragment.MainTabbarFragment;
import com.example.myapplication.fragment.pages.FeedListFragment;
import com.example.myapplication.fragment.pages.MyProfileFragment;
import com.example.myapplication.fragment.pages.NoteListFragment;
import com.example.myapplication.fragment.pages.SearchListFragment;

/**
 * Created by Administrator on 2016/12/3.
 */

public class HelloWorldActivity extends Activity {
    FeedListFragment contentFeedList = new FeedListFragment();
    NoteListFragment contentNoteList = new NoteListFragment();
    SearchListFragment contentSearchPage = new SearchListFragment();
    MyProfileFragment contentMyProfile = new MyProfileFragment();

    MainTabbarFragment tabbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helloworld);
        tabbar = (MainTabbarFragment) getFragmentManager().findFragmentById(R.id.frag_tabbar);
        //重写四个按钮事件
        tabbar.setOnTabSelectedListener(new MainTabbarFragment.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int index) {
                changeContentFragment(index);
            }
        });
        //重写加号按钮事件
        tabbar.setOnBtnGoNextListener(new MainTabbarFragment.OnBtnGoNextListener() {
            @Override
            public void onBtnGoNext() {
                startEditActivity();
            }
        });

    }

    //跳转inputeditActivity
    void startEditActivity() {
        Intent intent = new Intent(this, InputEditActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.none);

    }

    @Override
    protected void onResume() {
        super.onResume();
        tabbar.setSelectedItem(0);
        //HelloWorldActivity的onResume默认切换tab导致的崩溃
        //防止点击加号按钮事件后返回上一层时的tabbar[i]的不确定
        if (tabbar.getSelectedIndex() < 0) {
            tabbar.setSelectedItem(0);
        }
    }

    void changeContentFragment(int index) {
        Fragment newFrag = null;

        switch (index) {
            case 0:
                newFrag = contentFeedList;
                break;
            case 1:
                newFrag = contentNoteList;
                break;
            case 2:
                newFrag = contentSearchPage;
                break;
            case 3:
                newFrag = contentMyProfile;
                break;

            default:
                break;
        }

        if (newFrag == null) return;

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.contain, newFrag)
                .commit();
    }
}

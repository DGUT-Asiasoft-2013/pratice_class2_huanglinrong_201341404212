package com.example.myapplication.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.fragment.inputcells.SimpleTextInputCellFragment;

/**
 * Created by Administrator on 2016/12/6.
 */

public class PasswordRecoverStep1Fragment extends Fragment {
    View view;
    SimpleTextInputCellFragment fragEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_password_recover_step1, null);
            fragEmail = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_address_fragment);
            view.findViewById(R.id.button_input_address).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goNext();
                }
            });

        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fragEmail.setLabelText("邮箱：");
        fragEmail.setHintText("请输入邮箱");
    }

    //email返回方法
    public String getEmail() {
        return fragEmail.getText();
    }

    public static interface OnGoNextListener {
        void onGoNext();
    }

    OnGoNextListener onGoNextListener;

    public void setOnGoNextListener(OnGoNextListener onGoNextListener) {
        this.onGoNextListener = onGoNextListener;
    }

    void goNext() {
        if (onGoNextListener != null) {
            onGoNextListener.onGoNext();
        }
    }
}

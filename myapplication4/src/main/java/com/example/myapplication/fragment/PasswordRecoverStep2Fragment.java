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

public class PasswordRecoverStep2Fragment extends Fragment {
    View view;
    SimpleTextInputCellFragment fragPassword;
    SimpleTextInputCellFragment fragPasswordRepeat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_password_recover_step2, null);
        }
        fragPassword = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_password);
        fragPasswordRepeat = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_password_repeat);
        view.findViewById(R.id.btn_passwordRecover_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoPost();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fragPassword.setLabelText("密码");
        fragPassword.setHintText("请输入密码：");
        fragPassword.setIsPassword(true);
        fragPasswordRepeat.setLabelText("密码");
        fragPasswordRepeat.setHintText("请再次输入密码:");
        fragPasswordRepeat.setIsPassword(true);
    }

    //返回password方法
    public String getPassword() {
        return fragPassword.getText();
    }
    public String getPasswordRepeat() {
        return fragPasswordRepeat.getText();
    }

    public static interface OnGoPostListener {
        void OnGoPost();
    }

    OnGoPostListener onGoPostListener;

    public void setOnGoPostListener(OnGoPostListener onGoPostListener) {
        this.onGoPostListener = onGoPostListener;
    }

    void GoPost() {
        if (onGoPostListener != null) {
            onGoPostListener.OnGoPost();
        }
    }
}

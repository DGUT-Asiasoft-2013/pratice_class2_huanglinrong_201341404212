package com.example.myapplication.fragment;

import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;

/**
 * Created by Administrator on 2016/12/3.
 */

public class VersionFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_version,null);//
        TextView textVersion =(TextView)view.findViewById(R.id.text);

        PackageManager pkgm=getActivity().getPackageManager();
        try{
            PackageInfo packageInfo=pkgm.getPackageInfo(getActivity().getPackageName(),0);
            textVersion.setText(packageInfo.packageName+" "+packageInfo.versionName);
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
            textVersion.setText("ERROR");
        }

        return view;
    }
}

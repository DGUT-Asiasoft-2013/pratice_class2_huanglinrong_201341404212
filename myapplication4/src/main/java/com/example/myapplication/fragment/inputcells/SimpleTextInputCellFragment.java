package com.example.myapplication.fragment.inputcells;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;

/**
 * Created by Administrator on 2016/12/5.
 */

public class SimpleTextInputCellFragment extends BaseInputCellFragment {
    TextView label;
    EditText edit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inputcell_simpletext, container);
        label = (TextView) view.findViewById(R.id.label);
        edit = (EditText) view.findViewById(R.id.edit);
        return view;
    }

    public void setLabelText(String labelText) {
        label.setText(labelText);
    }

    public void setHintText(String hintText) {
        edit.setHint(hintText);
    }

    public String getText() {
        return edit.getText().toString();
    }

    public void setIsPassword(boolean isPassword) {
        if (isPassword) {
            edit.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
            //edit.setInputType(isPassword ? 111:222);
        } else {
            edit.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        }
    }
}

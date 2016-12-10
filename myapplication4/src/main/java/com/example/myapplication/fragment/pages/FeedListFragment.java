package com.example.myapplication.fragment.pages;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.FeedsShowActivity;
import com.example.myapplication.R;

import java.util.Calendar;

/**
 * Created by Administrator on 2016/12/7.
 */

public class FeedListFragment extends Fragment {
    View view;
    ListView listView;
    String[] data = new String[20];

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

            for (int i = 0; i < data.length; i++) {
                data[i] = "Kkkkkk" + i;
            }

        }
        return view;
    }

    void onItemClicked(int i) {
        String text = data[i];
        Intent intent = new Intent(getActivity(), FeedsShowActivity.class);
        intent.putExtra("text", text);
        startActivity(intent);


    }

    BaseAdapter listAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return data == null ? 0 : data.length;
        }

        @Override
        public Object getItem(int i) {
            return data[i];
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
            TextView nametext = (TextView) viewFeed.findViewById(R.id.name_textView);
            TextView detailstext = (TextView) viewFeed.findViewById(R.id.details_textView);
            TextView timetext = (TextView) viewFeed.findViewById(R.id.time_textView);
            nametext.setText(data[i]);
            detailstext.setText("Good Good Study!");
            String out = DateFormat.format("mm:ss", Calendar.getInstance().getTime()).toString();
            detailstext.setText(out);

            return viewFeed;
        }

    };
}

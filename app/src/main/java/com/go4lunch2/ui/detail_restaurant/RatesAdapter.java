package com.go4lunch2.ui.detail_restaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.go4lunch2.R;

public class RatesAdapter extends BaseAdapter {

    private final Context context; //context
    private final String[] grades;
    private final Integer[] icons;

    public RatesAdapter(Context context, String[] grades, Integer[] icons) {
        this.context = context;
        this.grades = grades;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return grades.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_listview_alert_rate, container, false);
        }

        ((TextView) view.findViewById(R.id.tv_item_rate)).setText(grades[position]);
        view.findViewById(R.id.iv_item_rate).setBackgroundResource(icons[position]);
        return view;
    }
}

package com.example.mobilecheckdevice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class WifiAdapter extends BaseAdapter {

    List<String> list ;
    LayoutInflater inflater ;
    Context c ;

    WifiAdapter(List<String> list , Context c ) {
        this.list = list ;
        this.c = c ;
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.wifi_unit , null);
        TextView name = view.findViewById(R.id.textView64);
        name.setText(list.get(i));
        return view;
    }
}

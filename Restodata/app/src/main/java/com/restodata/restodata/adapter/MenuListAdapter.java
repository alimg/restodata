package com.restodata.restodata.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.restodata.restodata.R;
import com.restodata.webapp.model.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuListAdapter extends BaseAdapter {
    private final Context context;
    private List<MenuItem> mList = new ArrayList<>();

    public MenuListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.menu_list_item, null);
        }
        ((TextView)convertView.findViewById(R.id.tv_name)).setText(mList.get(position).name);
        ((TextView)convertView.findViewById(R.id.tv_group)).setText(mList.get(position).group);
        ((TextView)convertView.findViewById(R.id.tv_price)).setText(""+mList.get(position).price);
        return convertView;
    }

    public void setList(List<MenuItem> list) {
        this.mList = list;
        notifyDataSetInvalidated();
    }
}

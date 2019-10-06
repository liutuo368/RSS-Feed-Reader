package com.example.rssreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class SourceListAdapter extends BaseAdapter {

    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public SourceListAdapter(Context context, List<Map<String, Object>> data){
        this.context=context;
        this.data=data;
        this.layoutInflater= LayoutInflater.from(context);
    }

    public final class Source {
        public TextView sourceName;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SourceListAdapter.Source source = null;
        if(convertView == null) {
            source = new SourceListAdapter.Source();
            convertView = layoutInflater.inflate(R.layout.source_list, null);
            source.sourceName = (TextView) convertView.findViewById(R.id.source_name);
            convertView.setTag(source);
        } else {
            source = (SourceListAdapter.Source) convertView.getTag();
        }

        source.sourceName.setText((String) data.get(position).get("sourceName"));
        return convertView;

    }
}

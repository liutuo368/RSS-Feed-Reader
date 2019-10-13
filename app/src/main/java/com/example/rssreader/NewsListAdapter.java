package com.example.rssreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class NewsListAdapter extends BaseAdapter{

    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public NewsListAdapter(Context context, List<Map<String, Object>> data){
        this.context=context;
        this.data=data;
        this.layoutInflater= LayoutInflater.from(context);
    }

    public final class News {
        public TextView title;
        public TextView date;
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
        News news = null;
        if(convertView == null) {
            news = new News();
            convertView = layoutInflater.inflate(R.layout.news_list, null);
            news.title = (TextView) convertView.findViewById(R.id.news_title);
            news.date = (TextView) convertView.findViewById(R.id.news_date);
            convertView.setTag(news);
        } else {
            news = (News) convertView.getTag();
        }

        news.title.setText((String)data.get(position).get("title"));
        news.date.setText((String) data.get(position).get("date"));
        return convertView;

    }

}

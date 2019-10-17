package com.example.rssreader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsFragment extends Fragment{

    private ListView listView;
    private List <Map<String, Object>> list;
    private NewsListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        listView = (ListView) view.findViewById(R.id.newsList);
        list = getData(); // The list of news to be shown on ListView
        adapter = new NewsListAdapter(getActivity(), list);
        listView.setAdapter(adapter); // Set up the adapter for ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Open ContentActivity and send information to it
                Intent intent = new Intent(getActivity(), ContentActivity.class);
                intent.setAction("newsInfo");
                intent.putExtra("title", (String) list.get(position).get("title"));
                intent.putExtra("date", MainActivity.dates.get(MainActivity.titles.get(position)));
                intent.putExtra("content", MainActivity.description.get(MainActivity.titles.get(position)));
                intent.putExtra("link", MainActivity.links.get(position));
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.news_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort:
                // Sort the item on News list
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Holo_Light_Dialog);
                builder.setTitle("Sort by");
                final String[] options = {"Title", "Date"};
                builder.setItems(options, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, final int which)
                    {
                        Collections.sort(list, new Comparator<Map<String, Object>>() {
                            @Override
                            public int compare(Map<String, Object> stringObjectMap, Map<String, Object> t1) {
                                String value1, value2;
                                if (which == 0) {
                                    value1 = stringObjectMap.get("title").toString();
                                    value2 = t1.get("title").toString();
                                } else {
                                    value1 = stringObjectMap.get("date").toString();
                                    value2 = t1.get("date").toString();
                                }
                                return value1.compareTo(value2);
                            }
                        });
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.show();
                return true;
            case R.id.menu_refresh:
                RSSObject rss = new RSSObject();
                rss.manualRefresh();
                list = getData();
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Get the data of News list from MainActivity
    public List <Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < MainActivity.titles.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", MainActivity.titles.get(i));
            map.put("date", MainActivity.dates.get(MainActivity.titles.get(i)));
            list.add(map);
        }
        return list;
    }


}

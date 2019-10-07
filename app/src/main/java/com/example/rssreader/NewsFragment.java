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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsFragment extends Fragment {

    private ListView listView;
    private List <Map<String, Object>> list;
    private NewsListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        listView = (ListView) view.findViewById(R.id.newsList);
        list = getData();
        adapter = new NewsListAdapter(getActivity(), list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ContentActivity.class);
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
                list = getData();
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public List <Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < 10; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            map.put("image", R.drawable.ic_launcher_foreground);
            map.put("title", "News title " + i);
            map.put("date", df.format(new Date()));
            list.add(map);
        }
        return list;
    }

}

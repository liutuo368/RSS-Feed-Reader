package com.example.rssreader;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.common.base.CharMatcher;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoritesFragment extends Fragment {

    private ListView listView;
    public static boolean removeFlag=false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        listView = (ListView) view.findViewById(R.id.favoriteList);
        final List<Map<String, Object>> list = getData(); // The list of news to be shown on ListView
        final NewsListAdapter adapter = new NewsListAdapter(getActivity(), list);
        listView.setAdapter(adapter); // Set up the adapter for ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Open ContentActivity and send information to it
                Intent intent = new Intent(getActivity(), ContentActivity.class);
                intent.setAction("newsInfo");
                intent.putExtra("title", MainActivity.favouriteTitles.get(position));
                intent.putExtra("date", MainActivity.favouriteDates.get(position));
                intent.putExtra("content", MainActivity.favouriteDescriptions.get(position));
                intent.putExtra("link", MainActivity.favouriteLinks.get(position));
                startActivity(intent);
            }
        });

        // Remove the news from favorite list
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                removeFavourites(MainActivity.favouriteTitles.get(i));
                list.remove(i);
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        return view;
    }


    // Get the data of favorite list from MainActivity
    public List <Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < MainActivity.favouriteLinks.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", MainActivity.favouriteTitles.get(i));
            map.put("date", MainActivity.favouriteDates.get(i));
            list.add(map);
        }
        return list;
    }

    // Creating the required database instance

    DatabaseReference mRootref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference reader = mRootref.child("Reader");
    DatabaseReference favourites = reader.child("Favourites");

    /**
     * Author : Jihirshu Narayan
     * @param title Name of the RSS Feed Site
     *
     * Description : This function takes the title (serves as primary key) of the favourite news and removes it from the user's
     *              firebase node under the Favourites database.
     */

    public void removeFavourites(String title)
    {
        String charsToRemove = ".#$[]";

        final String filtered = CharMatcher.anyOf(charsToRemove).removeFrom(title);
        favourites.child(MainActivity.user).child(title).removeValue();
    }

}

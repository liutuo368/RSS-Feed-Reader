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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        final List<Map<String, Object>> list = getData();
        final NewsListAdapter adapter = new NewsListAdapter(getActivity(), list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ContentActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //removeFavourites();
                list.remove(i);
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        return view;
    }



    public List <Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < 10; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", R.drawable.ic_launcher_foreground);
            map.put("title", "Favorite News: " + i);
            map.put("date", "2018");
            list.add(map);
        }
        return list;
    }


    DatabaseReference mRootref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference reader = mRootref.child("Reader");
    DatabaseReference favourites = reader.child("Favourites");

    public void removeFavourites(String title, String link)
    {
        String charsToRemove = ".#$[]";

        final String filtered = CharMatcher.anyOf(charsToRemove).removeFrom(title);
        favourites.child(MainActivity.user).child(title).removeValue();
    }

}

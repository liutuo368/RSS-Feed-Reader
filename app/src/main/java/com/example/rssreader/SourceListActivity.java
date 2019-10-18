package com.example.rssreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceListActivity extends AppCompatActivity {

    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_list);
        listView = (ListView) findViewById(R.id.source_list);
        List<Map<String, Object>> list = getData(); // The list of Source List in app library
        listView.setAdapter(new SourceListAdapter(SourceListActivity.this, list)); // Set the adapter for ListView
        // Add the source to user source list when the source was clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                addUserSource(MainActivity.appSourcesNames.get(i), MainActivity.appsourcesLinks.get(i));
                finish();
            }
        });

    }

    // Open Add Feed activity
    public void gotoAddFeed(View v) {
        Intent intent = new Intent(this, AddFeedActivity.class);
        startActivity(intent);

    }

    // Get source list information from MainActivity
    public List <Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < MainActivity.appSourcesNames.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("sourceName", MainActivity.appSourcesNames.get(i));
            list.add(map);
        }
        return list;
    }

    // Creating the required database instanes

    DatabaseReference mRootref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference reader = mRootref.child("Reader");
    DatabaseReference sourcedb = reader.child("Sources");
    DatabaseReference userRss = reader.child("UserRSS");


    /**
     * Author : Jihirshu Narayan
     * @param name Name of the RSS Feed Site
     * @param Link, the http link for the xml page of the rss feed
     *
     * Description : This function adds RSS source selected from the app's database to the user's sources database.
     */

    public void addUserSource(final String name, final String Link)
    {
        userRss.child(MainActivity.user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.getValue() == null) || (dataSnapshot.child(name).getValue() == null))
                {
                    userRss.child(MainActivity.user).child(name).setValue(Link);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

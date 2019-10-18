package com.example.rssreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.List;

public class AddFeedActivity extends AppCompatActivity {

    public static boolean validRSSLink = false;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);
        addListOnSpinner();
    }

    public void addListOnSpinner() {
        spinner = (Spinner)findViewById(R.id.category_choose);
        List<String> list = new ArrayList<>();
        list.add("News");
        list.add("Entertainment");
        list.add("Sports");
        list.add("Weather");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

    }

    public void addFeed(View v) throws ExecutionException, InterruptedException {
        EditText name = (EditText) findViewById(R.id.feedName);
        EditText link = (EditText) findViewById(R.id.feedLink);
        if(addRssSource(name.getText().toString(), link.getText().toString(), String.valueOf(spinner.getSelectedItem())).equals("Valid Link, Added")) {
            Toast.makeText(getApplicationContext(), "New feed added", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Invalid feed information", Toast.LENGTH_LONG).show();
        }
    }

    DatabaseReference mRootref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference reader = mRootref.child("Reader");
    DatabaseReference sourcedb = reader.child("Sources");
    DatabaseReference userRss = reader.child("UserRSS");

    /**
     * Author : Jihirshu Narayan
     * @param name Name of the RSS Feed Site
     * @param Link, the http link for the xml page of the rss feed
     * @param Category, Category of the feed
     *
     * Description : This function directs the request to the RSSObject class function addRssSource
     */


    public String addRssSource(final String name, final String Link, final String Category) throws ExecutionException, InterruptedException {
        RSSObject rss = new RSSObject();

        return rss.addRssSource(name, Link, Category);

    }


}

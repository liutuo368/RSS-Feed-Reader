package com.example.rssreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class ContentActivity extends AppCompatActivity{

    private String uri;
    TextView newsTitle;
    TextView newsDate;
    TextView newsContent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Twitter.initialize(this);
        newsTitle = (TextView) findViewById(R.id.news_title);
        newsDate = (TextView) findViewById(R.id.news_date);
        newsContent = (TextView) findViewById(R.id.news_content);
        Intent intent = getIntent();
        String action = intent.getAction();
        if(action.equals("newsInfo"))
        {
            newsTitle.setText(intent.getStringExtra("title"));
            newsDate.setText(intent.getStringExtra("date"));
            newsContent.setText(Html.fromHtml(intent.getStringExtra("content")));
            uri = intent.getStringExtra("link");
        }
    }


    public void btnLikeOnClick(View v) {

    }

    public void btnShareOnClick(View v) {
        try {
            TweetComposer.Builder builder = new TweetComposer.Builder(ContentActivity.this).url(new URL(uri));
            builder.show();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void btnGotoOnClick(View v) {
        Uri uri = Uri.parse(this.uri);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(uri);
        startActivity(intent);
    }

    DatabaseReference mRootref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference reader = mRootref.child("Reader");
    DatabaseReference favourites = reader.child("Favourites");

    public void addUserfavourites(final String title, final String link)
    {
        favourites.child(MainActivity.user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.getValue() == null) || (dataSnapshot.child(link).getValue() == null))
                {
                    favourites.child(MainActivity.user).child(link).setValue(title);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





}

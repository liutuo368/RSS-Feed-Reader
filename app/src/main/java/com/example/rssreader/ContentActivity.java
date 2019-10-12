package com.example.rssreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.net.MalformedURLException;
import java.net.URL;


public class ContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Twitter.initialize(this);
    }

    public void btnLikeOnClick(View v) {

    }

    public void btnShareOnClick(View v) {
        try {
            TweetComposer.Builder builder = new TweetComposer.Builder(ContentActivity.this).url(new URL("http://www.google.com"));
            builder.show();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void btnGotoOnClick(View v) {
        Uri uri = Uri.parse("http://www.google.com");
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(uri);
        startActivity(intent);
    }
}

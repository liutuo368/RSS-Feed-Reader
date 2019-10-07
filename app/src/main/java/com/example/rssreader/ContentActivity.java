package com.example.rssreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class ContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
    }

    public void btnLikeOnClick(View v) {

    }

    public void btnShareOnClick(View v) {

    }

    public void btnGotoOnClick(View v) {
        Uri uri = Uri.parse("http://www.google.com");
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(uri);
        startActivity(intent);
    }
}

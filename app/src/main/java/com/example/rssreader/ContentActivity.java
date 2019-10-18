package com.example.rssreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.common.base.CharMatcher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.net.MalformedURLException;
import java.net.URL;


public class ContentActivity extends AppCompatActivity{

    private String uri;
    private String title;
    private String date;
    private String description;

    TextView newsTitle;
    TextView newsDate;
    TextView newsContent;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Twitter.initialize(this);
        newsTitle = (TextView) findViewById(R.id.news_title);
        newsDate = (TextView) findViewById(R.id.news_date);
        newsContent = (TextView) findViewById(R.id.news_content);
        initFacebook();
        Intent intent = getIntent();
        String action = intent.getAction();
        if(action.equals("newsInfo"))
        {
            title = intent.getStringExtra("title");
            date = intent.getStringExtra("date");
            description = intent.getStringExtra("content");
            uri = intent.getStringExtra("link");
        }

        newsTitle.setText(title);
        newsDate.setText(date);
        newsContent.setText(Html.fromHtml(intent.getStringExtra("content")));
    }


    private void initFacebook() {
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
    }

    public void btnLikeOnClick(View v) {
        addUserfavourites(title, uri, description, date);
        Toast.makeText(this, "Added to favorite", Toast.LENGTH_LONG).show();
    }

    public void btnShareOnClick(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this,android.R.style.Theme_Holo_Light_Dialog);
        builder.setTitle("Share to");
        final String[] options = {"Facebook", "Twitter"};
        builder.setItems(options, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, final int which)
            {
                switch (which) {
                    case 0:
                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentUrl(Uri.parse(uri))
                                    .build();
                            shareDialog.show(linkContent);
                        }
                        break;
                    case 1:
                        try {
                            TweetComposer.Builder builder = new TweetComposer.Builder(ContentActivity.this).url(new URL(uri));
                            builder.show();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        break;
                        default:
                            break;
                }
            }
        });
        builder.show();
    }

    public void btnGotoOnClick(View v) {
        Uri uri = Uri.parse(this.uri);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(uri);
        startActivity(intent);
    }

    //Creating the required database instance

    DatabaseReference mRootref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference reader = mRootref.child("Reader");
    DatabaseReference favourites = reader.child("Favourites");

    /**
     * Author : Jihirshu Narayan
     * @param title Name of the RSS Feed Site
     * @param link, the http link for the xml page of the rss feed
     * @param description, Description text of the news
     * @param date, Publishing date of the feed
     *
     * Description : This function takes the details of the favourite news and checks whether it already exists in the user's
     *              firebase database or not. If it doesn't exist, then it creates a node under the user's node in the Favourites database.
     */

    public void addUserfavourites(final String title, final String link, final String description, final String date)
    {
        String charsToRemove = ".#$[]";

        final String filtered = CharMatcher.anyOf(charsToRemove).removeFrom(title);

        favourites.child(MainActivity.user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.getValue() == null) || (dataSnapshot.child(filtered).getValue() == null))
                {
                    favourites.child(MainActivity.user).child(filtered).child("title").setValue(title);
                    favourites.child(MainActivity.user).child(filtered).child("link").setValue(link);
                    favourites.child(MainActivity.user).child(filtered).child("description").setValue(description);
                    favourites.child(MainActivity.user).child(filtered).child("date").setValue(date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

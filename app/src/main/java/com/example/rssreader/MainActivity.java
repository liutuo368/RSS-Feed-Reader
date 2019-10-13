package com.example.rssreader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String user;
    public static List<String> titles = new ArrayList<>();
    public static List<String> links = new ArrayList<>();
    public static List<String> dates = new ArrayList<>();
    public static List<String> usersources = new ArrayList<>();
    public static List<String> images = new ArrayList<>();
    public static List<String> description = new ArrayList<>();

    Button btn;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_news:
                    selectedFragment = new NewsFragment();
                    break;
                case R.id.navigation_sources:
                    selectedFragment = new SourcesFragment();
                    break;
                case R.id.navigation_favorites:
                    selectedFragment = new FavoritesFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Intent intent = getIntent();
        String action = intent.getAction();
        if(action.equals("user")) {
            user = intent.getStringExtra("username");
        }

        getSources(new FirebaseCallback() {
            @Override
            public void onCallback(List<String> source) {
                MainActivity.usersources = source;

                if (source.size()>0)
                {
                    for (int i=0;i<source.size();i++)
                    {
                        new ProcessInBackGround().execute(source.get(i));
                    }
                }
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewsFragment()).commit();


    }

    public InputStream getInputStream(URL url)
    {
        try
        {
            return url.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            return null;
        }
    }



    public class ProcessInBackGround extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... strings) {
            Exception exception = null;
            try
            {
                URL Url = new URL(strings[0]);
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);

                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(getInputStream(Url), "UTF_8");

                boolean insideterm = false;

                int eventType = xpp.getEventType();

                while(eventType != XmlPullParser.END_DOCUMENT)
                {
                    if (eventType == XmlPullParser.START_TAG)
                    {
                        if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            insideterm = true;
                        }
                        else if (xpp.getName().equalsIgnoreCase("title"))
                        {
                            if (insideterm)
                            {
                                titles.add(xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("link"))
                        {
                            if (insideterm)
                            {
                                links.add(xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("pubDate"))
                        {
                            if (insideterm)
                            {
                                dates.add(xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("image"))
                        {
                            if (insideterm)
                            {
                                images.add(xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("description"))
                        {
                            if (insideterm)
                            {
                                description.add(xpp.nextText());
                            }
                        }
                    }
                    else if ((eventType == XmlPullParser.END_TAG) && (xpp.getName().equalsIgnoreCase("item")))
                    {
                        insideterm = false;
                    }

                    eventType = xpp.next();
                }

            }
            catch (MalformedURLException e)
            {
                exception = e;
            }
            catch (XmlPullParserException e)
            {
                exception = e;
            }
            catch (IOException e)
            {
                exception = e;
            }
            return null;
        }
    }

    public interface FirebaseCallback
    {
        void onCallback(List<String> source);
    }


    DatabaseReference mRootref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference reader = mRootref.child("Reader");
    DatabaseReference userrss = reader.child("UserRSS");


    public void getSources(final FirebaseCallback firebaseCallback)
    {
        ValueEventListener event = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        usersources.add(ds.getValue(String.class));
                    }
                }

                firebaseCallback.onCallback(usersources);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        userrss.child(MainActivity.user).addValueEventListener(event);


    }

}

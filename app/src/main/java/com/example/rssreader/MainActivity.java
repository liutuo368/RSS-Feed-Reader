package com.example.rssreader;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public static String user;
    public static List<String> titles = new ArrayList<>();
    public static List<String> links = new ArrayList<>();

    public static List<String> usersourcelinks = new ArrayList<>();
    public static List<String> usersourcenames = new ArrayList<>();


    public static List<String> appsourcesLinks = new ArrayList<>();
    public static List<String> appSourcesNames = new ArrayList<>();
    public static List<String> appSourcesCategories = new ArrayList<>();
    public static Map<String,String> images = new HashMap<>();
    public static Map<String,String> description = new HashMap<>();
    public static Map<String,String> dates = new HashMap<>();

    public static List<String> favouriteLinks = new ArrayList<>();
    public static List<String> favouriteTitles = new ArrayList<>();

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
    protected void onDestroy() {
        System.exit(0);
        super.onDestroy();
    }

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


        getuserSources(new FirebaseCallback() {
            @Override
            public void onCallback(List<String> usersourcenames, List<String> usersourcelinks) throws ExecutionException, InterruptedException {
                MainActivity.usersourcenames = usersourcenames;
                MainActivity.usersourcelinks = usersourcelinks;
                titles = new ArrayList<>();
                links = new ArrayList<>();

                dates = new HashMap<>();
                images = new HashMap<>();
                description = new HashMap<>();
                if (usersourcelinks.size()>0)
                {

                    for (int i=0;i<usersourcelinks.size();i++)
                    {
                        String str_result = new ProcessInBackGround().execute(usersourcelinks.get(i)).get();
                    }
                }

            }
        });

        getUserFavourites(new FirebaseCallback() {
            @Override
            public void onCallback(List<String> favouriteLink, List<String> favouriteTitle) {
                favouriteLinks = favouriteLink;
                favouriteTitles = favouriteTitle;

            }
        });

        getSources(new FirebaseCallback2() {
            @Override
            public void onCallback(List<String> rsssources, List<String> Categories, List<String> rssLinks)
            {
                MainActivity.appSourcesNames = rsssources;
                MainActivity.appSourcesCategories = Categories;
                MainActivity.appsourcesLinks = rssLinks;
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WelcomeFragment()).commit();
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



    public class ProcessInBackGround extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            Exception exception = null;
            String title="";
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
                                title = xpp.nextText();
                                titles.add(title);
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
                                dates.put(title, xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("image"))
                        {
                            if (insideterm)
                            {
                                images.put(title, xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("description"))
                        {
                            if (insideterm)
                            {
                                description.put(title, xpp.nextText());
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
            return "executed";
        }
    }

    public interface FirebaseCallback
    {
        void onCallback(List<String> sourcenames, List<String> sourcelinks) throws ExecutionException, InterruptedException;
    }


    DatabaseReference mRootref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference reader = mRootref.child("Reader");
    DatabaseReference userrss = reader.child("UserRSS");
    DatabaseReference sourcedb = reader.child("Sources");
    DatabaseReference favourites = reader.child("Favourites");


    public void getuserSources(final FirebaseCallback firebaseCallback)
    {
        ValueEventListener event = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersourcenames = new ArrayList<>();
                usersourcelinks = new ArrayList<>();
                if (dataSnapshot.getValue() != null)
                {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        usersourcenames.add(ds.getKey());
                        usersourcelinks.add(ds.getValue(String.class));
                    }
                }

                try {
                    firebaseCallback.onCallback(usersourcenames, usersourcelinks);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        userrss.child(MainActivity.user).addValueEventListener(event);


    }


    public interface FirebaseCallback2
    {
        void onCallback(List<String> rsssources, List<String> Categories, List<String> rssLinks);
    }


    public void getSources(final FirebaseCallback2 firebaseCallback2)
    {
        ValueEventListener event = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                appsourcesLinks = new ArrayList<>();
                appSourcesNames = new ArrayList<>();
                appSourcesCategories = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    appsourcesLinks.add(ds.child("Link").getValue(String.class));
                    appSourcesCategories.add(ds.child("Category").getValue(String.class));
                    appSourcesNames.add(ds.getKey());
                }

                firebaseCallback2.onCallback(appSourcesNames, appSourcesCategories, appsourcesLinks);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        };

        sourcedb.addValueEventListener(event);

    }


    public void getUserFavourites(final FirebaseCallback firebaseCallback)
    {
        ValueEventListener event = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                favouriteLinks = new ArrayList<>();
                favouriteTitles = new ArrayList<>();
                if (dataSnapshot.getValue() != null)
                {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        favouriteLinks.add(ds.child("link").getValue(String.class));
                        favouriteTitles.add(ds.child("title").getValue(String.class));
                    }
                }
                try {
                    firebaseCallback.onCallback(favouriteLinks, favouriteTitles);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        };

        favourites.child(MainActivity.user).addValueEventListener(event);

    }

}

package com.example.rssreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceListActivity extends AppCompatActivity {

    private ListView listView;
    public static boolean validRSSLink = false;
    public static boolean removeFlag = false;

    public class NewSource
    {
        public String Category;
        public String Link;

        public NewSource(String Category, String Link)
        {
            this.Category = Category;
            this.Link = Link;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_list);
        listView = (ListView) findViewById(R.id.source_list);
        List<Map<String, Object>> list = getData();
        listView.setAdapter(new SourceListAdapter(SourceListActivity.this, list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                addUserSource(MainActivity.appSourcesNames.get(i), MainActivity.appsourcesLinks.get(i));
                finish();
            }
        });

    }

    public List <Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < MainActivity.appSourcesNames.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("sourceName", MainActivity.appSourcesNames.get(i));
            list.add(map);
        }
        return list;
    }

    public InputStream getInputStream(URL url)
    {
        try
        {
            return url.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            validRSSLink = false;
            return null;
        }
    }

    DatabaseReference mRootref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference reader = mRootref.child("Reader");
    DatabaseReference sourcedb = reader.child("Sources");
    DatabaseReference userRss = reader.child("UserRSS");

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



    public String addRssSource(final String name, final String Link, final String Category)
    {
        String result = "";
        validRSSLink = false;
        new ProcessInBackGround().execute(Link);

        if (validRSSLink)
        {
            sourcedb.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null)
                    {
                        sourcedb.child(name).setValue(new NewSource(Category, Link));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            result = "Valid Link, Added";
        }
        else
        {
            result = "Invalid Link";
        }
        return result;
    }

    public class ProcessInBackGround extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... strings) {
            Exception exception = null;
            String link = "";
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
                                MainActivity.titles.add(xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("link"))
                        {
                            if (insideterm)
                            {
                                link = xpp.nextText();
                                MainActivity.links.add(link);
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("pubDate"))
                        {
                            if (insideterm)
                            {
                                MainActivity.dates.put(link, xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("image"))
                        {
                            if (insideterm)
                            {
                                MainActivity.images.put(link, xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("description"))
                        {
                            if (insideterm)
                            {
                                MainActivity.description.put(link, xpp.nextText());
                            }
                        }
                    }
                    else if ((eventType == XmlPullParser.END_TAG) && (xpp.getName().equalsIgnoreCase("item")))
                    {
                        insideterm = false;
                    }

                    eventType = xpp.next();
                }

                validRSSLink = true;

            }
            catch (MalformedURLException e)
            {
                exception = e;
                validRSSLink = false;
            }
            catch (XmlPullParserException e)
            {
                exception = e;
                validRSSLink = false;
            }
            catch (IOException e)
            {
                exception = e;
                validRSSLink = false;
            }
            return null;
        }
    }
}

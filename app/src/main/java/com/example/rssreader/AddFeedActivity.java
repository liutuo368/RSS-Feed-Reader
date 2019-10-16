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

    public String addRssSource(final String name, final String Link, final String Category) throws ExecutionException, InterruptedException {
        String result = "";
        validRSSLink = false;
        String execResult = new ProcessInBackGround().execute(Link).get();

        if (validRSSLink)
        {
            sourcedb.child(name.toUpperCase()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null)
                    {
                        sourcedb.child(name.toUpperCase()).setValue(new NewSource(Category, Link));
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

    public class ProcessInBackGround extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
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
            return "executed";
        }
    }

}

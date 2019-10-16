package com.example.rssreader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class NewsFragment extends Fragment{

    private ListView listView;
    private List <Map<String, Object>> list;
    private NewsListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        listView = (ListView) view.findViewById(R.id.newsList);
        list = getData();
        adapter = new NewsListAdapter(getActivity(), list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ContentActivity.class);
                intent.setAction("newsInfo");
                intent.putExtra("title", (String) list.get(position).get("title"));
                intent.putExtra("date", MainActivity.dates.get(MainActivity.links.get(position)));
                intent.putExtra("content", MainActivity.description.get(MainActivity.links.get(position)));
                intent.putExtra("link", MainActivity.links.get(position));
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.news_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Holo_Light_Dialog);
                builder.setTitle("Sort by");
                final String[] options = {"Title", "Date"};
                builder.setItems(options, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, final int which)
                    {
                        Collections.sort(list, new Comparator<Map<String, Object>>() {
                            @Override
                            public int compare(Map<String, Object> stringObjectMap, Map<String, Object> t1) {
                                String value1, value2;
                                if (which == 0) {
                                    value1 = stringObjectMap.get("title").toString();
                                    value2 = t1.get("title").toString();
                                } else {
                                    value1 = stringObjectMap.get("date").toString();
                                    value2 = t1.get("date").toString();
                                }
                                return value1.compareTo(value2);
                            }
                        });
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.show();
                return true;
            case R.id.menu_refresh:
                MainActivity.titles = new ArrayList<>();
                MainActivity.links = new ArrayList<>();
                MainActivity.dates = new HashMap<>();
                MainActivity.images = new HashMap<>();
                MainActivity.description = new HashMap<>();
                for (int i=0;i<MainActivity.usersourcelinks.size();i++)
                {
                    try {
                        String str_result = new ProcessInBackGround().execute(MainActivity.usersourcelinks.get(i)).get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                list = getData();
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public List <Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < MainActivity.titles.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", MainActivity.images.get(MainActivity.links.get(i)));
            map.put("title", MainActivity.titles.get(i));
            map.put("date", MainActivity.dates.get(MainActivity.links.get(i)));
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
            String link="";
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


    public void getuserSources(final MainActivity.FirebaseCallback firebaseCallback)
    {
        ValueEventListener event = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                {
                    MainActivity.usersourcenames = new ArrayList<>();
                    MainActivity.usersourcelinks = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        MainActivity.usersourcenames.add(ds.getKey());
                        MainActivity.usersourcelinks.add(ds.getValue(String.class));
                    }
                }

                try {
                    firebaseCallback.onCallback(MainActivity.usersourcenames, MainActivity.usersourcelinks);
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

}

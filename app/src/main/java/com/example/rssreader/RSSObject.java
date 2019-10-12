package com.example.rssreader;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.InputStream;
import java.util.ArrayList;

public class RSSObject
{
    public String sourceName;
    public String url;
    public String category;

    public ArrayList<String> titles = new ArrayList<>();
    public ArrayList<String> links = new ArrayList<>();

    public RSSObject(String sourceName, String url, String category)
    {
        this.sourceName = sourceName;
        this.url = url;
        this.category = category;
    }

    public RSSObject(String url)
    {
        this.url = url;
    }

    public boolean checkCorrectness()
    {
        return false;
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

    public class ProcessInBackGround extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            Exception exception = null;
            try
            {
                URL Url = new URL("http://feeds.news24.com/articles/fin24/tech/rss");
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
                        else if (xpp.getName().equalsIgnoreCase("links"))
                        {
                            if (insideterm)
                            {
                                links.add(xpp.nextText());
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

    public void getData()
    {
        Exception exception = null;
        try
        {
            URL Url = new URL(this.url);
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
                    else if (xpp.getName().equalsIgnoreCase("links"))
                    {
                        if (insideterm)
                        {
                            links.add(xpp.nextText());
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

    }

}

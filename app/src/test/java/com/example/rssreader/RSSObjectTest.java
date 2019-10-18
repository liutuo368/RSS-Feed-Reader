package com.example.rssreader;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class RSSObjectTest
{
    @Test
    public void testGetUserSources()
    {
        MainActivity.usersourcelinks = new ArrayList<>();
        MainActivity.usersourcelinks.add("https://www.buzzfeed.com/world.xml");
        MainActivity.titles = new ArrayList<>();
        MainActivity.links = new ArrayList<>();

        MainActivity.dates = new HashMap<>();
        MainActivity.images = new HashMap<>();
        MainActivity.description = new HashMap<>();

        RSSObject rss = new RSSObject();
        rss.getUserData();

        assertTrue("Titles not downloaded", (MainActivity.titles.size() > 0));
        assertTrue("Links not downloaded", (MainActivity.links.size() > 0));
        assertTrue("Dates not downloaded", (MainActivity.dates.size() > 0));
        assertTrue("Images not downloaded", (MainActivity.images.size() > 0));
        assertTrue("Description not downloaded", (MainActivity.description.size() > 0));
        assertTrue("Titles and links do not match", (MainActivity.titles.size() == MainActivity.links.size()));
    }

}
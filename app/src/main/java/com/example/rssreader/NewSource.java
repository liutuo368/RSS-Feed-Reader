/**
 * Author : Jihirshu Narayan
 *
 * Description : Instances of this class are inserted in the firebase rss sources databse as a single entity and the firebase api
 *              handles the creation of child nodes.
 */


package com.example.rssreader;

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

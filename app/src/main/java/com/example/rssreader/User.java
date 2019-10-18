/**
 * Author : Jihirshu Narayan
 *
 * Description : Instances of this class are inserted in the firebase rss userdata databse as a single entity and the firebase api
 *              handles the creation of child nodes.
 */


package com.example.rssreader;

import java.util.ArrayList;
import java.util.List;


public class User
{
    public String username;
    public String password;
    public String name;
    public List<String> Favourites = new ArrayList<String>();


    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
//        this.name = name;
    }

    public User(String username, String password, String name)
    {
        this.username = username;
        this.password = password;
        this.name = name;
    }


}
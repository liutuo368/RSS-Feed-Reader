package com.example.rssreader;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
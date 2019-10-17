package com.example.rssreader;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static List<String> favouriteDescriptions = new ArrayList<>();
    public static List<String> favouriteDates = new ArrayList<>();


    // Implement the function to switch between different fragments
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
        // Get user login information
        if(action.equals("user")) {
            user = intent.getStringExtra("username");
        }


        RSSObject rss = new RSSObject();
        rss.getUserData();
        rss.getUserFavourites();
        rss.getSources();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WelcomeFragment()).commit();
    }

}

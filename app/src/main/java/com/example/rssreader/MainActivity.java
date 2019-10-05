package com.example.rssreader;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;





public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    DatabaseReference mRootref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference reader = mRootref.child("Reader");
    DatabaseReference userdata = reader.child("UserData");
    DatabaseReference username = userdata.child("username");
    DatabaseReference password = userdata.child("password");


//    public void writedata (String username, String password)
//    {
//        mRootref.setValue(username);
//        mRootref.setValue(password);
//    }

    public void buttonaction(View view)
    {


        EditText Username = (EditText) findViewById(R.id.editText);
        EditText Password = (EditText) findViewById(R.id.editText2);
        TextView tv = (TextView) findViewById(R.id.textView2);

        String user = Username.getText().toString();
        String pass = Password.getText().toString();

        User created = new User(user, pass);

        userdata.child(user).setValue(created);
        tv.setText("Executed");

    }
}

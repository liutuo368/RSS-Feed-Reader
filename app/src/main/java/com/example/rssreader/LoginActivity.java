package com.example.rssreader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void loginOnClick(View v) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void signinOnClick(View v) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
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

    /*
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
    */

}

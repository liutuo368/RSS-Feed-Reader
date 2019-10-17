package com.example.rssreader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    public String validUser = null;
    public String Username;
    public String Password;
    public String RetrievedPassword = "";
    DatabaseReference mRootref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference reader = mRootref.child("Reader");
    DatabaseReference userdata = reader.child("UserData");
    DatabaseReference usernameNode = userdata.child("username");
    DatabaseReference passwordNode = userdata.child("password");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    public void loginOnClick(View v)
    {
        EditText username = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);
        Username = username.getText().toString();
        Password = password.getText().toString();

        validateUser(new FirebaseCallback() {
            @Override
            public void onCallback(String username, String password)
            {
                if (Password.equals(RetrievedPassword)) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setAction("user");
                    intent.putExtra("username", username);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Login failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void signinOnClick(View v)
    {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);

    }

    public void validateUser(final FirebaseCallback firebaseCallback)
    {
        ValueEventListener event = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                {
                    RetrievedPassword = dataSnapshot.child("password").getValue(String.class);
                }

                firebaseCallback.onCallback(Username, RetrievedPassword);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        userdata.child(Username).addValueEventListener(event);


    }


    public interface FirebaseCallback
    {
        void onCallback(String username, String password);
    }




}

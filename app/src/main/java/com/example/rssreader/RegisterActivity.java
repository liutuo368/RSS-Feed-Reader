package com.example.rssreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RegisterActivity extends AppCompatActivity {

    public String Username;
    public String Password;
    public Boolean RegisterFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void registerOnClick(View v)
    {
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        EditText conPass = (EditText) findViewById(R.id.conpass);
        Username = username.toString();
        Password = password.toString();
        if(password.getText().toString().equals(conPass.getText().toString())) {
            newUser(new FirebaseCallback() {
                @Override
                public void onCallback(Boolean flag) {
                    RegisterFlag = flag;
                }
            });

            if(RegisterFlag) {
                Toast.makeText(this, "Successfully signed up.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Username already exists, please try again", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Password don't match!", Toast.LENGTH_LONG).show();
        }
    }


    // Creating the required instaces of database

    DatabaseReference mRootref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference reader = mRootref.child("Reader");
    DatabaseReference userdata = reader.child("UserData");




    public void newUser(final FirebaseCallback firebaseCallback)
    {

        userdata.child(Username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag = false;
                if (dataSnapshot.getValue() == null)
                {
                    createNewUser(Username, Password);
                    flag = true;
                }

                firebaseCallback.onCallback(flag);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void createNewUser(String username, String password)
    {
        userdata.child(username).setValue(new User(username, password));
    }


    public interface FirebaseCallback
    {
        void onCallback(Boolean flag);
    }


}

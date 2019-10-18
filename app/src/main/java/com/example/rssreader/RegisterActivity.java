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
        Username = username.getText().toString();
        Password = password.getText().toString();
        if(password.getText().toString().equals(conPass.getText().toString())) {
            newUser(new FirebaseCallback() {
                @Override
                public void onCallback(Boolean flag) {
                    RegisterFlag = flag;

                    if(RegisterFlag) {
                        Toast.makeText(getApplicationContext(), "Successfully signed up.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Username already exists, please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(this, "Password don't match!", Toast.LENGTH_LONG).show();
        }
    }


    // Creating the required instaces of database

    DatabaseReference mRootref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference reader = mRootref.child("Reader");
    DatabaseReference userdata = reader.child("UserData");


    /**
     * Author : Jihirshu Narayan
     * @param firebaseCallback Callback function implementation of the FirebaseCallback interface
     *
     * Description : This function reads the firebase database and checks whether the username passed exists in the
     *               database or not and if it does not, it creates a new user with the given user name and password.
     */

    public void newUser(final FirebaseCallback firebaseCallback)
    {

        ValueEventListener event = new ValueEventListener() {
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
        };

        userdata.child(Username).addListenerForSingleValueEvent(event);
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

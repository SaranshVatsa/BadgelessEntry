package com.project.saransh.badgelessentry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth = null;
    TextView textView = null;
    DatabaseReference databaseReference = null;
    DatabaseReference userReference = null;
    FirebaseUser currentFirebaseUser = null;
    private SharedPreferences sharedPreferences = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.textView = (TextView) findViewById(R.id.textView2);
        this.firebaseAuth = FirebaseAuth.getInstance();
        if (this.firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Log In First!", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        this.textView.setText("Welcome " + firebaseAuth.getCurrentUser().getEmail());

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userReference = databaseReference.child("entryExit");
        sharedPreferences = getApplicationContext().getSharedPreferences("PhoneOwnerEmail", 0);

        String uID = currentFirebaseUser.getUid();
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String entryTime = s.format(new Date());
        String exitTime = "";

        EntryExitTime entryExitTime = new EntryExitTime(entryTime, exitTime);
        String entryKey = userReference.child(uID).push().getKey();
        userReference.child(uID).child(entryKey).setValue(entryExitTime);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("entryKey", entryKey);
        editor.putString("entryTime", entryTime);
        editor.apply();

    }

    public void signOutUser(View view) {
        this.firebaseAuth.signOut();
        Toast.makeText(this, "Sign Out successful!", Toast.LENGTH_SHORT).show();
        String entryKey = this.sharedPreferences.getString("entryKey", null);
        String entryTime = this.sharedPreferences.getString("entryTime", null);
        String uID = currentFirebaseUser.getUid();

        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String exitTime = s.format(new Date());

        EntryExitTime entryExitTime = new EntryExitTime(entryTime, exitTime);
        userReference.child(uID).child(entryKey).setValue(entryExitTime);
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}

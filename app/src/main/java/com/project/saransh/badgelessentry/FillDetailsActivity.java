package com.project.saransh.badgelessentry;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FillDetailsActivity extends AppCompatActivity {

    EditText nameField = null;
    EditText desField = null;
    EditText phNoField = null;

    DatabaseReference databaseReference = null;
    DatabaseReference userReference = null;
    FirebaseUser currentFirebaseUser = null;
    private SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filldetails);
        nameField = (EditText)findViewById(R.id.editText2);
        desField = (EditText)findViewById(R.id.editText3);
        phNoField = (EditText)findViewById(R.id.editText4);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        sharedPreferences = getApplicationContext().getSharedPreferences("PhoneOwnerEmail", 0);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        userReference = databaseReference.child("users");
    }

    public void submitDetails(View view){
        String uID = currentFirebaseUser.getUid();
        String fName = nameField.getText().toString();
        String des = desField.getText().toString();
        String phNo = phNoField.getText().toString();

        User user = new User(uID, fName, des, phNo);
        userReference.push().setValue(user);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("FirstTimeLogin", "true");
        editor.apply();
    }
}

package com.project.saransh.badgelessentry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button registerButton = null;
    private Button signUpButton = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.registerButton = (Button) findViewById(R.id.button);
        this.signUpButton = (Button) findViewById(R.id.button2);
    }

    public void registerUser(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void signInUser(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}

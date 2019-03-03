package com.project.saransh.badgelessentry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    EditText emailEditText = null;
    private FirebaseAuth firebaseAuth = null;
    ProgressDialog progressDialog = null;
    Button registerButton = null;
    private SharedPreferences sharedPreferences = null;
    private TelephonyManager telephonyManager = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.sharedPreferences = getApplicationContext().getSharedPreferences("PhoneOwnerEmail", 0);
        this.emailEditText = (EditText) findViewById(R.id.editText);
        this.registerButton = (Button) findViewById(R.id.button3);
        if (this.sharedPreferences.getString("Registered Email", null) != null) {
            Toast.makeText(this, "Already Registered!", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        this.firebaseAuth = FirebaseAuth.getInstance();
        if (this.firebaseAuth.getCurrentUser() != null) {
            Toast.makeText(this, "Already Logged In!", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }
        progressDialog = new ProgressDialog(this);
    }

    public void registerUser(View view) {
        String email = emailEditText.getText().toString();
        Editor editor = sharedPreferences.edit();
        editor.putString("Registered Email", email);
        editor.apply();
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") == 0) {
            progressDialog.setMessage("Registering Please Wait...");
            progressDialog.show();
            this.firebaseAuth.createUserWithEmailAndPassword(email, telephonyManager.getDeviceId())
                .addOnCompleteListener((Activity)this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            finish();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Couldn't Register, Please Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }
}

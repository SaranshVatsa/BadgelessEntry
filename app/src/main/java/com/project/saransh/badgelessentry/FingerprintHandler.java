package com.project.saransh.badgelessentry;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintManager.AuthenticationCallback;
import android.hardware.fingerprint.FingerprintManager.AuthenticationResult;
import android.hardware.fingerprint.FingerprintManager.CryptoObject;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

@TargetApi(23)
public class FingerprintHandler extends AuthenticationCallback {
    private Context appContext;
    private CancellationSignal cancellationSignal;
    private FirebaseAuth firebaseAuth = null;
    private ProgressDialog progressDialog = null;
    private SharedPreferences sharedPreferences = null;
    private TelephonyManager telephonyManager = null;

    FingerprintHandler(Context context) {

        appContext = context;
    }

    void startAuth(FingerprintManager manager, CryptoObject cryptoObject) {
        this.cancellationSignal = new CancellationSignal();
        if (ContextCompat.checkSelfPermission(appContext, "android.permission.USE_FINGERPRINT") == 0) {
            firebaseAuth = FirebaseAuth.getInstance();
            progressDialog = new ProgressDialog((Activity)appContext);
            manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        }
    }

    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        Toast.makeText(this.appContext, "Authentication error\n" + errString, Toast.LENGTH_SHORT).show();
    }

    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        Toast.makeText(this.appContext, "Authentication help\n" + helpString, Toast.LENGTH_SHORT).show();
    }

    public void onAuthenticationFailed() {
        Toast.makeText(this.appContext, "Authentication failed.", Toast.LENGTH_SHORT).show();
    }

    public void onAuthenticationSucceeded(AuthenticationResult result) {
        Toast.makeText(this.appContext, "Authentication succeeded.", Toast.LENGTH_SHORT).show();
        this.telephonyManager = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(appContext, "android.permission.READ_PHONE_STATE") == 0) {
            sharedPreferences = appContext.getSharedPreferences("PhoneOwnerEmail", 0);
            String email = sharedPreferences.getString("Registered Email", null);
            String password = telephonyManager.getDeviceId();
            if (email == null) {
                Toast.makeText(appContext, "Register First!", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                progressDialog.setMessage("Logging In. Please Wait...");
                progressDialog.show();
            }catch (Exception e){
                Toast.makeText(appContext, "Log In under progress.", Toast.LENGTH_SHORT).show();
            }
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity)appContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            try {
                                progressDialog.dismiss();
                            }catch (Exception e){
                                Toast.makeText(appContext, "Log In Successful.", Toast.LENGTH_SHORT).show();
                            }
                            if(sharedPreferences.getString("FirstTimeLogin", null) == null){
                                Intent intent = new Intent(appContext, FillDetailsActivity.class);
                                appContext.startActivity(intent);
                            }else {
                                Intent intent = new Intent(appContext, ProfileActivity.class);
                                appContext.startActivity(intent);
                            }
                        }
                        else {
                            try {
                                progressDialog.dismiss();
                            }catch (Exception e){
                                Toast.makeText(appContext, "Log In Failed.", Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(appContext, "Email or Password Not found, Please Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }
}

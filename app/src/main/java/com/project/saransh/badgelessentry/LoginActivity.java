package com.project.saransh.badgelessentry;

import android.app.KeyguardManager;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintManager.CryptoObject;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class LoginActivity extends AppCompatActivity {
    private static final String KEY_NAME = "example_key";
    private Cipher cipher;
    private CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private FirebaseAuth firebaseAuth = null;
    private FingerprintHandler helper = null;
    private KeyGenerator keyGenerator;
    private KeyStore keyStore;
    private KeyguardManager keyguardManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(VERSION.SDK_INT>23) {
            this.firebaseAuth = FirebaseAuth.getInstance();
            if (this.firebaseAuth.getCurrentUser() != null) {
                Toast.makeText(this, "Already Logged In!", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(this, ProfileActivity.class));
            }
            prepareFingerprint();
        }
    }

    public void prepareFingerprint() {
        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        if (VERSION.SDK_INT <= 23) {
            return;
        }
        if (!keyguardManager.isKeyguardSecure()) {
            Toast.makeText(this, "Lock screen security not enabled in Settings", Toast.LENGTH_SHORT).show();
        } else if (ContextCompat.checkSelfPermission(this, "android.permission.USE_FINGERPRINT") != 0) {
            Toast.makeText(this, "Fingerprint authentication permission not enabled", Toast.LENGTH_SHORT).show();
        } else if (fingerprintManager.hasEnrolledFingerprints()) {
            generateKey();
            if (cipherInit()) {
                cryptoObject = new CryptoObject(cipher);
                helper = new FingerprintHandler(LoginActivity.this);
                helper.startAuth(fingerprintManager, cryptoObject);
            }
        } else {
            Toast.makeText(this, "Register at least one fingerprint in Settings", Toast.LENGTH_SHORT).show();
        }
    }

    protected void generateKey() {
        if (VERSION.SDK_INT > 23) {
            try {
                keyStore = KeyStore.getInstance("AndroidKeyStore");
            } catch (Exception e) {
                Toast.makeText(this, "Error in getting Keystore Instance", Toast.LENGTH_SHORT).show();
            }

            try {
                keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
                keyStore.load(null);
                keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setUserAuthenticationRequired(true)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .build());
                keyGenerator.generateKey();
            } catch (Exception e) {
                Toast.makeText(this, "Error in generating key", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES
                    + "/" + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);

            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (Exception e) {
            Toast.makeText(this, "Error in cipher", Toast.LENGTH_SHORT).show();
            return  false;
        }
    }
}

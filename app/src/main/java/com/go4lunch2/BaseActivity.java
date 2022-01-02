package com.go4lunch2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.go4lunch2.ui.login.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

abstract public class BaseActivity extends AppCompatActivity {

    public static FirebaseAuth mAuth;
    public static FirebaseUser currentUser;
    public static FirebaseUser user;
    private final String TAG = "MyLog BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    Log.i(TAG, "onComplete: Signout");
                    startActivity(new Intent(BaseActivity.this, LogInActivity.class));
                    finish();
                });
    }
}

package com.birjulabsinc.googlelogin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.birjulabsinc.googlelogin.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class MainActivity extends AppCompatActivity {
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;/// used for Login with Google

    ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getting our root layout in our view.
        // below line is to set
        // Content view for our layout.
//        setContentView(R.layout.activity_main);
//        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);


// Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.AUTH_ID))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
// Set the dimensions of the sign-in button.
        activityMainBinding.signInButton.setSize(SignInButton.SIZE_STANDARD);
        activityMainBinding.signInButton.setOnClickListener(v -> signIn());

    }


    @Override
    protected void onStart() {
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Toast.makeText(getApplicationContext(), "not null", Toast.LENGTH_SHORT).show();
            // signed in. Show the "sign out" button and explanation.
            // ...
            updateUi(account);
        } else {
            // not signed in. Show the "sign in" button and explanation.
            // ...
            Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_SHORT).show();
        }

        super.onStart();

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            if (account != null) {
                String personName = account.getDisplayName();
                String personGivenName = account.getGivenName();
                String personFamilyName = account.getFamilyName();
                String personEmail = account.getEmail();
                String personId = account.getId();
                Uri personPhoto = account.getPhotoUrl();
                updateUi(account);
            }

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void updateUi(GoogleSignInAccount account) {
        activityMainBinding.layoutData.setVisibility(View.VISIBLE);
        activityMainBinding.signInButton.setVisibility(View.GONE);
        activityMainBinding.personName.setText(account.getDisplayName());
        activityMainBinding.personGivenName.setText(account.getGivenName());
        activityMainBinding.personFamilyName.setText(account.getFamilyName());
        activityMainBinding.personEmail.setText(account.getEmail());
        activityMainBinding.personId.setText(account.getId());
        activityMainBinding.personPhoto.setText(account.getPhotoUrl().toString());
        activityMainBinding.logout.setOnClickListener(v -> {
            mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    activityMainBinding.layoutData.setVisibility(View.GONE);
                    activityMainBinding.signInButton.setVisibility(View.VISIBLE);
                }
            });
        });


    }

}
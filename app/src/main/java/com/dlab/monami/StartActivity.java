package com.dlab.monami;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class StartActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.emailStartButton).setOnClickListener(this);
        findViewById(R.id.GoogleStartButton).setOnClickListener(this);
        findViewById(R.id.startlogin).setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //updateUI(account);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult","start: "+resultCode);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if(resultCode != RESULT_CANCELED) {
            if (requestCode == RC_SIGN_IN) {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
                Log.d("google","login completed");
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d("googlelog",account.toString());
            // Signed in successfully, show authenticated UI.
            googlelogin(account.getEmail(), account);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }


    private void updateUI(GoogleSignInAccount account) {
        Log.d("updateUI",account.getEmail()+account.getGivenName()+account.getId());
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        //intent.putExtra(SignupActivity.GOOGLE_ACCOUNT, account);
        intent.putExtra("email",account.getEmail());
        intent.putExtra("name",account.getGivenName());

        startActivityForResult(intent,1001);
        finish();
    }

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]


/*
    private void updateUI(FirebaseUser user) {
        hideProgressBar();
        if (user != null) {
            mBinding.status.setText(getString(R.string.google_status_fmt, user.getEmail()));
            mBinding.detail.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            mBinding.signInButton.setVisibility(View.GONE);
            mBinding.signOutAndDisconnect.setVisibility(View.VISIBLE);
        } else {
            mBinding.status.setText(R.string.signed_out);
            mBinding.detail.setText(null);

            mBinding.signInButton.setVisibility(View.VISIBLE);
            mBinding.signOutAndDisconnect.setVisibility(View.GONE);
        }
    }*/

    private void googlelogin(String email, GoogleSignInAccount account) {
        Log.d(TAG, "google login ========");

        mAuth = FirebaseAuth.getInstance();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing in...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);

        progressDialog.show();
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                        if (isNewUser) {
                            progressDialog.dismiss();
                            Log.e("TAG", "Is New User!");
                            updateUI(account);
                        } else {
                            progressDialog.dismiss();
                            Log.e("TAG", "Is Old User!");
                            Intent gotomain = new Intent(StartActivity.this, MainActivity.class);
                            gotomain.putExtra("email",email);
                            startActivity(gotomain);
                            finish();
                        }

                    }
                });

        /*
        mAuth.createUserWithEmailAndPassword(email, "01001010101")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //loaderLayout.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            try
                            {
                                throw task.getException();
                            }
                            // if user enters wrong email.
                            catch (FirebaseAuthWeakPasswordException weakPassword)
                            {
                                Log.d(TAG, "onComplete: weak_password");
                                // TODO: take your actions!
                                updateUI(account);
                            }
                            // if user enters wrong password.
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                            {
                                Log.d(TAG, "onComplete: malformed_email");
                                // TODO: Take your action
                            }
                            catch (FirebaseAuthUserCollisionException existEmail)
                            { // google Login
                                Log.d(TAG, "onComplete: exist_email");
                                Intent gotomain = new Intent(StartActivity.this, MainActivity.class);
                                startActivity(gotomain);
                            }
                            catch (Exception e)
                            {
                                Log.d(TAG, "onComplete: " + e.getMessage());
                                updateUI(account);
                            }
                        }
                    }
                });*/
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i==R.id.GoogleStartButton) {
            signIn();
        } else if (i == R.id.emailStartButton) {
            Intent loginIntent = new Intent(StartActivity.this, SignupActivity.class);
            startActivity(loginIntent);
        } else if (i == R.id.startlogin) {
            Intent loginIntent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        }
    }
}
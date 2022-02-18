package com.example.socialmedialogin;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private OAuthProvider.Builder provider;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker accessTokenTracker;
    private static final String EMAIL = "email";
    private static final String PROFILE = "public_profile";
    private MaterialButton googleSignInBtn;
    private LoginButton facebookSignInBtn;
    static Intent mSendIntent;
    static User mUser;
    FirebaseUser mFirebaseUser;
    Uri image;
    String name;
    String mail;

    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            updateUI(new User(currentUser.getPhotoUrl(),
                    currentUser.getDisplayName(),
                    currentUser.getEmail()));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.getSdkVersion();

        googleSignInBtn = findViewById(R.id.google_btn);

        facebookSignInBtn = (LoginButton) findViewById(R.id.facebook_btn);
        mAuth = FirebaseAuth.getInstance();

        processSignInRequest();
        //facebook callback manager
        mCallbackManager = CallbackManager.Factory.create();
        facebookSignInBtn.setReadPermissions(Arrays.asList(EMAIL));
        facebookSignInBtn.setPermissions(Arrays.asList(EMAIL, PROFILE));
       // provider.build();
        List<String> scopes = new ArrayList<String>() {{
            add("user:email");
        }};
      //  provider.setScopes(scopes);

        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processGoogleUserLoginRequest();
            }
        });

        //configure facebook sign in callback
        facebookSignInBtn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                    handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


//facebookbtn onclick listener
        facebookSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            callFaceBookLoginManager();

            }
        });



    }




    //oncreate method end
    void callFaceBookLoginManager(){
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                      //  showToast(getApplicationContext(),"callback success");

                       handleFacebookAccessToken(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }


    private void handleFacebookAccessToken(AccessToken token) {


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mUser=null;
                            showToast(getApplicationContext(),"ui updated");
                            mUser=getUser();
                            if(mUser.getEmail()==null){
                                showToast(getApplicationContext(),"null mail");
                            }

                            if(mUser!=null){
                              updateUI(mUser);

                            }
                        } else {
                            LoginManager.getInstance().logOut();
                            showToast(getApplicationContext(),"ui-update-failed\n");
                            showToast(getApplicationContext(),"you already signed-up with google\n");

                        }
                    }
                });
    }
//google sign in request processing
    private void processSignInRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id1))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void processGoogleUserLoginRequest() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {

            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(), "sign-in-succesfully", Toast.LENGTH_SHORT).show();
                            mUser = getUser();
                            updateUI(mUser);


                        } else {
                            FirebaseAuth.getInstance().signOut();
                            showToast(getApplicationContext(),"you already signed up using facebook\n");
                        }
                    }
                });
    }


    private User getUser() {
        mFirebaseUser = mAuth.getCurrentUser();
        assert mFirebaseUser != null;
        image = mFirebaseUser.getPhotoUrl();
        name = mFirebaseUser.getDisplayName();
        mail = mFirebaseUser.getEmail();
        mFirebaseUser=null;
        return new User(image, name, mail);
    }

    private void updateUI(User user) {
        mSendIntent = new Intent(getApplicationContext(), CurrentUserInformationActivity.class);
        mSendIntent.putExtra("user", user);
        startActivity(mSendIntent);
        finish();

    }
    private void showToast(Context activityContex,String content){
        Toast.makeText(activityContex,content, Toast.LENGTH_SHORT).show();

    }
}


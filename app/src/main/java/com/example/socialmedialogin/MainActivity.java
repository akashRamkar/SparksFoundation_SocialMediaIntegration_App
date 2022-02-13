package com.example.socialmedialogin;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GithubAuthCredential;
import com.google.firebase.auth.GithubAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;

import java.security.AuthProvider;


public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private OAuthProvider.Builder provider;
    MaterialButton googleSignInBtn,gitHubSignInBtn;
    static  String logged_in_user="";
    static Intent mSendIntent;
    static  User mUser;

    private FirebaseAuth mAuth;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
       // FirebaseUser currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FirebaseApp.initializeApp(this);
        googleSignInBtn=findViewById(R.id.google_btn);
        gitHubSignInBtn=findViewById(R.id.gitHub_btn);
        processSignInRequest();
         provider = OAuthProvider.newBuilder("github.com");
        mAuth=FirebaseAuth.getInstance();




        // Configure Google Sign In

        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processGoogleUserLoginRequest();
            }
        });



        gitHubSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            startActivityForGitHubSignIn();
            }
        });
/*
        facebookSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

 */






    }



    private void processSignInRequest(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id1))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void processGoogleUserLoginRequest(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                            Toast.makeText(getApplicationContext(),"sign-in-succesfully",Toast.LENGTH_SHORT).show();

                            mUser=getUser();
                           updateUI(mUser);
                           mUser=null;



                        } else {

                        }
                    }
                });
    }






   void getPendingAuthResult(){
        Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // User is signed in.
                                    // IdP data available in
                                    // authResult.getAdditionalUserInfo().getProfile().
                                    // The OAuth access token can also be retrieved:
                                    // authResult.getCredential().getAccessToken().
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure.
                                }
                            });
        } else {
            // There's no pending result so you need to start the sign-in flow.
            // See below.
        }
    }

    void startActivityForGitHubSignIn(){

                mAuth.startActivityForSignInWithProvider(/* activity= */ this, provider.build())
                .addOnSuccessListener(
                        new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                // User is signed in.
                                // IdP data available in
                                // authResult.getAdditionalUserInfo().getProfile().
//                                Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                                // The OAuth access token can also be retrieved:
                                // authResult.getCredential().getAccessToken().
//                                String name=getUser().getUserName();
//                                Uri img=authResult.getUser().getPhotoUrl();
//                                mUser=new User(img,name);


//                                updateUI(mUser);
//                                mUser=null;

                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure.
                            }
                        });
    }


  private User getUser(){
      FirebaseUser currentLoggedInUser =mAuth.getCurrentUser();
      assert currentLoggedInUser != null;
      Uri image=currentLoggedInUser.getPhotoUrl();
      String name=currentLoggedInUser.getDisplayName();
      return new User(image,name);
  }
  private void updateUI(User user){
      mSendIntent =new Intent(getApplicationContext(),CurrentUserInformationActivity.class);
      mSendIntent.putExtra("user",user);
      startActivity(mSendIntent);
      finish();
  }


}
package com.example.socialmedialogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class CurrentUserInformationActivity extends AppCompatActivity {
    ImageView imageView;
    MaterialTextView name,email;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_user_information);
        name=findViewById(R.id.user_name);
        email=findViewById(R.id.user_mail);
        imageView=findViewById(R.id.user_image);
        Intent mainActivityIntent=getIntent();
        currentUser=mainActivityIntent.getParcelableExtra("user");
        Picasso.get().load(currentUser.getProfileImage()).into(imageView);
        name.setText(currentUser.getUserName().toString());
        email.setText(currentUser.getEmail());


    }
    public void signOutUser(View v){
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Toast.makeText(getApplicationContext(), "sign-out-succefully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
}
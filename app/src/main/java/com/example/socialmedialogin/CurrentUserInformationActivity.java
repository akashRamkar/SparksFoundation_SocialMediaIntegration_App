package com.example.socialmedialogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

public class CurrentUserInformationActivity extends AppCompatActivity {
    private ShapeableImageView imageView;
    MaterialTextView name,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_user_information);
        name=findViewById(R.id.user_name);
        email=findViewById(R.id.user_mail);
        imageView=findViewById(R.id.user_image);
    }
    public void signOutUser(View v){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getApplicationContext(), "sign-out-succefully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
}
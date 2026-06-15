package com.rao.multiaccountcredentials;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    // Declare views
    private ImageView profileImage;
    private TextView usernameTextView, fullNameTextView;
    private Button resetFaceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);
        ImageView backicon_profile;
        backicon_profile=findViewById(R.id.backicon_profile);
        backicon_profile.setOnClickListener(v->{
            onBackPressed();
            finish();
        });

        // Initialize views
        profileImage = findViewById(R.id.profile_image);
        usernameTextView = findViewById(R.id.username);
        fullNameTextView = findViewById(R.id.full_name);
        resetFaceButton = findViewById(R.id.reset_face_button);

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "N/A");
        String fullName = sharedPreferences.getString("full_name", "N/A");

        // Set user data to TextViews
        usernameTextView.setText("Username: " + username);
        fullNameTextView.setText("Full Name: " + fullName);

        // Set OnClickListener for reset button
        resetFaceButton.setOnClickListener(v -> {
            // Clear SharedPreferences for face data (if any)
            Intent intent=new Intent(this,TestingActiviyt.class);
            intent.putExtra("state","reset");
            startActivity(intent);


        });
    }
}

package com.rao.multiaccountcredentials;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rao.multiaccountcredentials.comman.APIRequestHandler;
import com.rao.multiaccountcredentials.comman.APIs;
import com.rao.multiaccountcredentials.comman.CustomSnackbar;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    EditText fullName, username, password, confirmPassword;
    TextView signupButton;
    TextView already_have_account;
    ProgressBar progress_overlay_signup;

    FrameLayout signup_button_container;
    ConstraintLayout root_layout_sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Set status bar color

        root_layout_sign_up=findViewById(R.id.root_layout_sign_up);

        // Initialize Viewssignup_button_container
        signup_button_container=findViewById(R.id.signup_button_container);
        fullName = findViewById(R.id.full_name);
        username = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        signupButton = findViewById(R.id.signup_button);
        already_have_account = findViewById(R.id.already_have_account);
        progress_overlay_signup=findViewById(R.id.progress_overlay_signup);

        // Navigate to Login Activity if "Already have an account" is clicked
        already_have_account.setOnClickListener(v -> startActivity(new Intent(SignUpActivity.this, MainActivity.class)));

        SpannableString spannableString1 = new SpannableString("Already have an account? Login");
        spannableString1.setSpan(new ForegroundColorSpan(Color.parseColor("#09090B")), 0, spannableString1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Black for entire text
        spannableString1.setSpan(new ForegroundColorSpan(Color.parseColor("#2563EB")), 24, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        already_have_account.setText(spannableString1);
        // Set onClick listener for the Signup button
        signupButton.setOnClickListener(v -> validateInputs());
    }

    // Validation Function
    private void validateInputs() {
        // Get user inputs
        String fullNameText = fullName.getText().toString().trim();
        String usernameText = username.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        String confirmPasswordText = confirmPassword.getText().toString().trim();

        // Check if any field is empty
        if (TextUtils.isEmpty(fullNameText)) {
            CustomSnackbar.showSnackbar(SignUpActivity.this, root_layout_sign_up, "Full name is required", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);

//            Toast.makeText(this, "Full name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(usernameText)) {
//            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
            CustomSnackbar.showSnackbar(SignUpActivity.this, root_layout_sign_up, "Username is required", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
            return;
        }

        if (TextUtils.isEmpty(passwordText)) {
//            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            CustomSnackbar.showSnackbar(SignUpActivity.this, root_layout_sign_up, "Password is required", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
            return;
        }

        if (TextUtils.isEmpty(confirmPasswordText)) {
//            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
            CustomSnackbar.showSnackbar(SignUpActivity.this, root_layout_sign_up, "Please confirm your password", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
            return;
        }

        // Check if the password and confirm password fields match
        if (!passwordText.equals(confirmPasswordText)) {
//            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            CustomSnackbar.showSnackbar(SignUpActivity.this, root_layout_sign_up, "Passwords do not match", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
            return;
        }


        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
        if (isConnected) {
            signup_button_container.setEnabled(false);
            signupButton.setVisibility(View.GONE);
            progress_overlay_signup.setVisibility(View.VISIBLE);

            // Make EditTexts non-editable
            username.setFocusable(false);
            username.setFocusableInTouchMode(false);
            fullName.setFocusable(false);
            fullName.setFocusableInTouchMode(false);
            confirmPassword.setFocusable(false);
            confirmPassword.setFocusableInTouchMode(false);
            password.setFocusable(false);
            password.setFocusableInTouchMode(false);
            registerUser(fullNameText, usernameText, passwordText);
        } else {
            CustomSnackbar.showSnackbar(SignUpActivity.this, root_layout_sign_up, "No Internet", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
        }

        // Proceed with sign-up logic (e.g., save data or call an API)



    }

    private void registerUser(String fullName1, String usename1, String password1) {

        Map<String, String> params = new HashMap<>();
        params.put("username", usename1);
//        params.put("full_name", fullName);
//        params.put("password", password);


        APIRequestHandler apiRequestHandler = new APIRequestHandler(
                this,
                APIs.check_username, // Your registration API endpoint
                params,
                null, // No auth token
                null // No progress overlay
        );

        apiRequestHandler.sendRequest(new APIRequestHandler.APICallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String status = response.getString("status");
                    String message = response.getString("message");
                    if (status.equals("success")){
                        Intent intent=new Intent(SignUpActivity.this,FaceRegistrationScreen.class);
                        intent.putExtra("username",usename1);
                        intent.putExtra("full_name",fullName1);
                        intent.putExtra("password",password1);
                        intent.putExtra("state","1");
                        startActivity(intent);
                        finish();
                    }
                    else {
                        signup_button_container.setEnabled(true);
                        signupButton.setVisibility(View.VISIBLE);
                        progress_overlay_signup.setVisibility(View.GONE);

                        // Make EditTexts non-editable
                        username.setFocusable(true);
                        username.setFocusableInTouchMode(true);
                        fullName.setFocusable(true);
                        fullName.setFocusableInTouchMode(true);
                        confirmPassword.setFocusable(true);
                        confirmPassword.setFocusableInTouchMode(true);
                        password.setFocusable(true);
                        password.setFocusableInTouchMode(true);
                        CustomSnackbar.showSnackbar(SignUpActivity.this, root_layout_sign_up, message, R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
                    }

                } catch (Exception e) {
                    signup_button_container.setEnabled(true);
                    signupButton.setVisibility(View.VISIBLE);
                    progress_overlay_signup.setVisibility(View.GONE);

                    // Make EditTexts non-editable
                    username.setFocusable(true);
                    username.setFocusableInTouchMode(true);
                    fullName.setFocusable(true);
                    fullName.setFocusableInTouchMode(true);
                    confirmPassword.setFocusable(true);
                    confirmPassword.setFocusableInTouchMode(true);
                    password.setFocusable(true);
                    password.setFocusableInTouchMode(true);
                    Toast.makeText(SignUpActivity.this, "Something went wrong+++", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                signup_button_container.setEnabled(true);
                signupButton.setVisibility(View.VISIBLE);
                progress_overlay_signup.setVisibility(View.GONE);

                // Make EditTexts non-editable
                username.setFocusable(true);
                username.setFocusableInTouchMode(true);
                fullName.setFocusable(true);
                fullName.setFocusableInTouchMode(true);
                confirmPassword.setFocusable(true);
                confirmPassword.setFocusableInTouchMode(true);
                password.setFocusable(true);
                password.setFocusableInTouchMode(true);
                Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

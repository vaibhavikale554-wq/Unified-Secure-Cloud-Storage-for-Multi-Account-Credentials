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

public class MainActivity extends AppCompatActivity {
    TextView dont_have_account;
    ConstraintLayout root_layout_login;
    TextView login_button;
    EditText email, password;
    ProgressBar login_button_progress;
    FrameLayout loginButtonContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
//        getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
        login_button_progress=findViewById(R.id.login_button_progress);
        dont_have_account = findViewById(R.id.dont_have_account);
        dont_have_account.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
        root_layout_login=findViewById(R.id.root_layout_login);
        loginButtonContainer = findViewById(R.id.login_button_container);
        login_button = findViewById(R.id.login_button);


        SpannableString spannableString1 = new SpannableString("Don't have an account? Sign Up");
        spannableString1.setSpan(new ForegroundColorSpan(Color.parseColor("#09090B")), 0, spannableString1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Black for entire text
        spannableString1.setSpan(new ForegroundColorSpan(Color.parseColor("#2563EB")), 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        dont_have_account.setText(spannableString1);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate input fields
                if (email.getText().toString().isEmpty() && password.getText().toString().isEmpty()) {
                    CustomSnackbar.showSnackbar(MainActivity.this, root_layout_login, "Username and Password are empty", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
//                    Toast.makeText(MainActivity.this, "Email and Password are empty", Toast.LENGTH_SHORT).show();
                } else if (email.getText().toString().isEmpty()) {
                    CustomSnackbar.showSnackbar(MainActivity.this, root_layout_login, "Username is empty", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
//                    Toast.makeText(MainActivity.this, "Email is empty", Toast.LENGTH_SHORT).show();
                } else if (password.getText().toString().isEmpty()) {
//                    Toast.makeText(MainActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
                    CustomSnackbar.showSnackbar(MainActivity.this, root_layout_login, "Password is empty", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
                } else {
                    // Perform login
                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    boolean isConnected = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
                    if (isConnected) {
                        loginButtonContainer.setEnabled(false);
                        login_button.setVisibility(View.GONE);
                        login_button_progress.setVisibility(View.VISIBLE);

                        // Make EditTexts non-editable
                        email.setFocusable(false);
                        email.setFocusableInTouchMode(false);
                        password.setFocusable(false);
                        password.setFocusableInTouchMode(false);
                        loginUser(email.getText().toString(), password.getText().toString());

                    } else {
                        CustomSnackbar.showSnackbar(MainActivity.this, root_layout_login, "No Internet", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
                    }

                }
            }
        });
    }

    private void loginUser(String email1, String password1) {
        Map<String, String> params = new HashMap<>();
        params.put("username", email1);
        params.put("password", password1);

        APIRequestHandler apiRequestHandler = new APIRequestHandler(
                this,
                APIs.user_login, // Your login API endpoint
                params,
                null, // No auth token
                null // No progress overlay
        );

        apiRequestHandler.sendRequest(new APIRequestHandler.APICallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
//                    Toast.makeText(MainActivity.this, ""+response.toString(), Toast.LENGTH_SHORT).show();
                    String status = response.getString("status");
                    String message = response.getString("message");



                    if (status.equals("success")) {
                        String username=response.getString("username");
                        String full_name=response.getString("full_name");
                        // Save user information to SharedPreferences
                        saveToSharedPreferences(username, full_name);

                        // Proceed to the home activity
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        loginButtonContainer.setEnabled(true);
                        login_button.setVisibility(View.VISIBLE);
                        login_button_progress.setVisibility(View.GONE);

                        // Make EditTexts non-editable
                        email.setFocusable(true);
                        email.setFocusableInTouchMode(true);
                        password.setFocusable(true);
                        password.setFocusableInTouchMode(true);
                        CustomSnackbar.showSnackbar(MainActivity.this, root_layout_login, message, R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
                    }
                } catch (Exception e) {
                    loginButtonContainer.setEnabled(true);
                    login_button.setVisibility(View.VISIBLE);
                    login_button_progress.setVisibility(View.GONE);

                    // Make EditTexts non-editable
                    email.setFocusable(true);
                    email.setFocusableInTouchMode(true);
                    password.setFocusable(true);
                    password.setFocusableInTouchMode(true);
//                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    CustomSnackbar.showSnackbar(MainActivity.this, root_layout_login, "Something went wrong", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
                }
            }

            @Override
            public void onError(String error) {
                loginButtonContainer.setEnabled(true);
                login_button.setVisibility(View.VISIBLE);
                login_button_progress.setVisibility(View.GONE);

                // Make EditTexts non-editable
                email.setFocusable(true);
                email.setFocusableInTouchMode(true);
                password.setFocusable(true);
                password.setFocusableInTouchMode(true);
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToSharedPreferences(String username,String fullName) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putBoolean("islogin",true);
        editor.putString("full_name", fullName);
        editor.apply();
    }
}

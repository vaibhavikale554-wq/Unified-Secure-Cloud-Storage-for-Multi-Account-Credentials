package com.rao.multiaccountcredentials;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.WindowManager;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        setContentView(R.layout.activity_splash);
//        getWindow().setStatusBarColor(Color.parseColor("#D32F2F")); // Dark red
        TextView textView = findViewById(R.id.textView);

        SpannableString spannableString = new SpannableString("Securely store and manage multiple accounts in one place.");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#09090B")), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Black for entire text
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#2563EB")), 26, 43, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Blue for "extract number plate"

        textView.setText(spannableString);
        new Handler().postDelayed(() -> {
            // Start MainActivity or LoginActivity
            if (sharedPreferences.getBoolean("islogin",false)){
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
           // Finish splash activity so it's removed from back stack
        }, SPLASH_DURATION);
    }
}
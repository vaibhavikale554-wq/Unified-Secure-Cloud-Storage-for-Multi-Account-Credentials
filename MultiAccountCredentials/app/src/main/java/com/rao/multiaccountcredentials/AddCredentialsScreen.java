package com.rao.multiaccountcredentials;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.rao.multiaccountcredentials.Models.Field;
import com.rao.multiaccountcredentials.comman.CustomSnackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class AddCredentialsScreen extends AppCompatActivity {

    private LinearLayout formLayout;
    LinearLayout root_layout_add_credent;
    ImageView backicon_add;
    TextView note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_add_credentials_screen);

        // Initialize the LinearLayout
        formLayout = findViewById(R.id.form_layout);
        backicon_add=findViewById(R.id.backicon_add);
        note=findViewById(R.id.note);
        backicon_add.setOnClickListener(v->{
            onBackPressed();
        });
        TextView application_name_add=findViewById(R.id.application_name_add);
        application_name_add.setText(getIntent().getStringExtra("application_name"));

        // Retrieve the field data from the Intent
        ArrayList<Field> fields = getIntent().getParcelableArrayListExtra("fields");
        root_layout_add_credent=findViewById(R.id.root_layout_add_credent);
        // Retrieve application name and URL from Intent
        String applicationName = getIntent().getStringExtra("application_name");
        String applicationUrl = getIntent().getStringExtra("application_url");
        ImageView imageView = findViewById(R.id.application_logo_add);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.poppins);
        note.setText("Note: Please add your "+applicationName+" credentials securely. Optional fields are marked.");
        Picasso.get()
                .load(applicationUrl)
                .placeholder(R.drawable.default_log) // optional placeholder
                .into(imageView);
        if (fields != null) {
            // Create EditText fields for the dynamic fields
            for (Field field : fields) {
                EditText editText = new EditText(this);

                editText.setHint(field.getName() + (field.isOptional() ? " (Optional)" : ""));
                editText.setInputType(field.getInputType());
                editText.setBackgroundResource(R.drawable.edit_text_rounded_border);
                editText.setPadding(24, 24, 24, 24);
                editText.setTypeface(typeface);
                // Layout parameters for the EditText
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(16, 16, 16, 0);
                editText.setLayoutParams(params);

                // Add the EditText to the form layout
                formLayout.addView(editText);
            }

            // Create an EditText for the application URL
//            EditText urlEditText = new EditText(this);
//            urlEditText.setHint("Application URL");
//            urlEditText.setInputType(InputType.TYPE_TEXT_VARIATION_URI); // Set input type to URL
//            urlEditText.setBackgroundResource(R.drawable.edittext_background);
//            urlEditText.setPadding(24, 24, 24, 24);
//            LinearLayout.LayoutParams urlParams = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//            );
//            urlParams.setMargins(16, 16, 16, 0);
//            urlEditText.setLayoutParams(urlParams);
//
//            // Set the URL EditText with the URL passed in the Intent
//            if (applicationUrl != null) {
//                urlEditText.setText(applicationUrl);
//            }
//
//            formLayout.addView(urlEditText);
//
//            // Create an EditText for the application name
//            EditText nameEditText = new EditText(this);
//            nameEditText.setHint("Application Name");
//            nameEditText.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL); // Regular text
//            nameEditText.setBackgroundResource(R.drawable.edittext_background);
//            nameEditText.setPadding(24, 24, 24, 24);
//            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//            );
//            nameParams.setMargins(16, 16, 16, 0);
//            nameEditText.setLayoutParams(nameParams);

            // Set the application name EditText with the name passed in the Intent
//            if (applicationName != null) {
//                nameEditText.setText(applicationName);
//            }
//
//            formLayout.addView(nameEditText);
        }

        // Add a Save button
        Button saveButton = new Button(this);
        saveButton.setText("SAVE CREDENTIALS");
        saveButton.setAllCaps(true);
        saveButton.setTextColor(Color.WHITE);
        saveButton.setPadding(12, 12, 12, 12);
        saveButton.setTextSize(16);
        saveButton.setTypeface(typeface);
        saveButton.setBackgroundResource(R.drawable.button_background); // Apply drawable background


        // Layout parameters for the button
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.setMargins(0, 32, 0, 0);
        saveButton.setLayoutParams(buttonParams);

        saveButton.setOnClickListener(v -> {
            JSONObject credentials = new JSONObject();
//            Toast.makeText(this, ""+formLayout.getChildCount(), Toast.LENGTH_SHORT).show();

            // Save the application name and URL
            String appName = applicationName;
            String appUrl = applicationUrl;

            try {
                boolean allFieldsEmpty = true;
                boolean hasError = false;

                // Iterate through other fields (credentials)
                for (int i = 0; i < formLayout.getChildCount(); i++) { // Skip the application name and URL fields
                    View child = formLayout.getChildAt(i);
                    if (child instanceof EditText) {
                        EditText inputField = (EditText) child;
                        String input = inputField.getText().toString().trim();
                        Field currentField = fields.get(i); // Adjusting index for fields list
//                        Toast.makeText(this, ""+inputField.getText(), Toast.LENGTH_SHORT).show();
                        // Check if required field is empty
                        if (!currentField.isOptional() && input.isEmpty()) {
                            CustomSnackbar.showSnackbar(AddCredentialsScreen.this, root_layout_add_credent, currentField.getName() + " is required.", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
//                            Toast.makeText(this, currentField.getName() + " is required.", Toast.LENGTH_SHORT).show();
                            hasError = true;
                            break; // Stop processing as we found an error
                        }

                        if (!input.isEmpty()) {
                            allFieldsEmpty = false;
                        }

                        credentials.put(currentField.getName(), input.isEmpty() ? "Not provided" : input);
                    }
                }

                // If there's an error or all fields are empty, return early
                if (hasError || allFieldsEmpty) {
                    return;
                }

                // Add the application name and URL to credentials
                credentials.put("application_name", appName);
                credentials.put("application_url", appUrl);

                // Generate a unique ID for this credential entry
                String uniqueId = UUID.randomUUID().toString();
                credentials.put("unique_id", uniqueId);

                saveCredentials(appName, uniqueId, credentials);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        formLayout.addView(saveButton);
    }

    private static final String PREF_NAME = "credentials_data";

    private void saveCredentials(String applicationName, String uniqueId, JSONObject credentials) {
        try {
            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            // Retrieve existing data
            String data = prefs.getString("credentials_list", "[]");
            JSONArray credentialsList = new JSONArray(data);

            // Add new application credentials with unique ID
            JSONObject newItem = new JSONObject();
            newItem.put("application", applicationName);
            newItem.put("unique_id", uniqueId);
            newItem.put("credentials", credentials);
            credentialsList.put(newItem);

            // Save updated data
            editor.putString("credentials_list", credentialsList.toString());
            editor.apply();
            startActivity(new Intent(AddCredentialsScreen.this,HomeActivity.class));

            Toast.makeText(this, "Credentials saved successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving credentials.", Toast.LENGTH_SHORT).show();
        }
    }

    // Deleting credentials by unique ID
    public void deleteCredentialsById(String uniqueId) {
        try {
            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            // Retrieve existing data
            String data = prefs.getString("credentials_list", "[]");
            JSONArray credentialsList = new JSONArray(data);

            // Find the item with the unique ID and remove it
            for (int i = 0; i < credentialsList.length(); i++) {
                JSONObject item = credentialsList.getJSONObject(i);
                if (item.getString("unique_id").equals(uniqueId)) {
                    credentialsList.remove(i);
                    break;
                }
            }

            // Save updated data
            editor.putString("credentials_list", credentialsList.toString());
            editor.apply();

            Toast.makeText(this, "Credential deleted successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error deleting credential.", Toast.LENGTH_SHORT).show();
        }
    }
}

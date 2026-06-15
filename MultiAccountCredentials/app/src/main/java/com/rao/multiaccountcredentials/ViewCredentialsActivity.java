package com.rao.multiaccountcredentials;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.rao.multiaccountcredentials.Adapter.ViewCredentialAdapter;
import com.rao.multiaccountcredentials.Models.ViewCredentialModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ViewCredentialsActivity extends AppCompatActivity {
    private static final String PREF_NAME = "credentials_data";
    TextView application_name;
    ImageView backicon_view;
    ImageView delted_icon;
    RecyclerView view_recycle_view;
    ArrayList<ViewCredentialModel> viewCredentialModelArrayList;
  ViewCredentialAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_view_credentials);
        application_name=findViewById(R.id.application_name);
        backicon_view=findViewById(R.id.backicon_view);
        application_name.setText(getIntent().getStringExtra("name"));
        view_recycle_view=findViewById(R.id.view_recycle_view);
        delted_icon=findViewById(R.id.delted_icon);
        delted_icon.setOnClickListener(v->{
            showDeleteConfirmationDialog();
        });
        viewCredentialModelArrayList=new ArrayList<>();
        adapter=new ViewCredentialAdapter(getIntent().getStringExtra("id"),viewCredentialModelArrayList,this,this);
        view_recycle_view.setLayoutManager(new LinearLayoutManager(this));
        view_recycle_view.setAdapter(adapter);
        ImageView application_logo_view=findViewById(R.id.application_logo_view);
        Picasso.get()
                .load(getIntent().getStringExtra("url"))
                .placeholder(R.drawable.default_log) // optional placeholder
                .into(application_logo_view);

        backicon_view.setOnClickListener(v->{
            onBackPressed();
        });
        loadData();

        // Find the LinearLayout inside the ScrollView
//        LinearLayout linearLayout = findViewById(R.id.credentials_text);
// Create a TextInputLayout


        // Example JSON passed through intent (replace with actual intent data)

    }
    public void loadData(){
//        Toast.makeText(this, "call", Toast.LENGTH_SHORT).show();
        viewCredentialModelArrayList.clear();
        String jsonData = getIntent().getStringExtra("data");
//        Toast.makeText(this, ""+jsonData, Toast.LENGTH_SHORT).show();

        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            // Keys to exclude
            String[] excludedKeys = {"application_name", "application_url", "unique_id"};

            // Iterate over keys in the JSONObject
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();

                // Check if the key is in the excluded list
                boolean isExcluded = false;
                for (String excludedKey : excludedKeys) {
                    if (excludedKey.equals(key)) {
                        isExcluded = true;
                        break;
                    }
                }

                if (!isExcluded) {

                    ViewCredentialModel viewCredentialModel=new ViewCredentialModel(key,jsonObject.optString(key,""));
                    viewCredentialModelArrayList.add(viewCredentialModel);
                    // Create a TextView for the key
//                    TextView keyTextView = new TextView(this);
//                    keyTextView.setText(key);
//
//                    keyTextView.setTextSize(20);
//                    keyTextView.setLayoutParams(new LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.WRAP_CONTENT,  // Width
//                            LinearLayout.LayoutParams.WRAP_CONTENT   // Height
//                    ));
//                    keyTextView.setPadding(20, 10, 10, 5);
////                    keyTextView.setBackgroundR(R.drawable.text_background);
//                    keyTextView.setTextColor(getResources().getColor(R.color.black));
//                    keyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_ic, 0);
//                    keyTextView.setCompoundDrawablePadding(10);
//                    TextView valueTextView = new TextView(this);
//                    valueTextView.setText("Value: " + jsonObject.optString(key, "N/A"));
//                    valueTextView.setTextSize(16);
//                    valueTextView.setPadding(20, 20, 10, 20);
//
//                    // Add the TextViews to the LinearLayout
//                    linearLayout.addView(keyTextView);
//                    linearLayout.addView(valueTextView);
                }
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Editing credentials by unique ID, key, and new value
    public void editCredential(String uniqueId, String key, String newValue) {
        try {
            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            // Retrieve existing data
            String data = prefs.getString("credentials_list", "[]");
            JSONArray credentialsList = new JSONArray(data);

            boolean credentialUpdated = false;

            // Find the item with the unique ID and update the specified key
            for (int i = 0; i < credentialsList.length(); i++) {
                JSONObject item = credentialsList.getJSONObject(i);
                if (item.getString("unique_id").equals(uniqueId)) {
                    JSONObject credentials = item.getJSONObject("credentials");

                    // Check if the key exists in the credentials
                    if (credentials.has(key)) {
                        credentials.put(key, newValue); // Update the value
                        credentialUpdated = true;
                    } else {
                        Toast.makeText(this, "Key not found in credentials.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    break;
                }
            }

            if (credentialUpdated) {
                // Save updated data
                editor.putString("credentials_list", credentialsList.toString());
                editor.apply();
                Toast.makeText(this, "Credential updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Credential with the given unique ID not found.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error updating credential.", Toast.LENGTH_SHORT).show();
        }
    }
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
            startActivity(new Intent(ViewCredentialsActivity.this,HomeActivity.class));
            finish();
            Toast.makeText(this, "Credential deleted successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error deleting credential.", Toast.LENGTH_SHORT).show();
        }
    }
    private void showDeleteConfirmationDialog() {
        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_delete_confirmation, null);

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Set up dialog buttons
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel_button);
        Button yesButton = dialogView.findViewById(R.id.dialog_yes_button);

        cancelButton.setOnClickListener(v -> {
            // Handle Cancel button click
            dialog.dismiss();
        });

        yesButton.setOnClickListener(v -> {
            // Handle Yes button click
//            deleteCredential(); // Your method to delete the credential
            deleteCredentialsById(getIntent().getStringExtra("id"));
            dialog.dismiss();
        });

        // Show the dialog
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

}
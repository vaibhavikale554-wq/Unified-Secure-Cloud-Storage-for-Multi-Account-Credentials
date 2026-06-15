package com.rao.multiaccountcredentials;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CredentialsListActivity extends AppCompatActivity {

    private ArrayList<String> applicationNames;
    private ArrayList<JSONObject> credentialsData;
    private ArrayAdapter<String> adapter;
    private static final String PREF_NAME = "credentials_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials_list);

        // Initialize the ListView and the data containers
        ListView listView = findViewById(R.id.credentials_list);
        applicationNames = new ArrayList<>();
        credentialsData = new ArrayList<>();

        // Load credentials from SharedPreferences
        loadCredentials();

        // Set the adapter for displaying application names
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, applicationNames);
        listView.setAdapter(adapter);

        // Long click listener to delete the selected credentials
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            JSONObject selectedCredentials = credentialsData.get(position);
            String applicationName = selectedCredentials.optString("application");
            String uniqueId = selectedCredentials.optString("unique_id"); // Fetch the unique ID

            // Remove the selected credentials using the unique ID
            if (deleteCredentialsById(uniqueId)) {
                // Notify the user and refresh the list
                Toast.makeText(this, "Deleted: " + applicationName, Toast.LENGTH_SHORT).show();

                // After deleting, clear and reload the data
                applicationNames.clear();
                credentialsData.clear();
                loadCredentials(); // Reload credentials

                // Notify the adapter to update the list
                adapter.notifyDataSetChanged();
            } else {
                // Notify user if deletion fails
                Toast.makeText(this, "Error deleting credentials", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        // Add item click listener to show application data in a Toast
        listView.setOnItemClickListener((parent, view, position, id) -> {
            JSONObject selectedCredentials = credentialsData.get(position);
            String applicationName = selectedCredentials.optString("application");
            String uniqueId = selectedCredentials.optString("unique_id");
            String credentials = selectedCredentials.optJSONObject("credentials").toString();

            // Show the application data in a Toast
            Toast.makeText(this, "App: " + applicationName + "\nID: " + uniqueId + "\nCredentials: " + credentials, Toast.LENGTH_LONG).show();
        });
    }

    // Method to load credentials from SharedPreferences
    private void loadCredentials() {
        try {
            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            String data = prefs.getString("credentials_list", "[]");
            JSONArray credentialsList = new JSONArray(data);

            for (int i = 0; i < credentialsList.length(); i++) {
                JSONObject item = credentialsList.getJSONObject(i);
                applicationNames.add(item.getString("application"));
                credentialsData.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any exceptions during data loading
            Toast.makeText(this, "Error loading credentials", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to remove credentials from SharedPreferences by unique ID
    public Boolean deleteCredentialsById(String uniqueId) {
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error deleting credential.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}


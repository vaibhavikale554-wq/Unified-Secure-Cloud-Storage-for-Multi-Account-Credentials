package com.rao.multiaccountcredentials;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rao.multiaccountcredentials.Adapter.ApplicationAdapter;
import com.rao.multiaccountcredentials.Adapter.CredentialAdapter;
import com.rao.multiaccountcredentials.Models.ApplicationModel;
import com.rao.multiaccountcredentials.Models.Credential;
import com.rao.multiaccountcredentials.Models.CredentialModel;
import com.rao.multiaccountcredentials.Models.Field;
import com.rao.multiaccountcredentials.comman.APIRequestHandler;
import com.rao.multiaccountcredentials.comman.APIRequestHandlerGet;
import com.rao.multiaccountcredentials.comman.APIs;
import com.rao.multiaccountcredentials.comman.CustomSnackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
     RecyclerView credentialsListrv;
    private CredentialAdapter adapter;
    ArrayList<CredentialModel> credentialsData;
    private List<ApplicationModel> applicationList;
    private List<ApplicationModel> filteredList;
    private ApplicationAdapter adapter_application;
    ImageView profile_icon,profile_icon_home;
    TextView logout_icon;
    LinearLayout no_data;



    private static final String PREF_NAME = "credentials_data";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        getWindow().setStatusBarColor(Color.parseColor("#D32F2F")); // Dark red
        TextView textView = findViewById(R.id.app_name_home);
        profile_icon_home=findViewById(R.id.profile_icon_home);
        no_data=findViewById(R.id.no_data);

        profile_icon_home.setOnClickListener(v->{
            showUserProfileDialog();
        });
        SpannableString spannableString = new SpannableString("Multi Credentials.");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#09090B")), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Black for entire text
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#2563EB")), 6, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        TextView analyzingText=findViewById(R.id.analyzingText);
        SpannableString spannableString1 = new SpannableString("Get stated by clicking Add Button.");
        spannableString1.setSpan(new ForegroundColorSpan(Color.parseColor("#09090B")), 0, spannableString1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Black for entire text
        spannableString1.setSpan(new ForegroundColorSpan(Color.parseColor("#2563EB")), 23, 34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        analyzingText.setText(spannableString1);


        credentialsListrv = findViewById(R.id.credentials_list);
        credentialsListrv.setLayoutManager(new LinearLayoutManager(this));
        credentialsData=new ArrayList<>();
        applicationList = new ArrayList<>();
        profile_icon=findViewById(R.id.profile_icon);
        profile_icon.setOnClickListener(v->{
            startActivity(new Intent(this,ProfileActivity.class));

        });
        logout_icon=findViewById(R.id.logout_icon);
        logout_icon.setOnClickListener(v->{
            showLogoutDialog();
        });
        loadapplications();

//        applicationList.add(new ApplicationModel("Instagram", "https://upload.wikimedia.org/wikipedia/commons/f/f6/Microsoft_Office_Teams_%282018%E2%80%93present%29.svg.png",""));
//        applicationList.add(new ApplicationModel("Facebook", "https://upload.wikimedia.org/wikipedia/commons/f/f6/Microsoft_Office_Teams_%282018%E2%80%93present%29.svg.png",""));
        // Add more applications


        // Sample static data
//        List<Credential> sampleCredentials = new ArrayList<>();
//        sampleCredentials.add(new Credential("Google", "user123","1"));
//        sampleCredentials.add(new Credential("Facebook", "example_user","2"));
//        sampleCredentials.add(new Credential("Twitter", "my_user","3"));
//        sampleCredentials.add(new Credential("Sanpchat", "username123","4"));
//        sampleCredentials.add(new Credential("Instagram", "om","5"));
//        sampleCredentials.add(new Credential("MountreachSolution", "rao124421","0"));
//        sampleCredentials.add(new Credential("MountreachSolution", "rao124421","0"));

        // Set up the adapter
        adapter = new CredentialAdapter(credentialsData,this);
        credentialsListrv.setAdapter(adapter);
        loadCredentials();
        FloatingActionButton fab = findViewById(R.id.fab_add_credentials);
        fab.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inflate the custom dialog layout
//                View dialogView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.dialog_custom, null);
//
//                // Create AlertDialog Builder and set the custom view
//                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this)
//                        .setView(dialogView);
//
//                // Create and display the dialog
//                AlertDialog dialog = dialogBuilder.create();
//                if (dialog.getWindow() != null) {
//                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                }
//                dialog.show();



//                ArrayList<Field> fields = new ArrayList<>();
//                fields.add(new Field("Username", false, android.text.InputType.TYPE_CLASS_TEXT));
//                fields.add(new Field("Email Address", true, android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS));
//                fields.add(new Field("Password", false, android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD));
//                fields.add(new Field("Phone Number", true, android.text.InputType.TYPE_CLASS_PHONE));
//                fields.add(new Field("Backup Codes", true, android.text.InputType.TYPE_CLASS_TEXT));
//
//                // Pass the data to AddCredentialsScreen
//                Intent intent = new Intent(HomeActivity.this, AddCredentialsScreen.class);
//                intent.putParcelableArrayListExtra("fields", fields);
//                intent.putExtra("application_name","Instagram");
//                intent.putExtra("application_url","https://upload.wikimedia.org/wikipedia/commons/c/cc/Uber_logo_2018.png");
//                startActivity(intent);

                showApplicationDialog();


            }
        });
        EditText searchEditText = findViewById(R.id.search_edit_text); // Make sure to replace with your actual EditText ID
        adapter.filter("");
// Set a TextWatcher to listen for changes in the search field
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Do nothing before text changes
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Filter the adapter whenever the text changes
                adapter.filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing after text changes
            }
        });


    }
    private void loadCredentials() {
        credentialsData.clear();
        try {
            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            String data = prefs.getString("credentials_list", "[]");
            JSONArray credentialsList = new JSONArray(data);

            for (int i = 0; i < credentialsList.length(); i++) {
                JSONObject item = credentialsList.getJSONObject(i);

                // Retrieve the credential JSON object
                JSONObject credentialsObject = item.optJSONObject("credentials");
                String firstField = "";
                String url = "";

                if (credentialsObject != null) {
                    // Priority-based field extraction
                    if (credentialsObject.has("Username")) {
                        firstField = credentialsObject.getString("Username");
                    } else if (credentialsObject.has("Email Address")) {
                        firstField = credentialsObject.getString("Email Address");
                    } else if (credentialsObject.has("Phone Number")) {
                        firstField = credentialsObject.getString("Phone Number");
                    } else {
                        firstField = "No Primary Field Found"; // Default fallback
                    }

                    // Extract application URL (if present)
                    url = credentialsObject.optString("application_url", "No URL");
                }

                // Create CredentialModel from the JSONObject
                String id = item.getString("unique_id");
                String applicationName = item.getString("application");
                String credentialData = credentialsObject != null ? credentialsObject.toString() : "";

                // Create and add the model
                CredentialModel model = new CredentialModel(id, applicationName, url, firstField, credentialData);
                credentialsData.add(model);
            }
            if(credentialsData.isEmpty()){
                no_data.setVisibility(View.VISIBLE);
                credentialsListrv.setVisibility(View.GONE);

            }
            else {
                no_data.setVisibility(View.GONE);
                credentialsListrv.setVisibility(View.VISIBLE);
            }
            // Notify adapter
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading credentials", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        loadCredentials();
    }
    private void showApplicationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_application_list);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText searchEditText = dialog.findViewById(R.id.search_edit_text);
        RecyclerView recyclerView = dialog.findViewById(R.id.recycler_view_applications);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        filteredList = new ArrayList<>(applicationList);
        adapter_application = new ApplicationAdapter(this, filteredList);
        recyclerView.setAdapter(adapter_application);
        // Add search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                // Call the filter method of your adapter with the query
                adapter_application.filter(query);
            }
        });
        dialog.show();
    }

    private void filterApplications(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(applicationList);
        } else {
            for (ApplicationModel app : applicationList) {
                if (app.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(app);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
    private void loadapplications() {
        Map<String, String> params = new HashMap<>();


        APIRequestHandlerGet apiRequestHandler = new APIRequestHandlerGet(
                this,
                APIs.get_applications, // Your login API endpoint
                params,
                null, // No auth token
                null // No progress overlay
        );

        apiRequestHandler.sendRequest(new APIRequestHandlerGet.APICallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray applications = response.getJSONArray("applications");

                    for (int i = 0; i < applications.length(); i++) {
                        JSONObject application = applications.getJSONObject(i);
                        String appName = application.getString("app_name");
                        String appUrl = application.getString("app_url");

                        // Get the fields as a full JSON string
                        JSONObject fieldsObject = application.getJSONObject("fields");
                        String fieldsString = fieldsObject.toString();
                        ApplicationModel applicationModel=new ApplicationModel(appName,appUrl,fieldsString);
                        applicationList.add(applicationModel);

                        // Print or log the values
                        Log.d("ApplicationData", "App Name: " + appName);
                        Log.d("ApplicationData", "App URL: " + appUrl);
                        Log.d("ApplicationData", "Fields: " + fieldsString);
                    }
//                    adapter_application.notifyDataSetChanged();
                }catch (Exception e) {

                    Toast.makeText(HomeActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//                    CustomSnackbar.showSnackbar(HomeActivity.this, root_layout_login, "Something went wrong", R.drawable.error_ic, CustomSnackbar.TYPE_ERROR);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(HomeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLogoutDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.logout_dialog1, null);

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogTheme);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        // Find views in the dialog
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        Button logoutButton = dialogView.findViewById(R.id.logout_button);

        // Set click listeners
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        logoutButton.setOnClickListener(v -> {
            // Handle logout logic here
            clearAllCredentials();
            dialog.dismiss();
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putBoolean("islogin",false);
            editor.apply();
            finish();
        });

        // Show the dialog
        dialog.show();
    }
    // Add this method to your AddCredentialsScreen class
    public void clearAllCredentials() {
        try {
            // Get the SharedPreferences instance
            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            // Clear all stored credentials
            editor.remove("credentials_list"); // Remove only the credentials list
            editor.apply();

            // Show a success message
            Toast.makeText(this, "All credentials have been cleared!", Toast.LENGTH_SHORT).show();

            // Optionally, navigate to another screen or refresh the UI
//            startActivity(new Intent(AddCredentialsScreen.this, HomeActivity.class));
//            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error clearing credentials.", Toast.LENGTH_SHORT).show();
        }
    }
    private void showUserProfileDialog() {
        // Create the dialog
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_user_profile);
        dialog.setCancelable(true);


        // Set the dialog's window background to transparent
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Set user details
//        ImageView profileIcon = dialog.findViewById(R.id.profile_icon);
        TextView full_name_tv = dialog.findViewById(R.id.full_name_tv);
        TextView userName_tv = dialog.findViewById(R.id.userName_tv);
        TextView reset_face=dialog.findViewById(R.id.reset_face);

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "N/A");
        String fullName = sharedPreferences.getString("full_name", "N/A");
        userName_tv.setText(username);
        full_name_tv.setText(fullName);
        reset_face.setOnClickListener(v -> {
            // Clear SharedPreferences for face data (if any)
            Intent intent=new Intent(this,TestingActiviyt.class);
            intent.putExtra("state","reset");
            startActivity(intent);


        });
//        TextView email_address = dialog.findViewById(R.id.email_address);

//        userName.setText(sharedPreferences.getString("name",""));
//        contactNumber.setText(sharedPreferences.getString("phone",""));
//        email_address.setText(sharedPreferences.getString("email",""));

        Button closeIcon = dialog.findViewById(R.id.close_icon);



//
//        // You can customize these values dynamically
////        profileIcon.setImageResource(R.drawable.ic_user_profile); // Replace with dynamic image if available
//        userName.setText("John Doe"); // Replace with the user's name
//        contactNumber.setText("+1 123-456-7890"); // Replace with the user's contact number

        // Handle close icon click
        closeIcon.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }


}
package com.rao.multiaccountcredentials.Adapter;

import static android.content.Context.HARDWARE_PROPERTIES_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.rao.multiaccountcredentials.HomeActivity;
import com.rao.multiaccountcredentials.Models.CredentialModel;
import com.rao.multiaccountcredentials.Models.ViewCredentialModel;
import com.rao.multiaccountcredentials.R;
import com.rao.multiaccountcredentials.ViewCredentialsActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewCredentialAdapter extends RecyclerView.Adapter<ViewCredentialAdapter.CredentialViewHolder> {

    private ArrayList<ViewCredentialModel> credentialList;
    private Context context;
    Activity activity;
    String id;
    private static final String PREF_NAME = "credentials_data";

    public ViewCredentialAdapter(String id,ArrayList<ViewCredentialModel> credentialList, Context context,Activity activity) {
        this.credentialList = credentialList;
        this.context=context;
        this.id=id;
        this.activity=activity;
    }


    @NonNull
    @Override
    public CredentialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_label_value, parent, false);
        return new CredentialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CredentialViewHolder holder, int position) {
        ViewCredentialModel credential = credentialList.get(position);
        holder.label_text.setText(credential.getKey());
        holder.value_text.setText(credential.getValue());
        holder.edit_icon.setOnClickListener(v->{
            showEditDialog(id,credential.getKey(),credential.getValue());
        });

//        holder.credentialName.setText(credential.getAppName());
//        holder.credentialUsername.setText("Username: " + credential.getUsername());
//        if (credential.getUnique_id().equals("1")){
//            holder.app_icon.setImageResource(R.drawable.google_logo);
//
//        }
//        else if (credential.getUnique_id().equals("2")){
//            holder.app_icon.setImageResource(R.drawable.facebook_logo);
//        }
//        else if (credential.getUnique_id().equals("3")){
//            holder.app_icon.setImageResource(R.drawable.x_logo);
//        }
//        else if (credential.getUnique_id().equals("4")){
//            holder.app_icon.setImageResource(R.drawable.sanpchat_logo);
//        }
//        else if (credential.getUnique_id().equals("5")){
//            holder.app_icon.setImageResource(R.drawable.instagram_img);
//        }
//        else{
//            holder.app_icon.setImageResource(R.drawable.default_log);
//        }
//        holder.root_layout_item_credential.setOnClickListener(v->{
//            Intent intent=new Intent(new Intent(context, ViewCredentialsActivity.class));
//            intent.putExtra("data",credential.getData());
//
//            context.startActivity(intent);
//
////            Toast.makeText(context, ""+credential.getUrl(), Toast.LENGTH_SHORT).show();
//
//        });
    }

    @Override
    public int getItemCount() {
        return credentialList.size();
    }

    static class CredentialViewHolder extends RecyclerView.ViewHolder {
        TextView label_text;
        TextView value_text;
        ImageView edit_icon;
//        LinearLayout root_layout_item_credential;

        public CredentialViewHolder(@NonNull View itemView) {
            super(itemView);
            label_text = itemView.findViewById(R.id.label_text);
            value_text = itemView.findViewById(R.id.value_text);
            edit_icon=itemView.findViewById(R.id.edit_icon);
//            root_layout_item_credential=itemView.findViewById(R.id.root_layout_item_credential);

        }
    }
    public void editCredential(String uniqueId, String key, String newValue) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
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
                        Toast.makeText(context, "Key not found in credentials.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    break;
                }
            }

            if (credentialUpdated) {
                // Save updated data
                editor.putString("credentials_list", credentialsList.toString());
                editor.apply();
                Toast.makeText(context, "Credential updated successfully!", Toast.LENGTH_SHORT).show();
//                if (activity instanceof ViewCredentialsActivity) {
////                    ((ViewCredentialsActivity) activity).loadData();
////                }
                context.startActivity(new Intent(context, HomeActivity.class));
            } else {
                Toast.makeText(context, "Credential with the given unique ID not found.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error updating credential.", Toast.LENGTH_SHORT).show();
        }
    }
    private void showEditDialog(String id ,String label, String currentValue) {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_value, null);

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Find views in the dialog layout
        TextView labelText = dialogView.findViewById(R.id.label_text);
        EditText editValue = dialogView.findViewById(R.id.edit_value);
        Button saveButton = dialogView.findViewById(R.id.save_button);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);

        // Set the label and current value
        labelText.setText("Edit " + label);
        editValue.setText(currentValue);

        // Handle Save button click
        saveButton.setOnClickListener(v -> {
            String newValue = editValue.getText().toString().trim();
            if (!newValue.isEmpty()) {
                // Save the new value (implement your logic here)
                Toast.makeText(context, label + " updated to " + newValue, Toast.LENGTH_SHORT).show();
                editCredential(id,label,newValue);
                dialog.dismiss();

            } else {
                editValue.setError("Value cannot be empty");
            }
        });

        // Handle Cancel button click
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }

}

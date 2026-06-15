package com.rao.multiaccountcredentials.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.rao.multiaccountcredentials.CredentialsListActivity;
import com.rao.multiaccountcredentials.Models.Credential;
import com.rao.multiaccountcredentials.Models.CredentialModel;
import com.rao.multiaccountcredentials.R;
import com.rao.multiaccountcredentials.TestingActiviyt;
import com.rao.multiaccountcredentials.ViewCredentialsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CredentialAdapter extends RecyclerView.Adapter<CredentialAdapter.CredentialViewHolder> {

    private ArrayList<CredentialModel> credentialList;
    private ArrayList<CredentialModel> filteredList;
    private Context context;

    public CredentialAdapter(ArrayList<CredentialModel> credentialList, Context context) {
        this.credentialList = credentialList;
        this.filteredList = new ArrayList<>(credentialList); // Initialize filteredList with the full data
        this.context = context;
    }

    // Filter method for search
    public void filter(String query) {
        filteredList.clear(); // Clear the current filtered list
        if (query.isEmpty()) {
            // If query is empty, show all items
            filteredList.addAll(credentialList);
        } else {
            query = query.toLowerCase().trim(); // Convert query to lowercase for case-insensitive search
            for (CredentialModel credential : credentialList) {
                // Check if the application name or first field matches the query
                if (credential.getApplicationName().toLowerCase().contains(query) ||
                        credential.getFirstField().toLowerCase().contains(query)) {
                    filteredList.add(credential);
                }
            }
        }
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    @NonNull
    @Override
    public CredentialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_credential, parent, false);
        return new CredentialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CredentialViewHolder holder, int position) {
        CredentialModel credential = filteredList.get(position); // Use filtered list for binding data
        holder.credentialName.setText(credential.getApplicationName());
        holder.credentialUsername.setText(credential.getFirstField());

        holder.root_layout_item_credential.setOnClickListener(v -> {
            Intent intent = new Intent(context, TestingActiviyt.class);
            intent.putExtra("data", credential.getData());
            intent.putExtra("id", credential.getId());
            intent.putExtra("name", credential.getApplicationName());
            intent.putExtra("url", credential.getUrl());
            intent.putExtra("state","view");
            context.startActivity(intent);
        });

        Picasso.get()
                .load(credential.getUrl())
                .placeholder(R.drawable.default_log) // Optional placeholder
                .into(holder.app_icon);
    }

    @Override
    public int getItemCount() {
        return filteredList.size(); // Return size of filtered list
    }

    static class CredentialViewHolder extends RecyclerView.ViewHolder {
        TextView credentialName;
        TextView credentialUsername;
        ShapeableImageView app_icon;
        LinearLayout root_layout_item_credential;

        public CredentialViewHolder(@NonNull View itemView) {
            super(itemView);
            credentialName = itemView.findViewById(R.id.credential_name);
            credentialUsername = itemView.findViewById(R.id.credential_username);
            app_icon = itemView.findViewById(R.id.app_icon);
            root_layout_item_credential = itemView.findViewById(R.id.root_layout_item_credential);
        }
    }
}



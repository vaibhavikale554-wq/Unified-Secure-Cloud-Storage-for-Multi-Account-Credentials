package com.rao.multiaccountcredentials.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.imageview.ShapeableImageView;
import com.rao.multiaccountcredentials.AddCredentialsScreen;
import com.rao.multiaccountcredentials.Models.ApplicationModel;
import com.rao.multiaccountcredentials.Models.Field;
import com.rao.multiaccountcredentials.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    private final Context context;
    private final List<ApplicationModel> applications;

    List<ApplicationModel> filteredCategoryList;

    public ApplicationAdapter(Context context, List<ApplicationModel> applications) {
        this.context = context;
        this.applications = applications;
        this.filteredCategoryList=new ArrayList<>(applications);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApplicationModel application = applications.get(position);
        holder.root_layout_application_item.setOnClickListener(v->{
//            Toast.makeText(context, ""+application.getData(), Toast.LENGTH_SHORT).show();
            try {
                JSONObject fieldsJson = new JSONObject(application.getData());

                // Create an ArrayList to store Field objects
                ArrayList<Field> fields = new ArrayList<>();

                // Iterate through the keys of the JSON object
                Iterator<String> keys = fieldsJson.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject fieldDetails = fieldsJson.getJSONObject(key);

                    // Capitalize the first letter of the key
                    String displayName = capitalizeFirstLetter(key.replace("_", " "));

                    // Parse the optional and input_type values
                    boolean isOptional = fieldDetails.getString("optional").equalsIgnoreCase("true");
                    String inputType = fieldDetails.getString("input_type");
                    int inputTypeCode = getInputTypeCode(inputType);

                    // Add a new Field object to the list
                    fields.add(new Field(displayName, isOptional, inputTypeCode));
                }

                // Print or use the fields list
              Intent intent = new Intent(context, AddCredentialsScreen.class);
                intent.putParcelableArrayListExtra("fields", fields);
                intent.putExtra("application_name",application.getName());
                intent.putExtra("application_url",application.getIconUrl());
                context.startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        holder.appName.setText(application.getName());
        Picasso.get()
                .load(application.getIconUrl())
                .placeholder(R.drawable.default_log) // Optional placeholder
                .into(holder.appIcon);

    }

    @Override
    public int getItemCount() {
        return filteredCategoryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        LinearLayout root_layout_application_item;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon_application);
            appName = itemView.findViewById(R.id.app_name);
            root_layout_application_item=itemView.findViewById(R.id.root_layout_application_item);
        }
    }
    // Helper method to capitalize the first letter of each word
    public static String capitalizeFirstLetter(String input) {
        String[] words = input.split(" ");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            capitalized.append(word.substring(0, 1).toUpperCase())
                    .append(word.substring(1))
                    .append(" ");
        }
        return capitalized.toString().trim();
    }
        public void filter(String query) {
        filteredCategoryList.clear();
        if (query.isEmpty()) {
            filteredCategoryList.addAll(applications);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (ApplicationModel category : applications) {
                if (category.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredCategoryList.add(category);
                }
            }
        }
        notifyDataSetChanged();
    }
    // Helper method to convert input type string to Android input type code
    public static int getInputTypeCode(String inputType) {
        switch (inputType) {
            case "text":
                return android.text.InputType.TYPE_CLASS_TEXT;
            case "email":
                return android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
            case "password":
                return android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
            case "phone":
                return android.text.InputType.TYPE_CLASS_PHONE;
            default:
                return android.text.InputType.TYPE_CLASS_TEXT;
        }
    }
}

package com.rao.multiaccountcredentials.comman;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class APIRequestHandler {

    private Context context;
    private String url;
    private Map<String, String> params;
    private String authToken;
    private RelativeLayout progressBar;

    public APIRequestHandler(Context context, String url, Map<String, String> params, String authToken, RelativeLayout progressBar) {
        this.context = context;
        this.url = url;
        this.params = params;
        this.authToken = authToken;
        this.progressBar = progressBar;


    }

    public void sendRequest(final APICallback callback) {
        showProgressOverlay();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(context, ""+response.toString(), Toast.LENGTH_SHORT).show();

                        hideProgressOverlay();
                        try {
                            JSONObject obj = new JSONObject(response);
                            callback.onSuccess(obj);
                        } catch (JSONException e) {
                            callback.onError("JSON Parsing error rao: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressOverlay();
                        callback.onError("Volley error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"));
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (authToken != null && !authToken.isEmpty()) {

                    headers.put("Authorization", authToken);
                }
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void showProgressOverlay() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressOverlay() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    public interface APICallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }
}

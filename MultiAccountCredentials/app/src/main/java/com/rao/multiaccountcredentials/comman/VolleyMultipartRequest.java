package com.rao.multiaccountcredentials.comman;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.rao.multiaccountcredentials.Models.DataPart;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VolleyMultipartRequest extends Request<NetworkResponse> {

    private final Map<String, String> headers;
    private final Map<String, DataPart> byteData;
    private final Response.Listener<NetworkResponse> listener;

    public VolleyMultipartRequest(int method, String url, Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
        this.headers = new HashMap<>();
        this.byteData = new HashMap<>();
    }

    @Override
    protected Map<String, String> getParams() {
        return null; // No regular params needed for this multipart request
    }


    protected Map<String, DataPart> getByteData() {
        return byteData; // Return the byte data for image files
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers; // Return any custom headers
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        listener.onResponse(response);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addDataPart(String key, DataPart dataPart) {
        byteData.put(key, dataPart);
    }


}

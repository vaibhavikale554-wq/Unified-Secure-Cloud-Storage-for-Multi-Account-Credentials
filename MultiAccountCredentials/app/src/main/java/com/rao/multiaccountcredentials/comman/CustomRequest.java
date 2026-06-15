package com.rao.multiaccountcredentials.comman;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.rao.multiaccountcredentials.Models.DataPart;

import java.util.Map;




import java.util.Map;

public class CustomRequest extends VolleyMultipartRequest {
    private final Map<String, String> params;
    private final Map<String, DataPart> byteData;

    public CustomRequest(int method, String url, Map<String, String> params, Map<String, DataPart> byteData,
                         Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.params = params;
        this.byteData = byteData;
    }

    @Override
    protected Map<String, String> getParams() {
        return params;
    }

    @Override
    protected Map<String, DataPart> getByteData() {
        return byteData;
    }
}

package com.eland.android.eoas.Util;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by liu.wenbin on 15/11/10.
 */
public class HttpRequstUtil {

//    private static final String baseURL = "http://10.202.101.11:30002/";
    private static final String baseURL = "http://182.92.65.253:30001/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    static {
        client.setMaxRetriesAndTimeout(2, 1000);
    }

    public static void get(String url, RequestParams params, JsonHttpResponseHandler response) {
        client.get(getAbsoluteUri(url), params, response);
    }

    public static void get(Context context, String url, RequestParams params, JsonHttpResponseHandler response) {
        client.get(context, getAbsoluteUri(url), params, response);
    }

    public static void post(Context context, String url, StringEntity stringEntity, JsonHttpResponseHandler response) {
        client.post(context, getAbsoluteUri(url), stringEntity, "application/json", response);
    }

    public static void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(context, getAbsoluteUri(url), params, responseHandler);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUri(url), params, responseHandler);
    }

    public static void cancelSingleRequest(Context context, boolean cancel) {
        client.cancelRequests(context, cancel);
    }

    private static String getAbsoluteUri(String url) {
        return baseURL + url;
    }
}

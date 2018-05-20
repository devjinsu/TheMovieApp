package com.jinsukim.themovieapp.utils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class RestTask extends AsyncTask<HttpUriRequest, Void, String>
{
    private static final String TAG = "AARestTask";
    public static final String HTTP_RESPONSE = "httpResponse";

    private Context mContext;
    private HttpClient mClient;
    private String mAction;

    public RestTask(Context context, String action)
    {
        mContext = context;
        mAction = action;
        mClient = new DefaultHttpClient();
    }

    public RestTask(Context context, String action, HttpClient client)
    {
        mContext = context;
        mAction = action;
        mClient = client;
    }

    @Override
    protected String doInBackground(HttpUriRequest... params)
    {
        try
        {
            HttpUriRequest request = params[0];
            HttpResponse serverResponse = mClient.execute(request);
            BasicResponseHandler handler = new BasicResponseHandler();
            return handler.handleResponse(serverResponse);
        }
        catch (Exception e)
        {
            // TODO handle this properly
            e.printStackTrace();
            return "";
        }
    }

    /**
     * `onPostExecute` is run after `doInBackground`, and it's
     * run on the main/ui thread, so you it's safe to update ui
     * components from it. (this is the correct way to update ui
     * components.)
     */
    @Override
    protected void onPostExecute(String result)
    {
        Log.i(TAG, "RESULT = " + result);
        Intent intent = new Intent(mAction);
        intent.putExtra(HTTP_RESPONSE, result);

        // broadcast the completion
        mContext.sendBroadcast(intent);
    }

}
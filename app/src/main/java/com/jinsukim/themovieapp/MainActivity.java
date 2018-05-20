package com.jinsukim.themovieapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jinsukim.themovieapp.data.Movie;
import com.jinsukim.themovieapp.layouts.MovieArrayAdapter;
import com.jinsukim.themovieapp.utils.Constants;
import com.jinsukim.themovieapp.utils.RestTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;

import cz.msebera.android.httpclient.client.methods.HttpGet;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    private static final String ACTION_FOR_INTENT_CALLBACK_NOWPLAY = "GOT_NOW_PLAY_MOVIE";

    private ListView listView ;
    private MovieArrayAdapter mAdapter;
    private ArrayList<Movie> mMovies;
    private int mCurrentPage = 1;
    private boolean isLoadMore = false;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: Clear all poster image every 2-weeks.

        //Init views
        mMovies = new ArrayList<Movie>();
        listView = (ListView) findViewById(R.id.lv_movie);
        mAdapter = new MovieArrayAdapter(this, 0, mMovies);
        listView.setAdapter(mAdapter);
        setListViewFooter();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)  {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !isLoadMore && (firstVisibleItem != 0)) {
                    isLoadMore = true;
                    addMoreItems();
                }
            }
        });

        //download init movie data(page=1)
        addMoreItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(ACTION_FOR_INTENT_CALLBACK_NOWPLAY));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private void setListViewFooter(){
        View view = LayoutInflater.from(this).inflate(R.layout.footer_listview_progressbar, null);
        progressBar = view.findViewById(R.id.progressBar);
        listView.addFooterView(progressBar);
    }

    public void addMoreItems(){

        if (mCurrentPage > Constants.MAX_MOVIE_PAGES) {
            Toast.makeText(this, "No More Data", Toast.LENGTH_SHORT).show();
            return;
        }

        // the request
        try
        {
            progressBar.setVisibility(View.VISIBLE);
            HttpGet httpGet = new HttpGet(new URI(Constants.NOWPLAY_URL +mCurrentPage));
            RestTask task = new RestTask(this, ACTION_FOR_INTENT_CALLBACK_NOWPLAY);
            task.execute(httpGet);
            mCurrentPage += 1;
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    public void MovieParser(String rawData){
        JSONObject reader = null;
        try {
            reader = new JSONObject(rawData);
            JSONArray results  = reader.getJSONArray("results");
            Log.d(TAG, results.toString());

            for(int i=0 ; i < results.length(); i++){
                int id = results.getJSONObject(i).getInt("id");
                String title = results.getJSONObject(i).getString("title");
                double popularity = results.getJSONObject(i).getDouble("popularity");
                String overview = results.getJSONObject(i).getString("overview");
                String poster = results.getJSONObject(i).getString("poster_path");
                String date = results.getJSONObject(i).getString("release_date");

                Movie movie = new Movie(id,title,popularity,poster,overview,date);
                mMovies.add(movie);
            }

            mAdapter.notifyDataSetChanged();
            isLoadMore = false;

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // clear the progress indicator
            progressBar.setVisibility(View.GONE);
            String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
            Log.i(TAG, "RESPONSE = " + response);
            MovieParser(response);
        }
    };

}

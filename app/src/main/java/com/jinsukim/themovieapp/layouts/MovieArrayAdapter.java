package com.jinsukim.themovieapp.layouts;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinsukim.themovieapp.R;
import com.jinsukim.themovieapp.data.Movie;
import com.jinsukim.themovieapp.utils.SharePref;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpStatus;

public class MovieArrayAdapter extends ArrayAdapter<Movie> {
    private Context context;
    private List<Movie> mMovies;
    private LayoutInflater inflater;

    //constructor, call on creation
    public MovieArrayAdapter(Context context, int resource, ArrayList<Movie> objects) {
        super(context, resource, objects);
        this.inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.mMovies = objects;
        SharePref.getInstance(this.context);
    }

    //called when rendering the list
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        //get the movie we are displaying
        Movie movie = mMovies.get(position);

        if(convertView == null){
            //get the inflater and inflate the XML layout for each item
            convertView = inflater.inflate(R.layout.movie_brief_layout, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            holder.titleView = (TextView) convertView.findViewById(R.id.title);
            holder.popularity = (TextView) convertView.findViewById(R.id.popularity);
            holder.date = (TextView) convertView.findViewById(R.id.rel_date);
            holder.overviewView = (TextView) convertView.findViewById(R.id.overview);
            convertView.setTag(holder);
        } else{
            holder = (ViewHolder) convertView.getTag();
        }

        //display trimmed excerpt for overview
        int descriptionLength = movie.getOverview().length();
        if(descriptionLength >= 100){
            String descriptionTrim = movie.getOverview().substring(0, 100) + "...";
            holder.overviewView.setText(descriptionTrim);
        }else{
            holder.overviewView.setText(movie.getOverview());
        }

        //set movie title
        holder.titleView.setText(String.valueOf(movie.getTitle()));
        holder.popularity.setText("Popularity: " + new DecimalFormat("##.##").format(movie.getPopularity()));
        holder.date.setText("Release: " + movie.getDate());


        //get Poster, Show image without download if image already cached.
        if(holder.imageView != null){
            String encoded = SharePref.getInstance(this.context).getPoster(movie.getId());
            if(encoded == null || encoded == ""){
                new ImageDownloaderTask(holder.imageView, movie.getId()).execute("https://image.tmdb.org/t/p/w500" + movie.getPoster());
            }else{
                byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
                holder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            }
        }

        return convertView;
    }


    static class ViewHolder {
        TextView titleView;
        TextView overviewView;
        TextView popularity;
        TextView date;
        ImageView imageView;
    }

    private Bitmap downloadBitmap(String url, int movieID) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                String encoded = Base64.encodeToString(b, Base64.DEFAULT);
                if(!encoded.isEmpty() && encoded.length() > 1){
                    //save Image.
                    SharePref.getInstance(this.context).savePoster(movieID,encoded);
                }

                return bitmap;
            }
        } catch (Exception e) {
            urlConnection.disconnect();
            Log.w("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }


    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private final int mID;

        public ImageDownloaderTask(ImageView imageView, int movieID) {
            imageViewReference = new WeakReference<ImageView>(imageView);
            mID = movieID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                Drawable placeholder = context.getResources().getDrawable(R.drawable.loading);
                imageView.setImageDrawable(placeholder);
            }
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadBitmap(params[0], mID);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.loading);
                        imageView.setImageDrawable(placeholder);
                    }
                }
            }
        }
    }
}
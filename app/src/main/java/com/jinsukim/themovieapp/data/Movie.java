package com.jinsukim.themovieapp.data;

public class Movie {

    private int mId;
    private String mTitle;
    private double mPopularity;
    private String mPoster;
    private String mOverview;
    private String mDate;

    public Movie(int id, String title, double popularity, String poster, String overview, String date) {
        mId = id;
        mTitle = title;
        mPopularity = popularity;
        mPoster = poster;
        mOverview = overview;
        mDate = date;
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public double getPopularity() {
        return mPopularity;
    }

    public String getPoster() {
        return mPoster;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getDate() {
        return mDate;
    }
}

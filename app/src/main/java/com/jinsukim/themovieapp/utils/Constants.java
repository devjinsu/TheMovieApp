package com.jinsukim.themovieapp.utils;

public class Constants {
    public static final String BASE_URL = "https://api.themoviedb.org/3/movie";
    public static final String TMDB_API_KEY = "3ac6156386eb43ab5c13f23e23ca9e4a";
    public static final String NOWPLAY_URL = BASE_URL + "/now_playing?language=en-US&api_key=" + TMDB_API_KEY + "&page=";

    public static final int MAX_MOVIE_PAGES = 1000;
}

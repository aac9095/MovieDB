/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.ayush.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Manages a local database for weather data.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = MoviesDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 17;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MoviesContract.MovieEntry.TABLE_MOVIES + "( " +
                MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_BAKCDROP_PATH + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_PLOT + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_RATING + " REAL NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_VIDEOS_URL + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_REVIEWS + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT " +
                " );";

        final String CREATE_FAVORITE_TABLE = "CREATE TABLE " +
                MoviesContract.FavoriteMovieEntry.TABLE_FAVORITES + "( " +
                MoviesContract.FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.FavoriteMovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.FavoriteMovieEntry.COLUMN_BAKCDROP_PATH + " TEXT NOT NULL, " +
                MoviesContract.FavoriteMovieEntry.COLUMN_PLOT + " TEXT NOT NULL, " +
                MoviesContract.FavoriteMovieEntry.COLUMN_RATING + " REAL NOT NULL, " +
                MoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.FavoriteMovieEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                MoviesContract.FavoriteMovieEntry.COLUMN_VIDEOS_URL + " TEXT, " +
                MoviesContract.FavoriteMovieEntry.COLUMN_REVIEWS + " TEXT, " +
                MoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " TEXT " +
                " );";
        db.execSQL(CREATE_MOVIE_TABLE);
        db.execSQL(CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_MOVIES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.FavoriteMovieEntry.TABLE_FAVORITES);
        onCreate(sqLiteDatabase);
    }
}

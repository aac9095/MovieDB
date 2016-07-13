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


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;
import android.util.Log;

/**
 * Defines table and column names for the weather database.
 */
public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.example.ayush.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.ayush.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static final class MovieEntry implements BaseColumns {
        //table name
        public static final String TABLE_MOVIES = "movie";
        //columns
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BAKCDROP_PATH = "backdrop_string";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER = "poster_string";
        public static final String COLUMN_VIDEOS_URL = "video_url";
        public static final String COLUMN_REVIEWS = "reviews";
        public static final String COLUMN_MOVIE_ID = "column_movie_id";

        //create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(TABLE_MOVIES)
                .build();
        // create cursor of base type directory for multiple items
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIES;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIES;

        //for building Uris on insertion
        public static Uri buildMovieUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class FavoriteMovieEntry implements BaseColumns {
        //table name
        public static final String TABLE_FAVORITES = "favorite";
        //columns
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BAKCDROP_PATH = "backdrop_string";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER = "poster_string";
        public static final String COLUMN_VIDEOS_URL = "video_url";
        public static final String COLUMN_REVIEWS = "reviews";
        public static final String COLUMN_MOVIE_ID = "column_movie_id";

        //create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(TABLE_FAVORITES)
                .build();
        // create cursor of base type directoy for multiple items
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_FAVORITES;
        //create cursor of base type item for single enter
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_FAVORITES;

        //for building Uris on insertion
        public static Uri buildFavoriteMovieUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}

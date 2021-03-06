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

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.ayush.popularmovies.MoviesFragment;

public class MoviesProvider extends ContentProvider {

    private static final String LOG_TAG = MoviesProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = getBuildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    //Integer constants for the Uri
    private static final int MOVIE = 100;
    private static final int MOVIE_WITH_ID = 200;
    private static final int FAVORITE_MOVIE = 300;
    private static final int FAVORITE_MOVIE_WITH_ID = 400;

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }


    private  static UriMatcher getBuildUriMatcher() {
        // Build a Urimatcher by adding a specific code the return on a match
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;
        //add a code for each of uri you want
        matcher.addURI(authority, MoviesContract.MovieEntry.TABLE_MOVIES, MOVIE);
        matcher.addURI(authority, MoviesContract.MovieEntry.TABLE_MOVIES + "/#", MOVIE_WITH_ID);
        matcher.addURI(authority, MoviesContract.FavoriteMovieEntry.TABLE_FAVORITES, FAVORITE_MOVIE);
        matcher.addURI(authority, MoviesContract.FavoriteMovieEntry.TABLE_FAVORITES + "/#", FAVORITE_MOVIE_WITH_ID);
        return matcher;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE: {
                return MoviesContract.MovieEntry.CONTENT_DIR_TYPE;
            }
            case MOVIE_WITH_ID : {
                return MoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            }
            case FAVORITE_MOVIE: {
                return MoviesContract.FavoriteMovieEntry.CONTENT_DIR_TYPE;
            }
            case FAVORITE_MOVIE_WITH_ID: {
                return MoviesContract.FavoriteMovieEntry.CONTENT_ITEM_TYPE;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            //All movies selected
            case MOVIE: {
                retCursor = db.query(
                        MoviesContract.MovieEntry.TABLE_MOVIES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            //All favorite movie selected
            case FAVORITE_MOVIE: {
                retCursor = db.query(
                        MoviesContract.FavoriteMovieEntry.TABLE_FAVORITES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // Individual Movie based on Id
            case MOVIE_WITH_ID: {
                retCursor = db.query(
                        MoviesContract.MovieEntry.TABLE_MOVIES,
                        projection,
                        MoviesContract.MovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // Individual favorite movie based on Id
            case FAVORITE_MOVIE_WITH_ID: {
                retCursor = db.query(
                        MoviesContract.FavoriteMovieEntry.TABLE_FAVORITES,
                        projection,
                        MoviesContract.FavoriteMovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri retUri;
        switch (sUriMatcher.match(uri)) {
            case MOVIE_WITH_ID:{
                //returns the row id of the newly inserted row.
                Cursor cursor = db.query(
                        MoviesContract.MovieEntry.TABLE_MOVIES,
                        null,
                        MoviesContract.MovieEntry._ID  + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        null
                );
                if (cursor == null) {
                    long _id = db.insert(MoviesContract.MovieEntry.TABLE_MOVIES, null, values);
                    //insert unless it is already contained in the database.
                    if (_id > 0) {
                        retUri = MoviesContract.MovieEntry.buildMovieUriWithId(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into: " + uri);
                    }
                } else {
                    retUri = MoviesContract.MovieEntry
                            .buildMovieUriWithId(cursor.getLong(MoviesFragment.COL_MOVIE_ID));
                }
                break;
            }
            case FAVORITE_MOVIE_WITH_ID: {
                //returns the row id of the newly inserted row.
                Cursor cursor = db.query(
                        MoviesContract.FavoriteMovieEntry.TABLE_FAVORITES,
                        null,
                        MoviesContract.FavoriteMovieEntry._ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        null
                );
                if (cursor == null) {
                    long _id = db.insert(MoviesContract.FavoriteMovieEntry.TABLE_FAVORITES, null, values);
                    if (_id > 0 ) {
                        retUri = MoviesContract.FavoriteMovieEntry.buildFavoriteMovieUriWithId(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into: " + uri);
                    }
                }  else {
                    retUri = MoviesContract.FavoriteMovieEntry
                            .buildFavoriteMovieUriWithId(cursor.getLong(MoviesFragment.COL_MOVIE_ID));
                }
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        //By default cursor adapter objects will get notifications from notifyChange()
        getContext().getContentResolver().notifyChange(uri, null);
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numDeleted;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:{
                //returns the number of rows deleted
                numDeleted = db.delete(
                        MoviesContract.MovieEntry.TABLE_MOVIES,
                        selection,
                        selectionArgs
                );
                break;
            }
            case FAVORITE_MOVIE:{
                //returns the number of rows deleted
                numDeleted = db.delete(
                        MoviesContract.FavoriteMovieEntry.TABLE_FAVORITES,
                        selection,
                        selectionArgs
                );
                break;
            }
            case MOVIE_WITH_ID:{
                numDeleted = db.delete(
                        MoviesContract.MovieEntry.TABLE_MOVIES,
                        MoviesContract.MovieEntry._ID + " = ? ",
                        new String[] {String.valueOf(ContentUris.parseId(uri))}
                );
                break;
            }
            case FAVORITE_MOVIE_WITH_ID:{
                numDeleted = db.delete(
                        MoviesContract.FavoriteMovieEntry.TABLE_FAVORITES,
                        MoviesContract.FavoriteMovieEntry._ID + " = ? ",
                        new String[] {String.valueOf(ContentUris.parseId(uri))}
                );
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        if (numDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numUpdated;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:{
                numUpdated = db.update(
                        MoviesContract.MovieEntry.TABLE_MOVIES,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            case FAVORITE_MOVIE:{
                numUpdated = db.update(
                        MoviesContract.FavoriteMovieEntry.TABLE_FAVORITES,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            case MOVIE_WITH_ID: {
                numUpdated = db.update(
                        MoviesContract.MovieEntry.TABLE_MOVIES,
                        values,
                        MoviesContract.MovieEntry._ID + " = ? ",
                        new String[] {String.valueOf(ContentUris.parseId(uri))}
                );
                break;
            }
            case FAVORITE_MOVIE_WITH_ID: {
                numUpdated = db.update(
                        MoviesContract.FavoriteMovieEntry.TABLE_FAVORITES,
                        values,
                        MoviesContract.MovieEntry._ID + " = ? ",
                        new String[] {String.valueOf(ContentUris.parseId(uri))}
                );
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

        }
        if (numUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numInserted = 0;
        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                //allows multiple transactions
                db.beginTransaction();
                try {
                    for (ContentValues value: values) {
                        long _id = db.insert(
                                MoviesContract.MovieEntry.TABLE_MOVIES,
                                null,
                                value
                        );
                        if (_id != -1) {
                            numInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            case FAVORITE_MOVIE: {
                //allows multiple transaction
                db.beginTransaction();
                try {
                    for (ContentValues value: values) {
                        long _id = db.insert(
                                MoviesContract.FavoriteMovieEntry.TABLE_FAVORITES,
                                null,
                                value
                        );
                        if (_id != -1) {
                            numInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            default:{
                return super.bulkInsert(uri, values);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return numInserted;
    }
}
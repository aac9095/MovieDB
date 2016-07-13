package com.example.ayush.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.ayush.popularmovies.MainActivity;
import com.example.ayush.popularmovies.MoviesFragment;
import com.example.ayush.popularmovies.R;
import com.example.ayush.popularmovies.data.MoviesContract;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Vector;

import retrofit.MovieDB;
import retrofit.MovieDbAPI;
import retrofit.Results;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MoviesSyncAdapter extends AbstractThreadedSyncAdapter implements Callback<MovieDB> {
    public final String LOG_TAG = MoviesSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60*180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private Gson gson;
    private Retrofit retrofit;

    public MoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onResponse(Call<MovieDB> call, Response<MovieDB> response) {
        int code = response.code();
        if (code == 200) {
            MovieDB MovieDB = response.body();
            getMovieDataFromRetrofit(MovieDB);
        } else {
            Toast.makeText(getContext(), "Did not work: " + String.valueOf(code), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onFailure(Call<MovieDB> call, Throwable t) {
        Toast.makeText(getContext(), "Nope", Toast.LENGTH_LONG).show();
        Log.e("Throwable ",t.toString());
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        //Log.e(LOG_TAG, "Starting sync");

        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(MovieDbAPI.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MovieDbAPI movieDBAPI = retrofit.create(MovieDbAPI.class);

        Call<MovieDB> call = movieDBAPI.getAPPID(MoviesFragment.order, MainActivity.app_key);
        //asynchronous call
        call.enqueue(this);
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }



    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    //locationSetting -> postal code
    private void getMovieDataFromRetrofit(MovieDB MovieDB){

        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        List<Results> results = MovieDB.getResults();

        // Insert the new weather information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(results.size());

        for(int i = 0; i < results.size(); i++) {
            // These are the values that will be collected.
            String backdrop_path = results.get(i).getBackdrop_path();
            String original_title = results.get(i).getOriginal_title();
            String poster_path = results.get(i).getPoster_path();
            String overview = results.get(i).getOverview();
            Double vote_average = results.get(i).getVote_average();
            String release_date = results.get(i).getRelease_date().substring(0,4);
            int id = results.get(i).getId();
            

            ContentValues movieValues = new ContentValues();
            Log.e(LOG_TAG,original_title);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, original_title);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_BAKCDROP_PATH,backdrop_path);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_PLOT,overview);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_RATING, vote_average);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, release_date);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER, poster_path);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, id);
            
            cVVector.add(movieValues);
        }

        int inserted = 0;
        // add to database
        if ( cVVector.size() > 0 ) {
            // Student: call bulkInsert to add the weatherEntries to the database here
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            Cursor c = getContext().getContentResolver().query(
                    MoviesContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
            if (c != null) {
                getContext().getContentResolver().delete(
                        MoviesContract.MovieEntry.CONTENT_URI,
                        null,
                        null
                );
                c.close();
            }
            inserted = getContext().getContentResolver().bulkInsert(
                    MoviesContract.MovieEntry.CONTENT_URI,
                    cvArray
            );

        }
        //Log.e(LOG_TAG, "Complete. " + inserted + " Inserted");

    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }



}
package com.example.ayush.popularmovies;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ayush.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";
    private static final int DETAIL_LOADER = 0;
    private int id;
    private Uri mUri;
    public static final String[] MOVIE_COLUMNS = {
            MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_PLOT,
            MoviesContract.MovieEntry.COLUMN_RATING,
            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_POSTER,
            MoviesContract.MovieEntry.COLUMN_BAKCDROP_PATH,
            MoviesContract.MovieEntry.COLUMN_VIDEOS_URL,
            MoviesContract.MovieEntry.COLUMN_REVIEWS
    };

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_PLOT = 2;
    public static final int COL_MOVIE_RATING = 3;
    public static final int COL_RELEASE_DATE = 4;
    public static final int COL_POSTER = 5;
    public static final int COL_BACKDROP_POSTER = 6;
    public static final int COLUMN_VIDEOS_URL = 7;
    public static final int COLUMN_REVIEWS = 8;

    private TextView movieTitle,movieYear,movieRating,movieOverview,overview;
    private ImageView moviePoster,favMovie;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            Log.e(LOG_TAG,"Received Arguements");
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        else{
            Log.e(LOG_TAG,"Received Intent");
            Intent intent = getActivity().getIntent();
            mUri=intent.getData();
        }
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        favMovie = (ImageView) rootView.findViewById(R.id.fav_movie);
        moviePoster = (ImageView) rootView.findViewById(R.id.movie_poster);
        movieTitle = (TextView) rootView.findViewById(R.id.movie_title);
        movieYear = (TextView) rootView.findViewById(R.id.year);
        movieRating = (TextView) rootView.findViewById(R.id.ratings);
        movieOverview = (TextView) rootView.findViewById(R.id.overview_content);
        overview = (TextView) rootView.findViewById(R.id.overview);
        overview.setText(R.string.overview);
        id = favMovie.getId();
        favMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues value = new ContentValues();
                Cursor cursorMovie = getActivity().getContentResolver()
                                .query(mUri,
                                        MOVIE_COLUMNS,
                                        MoviesContract.MovieEntry._ID + " = ?",
                                        new String[]{String.valueOf(ContentUris.parseId(mUri))},
                                        null);
                cursorMovie.moveToFirst();
                ContentValues[] values = new ContentValues[cursorMovie.getCount()];
                value.put(MoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID,cursorMovie.getString(COL_MOVIE_ID));
                value.put(MoviesContract.FavoriteMovieEntry.COLUMN_TITLE, cursorMovie.getString(COL_MOVIE_TITLE));
                value.put(MoviesContract.FavoriteMovieEntry.COLUMN_PLOT, cursorMovie.getString(COL_MOVIE_PLOT));
                value.put(MoviesContract.FavoriteMovieEntry.COLUMN_POSTER, cursorMovie.getString(COL_POSTER));
                value.put(MoviesContract.FavoriteMovieEntry.COLUMN_BAKCDROP_PATH, cursorMovie.getString(COL_BACKDROP_POSTER));
                value.put(MoviesContract.FavoriteMovieEntry.COLUMN_RATING, cursorMovie.getString(COL_MOVIE_RATING));
                value.put(MoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, cursorMovie.getString(COL_RELEASE_DATE));
                value.put(MoviesContract.FavoriteMovieEntry.COLUMN_VIDEOS_URL, cursorMovie.getString(COLUMN_VIDEOS_URL));
                value.put(MoviesContract.FavoriteMovieEntry.COLUMN_REVIEWS, cursorMovie.getString(COLUMN_REVIEWS));
                values[0] = value;
                if(id==R.drawable.fav) {
                    int numdeleted = getActivity().getContentResolver().delete(
                            MoviesContract.FavoriteMovieEntry.CONTENT_URI,
                            MoviesContract.FavoriteMovieEntry.COLUMN_TITLE + " =  ? ",
                            new String[]{cursorMovie.getString(COL_MOVIE_TITLE)}

                    );
                    Log.d(LOG_TAG, "Remove from fav: " + numdeleted);
                    id = R.drawable.not_fav;
                }
                else {
                    int insert = getActivity().getContentResolver().bulkInsert(
                            MoviesContract.FavoriteMovieEntry.CONTENT_URI,
                            values);
                    Log.e(LOG_TAG,"Added to fav: "+insert);
                    id = R.drawable.fav;
                }
                Picasso.with(getContext())
                        .load(id)
                        .resize(50,50)
                        .centerCrop()
                        .into(favMovie);
                cursorMovie.close();
                restartLoader();
            }
        });

        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri!=null) {
            //Log.e(LOG_TAG,"URI is not null");
            return new CursorLoader(getActivity(),
                    mUri,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null);
        }
        else{
            Log.e(LOG_TAG,"URI is null");
            return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Cursor cursor = data;
        if(!cursor.moveToFirst())
            Log.e(LOG_TAG,"cursor not moved to first position");
        if(data!=null&&data.moveToFirst()){
            String title = data.getString(COL_MOVIE_TITLE);
            String year = data.getString(COL_RELEASE_DATE);
            Double ratings = data.getDouble(COL_MOVIE_RATING);
            String overview = data.getString(COL_MOVIE_PLOT);
            String baseURL = "http://image.tmdb.org/t/p/w342";
            String poster = data.getString(COL_BACKDROP_POSTER);

            Log.e("title",title);
            Log.e("year",year);

            Picasso.with(getContext())
                    .load(baseURL +poster)
                    .into(moviePoster);

            movieOverview.setText(overview);
            movieRating.setText(String.valueOf(ratings));
            movieTitle.setText(title);
            //Insted of release date i am displaying the release year just like themoviedb.org
            movieYear.setText(year.substring(0,4));

            Cursor cursorFavMovie = getActivity().getContentResolver().query(
                    MoviesContract.FavoriteMovieEntry.CONTENT_URI,
                    null,
                    MoviesContract.FavoriteMovieEntry.COLUMN_TITLE + " =  ? ",
                    new String[]{data.getString(COL_MOVIE_TITLE)},
                    null
            );
            int imageId;
            if (!cursorFavMovie.moveToFirst()){
                imageId = R.drawable.not_fav;
            }
            else{
                imageId = R.drawable.fav;
            }
            cursorFavMovie.close();
            Picasso.with(getContext())
                    .load(imageId)
                    .resize(50,50)
                    .centerCrop()
                    .into(favMovie);
        }
    }
    void restartLoader(){
        getLoaderManager().restartLoader(DETAIL_LOADER,null,this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

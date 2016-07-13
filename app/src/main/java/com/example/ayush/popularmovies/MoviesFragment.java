package com.example.ayush.popularmovies;


import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.ayush.popularmovies.data.MoviesContract;
import com.example.ayush.popularmovies.sync.MoviesSyncAdapter;

import adapter.MoviesAdapter;
import retrofit.MovieDB;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIES_LOADER = 0;
    public View rootView;
    public MovieDB movieDB;
    private String app_key ;
    public static String order ="popular";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MoviesAdapter mAdapter;
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

    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
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
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_movies, container, false);
        movieDB = new MovieDB();
        try {
            app_key = getArguments().getString(getString(R.string.app_key));
        }
        catch (NullPointerException n){
            n.printStackTrace();
        }

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_movies);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MoviesAdapter(getActivity(),null);
        mRecyclerView.setAdapter(mAdapter);
        updateMovies();
        return rootView;
    }


    public void updateMovies(){
        MoviesSyncAdapter.syncImmediately(getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.movies_fragment_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_popular)
            order = getString(R.string.popular_tag);
        else if(id == R.id.action_top_rated)
            order = getString(R.string.top_rated_tag);
        updateMovies();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMovies();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri movieUri;
//        if(order.equals(getActivity().getString(R.string.popular))||order.equals(getActivity().getString(R.string.top_rated)))
            movieUri = MoviesContract.MovieEntry.CONTENT_URI;
//        else
//            movieUri = MoviesContract.FavoriteMovieEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                movieUri,
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }
}

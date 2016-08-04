package com.example.ayush.popularmovies;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.ayush.popularmovies.data.MoviesContract;
import com.example.ayush.popularmovies.sync.MoviesSyncAdapter;

import adapter.MoviesAdapter;
import adapter.Utility;
import movieDB.MovieDB;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIES_LOADER = 0;
    public View rootView;
    public MovieDB movieDB;
    private String app_key ;
    public static String order ;
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
    public static int mPosition;
    private final String POSITION="position";


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
            app_key = MainActivity.app_key;
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
        getLoaderManager().restartLoader(MOVIES_LOADER,null,this);
        MoviesSyncAdapter.syncImmediately(getActivity());
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.movies_fragment_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_popular) {
            Utility.setUserChoice(getContext(), getActivity().getString(R.string.popular_tag));
            Log.e("Options Menu",getActivity().getString(R.string.popular_tag));
        }
        else if(id == R.id.action_top_rated) {
            Log.e("Options Menu",getActivity().getString(R.string.top_rated_tag));
            Utility.setUserChoice(getContext(),getActivity().getString(R.string.top_rated_tag));
        }
        else if(id == R.id.action_favorite){
            Log.e("Options Menu",getActivity().getString(R.string.favorite_movies_tag));
            Utility.setUserChoice(getContext(),getActivity().getString(R.string.favorite_movies_tag));
        }
        updateMovies();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(MOVIES_LOADER,null,this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION,mPosition);
        Log.v(MoviesFragment.class.getSimpleName(),mPosition+"saved");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri movieUri;
        order = Utility.getPreferredChoice(getContext());
        //Log.e(MoviesFragment.class.getSimpleName(), "onCreateLoader: "+order);
        if(order.equals(getActivity().getString(R.string.popular_tag))||order.equals(getActivity().getString(R.string.top_rated_tag)))
            movieUri = MoviesContract.MovieEntry.CONTENT_URI;
        else
            movieUri = MoviesContract.FavoriteMovieEntry.CONTENT_URI;

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
        mRecyclerView.getLayoutManager().scrollToPosition(mPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }


    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri movieUri);
    }


}

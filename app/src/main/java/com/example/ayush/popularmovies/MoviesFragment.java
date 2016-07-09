package com.example.ayush.popularmovies;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import adapter.MoviesAdapter;
import retrofit.MovieDB;
import retrofit.MovieDbAPI;
import retrofit.Results;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesFragment extends Fragment implements Callback<MovieDB> {
    public View rootView;
    public MovieDB movieDB;
    private String app_key ;
    private static String order ;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MoviesAdapter mAdapter;
    private Gson gson;
    private Retrofit retrofit;

    public MoviesFragment() {
        // Required empty public constructor
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
        //By default movies will be displayed by the most popular order
        order=getString(R.string.popular_tag);
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(MovieDbAPI.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_movies);
        updateMovies();
        return rootView;
    }

    @Override
    public void onResponse(Call<MovieDB> call, Response<MovieDB> response) {
        int code = response.code();
        if (code == 200) {
            movieDB = response.body();
            setRecyclerView(movieDB.getResults());
        } else {
            Toast.makeText(getActivity(), "Did not work: " + String.valueOf(code), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onFailure(Call<MovieDB> call, Throwable t) {
        Toast.makeText(getActivity(), "Nope", Toast.LENGTH_LONG).show();
        Log.e("Throwable ",t.toString());
    }
    public void updateMovies(){

        // prepare call in Retrofit 2.0
        MovieDbAPI movieDbAPI = retrofit.create(MovieDbAPI.class);

        Call<MovieDB> call = movieDbAPI.getAPPID(order,app_key);
        //asynchronous call
        call.enqueue(this);
    }

    private void setRecyclerView(List<Results> results){
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MoviesAdapter(results);
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
}

package com.example.ayush.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {


    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ImageView moviePoster = (ImageView) rootView.findViewById(R.id.movie_poster);
        TextView movieTitle = (TextView) rootView.findViewById(R.id.movie_title);
        TextView movieYear = (TextView) rootView.findViewById(R.id.year);
        TextView movieRating = (TextView) rootView.findViewById(R.id.ratings);
        TextView movieOverview = (TextView) rootView.findViewById(R.id.overview_content);


        Intent intent = getActivity().getIntent();
        String title = intent.getStringExtra("title");
        String year = intent.getStringExtra("year");
        Double ratings = intent.getDoubleExtra("ratings",0.0);
        String overview = intent.getStringExtra("overview content");
        String baseURL = "http://image.tmdb.org/t/p/w342";
        String poster = intent.getStringExtra("poster");

        Picasso.with(getContext())
                .load(baseURL +poster)
                .into(moviePoster);

        movieOverview.setText(overview);
        movieRating.setText(String.valueOf(ratings));
        movieTitle.setText(title);
        //Insted of release date i am displaying the release year just like themoviedb.org
        movieYear.setText(year.substring(0,4));

        return rootView;
    }

}

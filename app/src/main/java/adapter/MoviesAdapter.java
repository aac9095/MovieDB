package adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ayush.popularmovies.MoviesFragment;
import com.example.ayush.popularmovies.R;
import com.example.ayush.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    private String baseURL = "http://image.tmdb.org/t/p/w185";
    private Cursor mCursor;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView moviePoster;

        public ViewHolder(View v) {
            super(v);
            moviePoster = (ImageView) v.findViewById(R.id.movie_photo);
        }
    }

    public MoviesAdapter(Context context, Cursor c) {
        mContext = context;
        mCursor = c;
    }

    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_movie, null);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Context context = mContext;
        Cursor cursor = mCursor;
        cursor.moveToPosition(position);
        Picasso.with(context)
                .load(baseURL + cursor.getString(MoviesFragment.COL_POSTER))
                .into(holder.moviePoster);
        holder.moviePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoviesFragment.mPosition=position;
                Cursor cursor = mCursor;
                cursor.moveToPosition(position);
                String order = Utility.getPreferredChoice(mContext);
                Uri movieUri;
                Log.e("Movie id",""+cursor.getLong(MoviesFragment.COL_MOVIE_ID));
                if(!order.equals(mContext.getString(R.string.favorite_movies))){
                   movieUri  = MoviesContract.MovieEntry.buildMovieUriWithId(
                            cursor.getLong(MoviesFragment.COL_MOVIE_ID));
                }
                else{
                    movieUri = MoviesContract.FavoriteMovieEntry.buildFavoriteMovieUriWithId(
                            cursor.getLong(MoviesFragment.COL_MOVIE_ID));

                }
                ((MoviesFragment.Callback) mContext).onItemSelected(movieUri);
            }
        });
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor cursor){
        mCursor=cursor;
        //Log.e(MoviesAdapter.class.getSimpleName(),"notifying dataset changes");
        notifyDataSetChanged();
    }

    public int getPosition(){
        return mCursor.getPosition();
    }

}
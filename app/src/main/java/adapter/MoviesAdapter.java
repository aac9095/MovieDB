package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ayush.popularmovies.DetailActivity;
import com.example.ayush.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit.Results;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    private List<Results> results;
    private String baseURL = "http://image.tmdb.org/t/p/w185";

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView moviePoster;

        public ViewHolder(View v) {
            super(v);
            moviePoster = (ImageView) v.findViewById(R.id.movie_photo);
        }
    }

    public MoviesAdapter(List<Results> myDataset) {
        results = myDataset;
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
        final Context context = holder.moviePoster.getContext();

        Picasso.with(context)
                .load(baseURL + results.get(position).getPoster_path())
                .into(holder.moviePoster);
        holder.moviePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("poster",results.get(position).getBackdrop_path());
                intent.putExtra("title",results.get(position).getOriginal_title());
                intent.putExtra("year",results.get(position).getRelease_date());
                intent.putExtra("ratings",results.get(position).getVote_average());
                intent.putExtra("overview content",results.get(position).getOverview());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

}
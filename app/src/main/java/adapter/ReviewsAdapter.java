package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ayush.popularmovies.R;
import com.example.ayush.popularmovies.service.Review;

import java.util.ArrayList;

/**
 * Created by Ayush on 09-08-2016.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Review> mList;

    public ReviewsAdapter(Context mContext, ArrayList<Review> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_review, null);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.author.setText(mList.get(position).getAuthor());
        holder.content.setText(mList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        if(mList==null)
            return 0;
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView author,content;

        public ViewHolder(View v) {
            super(v);
            author = (TextView) v.findViewById(R.id.author_text);
            content = (TextView) v.findViewById(R.id.review_content);
        }
    }

    public void swapList(ArrayList<Review> list){
        mList = list;
        notifyDataSetChanged();
    }

}

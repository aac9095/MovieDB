package adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ayush.popularmovies.R;

import java.util.ArrayList;

/**
 * Created by Ayush on 09-08-2016.
 */
public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {


    private String baseURL = "http://image.tmdb.org/t/p/w185";
    private Context mContext;
    private ArrayList<String> mList;

    public TrailersAdapter(Context mContext, ArrayList<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_trailer, null);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String text = "Trailer " + (position+1);
        holder.trailer.setText(text);
        holder.trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = mList.get(position);
                try{
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
                    mContext.startActivity(intent);
                }catch (ActivityNotFoundException ex){
                    Intent intent=new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v="+id));
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mList==null)
            return 0;
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView trailer;

        public ViewHolder(View v) {
            super(v);
            trailer = (TextView) v.findViewById(R.id.trailer);
        }
    }

    public void swapList(ArrayList<String> list){
        mList = list;
        notifyDataSetChanged();
    }
}

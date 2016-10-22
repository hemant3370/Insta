package com.hsr.hemantsingh.insta;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by HemantSingh on 21/10/16.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<User> mDataset;
    CustomItemClickListener listener;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements  CustomItemClickListener {
        // each data item is just a string in this case
        public View mItemView;
        public  TextView nameTV;
        public NetworkImageView proPicIB,img1,img2,img3,img4;

        public ViewHolder(View v) {
            super(v);
            mItemView = v;
            nameTV = (TextView) v.findViewById(R.id.textViewName);
            proPicIB = (NetworkImageView) v.findViewById(R.id.imageButton2);
            img1 = (NetworkImageView) v.findViewById(R.id.imageView);
            img2 = (NetworkImageView) v.findViewById(R.id.imageView2);
            img3 = (NetworkImageView) v.findViewById(R.id.imageView3);
            img4 = (NetworkImageView) v.findViewById(R.id.imageView4);
        }

        @Override
        public void onItemClick(View v, int position) {

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<User> myDataset,CustomItemClickListener listener) {
        mDataset = myDataset;
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemview, parent, false);
        // set the view's size, margins, paddings and layout parameters

        final ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, vh.getAdapterPosition());
            }
        });
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.nameTV.setText(mDataset.get(position).getItems().first().getUser().getFull_name());

         holder.proPicIB.setImageUrl(mDataset.get(position).getItems().first().getUser().getProfilePicture(),VolleySingleton.getInstance().getImageLoader());
        holder.img1.setImageUrl(mDataset.get(position).getItems().first().getImages().getThumbnail().getUrl(),VolleySingleton.getInstance().getImageLoader());
        holder.img2.setImageUrl(mDataset.get(position).getItems().get(1).getImages().getThumbnail().getUrl(),VolleySingleton.getInstance().getImageLoader());
        holder.img3.setImageUrl(mDataset.get(position).getItems().get(2).getImages().getThumbnail().getUrl(),VolleySingleton.getInstance().getImageLoader());
        holder.img4.setImageUrl(mDataset.get(position).getItems().get(3).getImages().getThumbnail().getUrl(),VolleySingleton.getInstance().getImageLoader());


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}




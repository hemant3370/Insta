package com.hsr.hemantsingh.insta;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.NetworkImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by HemantSingh on 25/10/16.
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {
    private List<ImageData> mDataset;
    private final Context context;
    CustomItemClickListener listener;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements  CustomItemClickListener {
        // each data item is just a string in this case
        public View mItemView;

        public ImageView imgageView;

        public ViewHolder(View v) {
            super(v);
            mItemView = v;
            imgageView = (ImageView) v.findViewById(R.id.ivProfile);
        }

        @Override
        public void onItemClick(View v, int position) {

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public GridAdapter(Context context, List<ImageData> myDataset, CustomItemClickListener listener) {
        mDataset = myDataset;
        this.listener = listener;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.griditem, parent, false);
        // set the view's size, margins, paddings and layout parameters

        final GridAdapter.ViewHolder vh = new GridAdapter.ViewHolder(v);
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                + "/" + mDataset.get(0).getUser().getUsername() +"/");
//        AltexImageDownloader.readFromDiskAsync(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
//                + "/" + mDataset.get(0).getUser().getUsername() + "/" + folder.list()[position]), new AltexImageDownloader.OnImageReadListener() {
//            @Override
//            public void onImageRead(Bitmap bitmap) {
//                holder.imgageView.setImageBitmap(bitmap);
//            }
//
//            @Override
//            public void onReadFailed() {
//
//            }
//        });
        Picasso.with(context).load(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                + "/" + mDataset.get(0).getUser().getUsername() + "/" + folder.list()[position])).resize(MyApplication.getInstance().getMetrics().widthPixels/3,MyApplication.getInstance().getMetrics().widthPixels/3).into(holder.imgageView);


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                + "/" + mDataset.get(0).getUser().getUsername() +"/");
        return folder.list().length;
    }
}




package com.hsr.hemantsingh.insta;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.File;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

public class GridActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public CustomItemClickListener listener;
    private Realm realm;

    RealmList<ImageData> results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        mRecyclerView = (RecyclerView) findViewById(R.id.grid_recycler_view);
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(realmConfiguration);

        final Activity activity = this;

        listener = new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent o = new Intent(GridActivity.this, ImageTabsActivity.class);

                o.putExtra("id", results.first().getUser().getId());
                o.putExtra("index", position);
//                Bundle bundle = null;
//
//                if (activity != null) {
//                    ActivityOptionsCompat options =
//                            ActivityOptionsCompat.makeSceneTransitionAnimation(activity, v.findViewById(R.id.ivProfile), "change_image_transform");
//                    bundle = options.toBundle();
//                }
//                activity.startActivity(o, bundle);
                startActivity(o);
            }
        };

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        if(activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mLayoutManager = new GridLayoutManager(this,3);

        }
        else{
            mLayoutManager = new GridLayoutManager(this,5);
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        String userId = getIntent().getStringExtra("id");
        results = realm.where(User.class).equalTo("id", userId).findAll().first().getItems();
        setTitle(results.first().getUser().getFull_name());

        mAdapter = new GridAdapter(this,results,listener);
        mRecyclerView.setAdapter(mAdapter);

        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                + "/" + results.first().getUser().getUsername() +"/");
         AltexImageDownloader.readFromDiskAsync(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                 + "/" + results.first().getUser().getUsername() +"/" + folder.list()[new  Random().nextInt(folder.list().length - 1)]), new AltexImageDownloader.OnImageReadListener() {
             @Override
             public void onImageRead(Bitmap bitmap) {
                 Drawable d = new BitmapDrawable(getResources(), bitmap);
                 d.setAlpha(130);
                 mRecyclerView.setBackground(d);
             }

             @Override
             public void onReadFailed() {

             }
         });

//        mRecyclerView.setBackground(new GradientDrawable(activity.getResources().getConfiguration().orientation,));
    }



}

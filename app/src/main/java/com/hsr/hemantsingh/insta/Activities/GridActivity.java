package com.hsr.hemantsingh.insta.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hsr.hemantsingh.insta.Adapters.GridAdapter;
import com.hsr.hemantsingh.insta.R;
import com.hsr.hemantsingh.insta.listeners.CustomItemClickListener;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class GridActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    public CustomItemClickListener listener;
    private Realm realm;
    String userId;
//
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


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        GridLayoutManager mLayoutManager;
        if(activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mLayoutManager = new GridLayoutManager(this,3);
//            mLayoutManager = new StaggeredGridLayoutManager(2,Configuration.ORIENTATION_PORTRAIT);

        }
        else{
            mLayoutManager = new GridLayoutManager(this,5);
//            mLayoutManager = new StaggeredGridLayoutManager(3,Configuration.ORIENTATION_LANDSCAPE);
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
         userId = getIntent().getStringExtra("id");
//        results = realm.where(User.class).equalTo("id", userId).findAll().first().getItems();


        setTitle(getIntent().getStringExtra("username"));
//       final File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
//        + "/" + results.first().getUser().getUsername() +"/");
        final String[] files = getIntent().getStringArrayExtra("files");
       final String[] captions = getIntent().getStringArrayExtra("captions");
        listener = new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent o = new Intent(GridActivity.this, ImageTabsActivity.class);
                o.putExtra("files",files);
                o.putExtra("displayName",getIntent().getStringExtra("displayName"));
                o.putExtra("username",getIntent().getStringExtra("username"));
                o.putExtra("id", getIntent().getStringExtra("id"));
                o.putExtra("index", position);
                startActivity(o);
            }

            @Override
            public void onDeleteClick(View v, int position) {

            }

        };
        mAdapter = new GridAdapter(this,getIntent().getStringExtra("username"),files,listener);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
}

package com.hsr.hemantsingh.insta;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.internal.IOException;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public CustomItemClickListener listener;
    private Realm realm;
    ProgressDialog pd;
    RealmResults<User> results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
         realm = Realm.getInstance(realmConfiguration);

        results = realm.where(User.class).findAll();
        listener = new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent o = new Intent(MainActivity.this, ImageTabs.class);
                o.putExtra("id", results.get(position).getItems().first().getUser().getId());
                startActivity(o);
            }
        };
        if (results.size() > 0) {
            mAdapter = new MyAdapter(results,listener);
            mRecyclerView.setAdapter(mAdapter);
        }
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        final EditText txtUrl = new EditText(this);

// Set the default text to a link of the Queen
        txtUrl.setHint("Type Instagram username");

        // specify an adapter (see also next example)
         final AlertDialog name = new AlertDialog.Builder(this)
                 .setTitle("Download Pics")
                 .setMessage("Enter the Instagram username")
                 .setView(txtUrl)
                 .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int whichButton) {
                         String url = txtUrl.getText().toString();
                         if (url.length() > 0) {
                             pd = new ProgressDialog(MainActivity.this);
                           pd.show(MainActivity.this,"Fetching","",true,true);
                             jsonRequestVolley("https://www.instagram.com/" + url + "/media/");
                         }
                     }
                 })
                 .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int whichButton) {
                     }
                 }).create();
              name.show();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.show();
            }
        });
    }
    public void jsonRequestVolley(String volleyUrl) {
        //dialog.show();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, volleyUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("items");
                            int count = array.length();
                            if (count > 0 ) {
                                Gson gson = new GsonBuilder().create();
                                    String json = response.toString();
                                    final User u = gson.fromJson(json, User.class);
                                realm.beginTransaction();
                                u.setId(u.getItems().first().getUser().getId());
                                realm.commitTransaction();


                                if (checkIfExists(u.getItems().first().getId()) == false) {
                                    realm.beginTransaction();
                                    realm.copyToRealm(u);
                                    realm.commitTransaction();
                                }
                                results = realm.where(User.class).findAll();
                                mAdapter = new MyAdapter(results, listener);
                                mRecyclerView.setAdapter(mAdapter);
                                pd.hide();
                            }
                        } catch (JSONException e) {
                            Log.e("TAG", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        pd.setTitle(error.getMessage());

                    }
                });

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance().getRequestQueue().add(jsObjRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public boolean checkIfExists(String id){

        RealmQuery<ImageData> query = realm.where(ImageData.class)
                .equalTo("id", id);

        return query.count() != 0;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public List<ImageData> loadImages() throws IOException {
//
//        loadJsonFromStream();
//        loadJsonFromJsonObject();
//        loadJsonFromString();

        return realm.where(ImageData.class).findAll();
    }
}

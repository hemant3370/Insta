package com.hsr.hemantsingh.insta.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hsr.hemantsingh.insta.Adapters.MyAdapter;
import com.hsr.hemantsingh.insta.Models.User;
import com.hsr.hemantsingh.insta.Networking.VolleySingleton;
import com.hsr.hemantsingh.insta.R;
import com.hsr.hemantsingh.insta.listeners.CustomItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmQuery;
import tyrantgit.explosionfield.ExplosionField;

public class MainActivity extends AppCompatActivity  {

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    public CustomItemClickListener listener;
    private Realm realm;
    ProgressDialog pd;
    FloatingActionButton editFab,addFab;
    AlertDialog name;
    ExplosionField explosionField;
    AutoCompleteTextView txtUrl ;
    int count;
    List<User> results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
         realm = Realm.getInstance(realmConfiguration);
        txtUrl = new AutoCompleteTextView(this);
        editFab = (FloatingActionButton) findViewById(R.id.fabEdit);
        addFab = (FloatingActionButton) findViewById(R.id.fab1);
        editFab.setVisibility(View.GONE);
        final Activity activity = this;
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if(ActivityCompat.checkSelfPermission(activity, permission)
                == PackageManager.PERMISSION_GRANTED) {
            // Proceed with your code execution
        } else {
            // Uhhh I guess we have to ask for permission
            ActivityCompat.requestPermissions(activity,new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },1);
        }
        explosionField = ExplosionField.attach2Window(this);

        listener = new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent o = new Intent(MainActivity.this, GridActivity.class);
                o.putExtra("displayName",results.get(position).getFullName());
                o.putExtra("files", results.get(position).getUrls().toArray(new String[results.get(position).getUrls().size()]));
                o.putExtra("username",results.get(position).getUsername());
                o.putExtra("id", results.get(position).getId());
                if (mAdapter.editMode) {
                    mAdapter.editMode = false;
                    mAdapter.notifyDataSetChanged();
                }

                startActivity(o);
            }

            @Override
            public void onDeleteClick(View v, int position) {
                explosionField.explode(v);
                realm.beginTransaction();
                results.get(position).deleteFromRealm();
                if (mAdapter != null) {
                    mAdapter.notifyItemRemoved(position);
                }
                realm.commitTransaction();
            }
        };



        // use a linear layout manager
        if(activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mLayoutManager = new GridLayoutManager(this,2);
        }
        else{
            mLayoutManager = new GridLayoutManager(this,3);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);


          final String[] instagramHandles = new String[]{

          };
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, instagramHandles);
        txtUrl.setAdapter(adapter);

        txtUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                      jsonSuggestionVolley(charSequence.toString(), adapter);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
// Set the default text to a link of the Queen
        txtUrl.setHint("Type here...");

        // specify an adapter (see also next example)
          name = new AlertDialog.Builder(this)
                 .setTitle("Download Media")
                 .setMessage("Type username or handle")
                 .setView(txtUrl)
                 .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int whichButton) {
                         String url = txtUrl.getText().toString();
                         if (url.length() > 0) {
                             showPD();
                             jsonRequestVolley("https://www.instagram.com/" + url + "/?__a=1");
                         }
                     }
                 })
                 .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int whichButton) {
                     }
                 }).create();



        txtUrl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                name.dismiss();
                showPD();
                jsonRequestVolley("https://www.instagram.com/" + adapter.getItem(i) + "/?__a=1");
            }
        });
        results = realm.where(User.class).findAll();
        if (results.size() > 0) {
            mAdapter = new MyAdapter(results,listener, false);
            mRecyclerView.setAdapter(mAdapter);
        }
        else{
            name.show();
        }

    }
    public void showPD(){
    pd = new ProgressDialog(MainActivity.this);
        pd.setTitle("Fetching media");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
        pd.show();
    }
    public void jsonSuggestionVolley(String str, final ArrayAdapter<String> adapter){
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "https://www.instagram.com/web/search/topsearch/?context=blended&query=" + str, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("users");
                            int count = array.length();
                            if (count > 0) {
                                adapter.clear();
                            }
                            for (int i = 0; i < count; i++){
                                adapter.add(array.getJSONObject(i).getJSONObject("user").getString("username"));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e("TAG", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub


                    }
                });

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance().getRequestQueue().add(jsObjRequest);
    }
    public void fabClick(View v){
        txtUrl.setText("");
        name.show();
    }
    public void cancelEditClick(View v){
        if (mAdapter.editMode) {
            mAdapter.editMode = false;
            mAdapter.notifyDataSetChanged();
        }
       editFab.setVisibility(View.GONE);
        addFab.setVisibility(View.VISIBLE);
    }
    public void jsonRequestVolley(final String volleyUrl) {
        //dialog.show();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest

                (Request.Method.GET, volleyUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject user = response.getJSONObject("user");
                            JSONObject media = user.getJSONObject("media");
                            JSONArray array = media.getJSONArray("nodes");

                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                            Boolean hdBool = sharedPref.getBoolean("fullhdkey", false);
                            Boolean downloadVideo = sharedPref.getBoolean("videokey", false);
                           count = array.length();
                            if (count > 0 ) {
                                Gson gson = new GsonBuilder().create();
                                String json = user.toString();
                                final User u = gson.fromJson(json, User.class);
                                RealmList<String> urls = new RealmList<>();
                                for (int i=0; i < array.length(); i++) {
                                    try {
                                        String url = array.getJSONObject(i).getString("display_src");
                                        urls.add(url);
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                                u.setUrls(urls);
                                realm.beginTransaction();
                                u.setId(u.getId());
                                realm.commitTransaction();


//                                AltexImageDownloader.writeToDisk(MainActivity.this, u.getItems().first().getUser().getProfilePicture().replace("s150x150","s1080x1080"), volleyUrl.split("/")[3].trim() + "/");
                                if (!checkIfExists(u.getId())) {
                                    realm.beginTransaction();
                                    realm.copyToRealm(u);
                                    realm.commitTransaction();
                                }

                                results = realm.where(User.class).findAll();
                                if(MainActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                                    mLayoutManager = new GridLayoutManager(MainActivity.this,3);
                                }
                                else{
                                    mLayoutManager = new GridLayoutManager(MainActivity.this,5);
                                }
                                mAdapter = new MyAdapter(results, listener,false);
                                mRecyclerView.setAdapter(mAdapter);


                                pd.dismiss();
                            }
                            else {
                                pd.dismiss();
                                txtUrl.setText("");
                                name.show();
                                Toast.makeText(MainActivity.this, "No Images Found OR Account is Private.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.e("TAG", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        pd.setMessage(error.getLocalizedMessage());

                    }
                });

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance().getRequestQueue().add(jsObjRequest);
    }
    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);

            }
            fileOrDirectory.delete();
        }
        fileOrDirectory.delete();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }



    @Override
    public void onBackPressed() {
        if (mAdapter != null && mAdapter.editMode) {
            mAdapter.editMode = false;
            mAdapter.notifyDataSetChanged();
            editFab.setVisibility(View.GONE);
            addFab.setVisibility(View.VISIBLE);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public  boolean checkIfExists(String id){

        RealmQuery<User> query = realm.where(User.class)
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
        if (id == R.id.action_edit && mAdapter != null) {
            mAdapter.editMode = true;
            mAdapter.notifyDataSetChanged();
            addFab.setVisibility(View.GONE);
            editFab.setVisibility(View.VISIBLE);
//            for (User u :
//                    realm.where(User.class).findAll()) {
//                File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
//                        + "/" + u.getItems().first().getUser().getUsername() +"/");
//                deleteRecursive(folder);
//            }
//
//            realm.beginTransaction();
//            realm.deleteAll();
//            if (mAdapter != null) {
//                mAdapter.notifyDataSetChanged();
//            }
//            realm.commitTransaction();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }


}

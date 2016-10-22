package com.hsr.hemantsingh.insta;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.artjimlop.altex.AltexImageDownloader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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

        Activity activity = this;
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if(ActivityCompat.checkSelfPermission(activity, permission)
                == PackageManager.PERMISSION_GRANTED) {
            // Proceed with your code execution
        } else {
            // Uhhh I guess we have to ask for permission
            ActivityCompat.requestPermissions(activity,new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },1);
        }
        listener = new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent o = new Intent(MainActivity.this, ImageTabs.class);
                File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                        + "/" + results.get(position).getItems().first().getUser().getUsername() +"/");
                for (String str :
                        folder.list()) {
                    File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                            + "/" + results.get(position).getItems().first().getUser().getUsername() +"/" + str);
                    if  (!f.isFile() || f.isDirectory()){
                        f.delete();
                        if(f.exists()){
                            try {
                                f.getCanonicalFile().delete();
                            } catch (java.io.IOException e) {
                                e.printStackTrace();
                            }
                            if(f.exists()){
                                getApplicationContext().deleteFile(f.getName());
                            }
                            if(f.exists()){
                                f.getAbsoluteFile().delete();
                            }
                        }
                    }
                }
                o.putExtra("id", results.get(position).getItems().first().getUser().getId());
                startActivity(o);
            }
        };

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        final AutoCompleteTextView txtUrl = new AutoCompleteTextView(this);

          final String[] COUNTRIES = new String[]{

          };
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        txtUrl.setAdapter(adapter);

        txtUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                  if (charSequence.length() > 3){
                      jsonSuggestionVolley(charSequence.toString(), adapter);
                  }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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

                             jsonRequestVolley("https://www.instagram.com/" + url + "/media/");
                         }
                     }
                 })
                 .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int whichButton) {
                     }
                 }).create();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtUrl.setText("");
                name.show();
            }
        });
        txtUrl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                name.dismiss();
                jsonRequestVolley("https://www.instagram.com/" + adapter.getItem(i).toString() + "/media/");
            }
        });
        results = realm.where(User.class).findAll();
        if (results.size() > 0) {
            mAdapter = new MyAdapter(results,listener);
            mRecyclerView.setAdapter(mAdapter);
        }
        else{
            name.show();
        }
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
    public void jsonRequestVolley(final String volleyUrl) {
        //dialog.show();
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setTitle("Fetching");
        pd.show();
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
                                final AltexImageDownloader downloader = new AltexImageDownloader(new AltexImageDownloader.OnImageLoaderListener() {
                                    @Override
                                    public void onError(AltexImageDownloader.ImageError error) {
                                        // Here you should show something to the user, right?
                                    }

                                    @Override
                                    public void onProgressChange(int percent) {
                                        // Here you can show the percentage of progress and stuff
                                        pd.setProgress(percent);
                                    }

                                    @Override
                                    public void onComplete(Bitmap result) {
                                        // Do whatever you want, mate
                                    }
                                });
                                for (ImageData img:
                                    u.getItems() ) {
                                    if (img.getCaption() != null && img.getCaption().getText() != null){
                                        pd.setMessage("Downloading " + img.getCaption().getText());
                                    }
                                    if (img.getType().contains("video")){

                                        downloader.writeToDisk(MainActivity.this, img.getAlt_media_url(), volleyUrl.split("/")[3].trim() + "/");
                                    }
                                    else {
                                        downloader.writeToDisk(MainActivity.this, img.getImages().getStandard_resolution().getUrl(), volleyUrl.split("/")[3].trim() + "/");
                                    }
                                }


                                if (checkIfExists(u.getItems().first().getId()) == false) {
                                    realm.beginTransaction();
                                    realm.copyToRealm(u);
                                    realm.commitTransaction();
                                }
                                results = realm.where(User.class).findAll();
                                mAdapter = new MyAdapter(results, listener);
                                mRecyclerView.setAdapter(mAdapter);
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.hide();
                                        pd.dismiss();
                                    }
                                });

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
            realm.beginTransaction();
            realm.deleteAll();
            mAdapter.notifyDataSetChanged();
            realm.commitTransaction();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

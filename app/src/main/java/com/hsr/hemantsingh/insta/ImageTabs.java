package com.hsr.hemantsingh.insta;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.toolbox.NetworkImageView;

import java.io.File;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

public class ImageTabs extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_tabs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        userId = getIntent().getStringExtra("id");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), userId);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(0);



    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_SECTION_URL = "section_url";
        VideoView vv;
        FloatingActionButton fab;
        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String url) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_SECTION_URL, url);
            fragment.setArguments(args);

            return fragment;
        }



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_image_tabs, container, false);
            vv = (VideoView) rootView.findViewById(R.id.videoView);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView5);
             fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (vv.isPlaying()){
                       vv.pause();
                    }
                    else {
                        vv.start();
                    }
                }
            });
            if (this.getArguments().getString(ARG_SECTION_URL).contains(".jpg")){
                vv.setVisibility(View.INVISIBLE);
                fab.setVisibility(View.INVISIBLE);
//            imageView.setImageUrl(this.getArguments().getString(ARG_SECTION_URL), VolleySingleton.getInstance().getImageLoader());
                imageView.setImageBitmap(BitmapFactory.decodeFile(this.getArguments().getString(ARG_SECTION_URL)));
            }
            else {
                imageView.setVisibility(View.INVISIBLE);
                vv.setVideoPath(this.getArguments().getString(ARG_SECTION_URL));
                vv.seekTo(300);
                vv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (vv.isPlaying()) {
                            vv.pause();

                        }

                        else {
                            vv.start();
                        }
                    }
                });
            }

            return rootView;
        }

        @Override
        public void onDetach() {
            super.onDetach();
            vv.stopPlayback();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public RealmList<ImageData> img;

        public SectionsPagerAdapter(FragmentManager fm, String id) {
            super(fm);

            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().name(Realm.DEFAULT_REALM_NAME)
                    .schemaVersion(0)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            Realm realm = Realm.getInstance(realmConfiguration);


            img = realm.where(User.class).equalTo("id", id).findAll().first().getItems();

        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                    + "/" + img.get(position).getUser().getUsername() +"/");
            return PlaceholderFragment.newInstance(position + 1,Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                    + "/" + img.get(position).getUser().getUsername() +"/" + folder.list()[position]);
        }

        @Override
        public int getCount() {
            // Show 3 total pages
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                    + "/" + img.first().getUser().getUsername() +"/");
            return folder.list().length;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return img.get(position).getCaption().getText();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

package com.widelongapps.wallager;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.widelongapps.wallager.ui.main.SectionsPagerAdapter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;
    private static final String defaultAlbum = "Album.txt";
    private static final String albumsFileName = "albumNames.txt";
    final String PREFS_NAME = "MyPrefsFile";
    Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            Log.d("Comments", "First time");

            // first time task
            FileOutputStream outputStream;
            try {
                outputStream = openFileOutput(defaultAlbum, Context.MODE_PRIVATE);
                outputStream.write("".getBytes());
                outputStream = openFileOutput(albumsFileName, Context.MODE_PRIVATE);
                outputStream.write("".getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // record the fact that the app has been started at least once
            settings.edit().putBoolean("my_first_time", false).commit();
        }
        super.onCreate(savedInstanceState);
        setLocale();
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        ImageButton help = findViewById(R.id.helpButton);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHelp();
            }

        });

    }
    public ArrayList<String> getAlbums() {
        ArrayList<String> albums = new ArrayList<>();
        try {
            FileInputStream inputStream = openFileInput(albumsFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            while (line != null) {
                albums.add(line);
                line = reader.readLine();
                Log.i("pls", line);
            }
        }catch(Exception e){

        }
        return albums;
    }
    private void openHelp() {
        String url = "https://widelongapps.000webhostapp.com/Help/";
        startActivity(new Intent((Intent.ACTION_VIEW), Uri.parse(url)));
    }
    public void writeAlbums(ArrayList<String> files){
        FileOutputStream outputStream;
        FileOutputStream outputStream2;
        try {
            outputStream2 = openFileOutput(albumsFileName, Context.MODE_PRIVATE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                for (int i = 0; i < files.size(); i++){
                    outputStream2 = openFileOutput(albumsFileName, Context.MODE_APPEND);
                    outputStream2.write(files.get(i).getBytes());
                    outputStream2.write("\n".getBytes());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void writeFile(ClipData uris, String filename){
        FileOutputStream outputStream;
        FileOutputStream outputStream2;
        try {
            outputStream = openFileOutput(filename+".txt", Context.MODE_PRIVATE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                for (int i = 0; i < uris.getItemCount(); i++){
                    Uri path = uris.getItemAt(i).getUri();
                    outputStream.write(path.toString().getBytes());
                    outputStream.write("\n".getBytes());
                    Log.i("Path: " ,path.toString());
                }
                outputStream2 = openFileOutput(albumsFileName, Context.MODE_APPEND);
                outputStream2.write(filename.getBytes());
                outputStream2.write("\n".getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFile(Uri uri, String filename){
        FileOutputStream outputStream;
        FileOutputStream outputStream2;
        try {
            outputStream = openFileOutput(filename+".txt", Context.MODE_PRIVATE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    outputStream.write(uri.toString().getBytes());
                    outputStream.write("\n".getBytes());
                    Log.i("Path: " ,uri.toString());
                    outputStream2 = openFileOutput(albumsFileName, Context.MODE_APPEND);
                    outputStream2.write(filename.getBytes());
                    outputStream2.write("\n".getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setLocale() {
        String lang="";
        try{
        FileInputStream inputStream = openFileInput("lang.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        lang = reader.readLine();
        }catch(Exception e){
        }
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public void changeLanguagePersistence(String lang) {
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput("lang.txt", Context.MODE_PRIVATE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                outputStream.write(lang.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void reWriteFile(String newName, String oldName){
        try {
            FileOutputStream outputStream = openFileOutput(newName+".txt", Context.MODE_PRIVATE);
            FileInputStream inputStream = openFileInput(oldName+".txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            while (line != null) {
                outputStream.write(line.getBytes());
                outputStream.write("\n".getBytes());
                line = reader.readLine();
                Log.i("pls", line);
            }
        }catch(Exception e){

        }
        deleteFile(oldName+".txt");
    }
}
package com.widelongapps.wallager;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class albumActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;
    private String albumName = "";
    private LinearLayout layout;
    private ArrayList<Uri> uris;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        layout = findViewById(R.id.content);
        uris = new ArrayList<>();
        Bundle b = getIntent().getExtras();
        albumName = b.getString("key");
        drawImages();

        ImageButton help = findViewById(R.id.helpButton);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHelp();
            }

        });

        FloatingActionButton addAlbum = findViewById(R.id.addImageButton);
        addAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchImages();
            }

        });

    }
    private void drawImages(){
        layout.removeAllViews();
        try {
            uris.clear();
            FileInputStream inputStream = openFileInput(albumName+".txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            while (line != null) {
                uris.add(Uri.parse(line));
                line = reader.readLine();
                Log.i("pls", line);
            }
        }catch(Exception e){

        }
        for (int i = 0; i < uris.size(); i++){
            ImageView view = new ImageView(this);
            view.setImageURI(uris.get(i));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,0,5);
            view.setLayoutParams(params);
            view.setAdjustViewBounds(true);
            view.setScaleType(ImageView.ScaleType.FIT_START);

            view.setTag(uris.get(i).toString());
            view.setId(View.generateViewId());
            /*view.setOnClickListener(new customListener(uris.get(i)) {
                @Override
                public void onClick(View view) {
                    openWithGallery(uri);
                }
            });*/
            registerForContextMenu(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.showContextMenu();
                }
            });
            layout.addView(view);
        }
    }

    public class customListener implements View.OnClickListener {
        Uri uri;
        public customListener(Uri uri) {
            this.uri = uri;
        }

        @Override
        public void onClick(View v)
        {
            //read your lovely variable
        }

    };


    private void openWithGallery(Uri uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawImages();
    }

    private void searchImages() {
        Intent intent = new Intent();
        //Allows several files
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData()

            if (resultData != null) {
                ClipData uris = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    uris = resultData.getClipData();
                    if (uris == null) {
                        Uri uri = resultData.getData();
                        appendFile(uri);
                    }else {
                        appendFile(uris);
                    }
                }
            }
        }
    }

    private void openHelp() {
        String url = "https://widelongapps.000webhostapp.com/Help/";
        startActivity(new Intent((Intent.ACTION_VIEW), Uri.parse(url)));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(getResources().getString(R.string.actions));
        menu.add(0, v.getId(), 0, getResources().getString(R.string.setwallpaper));
        menu.add(0, v.getId(), 0, getResources().getString(R.string.deletefromalbum));
    }



    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == getResources().getString(R.string.setwallpaper)) {
            Uri uri = Uri.parse(findViewById(item.getItemId()).getTag().toString());
            Toast.makeText(this, getResources().getString(R.string.wallpaperset), Toast.LENGTH_SHORT).show();
            WallpaperManager myWallpaperManager
                    = WallpaperManager.getInstance(getApplicationContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                startActivity(myWallpaperManager.getCropAndSetWallpaperIntent(uri));
            }

        }
        else if (item.getTitle() == getResources().getString(R.string.deletefromalbum)){
            Uri uri = Uri.parse(findViewById(item.getItemId()).getTag().toString());
            layout.removeView(findViewById(item.getItemId()));
            uris.remove(uri);
            writeFile(uris);
            Toast.makeText(this, getResources().getString(R.string.imagedeleted), Toast.LENGTH_SHORT).show();
        }else {
            return false;
        }
        return true;
    }
    public void writeFile(ArrayList<Uri> uris){
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(albumName+".txt", Context.MODE_PRIVATE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                for (int i = 0; i < uris.size(); i++){
                    Uri path = uris.get(i);
                    outputStream.write(path.toString().getBytes());
                    outputStream.write("\n".getBytes());
                    Log.i("Path: " ,path.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void appendFile(ClipData uris){
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(albumName+".txt", Context.MODE_APPEND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                for (int i = 0; i < uris.getItemCount(); i++){
                    Uri path = uris.getItemAt(i).getUri();
                    outputStream.write(path.toString().getBytes());
                    outputStream.write("\n".getBytes());
                    Log.i("Path: " ,path.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void appendFile(Uri uri){
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(albumName+".txt", Context.MODE_APPEND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                outputStream.write(uri.toString().getBytes());
                outputStream.write("\n".getBytes());
                Log.i("Path: " ,uri.toString());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

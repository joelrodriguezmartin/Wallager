package com.widelongapps.wallager;

import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class fragmentalbum extends Fragment {
    private static final int READ_REQUEST_CODE = 42;
    private static final String defaultAlbumName = "imageUris.txt";
    private String filename = "";
    private LinearLayout layout;
    private ArrayList<String> albums;
    Button defaultAlbum;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragmentalbum2, container, false);
        FloatingActionButton addAlbum = view.findViewById(R.id.addAlbumButton);
        addAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchImages();
            }

        });
        defaultAlbum = view.findViewById(R.id.defaultAlbumButton);
        defaultAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeAlbum(defaultAlbumName);
            }
        });
        layout = view.findViewById(R.id.contentFrag);
        albums = new ArrayList<>();
        drawButtons();


       return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        drawButtons();
    }

    private void drawButtons() {
        layout.removeAllViews();
        layout.addView(defaultAlbum);
        defaultAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeAlbum(defaultAlbumName);
            }
        });
        albums.clear();
        albums = ((MainActivity)getActivity()).getAlbums();

        for (int i = 0; i < albums.size(); i++){
            Button view = new Button(getActivity());
            view.setText(albums.get(i));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,0,5);
            view.setLayoutParams(params);
            view.setBackground(defaultAlbum.getBackground());
            view.setTag(albums.get(i));
            view.setId(View.generateViewId());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seeAlbum(v.getTag().toString());
                }
            });
            registerForContextMenu(view);
            layout.addView(view);
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(getResources().getString(R.string.actions));
        menu.add(0, v.getId(), 0, getResources().getString(R.string.deletealbum));
        menu.add(0, v.getId(), 0, getResources().getString(R.string.renamealbum));
    }



    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == getResources().getString(R.string.deletealbum)){
            String filename = getView().findViewById(item.getItemId()).getTag().toString();
            layout.removeView(getView().findViewById(item.getItemId()));
            albums.remove(filename);
            ((MainActivity)getActivity()).writeAlbums(albums);
            Toast.makeText(getActivity(), getResources().getString(R.string.albumdeleted), Toast.LENGTH_SHORT).show();
        }else if(item.getTitle() == getResources().getString(R.string.renamealbum)){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.titlealbum);
            final int i = item.getItemId();
            final String oldName = getView().findViewById(item.getItemId()).getTag().toString();
            // Set up the input
            final EditText input = new EditText(getActivity());
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newName = input.getText().toString();
                    setNewName(i, newName, oldName);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
        else {
            return false;
        }
        return true;
    }
    void setNewName(int i, String newName, String oldName){
            layout.removeView(getView().findViewById(i));
            albums.indexOf(oldName);
            albums.set(albums.indexOf(oldName), newName);
            ((MainActivity)getActivity()).writeAlbums(albums);
            ((MainActivity)getActivity()).reWriteFile(newName, oldName);
            drawButtons();
    }

    private void seeAlbum(String albumName) {
        Intent intent = new Intent(getActivity(), albumActivity.class);
        Bundle b = new Bundle();
        b.putString("key", albumName);
        intent.putExtras(b);
        startActivity(intent);
    }

    private void searchImages() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.titlealbum);

        // Set up the input
        final EditText input = new EditText(getActivity());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filename = input.getText().toString();
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
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    uris = resultData.getClipData();
                    if (uris == null){
                         Uri uri = resultData.getData();
                         ((MainActivity) getActivity()).writeFile(uri, filename);
                    }else {
                        ((MainActivity) getActivity()).writeFile(uris, filename);
                    }
                }
            }
        }
    }


}

package com.hfad.photostory;

import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.app.ActionBar;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.EditText;
import android.widget.Toast;
import android.content.ContentValues;

import java.io.File;
import java.io.FileOutputStream;

public class CreateEditActivity extends Activity {

    private String storyName = "";
    public static final String STORY_ID = "storyId";
    public static final String STORY_EDIT = "storyEdit";
    private int storyId;
    private EditText storyTitle;
    private EditText storySynopsis;
    private SQLiteOpenHelper photoStoryDatabaseHelper;
    private Toast toast;
    private SQLiteDatabase db;
    private boolean isStoryEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.create_screen);
        //initialization of conponents
        storyTitle = (EditText)findViewById(R.id.title_text);
        storySynopsis = (EditText)findViewById(R.id.synopsis_text);
        photoStoryDatabaseHelper = new PhotoStoryDatabaseHelper(this);
        //get the intents
        isStoryEdit = getIntent().getExtras().getInt(STORY_EDIT) == 1 ? true : false;
        if(isStoryEdit){
            storyId = getIntent().getExtras().getInt(STORY_ID);
            new DisplayInfo().execute(storyId);
        }
    }

    private class DisplayInfo extends AsyncTask<Integer, Void, Boolean>{
        String title;
        String synopsis;
        @Override
        protected void onPreExecute(){}
        @Override
        protected Boolean doInBackground(Integer... story){
            try{
                db = photoStoryDatabaseHelper.getReadableDatabase();
                Cursor cursor = db.query("STORY_DETAILS",
                        new String[]{"NAME","SYNOPSIS"},
                        "_id = ?",
                        new String[]{Integer.toString(storyId)},
                        null,null,null);
                if(cursor.moveToFirst()){
                    title = cursor.getString(0);
                    synopsis = cursor.getString(1);
                }
                cursor.close();
                db.close();
                return true;
            }catch(SQLException ex){
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean success){
            if(success){
                storyTitle.setText(title);
                storySynopsis.setText(synopsis);
            }
            else{
                toast = Toast.makeText(CreateEditActivity.this, "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public void onAddPhotosClick(View view){
        if(saveData()){
            Intent intent = new Intent(this, AddPhotosActivity.class);
            startActivity(intent);
        }
    }

    public void onSaveClick(View view){
        if(saveData()){
            Toast toast = Toast.makeText(this, "Story saved", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public boolean saveData(){
        String titleText = storyTitle.getText().toString();
        storyName = titleText;
        if(titleText.length() == 0){
            toast = Toast.makeText(this, "Please give title to story", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        else{
            try{
                db = photoStoryDatabaseHelper.getWritableDatabase();
                String synopsisText = storySynopsis.getText().toString();
                ContentValues story = new ContentValues();
                story.put("NAME", titleText);
                story.put("SYNOPSIS", synopsisText);
                if(isStoryEdit){
                    db.update("STORY_DETAILS", story, "_id = ?", new String[]{Integer.toString(storyId)});
                }
                else{
                    db.insert("STORY_DETAILS", null, story);
                }
                db.close();
                //createFolder();
                if(isExternalStorageWritable()){
                    getAlbumStorageDir(storyName);
                }
                return true;
            }
            catch(SQLiteException ex){
                toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
        }
    }

    //to create internal storage
    private void createFolder(){
        String filename = "myfile";
        String string = "Hello world!";
        FileOutputStream outputStream;
        try{
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    //to check external storage availability - this is phone memory and not the sd card
    private boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }

    //to create external directory
    public File getAlbumStorageDir(String albumName){
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        if(!file.mkdirs()){
            Log.e("Log", "Directory not created");
        }
        return file;
    }
}

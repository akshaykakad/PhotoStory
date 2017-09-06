package com.hfad.photostory;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.app.ActionBar;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.EditText;
import android.widget.Toast;
import android.content.ContentValues;
import android.text.TextUtils;

public class CreateEditActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.create_screen);
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
        Toast toast;
        EditText storyTitle = (EditText)findViewById(R.id.title_text);
        String titleText = storyTitle.getText().toString();
        if(titleText.length() == 0){
            toast = Toast.makeText(this, "Please give title to story", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }else{
            SQLiteOpenHelper photoStoryDatabaseHelper = new PhotoStoryDatabaseHelper(this);
            try{
                SQLiteDatabase db = photoStoryDatabaseHelper.getWritableDatabase();
                EditText storySynopsis = (EditText)findViewById(R.id.synopsis_text);
                String synopsisText = storySynopsis.getText().toString();
                ContentValues story = new ContentValues();
                story.put("NAME", titleText);
                story.put("SYNOPSIS", synopsisText);
                db.insert("STORY_DETAILS", null, story);
                return true;
            }catch(SQLiteException ex){
                toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
        }

    }
}

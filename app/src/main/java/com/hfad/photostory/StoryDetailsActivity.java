package com.hfad.photostory;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.app.ActionBar;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class StoryDetailsActivity extends Activity {

    public static final String EXTRA_STORYID = "storyId";
    private SQLiteDatabase db;
    private int storyId;
    private CheckBox favoriteCheckBox;
    private SQLiteOpenHelper photoStoryDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_details);
        storyId = (Integer)getIntent().getExtras().get(EXTRA_STORYID);
        new GetStoryDetails().execute(storyId);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.story_details));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_create:
                Intent intent = new Intent(this, CreateEditActivity.class);
                intent.putExtra(CreateEditActivity.STORY_EDIT, 0);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class GetStoryDetails extends AsyncTask<Integer, Void, Boolean>{

        TextView nameTextView;

        protected void onPreExecute(){
            nameTextView = (TextView)findViewById(R.id.story_name);
            favoriteCheckBox = (CheckBox)findViewById(R.id.favorite);
        }
        protected Boolean doInBackground(Integer... stories){
            int storyNo = stories[0];
            photoStoryDatabaseHelper = new PhotoStoryDatabaseHelper(StoryDetailsActivity.this);
            try{
                db = photoStoryDatabaseHelper.getReadableDatabase();
                Cursor cursor = db.query("STORY_DETAILS",
                        new String[]{"NAME", "FAVORITE"},
                        "_id = ?",
                        new String[]{Integer.toString(storyNo)},
                        null, null, null);
                if(cursor.moveToFirst()){
                    String name = cursor.getString(0);
                    boolean isFavorite = (cursor.getInt(1) == 1);
                    nameTextView.setText(name);
                    favoriteCheckBox.setChecked(isFavorite);
                }
                cursor.close();
                db.close();
                return true;
            }catch(SQLiteException ex){
                return false;
            }
        }
        protected void onPostExecute(Boolean success){
            if(!success){
                Toast toast = Toast.makeText(StoryDetailsActivity.this, "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public void onFavoriteClick(View view){
        new UpdateStoryTask().execute(storyId);
    }

    private class UpdateStoryTask extends AsyncTask<Integer, Void, Boolean>{
        ContentValues storyValues;

        protected void onPreExecute(){
            storyValues = new ContentValues();
            storyValues.put("FAVORITE", favoriteCheckBox.isChecked());
        }
        protected Boolean doInBackground(Integer... stories){
            int storyNo = stories[0];
            try{
                db = photoStoryDatabaseHelper.getWritableDatabase();
                db.update("STORY_DETAILS", storyValues, "_id = ?", new String[]{Integer.toString(storyNo)});
                db.close();
                return true;
            }catch(SQLiteException ex){
                return false;
            }
        }
        protected void onPostExecute(Boolean success){
            if(!success){
                Toast toast = Toast.makeText(StoryDetailsActivity.this, "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public void onDeleteClick(View view){
        new DeleteRecord().execute();
    }

    private class DeleteRecord extends AsyncTask<Integer, Void, Boolean>{
        @Override
        protected void onPreExecute(){

        }
        @Override
        protected Boolean doInBackground(Integer... story){
            try{
                photoStoryDatabaseHelper = new PhotoStoryDatabaseHelper(StoryDetailsActivity.this);
                db = photoStoryDatabaseHelper.getWritableDatabase();
                db.delete("STORY_DETAILS","_id = ?", new String[]{Integer.toString(storyId)});
                return true;
            }catch(SQLException ex){
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean success){
            Toast toast;
            if(!success){
                toast = Toast.makeText(StoryDetailsActivity.this, "Database not available", Toast.LENGTH_SHORT);
                toast.show();
            }
            else{
                toast = Toast.makeText(StoryDetailsActivity.this, "Record Deleted", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(StoryDetailsActivity.this, ViewAllActivity.class);
                startActivity(intent);
            }
        }
    }

    public void onEditClick(View view){
        Intent intent = new Intent(StoryDetailsActivity.this, CreateEditActivity.class);
        intent.putExtra(CreateEditActivity.STORY_ID, storyId);
        intent.putExtra(CreateEditActivity.STORY_EDIT, 1);
        startActivity(intent);
    }
}

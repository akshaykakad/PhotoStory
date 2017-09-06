package com.hfad.photostory;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.app.ActionBar;
import android.os.AsyncTask;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.ListView;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

public class MainActivity extends Activity {

    private SQLiteDatabase db;
    private Cursor favoriteCursor;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new GetFavoritesTask().execute();
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));
        //event listener for the list of favorite stories
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> listView, View itemView, int position, long id){
                Intent intent = new Intent(MainActivity.this, StoryDetailsActivity.class);
                intent.putExtra(StoryDetailsActivity.EXTRA_STORYID, (int)id);
                startActivity(intent);
            }
        };
        listView = (ListView)findViewById(R.id.favorite_list);
        listView.setOnItemClickListener(itemClickListener);
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
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickViewAll(View view){
        Intent intent = new Intent(this, ViewAllActivity.class);
        startActivity(intent);
    }

    private class GetFavoritesTask extends AsyncTask<Integer, Void, Boolean>{
        protected void onPreExecute(){

        }
        protected Boolean doInBackground(Integer... stories){
            SQLiteOpenHelper photoStoryDatabaseHelper = new PhotoStoryDatabaseHelper(MainActivity.this);
            try{
                db = photoStoryDatabaseHelper.getReadableDatabase();
                favoriteCursor = db.query("STORY_DETAILS",
                        new String[] {"_id, NAME"},
                        "FAVORITE = ?",
                        new String[]{"1"},
                        null, null, null);
                CursorAdapter listAdapter = new SimpleCursorAdapter(MainActivity.this,
                        android.R.layout.simple_list_item_1,
                        favoriteCursor,
                        new String[]{"NAME"},
                        new int[]{android.R.id.text1},
                        0);
                listView = (ListView)findViewById(R.id.favorite_list);
                listView.setAdapter(listAdapter);
                return true;
            }catch(SQLiteException ex){
                return false;
            }
        }
        protected void onPostExecute(Boolean success){
            if(!success){
                Toast toast = Toast.makeText(MainActivity.this, "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    //no use
    private class GetFavoriteStoriesTask extends AsyncTask<Integer, Void, Boolean>{
        protected void onPreExecute(){
            listView = (ListView)findViewById(R.id.favorite_list);
        }
        protected Boolean doInBackground(Integer... stories){
            SQLiteOpenHelper photoStoryDatabaseHelper = new PhotoStoryDatabaseHelper(MainActivity.this);
            try{
                db = photoStoryDatabaseHelper.getReadableDatabase();
                Cursor newCursor = db.query("STORY_DETAILS",
                        new String[] {"_id, NAME"},
                        "FAVORITE = ?",
                        new String[]{"1"},
                        null, null, null);
                CursorAdapter adapter = (CursorAdapter) listView.getAdapter();
                adapter.changeCursor(newCursor);
                favoriteCursor = newCursor;
                return true;
            }catch(SQLiteException ex){
                return false;
            }
        }
        protected void onPostExecute(Boolean success){
            if(!success){
                Toast toast = Toast.makeText(MainActivity.this, "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void onRestart(){
        super.onRestart();
        //new GetFavoriteStoriesTask().execute();
        SQLiteOpenHelper photoStoryDatabaseHelper = new PhotoStoryDatabaseHelper(MainActivity.this);
        try{
            db = photoStoryDatabaseHelper.getReadableDatabase();
            Cursor newCursor = db.query("STORY_DETAILS",
                    new String[] {"_id, NAME"},
                    "FAVORITE = ?",
                    new String[]{"1"},
                    null, null, null);
            listView = (ListView)findViewById(R.id.favorite_list);
            CursorAdapter adapter = (CursorAdapter) listView.getAdapter();
            adapter.changeCursor(newCursor);
            favoriteCursor = newCursor;
        }catch(SQLiteException ex){
            Toast toast = Toast.makeText(MainActivity.this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        favoriteCursor.close();
        db.close();
    }
}

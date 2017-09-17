package com.hfad.photostory;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.app.ActionBar;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.os.AsyncTask;
import android.widget.AdapterView;
import android.widget.Toast;

public class ViewAllActivity extends Activity {

    private SQLiteDatabase db;
    private Cursor cursor;
    private ListView listStories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        new GetStoriesTask().execute();
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.view_all));
        //event listener for items in listView
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> listView,
                                    View itemView,
                                    int position,
                                    long id){
                Intent intent = new Intent(ViewAllActivity.this, StoryDetailsActivity.class);
                intent.putExtra(StoryDetailsActivity.EXTRA_STORYID, (int)id);
                startActivity(intent);
            }
        };
        listStories = (ListView) findViewById(R.id.list_stories);
        listStories.setOnItemClickListener(itemClickListener);
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

    //get stories from database
    private class GetStoriesTask extends AsyncTask<Integer, Void, Boolean>{
        protected void onPreExecute(){

        }
        protected Boolean doInBackground(Integer... stories){
            SQLiteOpenHelper photoStoryDatabaseHelper = new PhotoStoryDatabaseHelper(ViewAllActivity.this);
            try{
                db = photoStoryDatabaseHelper.getReadableDatabase();
                cursor = db.query("STORY_DETAILS", new String[]{"_id", "NAME"}, null, null, null, null, null);
                CursorAdapter listAdapter = new SimpleCursorAdapter(ViewAllActivity.this, android.R.layout.simple_list_item_1, cursor, new String[]{"NAME"}, new int[]{android.R.id.text1}, 0);
                listStories = (ListView) findViewById(R.id.list_stories);
                listStories.setAdapter(listAdapter);
                return true;
            }catch(SQLiteException e){
                return false;
            }

        }
        protected void onPostExecute(Boolean success){
            if(!success){
                Toast toast = Toast.makeText(ViewAllActivity.this, "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }
}

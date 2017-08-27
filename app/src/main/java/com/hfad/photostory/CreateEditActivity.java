package com.hfad.photostory;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

public class CreateEditActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit);
    }

    public void onAddPhotosClick(View view){
        Intent intent = new Intent(this, AddPhotosActivity.class);
        startActivity(intent);
    }
}

package com.hfad.photostory;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

public class AddPhotosActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photos);
    }

    public void onAddClick(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/jpg");
        startActivityForResult(intent, 0);
    }
}

package com.example.firestoredemo.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.firestoredemo.R;

public class ViewProfileActivity extends AppCompatActivity {

    ImageView iv_edit_personal,iv_edit_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        iv_edit_personal = findViewById(R.id.iv_edit_personal);
        iv_edit_address = findViewById(R.id.iv_edit_address);

        iv_edit_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =  new Intent(ViewProfileActivity.this,MainActivity.class);

                startActivity(intent);
            }
        });
    }
}

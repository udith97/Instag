package com.hit.instag;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class AddPostActivity extends AppCompatActivity {

    private Button mAddPostBtn;
    private EditText mCaptionText;
    private ImageView mPostImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mAddPostBtn = findViewById(R.id.save_post_btn);
        mCaptionText = findViewById(R.id.caption_text);
        mPostImage = findViewById(R.id.post_image);
    }
}
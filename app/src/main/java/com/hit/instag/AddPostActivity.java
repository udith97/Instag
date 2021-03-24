package com.hit.instag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddPostActivity extends AppCompatActivity {

    private EditText mCaption;
    private Button addPostBtn;
    private ImageView postImage;
    private Uri postImageUri = null;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private ProgressBar progressBar;
    private String currentUserId;
    private FirebaseAuth auth;
    private String random = FieldValue.serverTimestamp().toString();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);


        Toolbar mToolbar = findViewById(R.id.addpost_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add Post");
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        progressBar = findViewById(R.id.post_progressbar);
        progressBar.setVisibility(View.INVISIBLE);
        mCaption = findViewById(R.id.caption_text);
        addPostBtn = findViewById(R.id.save_post_btn);
        postImage = findViewById(R.id.post_image);

        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(3,2)
                        .setMinCropResultSize(512,512)
                        .start(AddPostActivity.this);
            }
        });

        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String caption = mCaption.getText().toString();

                if (!caption.isEmpty() && postImageUri !=null){
                    progressBar.setVisibility(View.VISIBLE);

                    StorageReference filePath = storageReference.child("post_images")
                            .child(random + ".jpg");
                    filePath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){

                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Map<String , Object> dataMap = new HashMap<>();
                                        dataMap.put("image" , uri.toString());
                                        dataMap.put("caption" , caption);
                                        dataMap.put("user" , currentUserId);
                                        dataMap.put("time" , FieldValue.serverTimestamp());

                                        firestore.collection("Posts").add(dataMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if (task.isSuccessful()){
                                                    progressBar.setVisibility(View.VISIBLE);
                                                    Toast.makeText(AddPostActivity.this, "Post Added", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(AddPostActivity.this , MainActivity.class));
                                                    finish();
                                                }else{
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(AddPostActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }else{
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(AddPostActivity.this, task.getException().toString() , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(AddPostActivity.this, "Please Select Image and Write Caption ...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){

                postImageUri = result.getUri();
                postImage.setImageURI(postImageUri);

            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Toast.makeText(this, result.getError().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
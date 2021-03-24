package com.hit.instag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hit.instag.Adapter.PostAdapter;
import com.hit.instag.model.Post;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Toolbar mainToolbar;
    private FirebaseFirestore firestore;
    private RecyclerView mRecyclerView;
    private FloatingActionButton fab;
    private PostAdapter adapter;
    private List<Post> list;
    private Query query;
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();

        mRecyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.floatingActionButton);

        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Instag");

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        list = new ArrayList<>();
        adapter = new PostAdapter(list, MainActivity.this);

        mRecyclerView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddPostActivity.class));
            }
        });

        if (firebaseAuth.getCurrentUser() != null){

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean isBottom = !mRecyclerView.canScrollVertically(1);
                    if (isBottom)
                        Toast.makeText(MainActivity.this, "Reached Bottom", Toast.LENGTH_SHORT).show();
                }
            });
            query = firestore.collection("Posts").orderBy("time", Query.Direction.DESCENDING);
            listenerRegistration = query.addSnapshotListener(MainActivity.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for (DocumentChange doc : value.getDocumentChanges()){
                        if (doc.getType() == DocumentChange.Type.ADDED){
                            Post post = doc.getDocument().toObject(Post.class);
                            list.add(post);
                            adapter.notifyDataSetChanged();
                        }else{
                            adapter.notifyDataSetChanged();
                        }
                    }
                    listenerRegistration.remove();
                }
            });
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//        if (currentUser == null){
//            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//            finish();
//        }else{
//            String currentUserId = firebaseAuth.getCurrentUser().getUid();
//            firestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()){
//                        if (task.getResult().exists()){
//                            startActivity(new Intent(MainActivity.this, SetupActivity.class));
//                            finish();
//                        }
//                    }
//                }
//            });
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profile_menu){
            startActivity(new Intent(MainActivity.this, SetupActivity.class));
        }else if (item.getItemId() == R.id.logout_menu){
            firebaseAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        return true;
    }
}
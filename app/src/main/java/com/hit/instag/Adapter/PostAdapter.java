package com.hit.instag.Adapter;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hit.instag.R;
import com.hit.instag.model.Post;


import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> mList;
    private Activity context;
    private FirebaseFirestore firestore;

    public PostAdapter(List<Post> mList, Activity context) {
        this.mList = mList;
        this.context = context;

    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.each_post, parent, false);
        firestore = FirebaseFirestore.getInstance();
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = mList.get(position);
        holder.setPostPic(post.getImage());
        holder.setPostCaption(post.getCaption());
        
        long milliseconds = post.getTime().getTime();
        String date = DateFormat.format("MM/dd/yyyy", new Date(milliseconds)).toString();
        holder.setPostDate(date);

        String userId = post.getUser();
        firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String username = task.getResult().getString("name");
                    String image = task.getResult().getString("image");

                    holder.setProfilePic(image);
                    holder.setPostUserName(username);
                }else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{
        ImageView postPic, commentPic, likePic;
        CircleImageView  profilePic;
        TextView postUserName, postDate, postCaption, postLikes;
        View mView;
        public PostViewHolder(@NonNull View itemView){
            super(itemView);
            mView = itemView;
        }
        public void setPostPic(String urlPost){
            postPic = mView.findViewById(R.id.user_post);
            Glide.with(context).load(urlPost).into(postPic);
        }
        public void setProfilePic(String urlProfile){
            profilePic = mView.findViewById(R.id.profile_pic);
            Glide.with(context).load(urlProfile).into(profilePic);
        }
        public void setPostUserName(String username){
            postUserName = mView.findViewById(R.id.tv_username);
            postUserName.setText(username);
        }
        public void setPostDate(String date){
            postDate = mView.findViewById(R.id.tv_date);
            postDate.setText(date);
        }
        public void setPostCaption (String caption){
            postCaption = mView.findViewById(R.id.tv_caption);
            postCaption.setText(caption);
        }
    }
}

package com.example.limenforum.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.limenforum.R;
import com.example.limenforum.data.model.Post;
import com.example.limenforum.PostDetailActivity;
import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList = new ArrayList<>();

    public void setPosts(List<Post> newPosts) {
        this.postList = newPosts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_card, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.username.setText(post.getUsername());
        holder.title.setText(post.getTitle());
        holder.content.setText(post.getContent());
        holder.timeAgo.setText(post.getTimeAgo());
        holder.likeCount.setText(String.valueOf(post.getLikeCount()));

        // --- 图片显示 (Glide) ---
        if (post.getImageUri() != null && !post.getImageUri().isEmpty()) {
            holder.postImage.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(post.getImageUri())
                    .into(holder.postImage);
        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        // --- 点赞逻辑 ---
        int color = post.isLiked() ? 0xFFFF0000 : 0xFF9CA3AF; // Red vs Gray
        holder.imgLikeIcon.setColorFilter(color);

        holder.likeButton.setOnClickListener(v -> {
            boolean newState = !post.isLiked();
            post.setLiked(newState);

            if (newState) {
                post.setLikeCount(post.getLikeCount() + 1);
            } else {
                post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            }
            notifyItemChanged(position);
        });

        // --- 点击卡片查看详情/评论 ---
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), PostDetailActivity.class);
            intent.putExtra(PostDetailActivity.EXTRA_POST, post);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public TextView username, title, content, timeAgo, likeCount;
        public ImageView postImage, userAvatar, imgLikeIcon;
        public View likeButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tvUserName);
            title = itemView.findViewById(R.id.postTitle);
            content = itemView.findViewById(R.id.tvContent);
            timeAgo = itemView.findViewById(R.id.postTime);
            likeCount = itemView.findViewById(R.id.tvLikeCount);
            postImage = itemView.findViewById(R.id.postImage);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            likeButton = itemView.findViewById(R.id.likeButton);
            imgLikeIcon = itemView.findViewById(R.id.imgLikeIcon);
        }
    }
}

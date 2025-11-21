package com.example.limenforum.ui.profile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.limenforum.LimenApplication;
import com.example.limenforum.LoginActivity;
import com.example.limenforum.R;
import com.example.limenforum.data.model.Post;
import com.example.limenforum.data.model.User;
import com.example.limenforum.data.service.PostService;
import com.example.limenforum.ui.adapter.PostAdapter;

import java.util.List;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PostAdapter adapter;
    private PostService postService;
    private TextView tvName, tvId, tvPostCount, tvLikeCount;
    private ImageView avatar;
    
    private View tabMyPosts, tabLikedPosts;
    private TextView tvTabMyPosts, tvTabLikedPosts;
    private View indicatorMyPosts, indicatorLikedPosts;
    
    private int currentTab = 0; // 0: My Posts, 1: Liked Posts

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        postService = LimenApplication.getInstance().getPostService();

        // Bind Views
        recyclerView = view.findViewById(R.id.recyclerViewProfile);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshProfile);
        tvName = view.findViewById(R.id.profileName);
        tvId = view.findViewById(R.id.profileId);
        tvPostCount = view.findViewById(R.id.profilePostCount);
        tvLikeCount = view.findViewById(R.id.profileLikeCount);
        avatar = view.findViewById(R.id.profileAvatar);
        
        // Tabs
        tabMyPosts = view.findViewById(R.id.tabMyPosts);
        tabLikedPosts = view.findViewById(R.id.tabLikedPosts);
        tvTabMyPosts = view.findViewById(R.id.tvTabMyPosts);
        tvTabLikedPosts = view.findViewById(R.id.tvTabLikedPosts);
        indicatorMyPosts = view.findViewById(R.id.indicatorMyPosts);
        indicatorLikedPosts = view.findViewById(R.id.indicatorLikedPosts);
        
        // Logout Button
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            User.getInstance().logout();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            if (getActivity() != null) getActivity().finish();
        });

        setupRecyclerView();
        updateUserInfo();
        setupTabs();

        swipeRefreshLayout.setOnRefreshListener(this::fetchData);
        fetchData();
    }
    
    private void setupTabs() {
        tabMyPosts.setOnClickListener(v -> switchTab(0));
        tabLikedPosts.setOnClickListener(v -> switchTab(1));
        switchTab(0); // Default
    }
    
    private void switchTab(int tabIndex) {
        currentTab = tabIndex;
        if (tabIndex == 0) {
            tvTabMyPosts.setTextColor(Color.parseColor("#111827"));
            tvTabMyPosts.setTypeface(null, android.graphics.Typeface.BOLD);
            indicatorMyPosts.setVisibility(View.VISIBLE);
            
            tvTabLikedPosts.setTextColor(Color.parseColor("#6B7280"));
            tvTabLikedPosts.setTypeface(null, android.graphics.Typeface.NORMAL);
            indicatorLikedPosts.setVisibility(View.INVISIBLE);
        } else {
            tvTabMyPosts.setTextColor(Color.parseColor("#6B7280"));
            tvTabMyPosts.setTypeface(null, android.graphics.Typeface.NORMAL);
            indicatorMyPosts.setVisibility(View.INVISIBLE);
            
            tvTabLikedPosts.setTextColor(Color.parseColor("#111827"));
            tvTabLikedPosts.setTypeface(null, android.graphics.Typeface.BOLD);
            indicatorLikedPosts.setVisibility(View.VISIBLE);
        }
        fetchData();
    }

    private void setupRecyclerView() {
        adapter = new PostAdapter();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void updateUserInfo() {
        User user = User.getInstance();
        tvName.setText(user.getUsername());
        tvId.setText("ID: " + Math.abs(user.getUsername().hashCode())); // Mock ID
        tvPostCount.setText(String.valueOf(user.getPostCount()));
        tvLikeCount.setText(String.valueOf(user.getLikeCount()));
        
        if (user.getAvatarUrl() != null) {
            Glide.with(this)
                .load(user.getAvatarUrl())
                .circleCrop()
                .into(avatar);
        }
    }

    private void fetchData() {
        if (currentTab == 0) {
            fetchUserPosts();
        } else {
            fetchLikedPosts();
        }
    }

    private void fetchUserPosts() {
        swipeRefreshLayout.setRefreshing(true);
        postService.getUserPosts(User.getInstance().getUserId(), new PostService.PostCallback() {
            @Override
            public void onSuccess(List<Post> posts) {
                adapter.setPosts(posts);
                updateUserInfo(); 
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String errorMessage) {
                swipeRefreshLayout.setRefreshing(false);
                showToast("加载失败: " + errorMessage);
            }
        });
    }
    
    private void fetchLikedPosts() {
        swipeRefreshLayout.setRefreshing(true);
        postService.getLikedPosts(User.getInstance().getUserId(), new PostService.PostCallback() {
            @Override
            public void onSuccess(List<Post> posts) {
                adapter.setPosts(posts);
                updateUserInfo();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String errorMessage) {
                swipeRefreshLayout.setRefreshing(false);
                showToast("加载失败: " + errorMessage);
            }
        });
    }
    
    private void showToast(String msg) {
        if (getContext() != null) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}
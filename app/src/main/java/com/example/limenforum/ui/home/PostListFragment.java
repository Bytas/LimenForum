package com.example.limenforum.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.limenforum.LimenApplication;
import com.example.limenforum.R;
import com.example.limenforum.data.model.Post;
import com.example.limenforum.data.service.PostService;
import com.example.limenforum.ui.adapter.PostAdapter;

import java.util.ArrayList;
import java.util.List;

public class PostListFragment extends Fragment {

    private static final String ARG_CATEGORY = "category";

    private String category;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PostAdapter adapter;
    private PostService postService;

    public static PostListFragment newInstance(String category) {
        PostListFragment fragment = new PostListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        postService = LimenApplication.getInstance().getPostService();

        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        setupRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(this::fetchPosts);

        fetchPosts();
    }

    private void setupRecyclerView() {
        adapter = new PostAdapter();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void fetchPosts() {
        // In a real app, we might pass the category to the service.
        // For now, we fetch all and filter locally as before.
        swipeRefreshLayout.setRefreshing(true);
        postService.getPosts(new PostService.PostCallback() {
            @Override
            public void onSuccess(List<Post> posts) {
                List<Post> filteredList = filterPosts(posts, category);
                adapter.setPosts(filteredList);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String errorMessage) {
                if (getContext() != null) {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "加载失败: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private List<Post> filterPosts(List<Post> allPosts, String tag) {
        List<Post> filteredList = new ArrayList<>();
        if ("All".equals(tag)) {
            filteredList.addAll(allPosts);
        } else {
            String targetTagChinese = convertTagToChinese(tag);
            for (Post p : allPosts) {
                if (p.getTagName().equals(targetTagChinese)) {
                    filteredList.add(p);
                }
            }
        }
        return filteredList;
    }

    private String convertTagToChinese(String tag) {
        switch (tag) {
            case "Programming": return "编程";
            case "Gaming": return "游戏";
            case "Campus": return "校园";
            case "Tech": return "科技";
            default: return "";
        }
    }

    public void scrollToTop() {
        if (recyclerView != null) recyclerView.smoothScrollToPosition(0);
    }
}

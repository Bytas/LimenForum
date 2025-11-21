package com.example.limenforum.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.limenforum.R;
import com.example.limenforum.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager;
    private LinearLayout channelsContainer;
    private View homeTopAvatar;
    private View homeSearchBar;
    
    // Channel List
    private final String[] channels = {"All", "Programming", "Gaming", "Campus", "Tech"};
    private final String[] channelTitles = {"全部", "编程", "游戏", "校园", "科技"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewPager = view.findViewById(R.id.viewPager);
        channelsContainer = view.findViewById(R.id.channelsContainer);
        homeTopAvatar = view.findViewById(R.id.homeTopAvatar);
        homeSearchBar = view.findViewById(R.id.homeSearchBar);

        setupTopBar();
        setupViewPager();
        setupChannelClicks();
    }

    private void setupTopBar() {
        homeTopAvatar.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToProfile();
            }
        });

        homeSearchBar.setOnClickListener(v -> {
            Toast.makeText(getContext(), "搜索功能开发中...", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupViewPager() {
        // Adapter
        HomePagerAdapter pagerAdapter = new HomePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Sync Page Change -> Tab Selection
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateChannelStyles(position);
                
                // Auto-scroll header if needed (optional optimization for many tabs)
                // For few tabs, horizontal scroll view handles it.
            }
        });
        
        // Initial state
        viewPager.setCurrentItem(0, false);
        updateChannelStyles(0);
    }

    private void setupChannelClicks() {
        int childCount = channelsContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = channelsContainer.getChildAt(i);
            if (view instanceof TextView) {
                int position = i;
                view.setOnClickListener(v -> {
                    viewPager.setCurrentItem(position, true);
                });
            }
        }
    }

    private void updateChannelStyles(int selectedPosition) {
        int count = channelsContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = channelsContainer.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                if (i == selectedPosition) {
                    tv.setTextColor(Color.parseColor("#111827"));
                    tv.setTypeface(null, android.graphics.Typeface.BOLD);
                    tv.setBackgroundResource(R.drawable.bg_channel_underline);
                } else {
                    tv.setTextColor(Color.parseColor("#6B7280"));
                    tv.setTypeface(null, android.graphics.Typeface.NORMAL);
                    tv.setBackgroundResource(0);
                }
            }
        }
    }

    public void scrollToTop() {
        // Scroll current fragment's list to top
        // We need to find the current fragment instance
        // But with ViewPager2 + FragmentStateAdapter, finding fragment by tag is complex.
        // For now, we can simplify: re-set current item or broadcast event.
        // Or just ignore deep linking for this refactor step.
        // A robust way: adapter logic.
        // skipping strictly for this step unless critical.
    }

    // Inner Adapter Class
    private class HomePagerAdapter extends FragmentStateAdapter {
        public HomePagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Map position to category string
            String category = channels[position];
            return PostListFragment.newInstance(category);
        }

        @Override
        public int getItemCount() {
            return channels.length;
        }
    }
}

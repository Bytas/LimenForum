package com.example.limenforum.data.service;

import com.example.limenforum.data.model.Post;
import java.util.ArrayList;
import java.util.List;

public class MockPostService { // Renamed to be a helper, no longer implementing PostService directly for UI usage

    public List<Post> getInitialMockData() {
        List<Post> postList = new ArrayList<>();
        // Generate initial data manually to avoid JSON parsing inside helper
        
        postList.add(new Post("张小明", "Kotlin 入门求助", "求推荐 Kotlin 学习资源！从 Java 转过来有点不习惯。", "编程", "10分钟前", 128, 24));
        postList.add(new Post("李华", "今日食堂红烧肉", "学校食堂今天的红烧肉真不错，推荐大家去二楼。", "校园", "30分钟前", 56, 12));
        postList.add(new Post("Gamer007", "黑神话卡关了", "黑神话悟空太难了，卡在第一关怎么办？有没有攻略？", "游戏", "1小时前", 342, 89));
        
        Post p4 = new Post("TechGuru", "AI 新模型发布", "刚刚发布的 AI 模型参数量惊人，科技发展太快了。", "科技", "2小时前", 210, 45);
        postList.add(p4);
        
        Post p5 = new Post("CodeMaster", "Java 泛型擦除", "Java 中的泛型擦除原理是什么？有大佬解释一下吗？这对性能有什么影响？", "编程", "3小时前", 45, 5);
        postList.add(p5);

        // Image posts
        Post p6 = new Post("摄影爱好者", "雨后的校园", "今天下雨过后的操场，空气特别清新，随手拍了一张。", "校园", "4小时前", 88, 15);
        p6.setImageUri("https://picsum.photos/id/10/400/300");
        postList.add(p6);

        Post p7 = new Post("极客风", "我的新桌面搭建", "终于凑齐了这套外设，RGB 性能提升 100%！大家觉得怎么样？", "科技", "5小时前", 520, 66);
        p7.setImageUri("https://picsum.photos/id/1/400/300");
        postList.add(p7);
        
        return postList;
    }
}
package com.example.limenforum.data.service;

import com.example.limenforum.data.model.Post;
import com.example.limenforum.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class MockPostService {

    // Helper to create users
    public List<User> getInitialMockUsers() {
        List<User> users = new ArrayList<>();

        users.add(new User("101", "张小明", "https://testingbot.com/free-online-tools/random-avatar/200?u=101"));
        users.add(new User("102", "李华", "https://testingbot.com/free-online-tools/random-avatar/200?u=102"));
        users.add(new User("103", "Gamer007", "https://testingbot.com/free-online-tools/random-avatar/200?u=103"));
        users.add(new User("104", "TechGuru", "https://testingbot.com/free-online-tools/random-avatar/200?u=104"));
        users.add(new User("105", "CodeMaster", "https://testingbot.com/free-online-tools/random-avatar/200?u=105"));
        users.add(new User("106", "摄影爱好者", "https://testingbot.com/free-online-tools/random-avatar/200?u=106"));
        users.add(new User("107", "极客风", "https://testingbot.com/free-online-tools/random-avatar/200?u=107"));
        users.add(new User("108", "学习小能手", "https://testingbot.com/free-online-tools/random-avatar/200?u=108"));
        users.add(new User("109", "美食家", "https://testingbot.com/free-online-tools/random-avatar/200?u=109"));
        users.add(new User("110", "旅行者", "https://testingbot.com/free-online-tools/random-avatar/200?u=110"));
        users.add(new User("111", "音乐人", "https://testingbot.com/free-online-tools/random-avatar/200?u=111"));
        users.add(new User("112", "设计师", "https://testingbot.com/free-online-tools/random-avatar/200?u=112"));
        users.add(new User("113", "运动达人", "https://testingbot.com/free-online-tools/random-avatar/200?u=113"));
        users.add(new User("114", "读书人", "https://testingbot.com/free-online-tools/random-avatar/200?u=114"));
        users.add(new User("115", "电影迷", "https://testingbot.com/free-online-tools/random-avatar/200?u=115"));

        return users;
    }

    public List<Post> getInitialMockData() {
        List<Post> postList = new ArrayList<>();
        long now = System.currentTimeMillis();

        // 30 posts with various timestamps and users (101-115)
        postList.add(new Post("101", "Kotlin 入门求助", "求推荐 Kotlin 学习资源！从 Java 转过来有点不习惯。", "编程", now - 10 * 60 * 1000L, 128, 24));
        postList.add(new Post("102", "今日食堂红烧肉", "学校食堂今天的红烧肉真不错，推荐大家去二楼。", "校园", now - 30 * 60 * 1000L, 56, 12));
        postList.add(new Post("103", "黑神话卡关了", "黑神话悟空太难了，卡在第一关怎么办？有没有攻略？", "游戏", now - 60 * 60 * 1000L, 342, 89));
        
        Post p4 = new Post("104", "AI 新模型发布", "刚刚发布的 AI 模型参数量惊人，科技发展太快了。", "科技", now - 2 * 60 * 60 * 1000L, 210, 45);
        postList.add(p4);
        
        Post p5 = new Post("105", "Java 泛型擦除", "Java 中的泛型擦除原理是什么？有大佬解释一下吗？这对性能有什么影响？", "编程", now - 3 * 60 * 60 * 1000L, 45, 5);
        postList.add(p5);

        Post p6 = new Post("106", "雨后的校园", "今天下雨过后的操场，空气特别清新，随手拍了一张。", "校园", now - 4 * 60 * 60 * 1000L, 88, 15);
        p6.setImageUri("https://picsum.photos/id/10/400/300");
        postList.add(p6);

        Post p7 = new Post("107", "我的新桌面搭建", "终于凑齐了这套外设，RGB 性能提升 100%！大家觉得怎么样？", "科技", now - 5 * 60 * 60 * 1000L, 520, 66);
        p7.setImageUri("https://picsum.photos/id/1/400/300");
        postList.add(p7);
        
        postList.add(new Post("101", "Android Studio 快捷键分享", "分享一些常用的 Android Studio 快捷键，提高开发效率！", "编程", now - 6 * 60 * 60 * 1000L, 156, 32));

        Post p9 = new Post("102", "校园樱花开了", "今天路过樱花大道，樱花都开了，太美了！", "校园", now - 8 * 60 * 60 * 1000L, 234, 45);
        p9.setImageUri("https://picsum.photos/id/1015/400/300");
        postList.add(p9);
        
        postList.add(new Post("103", "原神新角色抽到了", "终于抽到了新角色，太开心了！", "游戏", now - 12 * 60 * 60 * 1000L, 189, 28));

        Post p11 = new Post("104", "ChatGPT 使用心得", "最近用 ChatGPT 辅助编程，效率提升了很多，推荐大家试试。", "科技", now - 18 * 60 * 60 * 1000L, 312, 67);
        postList.add(p11);

        postList.add(new Post("105", "Spring Boot 学习笔记", "整理了 Spring Boot 的学习笔记，希望对大家有帮助。", "编程", now - 25 * 60 * 60 * 1000L, 98, 12));

        Post p13 = new Post("106", "夕阳下的图书馆", "傍晚的图书馆特别安静，适合学习。", "校园", now - 2 * 24 * 60 * 60 * 1000L, 145, 23);
        p13.setImageUri("https://picsum.photos/id/1035/400/300");
        postList.add(p13);

        postList.add(new Post("107", "Python 数据分析入门", "开始学习 Python 数据分析，记录一下学习过程。", "编程", now - 3 * 24 * 60 * 60 * 1000L, 76, 8));

        Post p15 = new Post("101", "游戏推荐：独立游戏合集", "推荐几款好玩的独立游戏，都是精品！", "游戏", now - 4 * 24 * 60 * 60 * 1000L, 201, 34);
        postList.add(p15);

        Post p16 = new Post("102", "机器学习入门资源", "整理了一些机器学习入门资源，适合初学者。", "科技", now - 5 * 24 * 60 * 60 * 1000L, 167, 29);
        postList.add(p16);

        Post p17 = new Post("103", "校园生活分享", "分享一下最近的校园生活，每天都很充实。", "校园", now - 7 * 24 * 60 * 60 * 1000L, 89, 15);
        p17.setImageUri("https://picsum.photos/id/1041/400/300");
        postList.add(p17);
        
        // Additional posts to reach 30 total
        postList.add(new Post("108", "今天的学习计划", "制定了今天的学习计划，希望能完成！", "校园", now - 15 * 60 * 1000L, 67, 8));
        
        Post p19 = new Post("109", "推荐一家好吃的餐厅", "学校附近新开了一家餐厅，味道不错，价格也实惠。", "校园", now - 45 * 60 * 1000L, 123, 19);
        p19.setImageUri("https://picsum.photos/id/292/400/300");
        postList.add(p19);
        
        postList.add(new Post("110", "旅行照片分享", "上周末去了一趟周边城市，拍了很多照片。", "校园", now - 90 * 60 * 1000L, 198, 31));
        
        Post p21 = new Post("111", "音乐推荐", "最近发现了几首好听的歌，分享给大家。", "校园", now - 3 * 60 * 60 * 1000L, 145, 22);
        postList.add(p21);
        
        postList.add(new Post("112", "UI设计灵感", "收集了一些优秀的UI设计案例，对做项目很有帮助。", "编程", now - 7 * 60 * 60 * 1000L, 234, 45));
        
        Post p23 = new Post("113", "晨跑打卡", "今天早上6点起来跑步，感觉整个人都精神了！", "校园", now - 9 * 60 * 60 * 1000L, 89, 12);
        p23.setImageUri("https://picsum.photos/id/1005/400/300");
        postList.add(p23);
        
        postList.add(new Post("114", "读书笔记：算法导论", "正在读算法导论，记录一些重要的知识点。", "编程", now - 11 * 60 * 60 * 1000L, 112, 18));
        
        Post p25 = new Post("115", "电影推荐", "刚看完一部好电影，强烈推荐大家去看！", "校园", now - 13 * 60 * 60 * 1000L, 167, 28);
        postList.add(p25);
        
        postList.add(new Post("108", "React Native 开发体验", "最近在学习 React Native，分享一下开发体验。", "编程", now - 15 * 60 * 60 * 1000L, 98, 15));
        
        Post p27 = new Post("109", "美食探店", "今天去了一家网红餐厅，环境不错，菜品也很有特色。", "校园", now - 20 * 60 * 60 * 1000L, 201, 34);
        p27.setImageUri("https://picsum.photos/id/225/400/300");
        postList.add(p27);
        
        postList.add(new Post("110", "旅行攻略分享", "整理了一份详细的旅行攻略，希望对大家有帮助。", "校园", now - 30 * 60 * 60 * 1000L, 156, 27));
        
        Post p29 = new Post("111", "音乐制作心得", "最近在学习音乐制作，分享一些心得体会。", "校园", now - 36 * 60 * 60 * 1000L, 134, 21);
        postList.add(p29);
        
        Post p30 = new Post("112", "设计工具推荐", "推荐几个好用的设计工具，提高工作效率。", "编程", now - 48 * 60 * 60 * 1000L, 189, 33);
        postList.add(p30);

        return postList;
    }
}
package com.example.limenforum.data.service;

import android.os.Handler;
import android.os.Looper;
import com.example.limenforum.data.model.Post;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MockPostService implements PostService {

    @Override
    public void getPosts(PostCallback callback) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                String jsonResponse = getMockJsonData();
                List<Post> posts = parsePosts(jsonResponse);
                callback.onSuccess(posts);
            } catch (JSONException e) {
                callback.onFailure("JSON Parsing Error: " + e.getMessage());
            }
        }, 1000);
    }

    @Override
    public void getUserPosts(String username, PostCallback callback) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                String jsonResponse = getMockJsonData();
                List<Post> allPosts = parsePosts(jsonResponse);
                List<Post> userPosts = new ArrayList<>();
                
                // Basic filter logic
                // If username matches, add to list. 
                // For mock demo, let's just pick some or return mock user posts if "username" matches current user
                // Since "User" model stores username, and we pass it here.
                
                // Let's create a few "My Posts" specifically if none exist in the big list
                // Or simply filter by a hardcoded name if the passed username matches our logged in user.
                
                // Add some specific posts for "My Profile" view
                userPosts.add(new Post(username, "我的第一个帖子", "这是我在 LimenForum 发布的第一条内容！", "生活", "1天前", 5, 2));
                userPosts.add(new Post(username, "测试图片上传", "发一张风景照试试看。", "摄影", "2天前", 10, 5));
                userPosts.get(1).setImageUri("https://picsum.photos/id/100/400/300");
                userPosts.add(new Post(username, "今天心情不错", "代码终于跑通了，没有 Bug 的一天。", "编程", "3天前", 88, 12));

                callback.onSuccess(userPosts);
            } catch (JSONException e) {
                callback.onFailure("JSON Parsing Error: " + e.getMessage());
            }
        }, 800);
    }

    private List<Post> parsePosts(String jsonString) throws JSONException {
        List<Post> postList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonString);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            String username = obj.getString("username");
            String title = obj.getString("title");
            String content = obj.getString("content");
            String tagName = obj.getString("tagName");
            String timeAgo = obj.getString("timeAgo");
            int likeCount = obj.getInt("likeCount");
            int commentCount = obj.getInt("commentCount");
            
            Post post = new Post(username, title, content, tagName, timeAgo, likeCount, commentCount);
            
            if (obj.has("imageUri")) {
                post.setImageUri(obj.getString("imageUri"));
            }
            postList.add(post);
        }
        return postList;
    }

    private String getMockJsonData() {
        return "[" +
            "{" +
                "\"username\": \"张小明\"," +
                "\"title\": \"Kotlin 入门求助\"," +
                "\"content\": \"求推荐 Kotlin 学习资源！从 Java 转过来有点不习惯。\"," +
                "\"tagName\": \"编程\"," +
                "\"timeAgo\": \"10分钟前\"," +
                "\"likeCount\": 128," +
                "\"commentCount\": 24" +
            "}," +
            "{" +
                "\"username\": \"李华\"," +
                "\"title\": \"今日食堂红烧肉\"," +
                "\"content\": \"学校食堂今天的红烧肉真不错，推荐大家去二楼。\"," +
                "\"tagName\": \"校园\"," +
                "\"timeAgo\": \"30分钟前\"," +
                "\"likeCount\": 56," +
                "\"commentCount\": 12" +
            "}," +
            "{" +
                "\"username\": \"Gamer007\"," +
                "\"title\": \"黑神话卡关了\"," +
                "\"content\": \"黑神话悟空太难了，卡在第一关怎么办？有没有攻略？\"," +
                "\"tagName\": \"游戏\"," +
                "\"timeAgo\": \"1小时前\"," +
                "\"likeCount\": 342," +
                "\"commentCount\": 89" +
            "}," +
            "{" +
                "\"username\": \"TechGuru\"," +
                "\"title\": \"AI 新模型发布\"," +
                "\"content\": \"刚刚发布的 AI 模型参数量惊人，科技发展太快了。\"," +
                "\"tagName\": \"科技\"," +
                "\"timeAgo\": \"2小时前\"," +
                "\"likeCount\": 210," +
                "\"commentCount\": 45" +
            "}," +
            "{" +
                "\"username\": \"CodeMaster\"," +
                "\"title\": \"Java 泛型擦除\"," +
                "\"content\": \"Java 中的泛型擦除原理是什么？有大佬解释一下吗？这对性能有什么影响？\"," +
                "\"tagName\": \"编程\"," +
                "\"timeAgo\": \"3小时前\"," +
                "\"likeCount\": 45," +
                "\"commentCount\": 5" +
            "}," +
            "{" +
                "\"username\": \"摄影爱好者\"," +
                "\"title\": \"雨后的校园\"," +
                "\"content\": \"今天下雨过后的操场，空气特别清新，随手拍了一张。\"," +
                "\"tagName\": \"校园\"," +
                "\"timeAgo\": \"4小时前\"," +
                "\"likeCount\": 88," +
                "\"commentCount\": 15," +
                "\"imageUri\": \"https://picsum.photos/id/10/400/300\"" +
            "}," +
            "{" +
                "\"username\": \"极客风\"," +
                "\"title\": \"我的新桌面搭建\"," +
                "\"content\": \"终于凑齐了这套外设，RGB 性能提升 100%！大家觉得怎么样？\"," +
                "\"tagName\": \"科技\"," +
                "\"timeAgo\": \"5小时前\"," +
                "\"likeCount\": 520," +
                "\"commentCount\": 66," +
                "\"imageUri\": \"https://picsum.photos/id/1/400/300\"" +
            "}," +
            "{" +
                "\"username\": \"旅行家\"," +
                "\"title\": \"远方的山\"," +
                "\"content\": \"在这个周末，逃离城市，去寻找内心的宁静。\"," +
                "\"tagName\": \"校园\"," +
                "\"timeAgo\": \"6小时前\"," +
                "\"likeCount\": 230," +
                "\"commentCount\": 30," +
                "\"imageUri\": \"https://picsum.photos/id/29/400/500\"" +
            "}," +
            "{" +
                "\"username\": \"GameBoy\"," +
                "\"title\": \"怀旧游戏时光\"," +
                "\"content\": \"翻出了以前的掌机，满满的回忆杀啊。\"," +
                "\"tagName\": \"游戏\"," +
                "\"timeAgo\": \"7小时前\"," +
                "\"likeCount\": 150," +
                "\"commentCount\": 42," +
                "\"imageUri\": \"https://picsum.photos/id/96/400/300\"" +
            "}," +
            "{" +
                "\"username\": \"DevOps\"," +
                "\"title\": \"服务器集群维护\"," +
                "\"content\": \"半夜起来修 Bug，运维人员的辛酸谁懂？\"," +
                "\"tagName\": \"科技\"," +
                "\"timeAgo\": \"8小时前\"," +
                "\"likeCount\": 99," +
                "\"commentCount\": 28," +
                "\"imageUri\": \"https://picsum.photos/id/60/400/300\"" +
            "}," +
            "{" +
                "\"username\": \"AndroidDev\"," +
                "\"title\": \"Jetpack Compose 真香\"," +
                "\"content\": \"用了 Compose 之后，再也不想写 XML 布局了，开发效率极高！\"," +
                "\"tagName\": \"编程\"," +
                "\"timeAgo\": \"9小时前\"," +
                "\"likeCount\": 180," +
                "\"commentCount\": 55," +
                "\"imageUri\": \"https://picsum.photos/id/180/400/400\"" +
            "}," +
            "{" +
                "\"username\": \"校园小记者\"," +
                "\"title\": \"运动会精彩瞬间\"," +
                "\"content\": \"抓拍到了百米冲刺的瞬间，太燃了！\"," +
                "\"tagName\": \"校园\"," +
                "\"timeAgo\": \"10小时前\"," +
                "\"likeCount\": 310," +
                "\"commentCount\": 22," +
                "\"imageUri\": \"https://picsum.photos/id/158/400/300\"" +
            "}," +
            "{" +
                "\"username\": \"HardwareFan\"," +
                "\"title\": \"显卡终于降价了\"," +
                "\"content\": \"等等党的大胜利！准备入手 4070。\"," +
                "\"tagName\": \"科技\"," +
                "\"timeAgo\": \"11小时前\"," +
                "\"likeCount\": 400," +
                "\"commentCount\": 102," +
                "\"imageUri\": \"https://picsum.photos/id/250/400/300\"" +
            "}," +
            "{" +
                "\"username\": \"IndieDev\"," +
                "\"title\": \"独立游戏开发日志 #1\"," +
                "\"content\": \"今天完成了角色的基本移动逻辑，虽然还有 Bug，但很有成就感。\"," +
                "\"tagName\": \"游戏\"," +
                "\"timeAgo\": \"12小时前\"," +
                "\"likeCount\": 75," +
                "\"commentCount\": 18," +
                "\"imageUri\": \"https://picsum.photos/id/119/400/300\"" +
            "}," +
            "{" +
                "\"username\": \"FullStack\"," +
                "\"title\": \"Node.js 性能优化\"," +
                "\"content\": \"分享几个 Node.js 处理高并发的小技巧，建议收藏。\"," +
                "\"tagName\": \"编程\"," +
                "\"timeAgo\": \"13小时前\"," +
                "\"likeCount\": 260," +
                "\"commentCount\": 33," +
                "\"imageUri\": \"https://picsum.photos/id/160/400/300\"" +
            "}" +
        "]";
    }
}

package com.example.limenforum.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    
    /**
     * 将时间戳转换为相对时间文本
     * @param timestamp 发布时间戳（毫秒）
     * @return 格式化后的时间文本
     */
    public static String formatTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        
        // 3分钟内显示"刚刚"
        if (diff < 3 * 60 * 1000) {
            return "刚刚";
        }
        
        // 显示分钟前
        long minutes = diff / (60 * 1000);
        if (minutes < 60) {
            return minutes + "分钟前";
        }
        
        // 显示小时前
        long hours = diff / (60 * 60 * 1000);
        if (hours < 24) {
            return hours + "小时前";
        }
        
        // 显示天数前
        long days = diff / (24 * 60 * 60 * 1000);
        if (days == 1) {
            return "1天前";
        }
        
        // 超过1天显示具体日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}

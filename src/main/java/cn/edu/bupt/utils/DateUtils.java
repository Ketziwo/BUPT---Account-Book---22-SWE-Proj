package cn.edu.bupt.utils;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtils {

    final static String ISO_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /*
     * 私有化构造函数，阻止类实例化。
     * 本类为工具类，所有方法均为静态方法
     */
    private DateUtils() {}

    /**
     * 将 Date 对象转换为符合 ISO 8601 标准的字符串格式
     * 
     * @param date 需要转换的日期对象（支持旧版 java.util.Date 类型）
     * @return ISO 8601 格式的日期字符串，例如 "2025-03-04 15:30:45"
     */
    public static String dateToISO8601(Date date, String pattern) {
        SimpleDateFormat ft = new SimpleDateFormat(pattern);
        return ft.format(date);
    }
    public static String dateToISO8601(Date date) {
        return dateToISO8601(date, ISO_PATTERN);
    }

    

    /*
     * 获取当前时间
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getDatetime() {
        return dateToISO8601(new Date());
    }

    /*
     * 获取当前日期
     * 格式：yyyymmdd
     */
    public static String getDate() {
        return dateToISO8601(new Date(), "yyyyMMdd");
    }

    public static void main(String[] args) {
        System.out.println(dateToISO8601(new Date()));
    }
}
package utils;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtils {

    final static String ISO_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private DateUtils() {}

    /**
     * 将 Date 对象转换为符合 ISO 8601 标准的字符串格式
     * 
     * @param date 需要转换的日期对象（支持旧版 java.util.Date 类型）
     * @return ISO 8601 格式的日期字符串，例如 "2025-03-04 15:30:45"
     */
    public static String dateToISO8601(Date date) {
        SimpleDateFormat ft = new SimpleDateFormat(ISO_PATTERN);
        return ft.format(date);
    }

    public static void main(String[] args) {
        System.out.println(dateToISO8601(new Date()));
    }
}
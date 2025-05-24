package cn.edu.bupt.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

public class DateUtils {

    private final static String DATEPATTERN = "yyyy-MM-dd";
    private final static String DATETIMEPATTERN = "yyyy-MM-dd HH:mm:ss";

    // 线程安全的日期格式容器
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER = 
        ThreadLocal.withInitial(() -> new SimpleDateFormat(DATEPATTERN));
    
    private static final ThreadLocal<SimpleDateFormat> DATETIME_FORMATTER =
        ThreadLocal.withInitial(() -> new SimpleDateFormat(DATETIMEPATTERN));

    /**
     * 获取当前日期字符串（线程安全）
     * @return yyyy-MM-dd 格式的日期
     */
    public static String getDate() {
        return DATE_FORMATTER.get().format(new Date());
    }

    /**
     * 获取当前完整日期时间（线程安全）
     * @return yyyy-MM-dd HH:mm:ss 格式的时间
     */
    public static String getDatetime() {
        return getDatetime(new Date());
    }
    public static String getDatetime(Date d) {
        return DATETIME_FORMATTER.get().format(d);
    }

    /**
     * 验证日期字符串有效性
     * @param dateStr 需要验证的字符串
     */
    public static boolean isValidDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATEPATTERN);
            sdf.setLenient(false); // 严格模式
            sdf.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static boolean isValidDatetime(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATETIMEPATTERN);
            sdf.setLenient(false); // 严格模式
            sdf.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 日期/日期时间比较
     * @param date1 yyyy-MM-dd 格式日期 yyyy-MM-dd HH:mm:ss 格式时间
     * @param date2 yyyy-MM-dd 格式日期 yyyy-MM-dd HH:mm:ss 格式时间
     * @return 比较结果：-1,0,1
     */
    public static int compareDates(String date1, String date2) {
        try {
            Date d1 = DATE_FORMATTER.get().parse(date1);
            Date d2 = DATE_FORMATTER.get().parse(date2);
            return d1.compareTo(d2);
        } catch (Exception e) {
            throw new IllegalArgumentException("日期格式错误");
        }
    }
    public static int compareDateTimes(String datetime1, String datetime2) {
        try {
            Date d1 = DATETIME_FORMATTER.get().parse(datetime1);
            Date d2 = DATETIME_FORMATTER.get().parse(datetime2);
            return d1.compareTo(d2);
        } catch (Exception e) {
            throw new IllegalArgumentException("日期时间格式错误", e);
        }
    }

    /**
     * 判断目标日期是否在起止日期范围内（包含边界）
     * @param target 要判断的目标日期（yyyy-MM-dd）
     * @param start 起始日期（yyyy-MM-dd）
     * @param end 结束日期（yyyy-MM-dd）
     * @return 是否在范围内（包含等于的情况）
     */
    public static boolean isDateInRange(String target, String start, String end) {
        // 验证所有日期有效性
        if (!isValidDate(target) 
            || !isValidDate(start) 
            || !isValidDate(end)) {
            return false;
        }

        // 自动处理倒序日期范围
        String lower = start;
        String upper = end;
        if (compareDates(start, end) > 0) {
            lower = end;
            upper = start;
        }

        // 比较日期范围
        return compareDates(target, lower) >= 0 && 
               compareDates(target, upper) <= 0;
    }
    public static boolean isDateTimeInRange(String target, String start, String end) {
        // 验证所有日期时间有效性
        if (!isValidDatetime(target) 
            || !isValidDatetime(start) 
            || !isValidDatetime(end)) {
            return false;
        }

        // 自动处理倒序时间范围
        String lower = start;
        String upper = end;
        if (compareDateTimes(start, end) > 0) {
            lower = end;
            upper = start;
        }

        // 比较时间范围
        return compareDateTimes(target, lower) >= 0 && 
            compareDateTimes(target, upper) <= 0;
    }

    public static JSpinner createDatetimeSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, DATETIMEPATTERN);
        spinner.setEditor(editor);
        spinner.setValue(new Date());
        return spinner;
    }
    
    public static void setJSpinner(JSpinner js, String datetime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATETIMEPATTERN);
            Date targetDate = sdf.parse(datetime);
            js.setValue(targetDate);
        }catch (Exception e) {
            e.printStackTrace();
        }        
    }
}
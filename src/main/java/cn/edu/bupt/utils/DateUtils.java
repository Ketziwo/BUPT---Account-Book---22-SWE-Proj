package cn.edu.bupt.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

/**
 * Utility class for date and time operations in the account book system.
 * This class provides thread-safe methods for formatting, validating, and comparing
 * dates and timestamps. It supports both date-only (yyyy-MM-dd) and date-time
 * (yyyy-MM-dd HH:mm:ss) formats.
 * 
 * The class also provides methods for checking if dates fall within specific ranges,
 * which is useful for filtering transactions and validating budget periods.
 * 
 * All date formatting operations use thread-local formatters to ensure thread safety.
 * 
 * @author BUPT Account Book Team
 * @version 1.0
 */
public class DateUtils {

    /** Date format pattern (yyyy-MM-dd) */
    private final static String DATEPATTERN = "yyyy-MM-dd";
    
    /** Date-time format pattern (yyyy-MM-dd HH:mm:ss) */
    private final static String DATETIMEPATTERN = "yyyy-MM-dd HH:mm:ss";

    // Thread-safe date formatter containers
    /** Thread-safe formatter for date-only strings */
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER = ThreadLocal.withInitial(() -> new SimpleDateFormat(DATEPATTERN));
    
    /** Thread-safe formatter for date-time strings */
    private static final ThreadLocal<SimpleDateFormat> DATETIME_FORMATTER = ThreadLocal.withInitial(() -> new SimpleDateFormat(DATETIMEPATTERN));
      /**
     * Gets the current date as a formatted string.
     * This method is thread-safe.
     * 
     * @return The current date in yyyy-MM-dd format
     */
    public static String getDate() {
        return DATE_FORMATTER.get().format(new Date());
    }    
    
    /**
     * Gets the current date and time as a formatted string.
     * This method is thread-safe.
     * 
     * @return The current date and time in yyyy-MM-dd HH:mm:ss format
     */
    public static String getDatetime() {
        return getDatetime(new Date());
    }
    
    /**
     * Formats a Date object as a date-time string.
     * This method is thread-safe.
     * 
     * @param d The Date object to format
     * @return The formatted date and time in yyyy-MM-dd HH:mm:ss format
     */
    public static String getDatetime(Date d) {
        return DATETIME_FORMATTER.get().format(d);
    }
      /**
     * Validates if a string represents a valid date in yyyy-MM-dd format.
     * This method uses strict validation rules.
     * 
     * @param dateStr The date string to validate
     * @return true if the string is a valid date, false otherwise
     */
    public static boolean isValidDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATEPATTERN);
            sdf.setLenient(false); // Strict mode
            sdf.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Validates if a string represents a valid date-time in yyyy-MM-dd HH:mm:ss format.
     * This method uses strict validation rules.
     * 
     * @param dateStr The date-time string to validate
     * @return true if the string is a valid date-time, false otherwise
     */    /**
     * Validates whether a string represents a valid date-time in the system's format.
     * This method uses strict validation and will reject dates like February 30.
     * 
     * @param dateStr The date-time string to validate in yyyy-MM-dd HH:mm:ss format
     * @return true if the string is a valid date-time, false otherwise
     */
    public static boolean isValidDatetime(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATETIMEPATTERN);
            sdf.setLenient(false); // Strict mode
            sdf.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }    /**
     * Compares two date strings in yyyy-MM-dd format.
     * 
     * @param date1 First date in yyyy-MM-dd format
     * @param date2 Second date in yyyy-MM-dd format
     * @return -1 if date1 is before date2, 0 if equal, 1 if date1 is after date2
     * @throws IllegalArgumentException If either date string is not in the correct format
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
    
    /**
     * Compares two date-time strings in yyyy-MM-dd HH:mm:ss format.
     * 
     * @param datetime1 First date-time in yyyy-MM-dd HH:mm:ss format
     * @param datetime2 Second date-time in yyyy-MM-dd HH:mm:ss format
     * @return -1 if datetime1 is before datetime2, 0 if equal, 1 if datetime1 is after datetime2
     * @throws IllegalArgumentException If either date-time string is not in the correct format
     */
    public static int compareDateTimes(String datetime1, String datetime2) {
        try {
            Date d1 = DATETIME_FORMATTER.get().parse(datetime1);
            Date d2 = DATETIME_FORMATTER.get().parse(datetime2);
            return d1.compareTo(d2);
        } catch (Exception e) {
            throw new IllegalArgumentException("日期时间格式错误", e);
        }
    }    /**
     * Checks if a target date is within a specified date range.
     * This method handles automatically reversing the range if start is after end.
     * 
     * @param target The date to check in yyyy-MM-dd format
     * @param start The start date of the range in yyyy-MM-dd format
     * @param end The end date of the range in yyyy-MM-dd format
     * @return true if the target date is within the range (inclusive), false otherwise
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
    }    /**
     * Checks if a target date-time is within a specified date-time range.
     * This method handles automatically reversing the range if start is after end.
     * 
     * @param target The date-time to check in yyyy-MM-dd HH:mm:ss format
     * @param start The start date-time of the range in yyyy-MM-dd HH:mm:ss format
     * @param end The end date-time of the range in yyyy-MM-dd HH:mm:ss format
     * @return true if the target date-time is within the range (inclusive), false otherwise
     */
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
    }    /**
     * Creates a JSpinner component configured for date-time selection.
     * The spinner is initialized with the current date and time and formatted
     * according to the system's date-time pattern.
     * 
     * @return A configured JSpinner for date-time selection
     */
    public static JSpinner createDatetimeSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, DATETIMEPATTERN);
        spinner.setEditor(editor);
        spinner.setValue(new Date());
        return spinner;
    }
    
    /**
     * Sets the value of a JSpinner to a specific date-time.
     * 
     * @param js The JSpinner to update
     * @param datetime The date-time string in yyyy-MM-dd HH:mm:ss format
     */
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
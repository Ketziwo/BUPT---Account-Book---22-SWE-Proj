package cn.edu.bupt.utils;

import static org.junit.Assert.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import javax.swing.JSpinner;

/**
 * DateUtils类的测试类
 */
public class DateUtilsTest {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private String validDate;
    private String invalidDate;
    private String validDateTime;
    private String invalidDateTime;

    @Before
    public void setUp() {
        // 准备测试数据
        validDate = "2025-05-24";
        invalidDate = "2025-05-32"; // 不存在的日期
        validDateTime = "2025-05-24 10:30:45";
        invalidDateTime = "2025-05-24 25:30:45"; // 小时超过24
    }

    /**
     * 测试获取当前日期字符串
     */
    @Test
    public void testGetDate() {
        String date = DateUtils.getDate();
        // 验证格式是否正确
        assertTrue("日期格式应该符合yyyy-MM-dd", DateUtils.isValidDate(date));
        
        // 验证是否是当前日期
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String expectedDate = sdf.format(new Date());
        assertEquals("获取的日期应该是当前日期", expectedDate, date);
    }

    /**
     * 测试获取当前时间字符串
     */
    @Test
    public void testGetDatetime() {
        String datetime = DateUtils.getDatetime();
        // 验证格式是否正确
        assertTrue("日期时间格式应该符合yyyy-MM-dd HH:mm:ss", DateUtils.isValidDatetime(datetime));
        
        // 验证和当前时间的差异不应超过3秒（考虑到测试执行时间）
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
            Date parsedDate = sdf.parse(datetime);
            Date currentDate = new Date();
            long difference = Math.abs(currentDate.getTime() - parsedDate.getTime());
            assertTrue("获取的时间应该接近当前时间", difference < 3000); // 差异不超过3秒
        } catch (Exception e) {
            fail("解析日期时发生异常: " + e.getMessage());
        }
    }

    /**
     * 测试带参数的getDatetime方法
     */
    @Test
    public void testGetDatetimeWithParameter() {
        Date testDate = new Date();
        String datetime = DateUtils.getDatetime(testDate);
        
        // 验证格式是否正确
        assertTrue("日期时间格式应该符合yyyy-MM-dd HH:mm:ss", DateUtils.isValidDatetime(datetime));
        
        // 验证是否是给定的日期
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
            String expectedDateTime = sdf.format(testDate);
            assertEquals("获取的日期时间应该匹配给定的Date对象", expectedDateTime, datetime);
        } catch (Exception e) {
            fail("解析日期时发生异常: " + e.getMessage());
        }
    }

    /**
     * 测试日期格式验证方法 - 有效日期
     */
    @Test
    public void testIsValidDateWithValidDate() {
        assertTrue("有效日期应该通过验证", DateUtils.isValidDate(validDate));
    }

    /**
     * 测试日期格式验证方法 - 无效日期
     */
    @Test
    public void testIsValidDateWithInvalidDate() {
        assertFalse("无效日期应该不通过验证", DateUtils.isValidDate(invalidDate));
        assertFalse("空日期应该不通过验证", DateUtils.isValidDate(""));
        assertFalse("格式错误的日期应该不通过验证", DateUtils.isValidDate("2025/05/24"));
    }

    /**
     * 测试日期时间格式验证方法 - 有效日期时间
     */
    @Test
    public void testIsValidDatetimeWithValidDateTime() {
        assertTrue("有效日期时间应该通过验证", DateUtils.isValidDatetime(validDateTime));
    }

    /**
     * 测试日期时间格式验证方法 - 无效日期时间
     */
    @Test
    public void testIsValidDatetimeWithInvalidDateTime() {
        assertFalse("无效日期时间应该不通过验证", DateUtils.isValidDatetime(invalidDateTime));
        assertFalse("空日期时间应该不通过验证", DateUtils.isValidDatetime(""));
        assertFalse("格式错误的日期时间应该不通过验证", DateUtils.isValidDatetime("2025/05/24 10:30:45"));
    }

    /**
     * 测试日期比较方法
     */
    @Test
    public void testCompareDates() {
        // 日期相等的情况
        assertEquals("相同的日期比较应该返回0", 0, DateUtils.compareDates(validDate, validDate));
        
        // 第一个日期早于第二个日期
        assertEquals("较早的日期比较应该返回-1", -1, DateUtils.compareDates("2025-05-23", validDate));
        
        // 第一个日期晚于第二个日期
        assertEquals("较晚的日期比较应该返回1", 1, DateUtils.compareDates("2025-05-25", validDate));
        
        // 测试异常情况
        try {
            DateUtils.compareDates("invalid-date", validDate);
            fail("无效日期应该抛出异常");
        } catch (IllegalArgumentException e) {
            // 预期异常
            assertEquals("异常消息应该正确", "日期格式错误", e.getMessage());
        }
    }

    /**
     * 测试日期时间比较方法
     */
    @Test
    public void testCompareDateTimes() {
        // 日期时间相等的情况
        assertEquals("相同的日期时间比较应该返回0", 0, DateUtils.compareDateTimes(validDateTime, validDateTime));
        
        // 第一个日期时间早于第二个日期时间
        assertEquals("较早的日期时间比较应该返回-1", -1, 
                DateUtils.compareDateTimes("2025-05-24 10:29:45", validDateTime));
        
        // 第一个日期时间晚于第二个日期时间
        assertEquals("较晚的日期时间比较应该返回1", 1, 
                DateUtils.compareDateTimes("2025-05-24 10:31:45", validDateTime));
        
        // 测试异常情况
        try {
            DateUtils.compareDateTimes("invalid-datetime", validDateTime);
            fail("无效日期时间应该抛出异常");
        } catch (IllegalArgumentException e) {
            // 预期异常
            assertEquals("异常消息应该正确", "日期时间格式错误", e.getMessage());
        }
    }

    /**
     * 测试日期范围检查方法
     */
    @Test
    public void testIsDateInRange() {
        String start = "2025-05-20";
        String end = "2025-05-30";
        
        // 日期在范围内
        assertTrue("日期在范围内应该返回true", 
                DateUtils.isDateInRange(validDate, start, end));
        
        // 日期等于起始日期
        assertTrue("日期等于起始日期应该返回true", 
                DateUtils.isDateInRange(start, start, end));
        
        // 日期等于结束日期
        assertTrue("日期等于结束日期应该返回true", 
                DateUtils.isDateInRange(end, start, end));
        
        // 日期在范围外
        assertFalse("日期在范围外应该返回false", 
                DateUtils.isDateInRange("2025-06-01", start, end));
        
        // 无效日期
        assertFalse("无效日期应该返回false", 
                DateUtils.isDateInRange(invalidDate, start, end));
        
        // 无效起始日期
        assertFalse("无效起始日期应该返回false", 
                DateUtils.isDateInRange(validDate, invalidDate, end));
        
        // 无效结束日期
        assertFalse("无效结束日期应该返回false", 
                DateUtils.isDateInRange(validDate, start, invalidDate));
        
        // 倒序日期范围（自动修正）
        assertTrue("倒序日期范围应该自动修正并返回true", 
                DateUtils.isDateInRange(validDate, end, start));
    }

    /**
     * 测试日期时间范围检查方法
     */
    @Test
    public void testIsDateTimeInRange() {
        String start = "2025-05-24 09:00:00";
        String end = "2025-05-24 11:00:00";
        
        // 日期时间在范围内
        assertTrue("日期时间在范围内应该返回true", 
                DateUtils.isDateTimeInRange(validDateTime, start, end));
        
        // 日期时间等于起始日期时间
        assertTrue("日期时间等于起始日期时间应该返回true", 
                DateUtils.isDateTimeInRange(start, start, end));
        
        // 日期时间等于结束日期时间
        assertTrue("日期时间等于结束日期时间应该返回true",
                DateUtils.isDateTimeInRange(end, start, end));
        
        // 日期时间在范围外
        assertFalse("日期时间在范围外应该返回false", 
                DateUtils.isDateTimeInRange("2025-05-24 12:00:00", start, end));
        
        // 无效日期时间
        assertFalse("无效日期时间应该返回false", 
                DateUtils.isDateTimeInRange(invalidDateTime, start, end));
        
        // 无效起始日期时间
        assertFalse("无效起始日期时间应该返回false", 
                DateUtils.isDateTimeInRange(validDateTime, invalidDateTime, end));
        
        // 无效结束日期时间
        assertFalse("无效结束日期时间应该返回false", 
                DateUtils.isDateTimeInRange(validDateTime, start, invalidDateTime));
        
        // 倒序日期时间范围（自动修正）
        assertTrue("倒序日期时间范围应该自动修正并返回true", 
                DateUtils.isDateTimeInRange(validDateTime, end, start));
    }

    /**
     * 测试创建日期时间选择控件方法
     * 注意：这个测试在没有UI环境的情况下可能会失败
     * 如果测试环境不支持，可以注释掉此方法
     */
    @Test
    public void testCreateDatetimeSpinner() {
        try {
            JSpinner spinner = DateUtils.createDatetimeSpinner();
            
            // 验证spinner不为null
            assertNotNull("创建的日期时间选择器不应为空", spinner);
            
            // 验证当前值不为null
            assertNotNull("日期时间选择器的值不应为空", spinner.getValue());
            
            // 验证值是Date类型
            assertTrue("日期时间选择器的值应该是Date类型", spinner.getValue() instanceof Date);
        } catch (Exception e) {
            // 在无头模式下可能会抛出异常，这里忽略
            System.out.println("测试环境不支持UI组件: " + e.getMessage());
        }
    }
}

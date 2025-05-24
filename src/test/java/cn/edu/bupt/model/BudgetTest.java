package cn.edu.bupt.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Budget类的测试类
 */
public class BudgetTest {

    private TransactionManager tm;
    private User testUser;
    private List<String> testTags;
    private String validStartDateTime;
    private String validEndDateTime;
    private int validAmount;
    private String description;    private Budget testBudget;
    
    // 保存原始的数据，测试结束后恢复
    private HashSet<Budget> originalBudgets;
    private HashSet<User> originalUsers;@Before
    public void setUp() {
        try {
            // 获取TransactionManager实例
            tm = TransactionManager.getInstance();
            
            // 保存原始数据
            originalBudgets = new HashSet<>(tm.Budgets);
            originalUsers = new HashSet<>(tm.Users);
            
            // 创建测试用户
            testUser = new User("testUser" + System.currentTimeMillis(), "password");
            
            // 设置有效的测试数据
            validAmount = 10000; // 100元
            testTags = new ArrayList<>();
            testTags.add("饮食");  // 假设这是一个有效标签
            validStartDateTime = "2025-01-01 00:00:00";
            validEndDateTime = "2025-12-31 23:59:59";
            description = "测试预算";
            
            // 创建一个测试预算对象
            testBudget = new Budget(testUser, validAmount, testTags, validStartDateTime, validEndDateTime, description);
        } catch (Exception e) {
            e.printStackTrace();
            fail("setUp failed: " + e.getMessage());
        }
    }    @After
    public void tearDown() {
        // 清理测试过程中创建的预算
        HashSet<Budget> budgetsToRemove = new HashSet<>(tm.Budgets);
        budgetsToRemove.removeAll(originalBudgets);
        for (Budget budget : budgetsToRemove) {
            budget.remove();
        }
        
        // 清理测试过程中创建的用户
        HashSet<User> usersToRemove = new HashSet<>(tm.Users);
        usersToRemove.removeAll(originalUsers);
        for (User user : usersToRemove) {
            tm.Users.remove(user);
        }
    }

    @Test
    public void testConstructor() {
        // 创建预算对象
        Budget budget = new Budget(testUser, validAmount, testTags, validStartDateTime, validEndDateTime, description);
        
        // 验证属性是否正确设置
        assertEquals(testUser, budget.getUser());
        assertEquals(validAmount, budget.getAmount());
        assertEquals(validStartDateTime, budget.getStartDateTime());
        assertEquals(validEndDateTime, budget.getEndDateTime());
        assertEquals(description, budget.getDescription());
        
        // 验证预算已添加到用户的预算集合中
        assertTrue(testUser.getBudgets().contains(budget));
        
        // 验证预算已添加到TransactionManager的预算集合中
        assertTrue(tm.Budgets.contains(budget));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullUser() {
        new Budget(null, validAmount, testTags, validStartDateTime, validEndDateTime, description);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNegativeAmount() {
        new Budget(testUser, -100, testTags, validStartDateTime, validEndDateTime, description);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithZeroAmount() {
        new Budget(testUser, 0, testTags, validStartDateTime, validEndDateTime, description);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithInvalidStartDate() {
        new Budget(testUser, validAmount, testTags, "invalid-date", validEndDateTime, description);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithInvalidEndDate() {
        new Budget(testUser, validAmount, testTags, validStartDateTime, "invalid-date", description);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithStartDateAfterEndDate() {
        new Budget(testUser, validAmount, testTags, "2025-12-31 23:59:59", "2025-01-01 00:00:00", description);
    }
    
    @Test
    public void testSetUser() {
        User newUser = new User("newTestUser" + System.currentTimeMillis(), "password");
        testBudget.setUser(newUser);
        assertEquals(newUser, testBudget.getUser());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetUserWithNull() {
        testBudget.setUser(null);
    }
    
    @Test
    public void testSetAmount() {
        int newAmount = 20000; // 200元
        testBudget.setAmount(newAmount);
        assertEquals(newAmount, testBudget.getAmount());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetAmountWithNegativeValue() {
        testBudget.setAmount(-100);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetAmountWithZero() {
        testBudget.setAmount(0);
    }
    
    @Test
    public void testGetTags() {
        Set<Tag> tags = testBudget.getTags();
        assertNotNull(tags);
        assertEquals(testTags.size(), tags.size());
    }
    
    @Test
    public void testSetStartDateTime() {
        String newStartDateTime = "2025-02-01 00:00:00";
        testBudget.setStartDateTime(newStartDateTime);
        assertEquals(newStartDateTime, testBudget.getStartDateTime());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetStartDateTimeWithInvalidFormat() {
        testBudget.setStartDateTime("invalid-datetime");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetStartDateTimeAfterEndDateTime() {
        testBudget.setStartDateTime("2026-01-01 00:00:00");
    }
    
    @Test
    public void testSetEndDateTime() {
        String newEndDateTime = "2025-11-30 23:59:59";
        testBudget.setEndDateTime(newEndDateTime);
        assertEquals(newEndDateTime, testBudget.getEndDateTime());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetEndDateTimeWithInvalidFormat() {
        testBudget.setEndDateTime("invalid-datetime");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetEndDateTimeBeforeStartDateTime() {
        testBudget.setEndDateTime("2024-12-31 23:59:59");
    }
    
    @Test
    public void testSetDescription() {
        String newDescription = "新的预算描述";
        testBudget.setDescription(newDescription);
        assertEquals(newDescription, testBudget.getDescription());
    }
    
    @Test
    public void testRemove() {
        // 确保预算存在于集合中
        assertTrue(tm.Budgets.contains(testBudget));
        
        // 移除预算
        testBudget.remove();
        
        // 验证预算已从集合中移除
        assertFalse(tm.Budgets.contains(testBudget));
    }
}

package cn.edu.bupt.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

/**
 * User类的测试类
 */
public class UserTest {

    private TransactionManager tm;
    private User testUser;
    private String testUserName;
    private String testUserPwd;
    
    // 保存原始的用户集合，测试结束后恢复
    private HashSet<User> originalUsers;
    
    @Before
    public void setUp() {
        try {
            // 获取当前的 TransactionManager 实例
            tm = TransactionManager.getInstance();
            
            // 保存原始的用户集合
            originalUsers = new HashSet<>(tm.Users);
            
            // 生成测试用户名和密码，确保唯一性
            testUserName = "TestUser_" + System.currentTimeMillis();
            testUserPwd = "TestPassword123";
            
            // 创建测试用户
            testUser = new User(testUserName, testUserPwd);
        } catch (Exception e) {
            e.printStackTrace();
            fail("设置测试环境失败: " + e.getMessage());
        }
    }
    
    @After
    public void tearDown() {
        try {
            // 恢复原始用户集合
            tm.Users.clear();
            tm.Users.addAll(originalUsers);
        } catch (Exception e) {
            e.printStackTrace();
            fail("清理测试环境失败: " + e.getMessage());
        }
    }
    
    @Test
    public void testUserCreation() {
        // 验证用户是否成功创建
        assertNotNull("用户创建失败", testUser);
        assertEquals("用户名设置不正确", testUserName, testUser.getname());
        assertEquals("密码设置不正确", testUserPwd, testUser.getPwd());
        
        // 验证TransactionPATH和BudgetPATH是否正确设置
        assertEquals("交易路径设置不正确", "data/Transaction_" + testUserName + ".csv", testUser.getTransactionPATH());
        assertEquals("预算路径设置不正确", "data/Budget_" + testUserName + ".csv", testUser.getBudgetPATH());
        
        // 验证用户是否已添加到TransactionManager的Users集合中
        assertTrue("用户未添加到TransactionManager", tm.Users.contains(testUser));
    }
    
    @Test
    public void testDefaultUser() {
        // 验证默认用户的属性
        assertNotNull("默认用户不存在", User.defaultUser);
        assertEquals("默认用户名不正确", "default", User.defaultUser.getname());
        assertEquals("默认用户密码不正确", "88888888", User.defaultUser.getPwd());
        assertEquals("默认用户交易路径不正确", "data/Transaction_default.csv", User.defaultUser.getTransactionPATH());
        assertEquals("默认用户预算路径不正确", "data/Budget_default.csv", User.defaultUser.getBudgetPATH());
    }
    
    @Test
    public void testGetTransactions() {
        // 初始状态下交易集合应为空
        Set<Transaction> transactions = testUser.getTransactions();
        assertNotNull("交易集合为null", transactions);
        assertTrue("初始交易集合不为空", transactions.isEmpty());
        
        // 添加一个交易到用户
        ArrayList<String> tags = new ArrayList<>();
        tags.add("测试");
        Transaction transaction = new Transaction(testUser, 1000, "2025-05-24 12:00:00", "测试交易", tags);
        testUser.getTransactions().add(transaction);
        
        // 验证交易已添加到集合中
        assertEquals("交易未成功添加到用户", 1, testUser.getTransactions().size());
        assertTrue("用户交易集合中找不到测试交易", testUser.getTransactions().contains(transaction));
    }
      @Test
    public void testGetBudgets() {
        // 初始状态下预算集合应为空
        Set<Budget> budgets = testUser.getBudgets();
        assertNotNull("预算集合为null", budgets);
        assertTrue("初始预算集合不为空", budgets.isEmpty());
        
        // 添加一个预算到用户
        ArrayList<String> tags = new ArrayList<>();
        tags.add("测试");
        Budget budget = new Budget(testUser, 5000, tags, "2025-05-01 00:00:00", "2025-05-31 23:59:59", "测试预算");
        
        // 验证预算已添加到集合中
        assertEquals("预算未成功添加到用户", 1, testUser.getBudgets().size());
        assertTrue("用户预算集合中找不到测试预算", testUser.getBudgets().contains(budget));
    }
    
    @Test
    public void testMultipleUsers() {
        // 创建另一个用户
        String anotherUserName = "AnotherTestUser_" + System.currentTimeMillis();
        User anotherUser = new User(anotherUserName, "AnotherPassword");
        
        // 验证两个用户是不同的实例
        assertNotEquals("两个用户实例应该不同", testUser, anotherUser);
        assertNotEquals("两个用户的名称应该不同", testUser.getname(), anotherUser.getname());
        
        // 验证两个用户的文件路径不同
        assertNotEquals("两个用户的交易路径应该不同", testUser.getTransactionPATH(), anotherUser.getTransactionPATH());
        assertNotEquals("两个用户的预算路径应该不同", testUser.getBudgetPATH(), anotherUser.getBudgetPATH());
        
        // 验证TransactionManager中包含了两个用户
        assertTrue("TransactionManager中找不到第一个测试用户", tm.Users.contains(testUser));
        assertTrue("TransactionManager中找不到第二个测试用户", tm.Users.contains(anotherUser));
    }
}

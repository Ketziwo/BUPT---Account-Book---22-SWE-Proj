package cn.edu.bupt.utils;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import cn.edu.bupt.model.Tag;
import cn.edu.bupt.model.Transaction;
import cn.edu.bupt.model.User;
import java.util.HashSet;
import java.util.Set;

/**
 * TransactionTypeUtils类的测试类
 */
public class TransactionTypeUtilsTest {

    private Transaction transaction;
    private User testUser;    @Before
    public void setUp() {
        // 创建测试用户，包含用户名和密码
        testUser = new User("test_user", "password");
        
        // 创建测试交易，使用当前时间作为交易时间
        transaction = new Transaction("", testUser, 1000, DateUtils.getDatetime(), "", "", "测试交易");
    }

    /**
     * 测试设置交易为支出
     */
    @Test
    public void testSetIOcomeToExpense() {
        // 执行设置为支出的操作
        TransactionTypeUtils.setIOcome(transaction, Tag.EXPENSE);
        
        // 验证交易标签包含支出标签
        assertTrue("交易应该包含支出标签", transaction.getTags().contains(Tag.EXPENSE));
        
        // 验证交易不包含收入和未知标签
        assertFalse("交易不应包含收入标签", transaction.getTags().contains(Tag.INCOME));
        assertFalse("交易不应包含未知标签", transaction.getTags().contains(Tag.UNKNOWN));
        
        // 验证交易包含默认支出类别标签
        assertTrue("交易应该包含默认支出类别标签", transaction.getTags().contains(Tag.EXPENSE_OTHERS));
    }

    /**
     * 测试设置交易为收入
     */
    @Test
    public void testSetIOcomeToIncome() {
        // 执行设置为收入的操作
        TransactionTypeUtils.setIOcome(transaction, Tag.INCOME);
        
        // 验证交易标签包含收入标签
        assertTrue("交易应该包含收入标签", transaction.getTags().contains(Tag.INCOME));
        
        // 验证交易不包含支出和未知标签
        assertFalse("交易不应包含支出标签", transaction.getTags().contains(Tag.EXPENSE));
        assertFalse("交易不应包含未知标签", transaction.getTags().contains(Tag.UNKNOWN));
        
        // 验证交易包含默认收入类别标签
        assertTrue("交易应该包含默认收入类别标签", transaction.getTags().contains(Tag.INCOME_OTHERS));
    }

    /**
     * 测试设置交易为未知
     */
    @Test
    public void testSetIOcomeToUnknown() {
        // 执行设置为未知的操作
        TransactionTypeUtils.setIOcome(transaction, Tag.UNKNOWN);
        
        // 验证交易标签包含未知标签
        assertTrue("交易应该包含未知标签", transaction.getTags().contains(Tag.UNKNOWN));
        
        // 验证交易不包含支出和收入标签
        assertFalse("交易不应包含支出标签", transaction.getTags().contains(Tag.EXPENSE));
        assertFalse("交易不应包含收入标签", transaction.getTags().contains(Tag.INCOME));
    }

    /**
     * 测试重复设置交易类型
     */
    @Test
    public void testSetIOcomeWithSameTag() {
        // 首先设置为支出
        TransactionTypeUtils.setIOcome(transaction, Tag.EXPENSE);
        
        // 再次设置为支出
        TransactionTypeUtils.setIOcome(transaction, Tag.EXPENSE);
        
        // 验证交易标签包含支出标签
        assertTrue("交易应该包含支出标签", transaction.getTags().contains(Tag.EXPENSE));
        
        // 确保只有一个支出标签
        int expenseTagCount = 0;
        for (Tag tag : transaction.getTags()) {
            if (tag == Tag.EXPENSE) {
                expenseTagCount++;
            }
        }
        assertEquals("交易应该只包含一个支出标签", 1, expenseTagCount);
    }

    /**
     * 测试设置无效的标签
     */
    @Test
    public void testSetIOcomeWithInvalidTag() {
        // 使用一个非收入支出类型的标签
        Tag invalidTag = Tag.EXPENSE_FOOD; // 这不是收入支出未知三种之一
        
        // 执行操作前先标记当前标签情况
        int initialTagCount = transaction.getTags().size();
        
        // 执行设置操作
        TransactionTypeUtils.setIOcome(transaction, invalidTag);
        
        // 验证交易标签数量未变化
        assertEquals("设置无效标签后，交易标签数量不应改变", initialTagCount, transaction.getTags().size());
    }

    /**
     * 测试设置支出类别
     */
    @Test
    public void testSetCategoryForExpense() {
        // 首先设置为支出
        TransactionTypeUtils.setIOcome(transaction, Tag.EXPENSE);
        
        // 设置支出类别为食品
        TransactionTypeUtils.setCategory(transaction, Tag.EXPENSE_FOOD);
        
        // 验证交易标签包含食品支出标签
        assertTrue("交易应该包含食品支出标签", transaction.getTags().contains(Tag.EXPENSE_FOOD));
        
        // 验证交易不包含其他支出类别标签
        assertFalse("交易不应包含默认支出类别标签", transaction.getTags().contains(Tag.EXPENSE_OTHERS));
    }

    /**
     * 测试设置收入类别
     */
    @Test
    public void testSetCategoryForIncome() {
        // 首先设置为收入
        TransactionTypeUtils.setIOcome(transaction, Tag.INCOME);
        
        // 设置收入类别为工资
        TransactionTypeUtils.setCategory(transaction, Tag.INCOME_SALARY);
        
        // 验证交易标签包含工资收入标签
        assertTrue("交易应该包含工资收入标签", transaction.getTags().contains(Tag.INCOME_SALARY));
        
        // 验证交易不包含其他收入类别标签
        assertFalse("交易不应包含默认收入类别标签", transaction.getTags().contains(Tag.INCOME_OTHERS));
    }

    /**
     * 测试设置不匹配的类别（支出交易设置收入类别）
     */
    @Test
    public void testSetMismatchedCategory() {
        // 首先设置为支出
        TransactionTypeUtils.setIOcome(transaction, Tag.EXPENSE);
        
        // 保存当前标签情况
        Set<Tag> initialTags = new HashSet<>(transaction.getTags());
        
        // 尝试设置收入类别
        TransactionTypeUtils.setCategory(transaction, Tag.INCOME_SALARY);
        
        // 验证交易标签没有改变
        assertEquals("交易标签不应该改变", initialTags, transaction.getTags());
    }

    /**
     * 测试改变交易类型后重设类别
     */
    @Test
    public void testChangeIOcomeAndCategory() {
        // 首先设置为支出并设置支出类别
        TransactionTypeUtils.setIOcome(transaction, Tag.EXPENSE);
        TransactionTypeUtils.setCategory(transaction, Tag.EXPENSE_FOOD);
        
        // 验证交易标签包含食品支出标签
        assertTrue("交易应该包含食品支出标签", transaction.getTags().contains(Tag.EXPENSE_FOOD));
        
        // 将交易类型改为收入
        TransactionTypeUtils.setIOcome(transaction, Tag.INCOME);
        
        // 验证交易不再包含食品支出标签，而是包含默认收入类别标签
        assertFalse("交易不应包含食品支出标签", transaction.getTags().contains(Tag.EXPENSE_FOOD));
        assertTrue("交易应该包含默认收入类别标签", transaction.getTags().contains(Tag.INCOME_OTHERS));
    }
}

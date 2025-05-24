package cn.edu.bupt.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import cn.edu.bupt.utils.DateUtils;

public class TransactionManagerTest {
    private TransactionManager tm;
    private User testUser;
    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;
    private Tag tag1;
    private Tag tag2;
    private Tag tag3;
    private Set<Transaction> testTransactions;

    @Before
    public void setUp() {
        // Get the singleton instance of TransactionManager
        tm = TransactionManager.getInstance();
        
        // Create a test user
        testUser = new User("testUser", "password");
        tm.Users.add(testUser);
        
        // Create test tags
        tag1 = new Tag("食品");
        tag2 = new Tag("娱乐");
        tag3 = new Tag("交通");
        
        tm.Tags.add(tag1);
        tm.Tags.add(tag2);
        tm.Tags.add(tag3);
        
        tm.tagRegistry.put("食品", tag1);
        tm.tagRegistry.put("娱乐", tag2);
        tm.tagRegistry.put("交通", tag3);
          // Create test transactions
        String baseTime = "2025-05-01 10:00:00";
        String currentTime = DateUtils.getDatetime(); // Use current time for created/modified times
        transaction1 = new Transaction("", testUser, 1000, baseTime, currentTime, currentTime, "午餐");
        transaction2 = new Transaction("", testUser, 2000, baseTime, currentTime, currentTime, "电影票");
        transaction3 = new Transaction("", testUser, 500, baseTime, currentTime, currentTime, "地铁");
        
        // Add tags to transactions
        tm.addTagToTA(transaction1, tag1);
        tm.addTagToTA(transaction2, tag2);
        tm.addTagToTA(transaction3, tag3);
        tm.addTagToTA(transaction3, tag1); // 地铁上买的零食
        
        // Add transactions to test set
        testTransactions = new HashSet<>();
        testTransactions.add(transaction1);
        testTransactions.add(transaction2);
        testTransactions.add(transaction3);
        
        // Add to the global transactions set
        tm.Transactions.add(transaction1);
        tm.Transactions.add(transaction2);
        tm.Transactions.add(transaction3);
    }
    
    @After
    public void tearDown() {
        // Clean up test data
        tm.Transactions.remove(transaction1);
        tm.Transactions.remove(transaction2);
        tm.Transactions.remove(transaction3);
        
        tm.Users.remove(testUser);
        
        tm.Tags.remove(tag1);
        tm.Tags.remove(tag2);
        tm.Tags.remove(tag3);
        
        tm.tagRegistry.remove("食品");
        tm.tagRegistry.remove("娱乐");
        tm.tagRegistry.remove("交通");
    }

    @Test
    public void testAddTagToTransaction() {
        // Test adding a tag to a transaction
        Tag newTag = new Tag("测试");
        tm.addTagToTA(transaction1, newTag);
        
        assertTrue("Transaction should contain the new tag", 
                transaction1.getTags().contains(newTag));
    }
    
    @Test
    public void testRemoveTagFromTransaction() {
        // Test removing a tag from a transaction
        tm.removeTagFromTA(transaction1, tag1);
        
        assertFalse("Transaction should not contain the removed tag", 
                transaction1.getTags().contains(tag1));
    }
    
    @Test
    public void testGetTransactionByTag() {
        // Test filtering transactions by a specific tag
        Set<Transaction> result = tm.getTransactionByTag(tag1, testTransactions);
        
        assertEquals("Should find 2 transactions with tag '食品'", 2, result.size());
        assertTrue("Result should contain transaction1", result.contains(transaction1));
        assertTrue("Result should contain transaction3", result.contains(transaction3));
        assertFalse("Result should not contain transaction2", result.contains(transaction2));
    }
    
    @Test
    public void testGetTransactionByTagString() {
        // Test filtering transactions by a tag string
        Set<Transaction> result = tm.getTransactionByTag("食品", testTransactions);
        
        assertEquals("Should find 2 transactions with tag '食品'", 2, result.size());
        assertTrue("Result should contain transaction1", result.contains(transaction1));
        assertTrue("Result should contain transaction3", result.contains(transaction3));
    }
    
    @Test
    public void testGetTransactionsByTagsUnion() {
        // Test filtering transactions with tag union (OR condition)
        Set<Tag> tags = new HashSet<>();
        tags.add(tag1);
        tags.add(tag2);
        
        Set<Transaction> result = tm.getTransactionsByTags(tags, testTransactions, true);
        
        assertEquals("Should find 3 transactions with either tag1 OR tag2", 3, result.size());
        assertTrue("Result should contain transaction1", result.contains(transaction1));
        assertTrue("Result should contain transaction2", result.contains(transaction2));
        assertTrue("Result should contain transaction3", result.contains(transaction3));
    }
    
    @Test
    public void testGetTransactionsByTagsIntersection() {
        // Test filtering transactions with tag intersection (AND condition)
        Set<Tag> tags = new HashSet<>();
        tags.add(tag1);
        tags.add(tag3);
        
        Set<Transaction> result = tm.getTransactionsByTags(tags, testTransactions, false);
        
        assertEquals("Should find 1 transaction with both tag1 AND tag3", 1, result.size());
        assertTrue("Result should contain transaction3", result.contains(transaction3));
    }
    
    @Test
    public void testCalculateAmountWithDateRangeAndTags() {
        // Test calculating the sum of transactions with specific tags and date range
        Set<Tag> tags = new HashSet<>();
        tags.add(tag1);
        
        String startDate = "2025-05-01 00:00:00";
        String endDate = "2025-05-02 00:00:00";
        
        int result = tm.calculateAmount(testTransactions, tags, startDate, endDate);
        
        // transaction1 (1000) + transaction3 (500) = 1500
        assertEquals("Total amount should be 1500", 1500, result);
    }
    
    @Test
    public void testCalculateAmountWithSingleTag() {
        // Test calculating the sum of transactions with a single tag
        String startDate = "2025-05-01 00:00:00";
        String endDate = "2025-05-02 00:00:00";
        
        int result = tm.calculateAmount(testTransactions, tag2, startDate, endDate);
        
        // Only transaction2 (2000) has tag2
        assertEquals("Total amount should be 2000", 2000, result);
    }
    
    @Test
    public void testGetTagsForTransaction() {
        // Test retrieving tags for a transaction
        Set<Tag> tags = tm.getTagsForTransaction(transaction3);
        
        assertEquals("Transaction3 should have 2 tags", 2, tags.size());
        assertTrue("Tags should contain tag1", tags.contains(tag1));
        assertTrue("Tags should contain tag3", tags.contains(tag3));
    }
      @Test
    public void testCalculateAmountWithBudget() {
        // Test calculating amount with a Budget object
        List<String> budgetTagStrings = new ArrayList<>();
        budgetTagStrings.add("食品");
        
        String startDate = "2025-05-01 00:00:00";
        String endDate = "2025-05-02 00:00:00";
        String description = "测试预算";
        
        Budget budget = new Budget(testUser, 2000, budgetTagStrings, startDate, endDate, description);
        
        int result = tm.calculateAmount(budget);
        
        // transaction1 (1000) + transaction3 (500) = 1500
        assertEquals("Total amount for budget should be 1500", 1500, result);
    }
}

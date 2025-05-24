package cn.edu.bupt.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import cn.edu.bupt.utils.DateUtils;

public class TransactionTest {    private TransactionManager tm;
    private User testUser;
    private Transaction transaction;
    private ArrayList<String> tags;
    private Set<User> createdUsers = new HashSet<>();    @Before
    public void setUp() {
        // Get the singleton instance of TransactionManager
        tm = TransactionManager.getInstance();
        // Set current user to ensure consistent testing environment
        tm.currentUser = User.defaultUser;
        
        // Create a test user
        testUser = new User("testUser", "password");
        createdUsers.add(testUser);
        
        // Initialize tags
        tags = new ArrayList<>();
        tags.add("test");
        tags.add("junit");
    }
    
    @After
    public void tearDown() {
        // Clean up any transactions created during tests
        if (transaction != null) {
            transaction.remove();
            transaction = null;
        }
        
        // Clean up created users
        for (User user : createdUsers) {
            tm.Users.remove(user);
        }
        createdUsers.clear();
        
        // Restore currentUser to defaultUser
        tm.currentUser = User.defaultUser;
    }

    @Test
    public void testConstructorWithCustomId() {
        // Test constructor with custom transaction ID
        String customId = "20250524-999";
        String datetime = "2025-05-24 10:30:00";
        String created = "2025-05-24 10:30:00";
        String modified = "2025-05-24 10:30:00";
        
        transaction = new Transaction(customId, testUser, 1000, datetime, created, modified, "Test transaction");
        
        assertEquals("Transaction ID should match custom ID", customId, transaction.getTransaction_id());
        assertEquals("User should match", testUser, transaction.getUser());
        assertEquals("Amount should match", 1000, transaction.getAmount());
        assertEquals("Datetime should match", datetime, transaction.getDatetime());
        assertEquals("Created time should match", created, transaction.getCreateTime());
        assertEquals("Modified time should match", modified, transaction.getModifiedTime());
        assertEquals("Description should match", "Test transaction", transaction.getDescription());    }
    
    @Test
    public void testConstructorWithInvalidId() {
        // Test constructor with invalid transaction ID format
        String invalidId = "invalid-id";
        String datetime = "2025-05-24 10:30:00";
        
        transaction = new Transaction(invalidId, testUser, 1000, datetime, datetime, datetime, "Test with invalid ID");
        
        // Should generate a new ID with correct format (YYYYMMDD-N)
        String currentDate = DateUtils.getDate(); // Get current date in YYYYMMDD format
        assertTrue("Transaction ID should follow pattern " + currentDate + "-N", 
                transaction.getTransaction_id().startsWith(currentDate + "-"));
    }
    
    @Test
    public void testConstructorWithTags() {
        // Test constructor with tags
        transaction = new Transaction(testUser, 2000, "2025-05-24 11:00:00", "Test with tags", tags);
        
        Set<Tag> transactionTags = transaction.getTags();
        assertEquals("Transaction should have 2 tags", 2, transactionTags.size());
        
        boolean foundTestTag = false;
        boolean foundJunitTag = false;
        
        for (Tag tag : transactionTags) {
            if (tag.getName().equals("test")) foundTestTag = true;
            if (tag.getName().equals("junit")) foundJunitTag = true;
        }
        
        assertTrue("Should contain 'test' tag", foundTestTag);
        assertTrue("Should contain 'junit' tag", foundJunitTag);    }
    
    @Test
    public void testDefaultConstructor() {
        // Test default constructor
        transaction = new Transaction();
        
        assertEquals("User should be current user", tm.currentUser, transaction.getUser());
        assertEquals("Amount should be 0", 0, transaction.getAmount());
        assertEquals("Description should be empty", "", transaction.getDescription());
    }
    
    @Test
    public void testAddAndRemoveTag() {
        // Create a transaction
        transaction = new Transaction(testUser, 3000, "2025-05-24 12:00:00", "Test add/remove tags", new ArrayList<>());
        
        // Add tags
        transaction.addTag("food");
        transaction.addTag("expense");
        
        Set<Tag> transactionTags = transaction.getTags();
        assertEquals("Transaction should have 2 tags", 2, transactionTags.size());
        
        // Remove a tag
        transaction.removeTag("food");
        
        transactionTags = transaction.getTags();
        assertEquals("Transaction should have 1 tag after removal", 1, transactionTags.size());
        
        boolean hasExpenseTag = false;
        for (Tag tag : transactionTags) {
            if (tag.getName().equals("expense")) hasExpenseTag = true;
        }
        
        assertTrue("Should still contain 'expense' tag", hasExpenseTag);    }
      @Test
    public void testModifyTransaction() {
        // Create a transaction with the testUser
        transaction = new Transaction("", testUser, 5000, "2025-05-24 14:00:00", "", "", "Original description");
        String originalModifiedTime = transaction.getModifiedTime();
        
        // Verify the transaction is properly set up
        assertEquals("Transaction should be associated with testUser", testUser, transaction.getUser());
        assertTrue("testUser should contain the transaction", testUser.getTransactions().contains(transaction));
          // Create a new user for testing
        User newUser = new User("newUser", "password");
        createdUsers.add(newUser);
        
        // Wait a moment to ensure timestamps differ
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        // Modify transaction
        transaction.setUser(newUser);
        transaction.setAmount(6000);
        transaction.setDescription("Modified description");
        transaction.setDatetime("2025-05-24 15:00:00");
        
        // Check modifications
        assertEquals("User should be updated", newUser, transaction.getUser());
        assertTrue("New user should contain the transaction", newUser.getTransactions().contains(transaction));
        assertFalse("Old user should no longer contain the transaction", testUser.getTransactions().contains(transaction));
        assertEquals("Amount should be updated", 6000, transaction.getAmount());
        assertEquals("Description should be updated", "Modified description", transaction.getDescription());
        assertEquals("Datetime should be updated", "2025-05-24 15:00:00", transaction.getDatetime());
    }
    
    @Test
    public void testInvalidDatetime() {
        // Test with invalid datetime format
        transaction = new Transaction(testUser, 1000, "invalid-datetime", "Test with invalid datetime", new ArrayList<>());
        
        // Datetime should be empty string when format is invalid
        assertEquals("Invalid datetime should result in empty string", "", transaction.getDatetime());
        
        // Try setting an invalid datetime
        transaction.setDatetime("another-invalid-datetime");
        assertEquals("Datetime should remain empty after invalid update", "", transaction.getDatetime());
        
        // Set a valid datetime
        transaction.setDatetime("2025-05-24 16:00:00");
        assertEquals("Valid datetime should be accepted", "2025-05-24 16:00:00", transaction.getDatetime());
    }
}

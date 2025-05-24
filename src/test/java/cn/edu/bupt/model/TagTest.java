package cn.edu.bupt.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

public class TagTest {

    private TransactionManager tm;
    private String uniqueTagPrefix;
    // Save the original tagRegistry
    private Map<String, Tag> originalTagRegistry;

    @Before
    public void setUp() {
        try {
            // Get the current TransactionManager instance
            tm = TransactionManager.getInstance();
            // Save the original tagRegistry
            originalTagRegistry = new HashMap<>(tm.tagRegistry);
            // Generate a unique prefix to avoid tag name conflicts
            uniqueTagPrefix = "Test_" + System.currentTimeMillis() + "_";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @After
    public void tearDown() {
        try {
            // Remove tags added during the test
            Map<String, Tag> tagsToRemove = new HashMap<>(tm.tagRegistry);
            tagsToRemove.keySet().removeAll(originalTagRegistry.keySet());
            for (String tagName : tagsToRemove.keySet()) {
                tm.tagRegistry.remove(tagName);
                tm.Tags.remove(tm.tagRegistry.get(tagName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTagCreation() {
        // Create a new tag with a unique prefix
        String tagName = uniqueTagPrefix + "TagCreation";
        Tag tag = new Tag(tagName);
        assertEquals(tagName, tag.getName());
        assertTrue("Tag should be added to TransactionManager.Tags collection", tm.Tags.contains(tag));
        assertTrue("Tag name should be added to tagRegistry map", tm.tagRegistry.containsKey(tagName));
        assertEquals("The tag obtained from tagRegistry should be the same as the created tag", tag, tm.tagRegistry.get(tagName));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateTagCreation() {
        // Test that creating a duplicate tag should throw an exception
        String tagName = uniqueTagPrefix + "Duplicate";
        new Tag(tagName);
        new Tag(tagName); // Should throw IllegalArgumentException
    }

    @Test
    public void testEquals() {
        // Test the equals method
        String tagName1 = uniqueTagPrefix + "Equal";
        String tagName2 = uniqueTagPrefix + "Different";
        Tag tag1 = new Tag(tagName1);
        Tag tag2 = new Tag(tagName2);
        // Reflexivity test
        assertTrue("A tag should be equal to itself", tag1.equals(tag1));
        // Symmetry test - compare with string
        assertTrue("A tag should be equal to its name string", tag1.equals(tagName1));
        // Inequality tests
        assertFalse("Tags with different names should not be equal", tag1.equals(tag2));
        assertFalse("A tag should not be equal to a different string", tag1.equals(tagName2));
        assertFalse("A tag should not be equal to null", tag1.equals(null));
        assertFalse("A tag should not be equal to an object of another type", tag1.equals(new Object()));
    }

    @Test
    public void testToString() {
        // Test the toString method
        String tagName = uniqueTagPrefix + "ToString";
        Tag tag = new Tag(tagName);
        assertEquals("The toString method should return the tag name", tagName, tag.toString());
    }

    @Test
    public void testGetDefaultTag() {
        // Test the getDefaultTag method
        // Check if the correct Tag object can be obtained by tag name
        assertEquals("Should return the correct food tag", Tag.EXPENSE_FOOD, Tag.getDefaultTag("food"));
        assertEquals("Should return the correct salary tag", Tag.INCOME_SALARY, Tag.getDefaultTag("salary"));
    }

    @Test
    public void testLabelMap() {
        // Test if LABEL_MAP is correctly initialized
        assertEquals("EXPENSE_FOOD tag should map to 'food'", "food", Tag.LABEL_MAP.get(Tag.EXPENSE_FOOD));
        assertEquals("INCOME_SALARY tag should map to 'salary'", "salary", Tag.LABEL_MAP.get(Tag.INCOME_SALARY));
        // Check if all expense tags are in LABEL_MAP
        for (Tag tag : tm.expenseTags) {
            assertTrue("All expense tags should be in LABEL_MAP", Tag.LABEL_MAP.containsKey(tag));
            assertNotNull("The mapping value of all expense tags should not be null", Tag.LABEL_MAP.get(tag));
        }
        // Check if all income tags are in LABEL_MAP
        for (Tag tag : tm.incomeTags) {
            assertTrue("All income tags should be in LABEL_MAP", Tag.LABEL_MAP.containsKey(tag));
            assertNotNull("The mapping value of all income tags should not be null", Tag.LABEL_MAP.get(tag));
        }
    }

    @Test
    public void testPredefinedTags() {
        // Test if predefined tags are correctly initialized
        assertTrue("expenseTags should contain EXPENSE_FOOD", tm.expenseTags.contains(Tag.EXPENSE_FOOD));
        assertTrue("expenseTags should contain EXPENSE_TRANSPORT", tm.expenseTags.contains(Tag.EXPENSE_TRANSPORT));
        assertTrue("incomeTags should contain INCOME_SALARY", tm.incomeTags.contains(Tag.INCOME_SALARY));
        assertTrue("incomeTags should contain INCOME_HONGBAO", tm.incomeTags.contains(Tag.INCOME_HONGBAO));
        // Check if the total number is correct
        assertEquals("The number of expenseTags should be 20", 20, tm.expenseTags.size());
        assertEquals("The number of incomeTags should be 4", 4, tm.incomeTags.size());
    }
}
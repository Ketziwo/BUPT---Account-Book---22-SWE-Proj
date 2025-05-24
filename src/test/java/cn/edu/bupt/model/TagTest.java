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
    
    // 保存原始的 tagRegistry
    private Map<String, Tag> originalTagRegistry;

    @Before
    public void setUp() {
        try {
            // 获取当前的 TransactionManager 实例
            tm = TransactionManager.getInstance();
            
            // 保存原始的 tagRegistry
            originalTagRegistry = new HashMap<>(tm.tagRegistry);
            
            // 生成唯一的前缀以避免标签名冲突
            uniqueTagPrefix = "Test_" + System.currentTimeMillis() + "_";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @After
    public void tearDown() {
        try {
            // 移除测试过程中添加的标签
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
        // 使用唯一前缀创建新标签
        String tagName = uniqueTagPrefix + "TagCreation";
        Tag tag = new Tag(tagName);
        assertEquals(tagName, tag.getName());
        assertTrue("标签应该被添加到 TransactionManager.Tags 集合中", tm.Tags.contains(tag));
        assertTrue("标签名称应该被添加到 tagRegistry 映射中", tm.tagRegistry.containsKey(tagName));
        assertEquals("通过 tagRegistry 获取的标签应该与创建的标签相同", tag, tm.tagRegistry.get(tagName));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateTagCreation() {
        // 测试创建重复标签时应抛出异常
        String tagName = uniqueTagPrefix + "Duplicate";
        new Tag(tagName);
        new Tag(tagName); // 应该抛出 IllegalArgumentException
    }

    @Test
    public void testEquals() {
        // 测试 equals 方法
        String tagName1 = uniqueTagPrefix + "Equal";
        String tagName2 = uniqueTagPrefix + "Different";
        
        Tag tag1 = new Tag(tagName1);
        Tag tag2 = new Tag(tagName2);
        
        // 自反性测试
        assertTrue("标签应该等于自身", tag1.equals(tag1));
        
        // 对称性测试 - 与字符串比较
        assertTrue("标签应该等于其名称字符串", tag1.equals(tagName1));
        
        // 不相等测试
        assertFalse("不同名称的标签应该不相等", tag1.equals(tag2));
        assertFalse("标签不应该等于不同的字符串", tag1.equals(tagName2));
        assertFalse("标签不应该等于 null", tag1.equals(null));
        assertFalse("标签不应该等于其他类型的对象", tag1.equals(new Object()));
    }

    @Test
    public void testToString() {
        // 测试 toString 方法
        String tagName = uniqueTagPrefix + "ToString";
        Tag tag = new Tag(tagName);
        assertEquals("toString 方法应该返回标签名称", tagName, tag.toString());
    }

    @Test
    public void testGetDefaultTag() {
        // 测试 getDefaultTag 方法
        // 检查是否能够正确通过标签名称获取对应的 Tag 对象
        assertEquals("应该返回正确的餐饮标签", Tag.EXPENSE_FOOD, Tag.getDefaultTag("餐饮"));
        assertEquals("应该返回正确的工资标签", Tag.INCOME_SALARY, Tag.getDefaultTag("工资"));
        assertEquals("对不存在的标签名称应该返回 UNKNOWN 标签", Tag.UNKNOWN, Tag.getDefaultTag("不存在的标签"));
    }

    @Test
    public void testLabelMap() {
        // 测试 LABEL_MAP 是否正确初始化
        assertEquals("EXPENSE_FOOD 标签应该映射到 '餐饮'", "餐饮", Tag.LABEL_MAP.get(Tag.EXPENSE_FOOD));
        assertEquals("INCOME_SALARY 标签应该映射到 '工资'", "工资", Tag.LABEL_MAP.get(Tag.INCOME_SALARY));
        
        // 检查所有的支出标签是否都在 LABEL_MAP 中
        for (Tag tag : tm.expenseTags) {
            assertTrue("所有支出标签都应该在 LABEL_MAP 中", Tag.LABEL_MAP.containsKey(tag));
            assertNotNull("所有支出标签的映射值不应该为 null", Tag.LABEL_MAP.get(tag));
        }
        
        // 检查所有的收入标签是否都在 LABEL_MAP 中
        for (Tag tag : tm.incomeTags) {
            assertTrue("所有收入标签都应该在 LABEL_MAP 中", Tag.LABEL_MAP.containsKey(tag));
            assertNotNull("所有收入标签的映射值不应该为 null", Tag.LABEL_MAP.get(tag));
        }
    }

    @Test
    public void testPredefinedTags() {
        // 测试预定义的标签是否正确初始化
        assertTrue("expenseTags 应该包含 EXPENSE_FOOD", tm.expenseTags.contains(Tag.EXPENSE_FOOD));
        assertTrue("expenseTags 应该包含 EXPENSE_TRANSPORT", tm.expenseTags.contains(Tag.EXPENSE_TRANSPORT));
        assertTrue("incomeTags 应该包含 INCOME_SALARY", tm.incomeTags.contains(Tag.INCOME_SALARY));
        assertTrue("incomeTags 应该包含 INCOME_HONGBAO", tm.incomeTags.contains(Tag.INCOME_HONGBAO));
        
        // 检查总数是否正确
        assertEquals("expenseTags 的数量应该为 20", 20, tm.expenseTags.size());
        assertEquals("incomeTags 的数量应该为 4", 4, tm.incomeTags.size());
    }
}
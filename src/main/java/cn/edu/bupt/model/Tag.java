package cn.edu.bupt.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The Tag class represents categories for transactions in the account book system.
 * Tags are used to categorize transactions for filtering, reporting, and budgeting purposes.
 * Each tag has a unique name and is managed by the TransactionManager.
 * 
 * The class provides predefined tags for common expense and income categories,
 * as well as methods for creating custom tags.
 * 
 * @author BUPT Account Book Team
 * @version 1.0
 */
public class Tag {    /**
     * Reference to the TransactionManager singleton for tag registration.
     */
    private static TransactionManager TM = TransactionManager.getInstance();

    /**
     * The unique name of this tag. This value is immutable after creation.
     */
    private final String name;   
    
    /**
     * Creates a new tag with the specified name.
     * Tags are automatically registered with the TransactionManager.
     * 
     * @param name The unique name for this tag
     * @throws IllegalArgumentException If a tag with the same name already exists
     */
    public Tag(String name) {
        // 直接检查tagRegistry中是否已存在该名称的标签
        if(TM.tagRegistry.containsKey(name)) {
            throw new IllegalArgumentException("Tag名称重复: " + name);
        }
        this.name = name;
        TM.Tags.add(this);
        TM.tagRegistry.put(name, this);
    }

    /**
     * Gets the name of this tag.
     * 
     * @return The tag name
     */
    public String getName() { return name; }    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // if (o instanceof Tag) return name.equals(((Tag) o).name);
        if (o instanceof String) return name.equals(((String)o));
        return false;
    }

    @Override
    public String toString() {
        return this.getName(); // 假设Tag有getName()方法
    }

    /**
     * Gets a default tag by its mapped label.
     * 
     * @param s The label to look up
     * @return The corresponding tag, or UNKNOWN if not found
     */
    public static Tag getDefaultTag(String s) {
        for(Tag key: LABEL_MAP.keySet()){
            if(Tag.LABEL_MAP.get(key).equals(s)){
                return key;
            }
        }
        return UNKNOWN;
    }    /**
     * Default tag for general expenses.
     */
    static public Tag EXPENSE = new Tag("__EXPENSE__");

    /**
     * Default tag for car-related expenses.
     */
    static public Tag EXPENSE_CAR = new Tag("__EXPENSE_CAR__");
    
    /**
     * Default tag for child-related expenses.
     */
    static public Tag EXPENSE_CHILD = new Tag("__EXPENSE_CHILD__");
    static public Tag EXPENSE_CLOTH = new Tag("__EXPENSE_CLOTH__");
    static public Tag EXPENSE_DEVICE = new Tag("__EXPENSE_DEVICE__");
    static public Tag EXPENSE_ENTERTAINMENT = new Tag("__EXPENSE_ENTERTAINMENT__");
    static public Tag EXPENSE_FOOD = new Tag("__EXPENSE_FOOD__");
    static public Tag EXPENSE_GIFT = new Tag("__EXPENSE_GIFT__");
    static public Tag EXPENSE_HOUSING = new Tag("__EXPENSE_HOUSING__");
    static public Tag EXPENSE_INTERNET = new Tag("__EXPENSE_INTERNET__");
    static public Tag EXPENSE_MAKEUP = new Tag("__EXPENSE_MAKEUP__");
    static public Tag EXPENSE_MEDICAL = new Tag("__EXPENSE_MEDICAL__");
    static public Tag EXPENSE_NECESSARY = new Tag("__EXPENSE_NECESSARY__");
    static public Tag EXPENSE_PET = new Tag("__EXPENSE_PET__");
    static public Tag EXPENSE_SNACK = new Tag("__EXPENSE_SNACK__");
    static public Tag EXPENSE_SPORT = new Tag("__EXPENSE_SPORT__");
    static public Tag EXPENSE_STUDY = new Tag("__EXPENSE_STUDY__");
    static public Tag EXPENSE_TABACCO_ALCOHOL = new Tag("__EXPENSE_TABACCO_ALCOHOL__");
    static public Tag EXPENSE_TRANSPORT = new Tag("__EXPENSE_TRANSPORT__");
    static public Tag EXPENSE_TRAVEL = new Tag("__EXPENSE_TRAVEL__");
    static public Tag EXPENSE_OTHERS = new Tag("__EXPENSE_OTHERS__");
    
    static public Tag INCOME = new Tag("__INCOME__");

    static public Tag INCOME_HONGBAO = new Tag("__INCOME_HONGBAO__");
    static public Tag INCOME_SALARY = new Tag("__INCOME_SALARY__");
    static public Tag INCOME_STOCK = new Tag("__INCOME_STOCK__");
    static public Tag INCOME_OTHERS = new Tag("__INCOME_OTHERS__");
    static public Tag UNKNOWN = new Tag("__UNKNOWN__");    
    /**
     * Mapping between internal tag objects and their display labels.
     * Used for UI display and parsing from external sources.
     */
    public static final Map<Tag,String> LABEL_MAP = new HashMap<>();
    static {
        // Expense categories (matching Tag definitions)
        LABEL_MAP.put(Tag.EXPENSE_CAR, "car");
        LABEL_MAP.put(Tag.EXPENSE_CHILD, "child");
        LABEL_MAP.put(Tag.EXPENSE_CLOTH, "cloth");
        LABEL_MAP.put(Tag.EXPENSE_DEVICE, "device");
        LABEL_MAP.put(Tag.EXPENSE_ENTERTAINMENT, "entertainment");
        LABEL_MAP.put(Tag.EXPENSE_FOOD, "food");
        LABEL_MAP.put(Tag.EXPENSE_GIFT, "gift");
        LABEL_MAP.put(Tag.EXPENSE_HOUSING, "housing");
        LABEL_MAP.put(Tag.EXPENSE_INTERNET, "internet");
        LABEL_MAP.put(Tag.EXPENSE_MAKEUP, "makeup");
        LABEL_MAP.put(Tag.EXPENSE_MEDICAL, "medical");
        LABEL_MAP.put(Tag.EXPENSE_NECESSARY, "necessary");
        LABEL_MAP.put(Tag.EXPENSE_PET, "pet");
        LABEL_MAP.put(Tag.EXPENSE_SNACK, "snack");
        LABEL_MAP.put(Tag.EXPENSE_SPORT, "sport");
        LABEL_MAP.put(Tag.EXPENSE_STUDY, "study");
        LABEL_MAP.put(Tag.EXPENSE_TABACCO_ALCOHOL, "tabacco_alcohol");
        LABEL_MAP.put(Tag.EXPENSE_TRANSPORT, "transport");
        LABEL_MAP.put(Tag.EXPENSE_TRAVEL, "travel");
        LABEL_MAP.put(Tag.EXPENSE_OTHERS, "others");

        // 收入分类
        LABEL_MAP.put(Tag.INCOME_HONGBAO, "hongbao");
        LABEL_MAP.put(Tag.INCOME_SALARY, "salary");
        LABEL_MAP.put(Tag.INCOME_STOCK, "stock");
        LABEL_MAP.put(Tag.INCOME_OTHERS, "others");
    }
}


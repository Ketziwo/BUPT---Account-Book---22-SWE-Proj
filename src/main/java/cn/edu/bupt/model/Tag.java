package cn.edu.bupt.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Tag
 */
public class Tag {

    private static TransactionManager TM = TransactionManager.getInstance();

    private final String name;   
    public Tag(String name) {
        // 直接检查tagRegistry中是否已存在该名称的标签
        if(TM.tagRegistry.containsKey(name)) {
            throw new IllegalArgumentException("Tag名称重复: " + name);
        }
        this.name = name;
        TM.Tags.add(this);
        TM.tagRegistry.put(name, this);
    }

    public String getName() { return name; }

    @Override
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

    public static Tag getDefaultTag(String s) {
        for(Tag key: LABEL_MAP.keySet()){
            if(Tag.LABEL_MAP.get(key).equals(s)){
                return key;
            }
        }
        return UNKNOWN;
    }

    static public Tag EXPENSE = new Tag("__EXPENSE__");
    static public Tag EXPENSE_CAR = new Tag("__EXPENSE_CAR__");
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

    public static final Map<Tag,String> LABEL_MAP = new HashMap<>();
    static {
        // 支出分类（与Tag定义完全匹配）
        LABEL_MAP.put(Tag.EXPENSE_CAR, "汽车");
        LABEL_MAP.put(Tag.EXPENSE_CHILD, "育儿");
        LABEL_MAP.put(Tag.EXPENSE_CLOTH, "服饰");
        LABEL_MAP.put(Tag.EXPENSE_DEVICE, "数码");
        LABEL_MAP.put(Tag.EXPENSE_ENTERTAINMENT, "娱乐");
        LABEL_MAP.put(Tag.EXPENSE_FOOD, "餐饮");
        LABEL_MAP.put(Tag.EXPENSE_GIFT, "礼物");
        LABEL_MAP.put(Tag.EXPENSE_HOUSING, "住房");
        LABEL_MAP.put(Tag.EXPENSE_INTERNET, "网络");
        LABEL_MAP.put(Tag.EXPENSE_MAKEUP, "美妆");
        LABEL_MAP.put(Tag.EXPENSE_MEDICAL, "医疗");
        LABEL_MAP.put(Tag.EXPENSE_NECESSARY, "日用品");
        LABEL_MAP.put(Tag.EXPENSE_PET, "宠物");
        LABEL_MAP.put(Tag.EXPENSE_SNACK, "零食");
        LABEL_MAP.put(Tag.EXPENSE_SPORT, "运动");
        LABEL_MAP.put(Tag.EXPENSE_STUDY, "学习");
        LABEL_MAP.put(Tag.EXPENSE_TABACCO_ALCOHOL, "烟酒");
        LABEL_MAP.put(Tag.EXPENSE_TRANSPORT, "交通");
        LABEL_MAP.put(Tag.EXPENSE_TRAVEL, "旅行");
        LABEL_MAP.put(Tag.EXPENSE_OTHERS, "其他");

        // 收入分类
        LABEL_MAP.put(Tag.INCOME_HONGBAO, "红包");
        LABEL_MAP.put(Tag.INCOME_SALARY, "工资");
        LABEL_MAP.put(Tag.INCOME_STOCK, "投资");
        LABEL_MAP.put(Tag.INCOME_OTHERS, "其他");
    }
}


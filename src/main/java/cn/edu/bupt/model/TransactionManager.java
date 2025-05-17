package cn.edu.bupt.model;

import java.util.*;

final public class TransactionManager {

    public User currentUser;

    // 存储全局Transaction和Tag对象
    public final Set<User> Users = new HashSet<User>();
    public final Set<Transaction> Transactions = new HashSet<Transaction>();
    public final Set<Tag> Tags = new HashSet<Tag>();
    public final Map<String, Tag> tagRegistry = new HashMap<String,Tag>();

    // 储存默认Tag
    public final Set<Tag> expenseTags = new HashSet<Tag>();
    public final Set<Tag> incomeTags = new HashSet<Tag>();

    // 单例实现
    private static TransactionManager INSTANCE;
    private TransactionManager() {}
    public static TransactionManager getInstance() {
        if (INSTANCE==null) {
            INSTANCE = new TransactionManager();
            INSTANCE.expenseTags.add(new Tag("__EXPENSE__"));
            INSTANCE.expenseTags.add(new Tag("__EXPENSE_CAR__"));
            INSTANCE.expenseTags.add(new Tag("__EXPENSE_CHILD__"));
            INSTANCE.expenseTags.add(new Tag("__EXPENSE_CLOTH__"));
            INSTANCE.expenseTags.add(new Tag("__EXPENSE_DEVICE__"));
            INSTANCE.expenseTags.add(new Tag("__EXPENSE_ENTERTAINMENT__"));
            INSTANCE.expenseTags.add(new Tag("__EXPENSE_FOOD__"));
            INSTANCE.expenseTags.add(new Tag("__EXPENSE_GIFT__"));
            INSTANCE.expenseTags.add(new Tag("__EXPENSE_HOUSING__"));
            INSTANCE.expenseTags.add(new Tag("__EXPENSE_INTERNET__"));
            INSTANCE.expenseTags.add(new Tag("__EXPENSE_MAKEUP__"));
            INSTANCE.expenseTags.add(new Tag("__EXPENSE_MEDICAL__"));
            INSTANCE.expenseTags.add(new Tag("__EXPENSE_NECESSARY__"));
            INSTANCE.expenseTags.add(new Tag("__EXPENSE_PET__"));
            INSTANCE.expenseTags.add(new Tag("__EXPENSE_SNACK__"));
            INSTANCE.expenseTags.add(new Tag("__EXPENSE_SPORT__"));
            INSTANCE.expenseTags.add(new Tag("__EXPENSE_STUDY__"));
            INSTANCE.expenseTags.add(new Tag("__EXPENSE_TABACCO_ALCOHOL__"));
            INSTANCE.expenseTags.add(new Tag("__EXPENSE_TRANSPORT__"));
            INSTANCE.expenseTags.add(new Tag("__EXPENSE_TRAVEL__"));
            INSTANCE.incomeTags.add(new Tag("__INCOME__"));
            INSTANCE.incomeTags.add(new Tag("__INCOME_HONGBAO__"));
            INSTANCE.incomeTags.add(new Tag("__INCOME_SALARY__"));
            INSTANCE.incomeTags.add(new Tag("__INCOME_STOCK__"));
        }
        return INSTANCE;
    }

    // 核心操作方法

    /**
     * 给交易添加一个标签
     * @param ta 交易
     * @param tag 标签
     */
    public void addTagToTA(Transaction ta, Tag tag) {
        ta.getTags().add(tag);
    }
    public void addTagToTA(Transaction ta, String tagStr) {
        if(tagRegistry.get(tagStr) != null) {
            addTagToTA(ta, tagRegistry.get(tagStr));
        }
        else {
            addTagToTA(ta, new Tag(tagStr));
        }
    }

    /**
     * 给交易移去该标签
     * @param ta 交易
     * @param tag 标签
     */
    public void removeTagFromTA(Transaction ta, Tag tag) {
        if(ta.getTags().contains(tag)) {
            ta.getTags().remove(tag);
        }
    }
    public void removeTagFromTA(Transaction ta, String tagStr) {
        if(tagRegistry.get(tagStr) != null) {
            removeTagFromTA(ta, tagRegistry.get(tagStr));
        }
    }

    // 查询方法

    public Set<Tag> getTagsForTransaction(Transaction ta) {
        return ta.getTags();
    }

    public Set<Transaction> getTransactionByTag(Tag tag, Set<Transaction> tas) {
        Set<Transaction> result = new HashSet<>();
        for(Transaction ta: tas) {
            if(ta.getTags().contains(tag)) {
                result.add(ta);
            }
        }
        return result;
    }

    public Set<Transaction> getTransactionByTag(String tagStr, Set<Transaction> tas) {
        if(tagRegistry.get(tagStr) != null) {
            return getTransactionByTag(tagRegistry.get(tagStr), tas);
        }
        else {
            return null;
        }
    }

    // public Set<Transaction> getTransactionByTag(Tag tag) {
    //     return getTransactionByTag(tag, Transactions);
    // }
    // public Set<Transaction> getTransactionByTag(String tagStr) {
    //     return getTransactionByTag(tagStr, Transactions);
    // }

    
}

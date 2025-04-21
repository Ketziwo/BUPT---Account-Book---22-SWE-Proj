package cn.edu.bupt.model;

import java.util.*;

final public class TransactionManager {

    public User currentUser;

    // 存储全局Transaction和Tag对象
    public final Set<User> Users = new HashSet<User>();
    public final Set<Transaction> Transactions = new HashSet<Transaction>();
    public final Set<Tag> Tags = new HashSet<Tag>();
    public final Map<String, Tag> tagRegistry = new HashMap<String,Tag>();

    // // 构建Transaction和Tag对象的双向对应表
    // private final Map<Transaction, Set<Tag>> taToTags = new HashMap<>();
    // private final Map<Tag, Set<Transaction>> tagToTas = new HashMap<>();
    
    // 单例实现
    private static final TransactionManager INSTANCE = new TransactionManager();
    private TransactionManager() {}
    public static TransactionManager getInstance() { return INSTANCE; }

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

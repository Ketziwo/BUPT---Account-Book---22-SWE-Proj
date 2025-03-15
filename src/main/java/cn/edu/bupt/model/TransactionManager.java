package cn.edu.bupt.model;

import java.util.*;

final public class TransactionManager {

    // 存储全局Transaction和Tag对象
    public final Set<Transaction> Transactions = new HashSet<Transaction>();
    public final Set<Tag> Tags = new HashSet<Tag>();
    public final Map<String, Tag> tagRegistry = new HashMap<String,Tag>();

    // 构建Transaction和Tag对象的双向对应表
    private final Map<Transaction, Set<Tag>> taToTags = new HashMap<>();
    private final Map<Tag, Set<Transaction>> tagToTas = new HashMap<>();
    
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
        // 更新 TA -> Tags 映射
        taToTags.computeIfAbsent(ta, k -> new HashSet<>()).add(tag);
        // 更新 Tag -> TAs 映射
        tagToTas.computeIfAbsent(tag, k -> new HashSet<>()).add(ta);
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
        // 从 TA 的标签集合中移除
        if (taToTags.containsKey(ta)) {
            taToTags.get(ta).remove(tag);
            if (taToTags.get(ta).isEmpty()) {
                taToTags.remove(ta);
            }
        }
        // 从 Tag 的 TA 集合中移除
        if (tagToTas.containsKey(tag)) {
            tagToTas.get(tag).remove(ta);
            if (tagToTas.get(tag).isEmpty()) {
                tagToTas.remove(tag);
            }
        }
    }
    public void removeTagFromTA(Transaction ta, String tagStr) {
        if(tagRegistry.get(tagStr) != null) {
            removeTagFromTA(ta, tagRegistry.get(tagStr));
        }         
    }

    // 查询方法
    public Set<Tag> getTagsForTransaction(Transaction ta) {
        return Collections.unmodifiableSet(taToTags.getOrDefault(ta, Collections.emptySet()));
    }

    public Set<Transaction> getTransactionByTag(Tag tag) {
        return Collections.unmodifiableSet(tagToTas.getOrDefault(tag, Collections.emptySet()));
    }
    public Set<Transaction> getTransactionByTag(String tagStr) {
        if(tagRegistry.get(tagStr) != null) {
            return getTransactionByTag(tagRegistry.get(tagStr));
        }
        else {
            return null;
        }
    }

}

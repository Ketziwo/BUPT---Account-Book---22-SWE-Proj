package cn.edu.bupt.model;

import java.util.*;

import cn.edu.bupt.utils.DateUtils;

/**
 * Transaction Manager class that serves as the central data manager for the application.
 * Implements the Singleton pattern to ensure a single global instance.
 * Manages all transactions, users, tags, and budgets in the system.
 */
final public class TransactionManager {

    /** Current logged-in user */
    public User currentUser;

    /** Set of all users in the system */
    public final Set<User> Users = new HashSet<User>();
    
    /** Set of all transactions in the system */
    public final Set<Transaction> Transactions = new HashSet<Transaction>();
    
    /** Set of all tags in the system */
    public final Set<Tag> Tags = new HashSet<Tag>();
    
    /** Map of tag names to tag objects for quick lookup */
    public final Map<String, Tag> tagRegistry = new HashMap<String,Tag>();
    
    /** Set of all budgets in the system */
    public final Set<Budget> Budgets = new HashSet<>();

    /** Set of expense category tags */
    public final Set<Tag> expenseTags = new HashSet<Tag>();
    
    /** Set of income category tags */
    public final Set<Tag> incomeTags = new HashSet<Tag>();

    // Singleton implementation
    private static TransactionManager INSTANCE;
    
    /**
     * Private constructor to enforce Singleton pattern
     */
    private TransactionManager() {}
    
    /**
     * Gets the singleton instance of the TransactionManager.
     * Initializes default tags if this is the first call.
     * 
     * @return The singleton TransactionManager instance
     */
    public static TransactionManager getInstance() {
        if (INSTANCE==null) {
            INSTANCE = new TransactionManager();
            INSTANCE.expenseTags.add(Tag.EXPENSE_CAR);
            INSTANCE.expenseTags.add(Tag.EXPENSE_CHILD);
            INSTANCE.expenseTags.add(Tag.EXPENSE_CLOTH);
            INSTANCE.expenseTags.add(Tag.EXPENSE_DEVICE);
            INSTANCE.expenseTags.add(Tag.EXPENSE_ENTERTAINMENT);
            INSTANCE.expenseTags.add(Tag.EXPENSE_FOOD);
            INSTANCE.expenseTags.add(Tag.EXPENSE_GIFT);
            INSTANCE.expenseTags.add(Tag.EXPENSE_HOUSING);
            INSTANCE.expenseTags.add(Tag.EXPENSE_INTERNET);
            INSTANCE.expenseTags.add(Tag.EXPENSE_MAKEUP);
            INSTANCE.expenseTags.add(Tag.EXPENSE_MEDICAL);
            INSTANCE.expenseTags.add(Tag.EXPENSE_NECESSARY);
            INSTANCE.expenseTags.add(Tag.EXPENSE_PET);
            INSTANCE.expenseTags.add(Tag.EXPENSE_SNACK);
            INSTANCE.expenseTags.add(Tag.EXPENSE_SPORT);
            INSTANCE.expenseTags.add(Tag.EXPENSE_STUDY);
            INSTANCE.expenseTags.add(Tag.EXPENSE_TABACCO_ALCOHOL);
            INSTANCE.expenseTags.add(Tag.EXPENSE_TRANSPORT);
            INSTANCE.expenseTags.add(Tag.EXPENSE_TRAVEL);
            INSTANCE.expenseTags.add(Tag.EXPENSE_OTHERS);
            INSTANCE.incomeTags.add(Tag.INCOME_HONGBAO);
            INSTANCE.incomeTags.add(Tag.INCOME_SALARY);
            INSTANCE.incomeTags.add(Tag.INCOME_STOCK);
            INSTANCE.incomeTags.add(Tag.INCOME_OTHERS);
        }
        return INSTANCE;
    }    
    // Core operation methods
    // 核心操作方法

    /**
     * Add a tag to a transaction.
     * 给交易添加一个标签。
     * 
     * @param ta Transaction / 交易
     * @param tag Tag / 标签
     */
    public void addTagToTA(Transaction ta, Tag tag) {
        ta.getTags().add(tag);
    }    /**
     * Add a tag to a transaction by tag name.
     * Creates a new tag if the name doesn't exist in the registry.
     * 
     * @param ta Transaction to add the tag to
     * @param tagStr Tag name
     */
    public void addTagToTA(Transaction ta, String tagStr) {
        if(tagRegistry.get(tagStr) != null) {
            addTagToTA(ta, tagRegistry.get(tagStr));
        }
        else {
            addTagToTA(ta, new Tag(tagStr));
        }
    }    
    
    /**
     * Remove the tag from the transaction.
     * 给交易移去该标签。
     * 
     * @param ta Transaction / 交易
     * @param tag Tag / 标签
     */
    public void removeTagFromTA(Transaction ta, Tag tag) {
        if(ta.getTags().contains(tag)) {
            ta.getTags().remove(tag);
        }
    }    /**
     * Remove a tag from a transaction by tag name.
     * 
     * @param ta Transaction to remove the tag from
     * @param tagStr Tag name to remove
     */
    public void removeTagFromTA(Transaction ta, String tagStr) {
        if(tagRegistry.get(tagStr) != null) {
            removeTagFromTA(ta, tagRegistry.get(tagStr));
        }
    }

    // Query methods
    // 查询方法

    /**
     * Gets a tag by name, creating a new one if it doesn't exist.
     * 
     * @param tagStr Tag name to retrieve
     * @return The existing or newly created tag
     */
    public Tag getTag(String tagStr) {
        Tag t = tagRegistry.get(tagStr);
        if(t==null){
            System.out.println("创建了新的Tag"+tagStr);
            return new Tag(tagStr);
            
        }
        else return t;

    }    /**
     * Gets a set of tags from a set of tag names.
     * 
     * @param tagStrs Set of tag names to retrieve
     * @return Set of corresponding Tag objects
     */
    public Set<Tag> getTags(Set<String> tagStrs) {
        Set<Tag> result = new HashSet<Tag>();
        for(String tagStr:tagStrs) {
            Tag t;
            if((t = getTag(tagStr)) != null) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Gets all tags associated with a transaction.
     * 
     * @param ta Transaction to query
     * @return Set of tags associated with the transaction
     */
    public Set<Tag> getTagsForTransaction(Transaction ta) {
        return ta.getTags();
    }

    /**
     * Filters transactions by tag.
     * 
     * @param tag Tag to filter by
     * @param tas Set of transactions to filter
     * @return Subset of transactions that have the specified tag
     */
    public Set<Transaction> getTransactionByTag(Tag tag, Set<Transaction> tas) {
        if(tag==null)return null;
        Set<Transaction> result = new HashSet<>();
        for(Transaction ta: tas) {
            if(ta.getTags().contains(tag)) {
                result.add(ta);
            }
        }
        return result;
    }    /**
     * Filters transactions by tag name.
     * 
     * @param tagStr Tag name to filter by
     * @param transactions Set of transactions to filter
     * @return Subset of transactions that have the specified tag
     */
    public Set<Transaction> getTransactionByTag(String tagStr, Set<Transaction> transactions) {
        return getTransactionByTag(getTag(tagStr), transactions);
    }    /**
     * Filters transactions by multiple tags, applying either union or intersection logic.
     * 
     * @param tags Set of tags to filter by
     * @param transactions Set of transactions to filter
     * @param isUnion If true, returns transactions with ANY of the tags (union);
     *                if false, returns transactions with ALL of the tags (intersection)
     * @return Filtered set of transactions
     */
    public Set<Transaction> getTransactionsByTags(Set<Tag> tags, Set<Transaction> transactions, boolean isUnion) {
        Set<Transaction> result = new HashSet<Transaction>();

        if(isUnion) { // 并集
            for(Tag tag:tags) {
                result.addAll(getTransactionByTag(tag, transactions));
            }
        }
        else { // 交集
            result = transactions;
            for(Tag tag:tags) {
                result = getTransactionByTag(tag, result);
            }
        }
        return result;
    }

    /**
     * Calculates the total amount of transactions that match the specified criteria.
     * 
     * @param transactions Set of transactions to consider
     * @param tags Set of tags to filter by (union logic)
     * @param startdatetime Start date-time for the time range filter
     * @param enddatetime End date-time for the time range filter
     * @return Total amount in cents
     */
    public int calculateAmount(Set<Transaction> transactions, Set<Tag> tags, String startdatetime, String enddatetime) {
        Set<Transaction> tas = getTransactionsByTags(tags, transactions, true);
        Set<Transaction> filtered = new HashSet<>();
        for (Transaction ta : tas) {
            if (DateUtils.isDateTimeInRange(ta.getDatetime(), startdatetime, enddatetime)) {
                filtered.add(ta);
            }
        }
        int sum = 0;
        for (Transaction ta : filtered) {
            sum += ta.getAmount();
        }
        return sum;
    }

    public int calculateAmount(Set<Transaction> transactions, Tag tag, String startdatetime, String enddatetime) {
        Set<Tag> tags = new HashSet<Tag>();
        tags.add(tag);
        return calculateAmount(transactions, tags, startdatetime, enddatetime);
    }
      /**
     * Calculate the total amount of transactions related to a budget.
     * 根据预算设置计算相关交易总金额。
     * 
     * @param budget Budget object / 预算对象
     * @return Total amount of transactions matching the budget criteria (unit: cents) / 符合预算条件的交易总金额（单位：分）
     */
    public int calculateAmount(Budget budget) {
        return calculateAmount(budget.getUser().getTransactions(), budget.getTags(), budget.getStartDateTime(), budget.getEndDateTime());
    }

    // New budget query methods
    // 新增预算查询方法
    public Set<Budget> getBudgetsByUser(User user) {
        Set<Budget> result = new HashSet<>();
        for (Budget budget : Budgets) {
            if (budget.getUser().equals(user)) {
                result.add(budget);
            }
        }
        return result;
    }
}

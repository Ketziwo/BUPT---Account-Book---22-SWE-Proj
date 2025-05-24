package cn.edu.bupt.model;

import java.util.*;

import cn.edu.bupt.utils.DateUtils;

final public class TransactionManager {

    public User currentUser;

    // 存储全局Transaction和Tag对象
    public final Set<User> Users = new HashSet<User>();
    public final Set<Transaction> Transactions = new HashSet<Transaction>();
    public final Set<Tag> Tags = new HashSet<Tag>();
    public final Map<String, Tag> tagRegistry = new HashMap<String,Tag>();
    public final Set<Budget> Budgets = new HashSet<>();

    // 储存默认Tag
    public final Set<Tag> expenseTags = new HashSet<Tag>();
    public final Set<Tag> incomeTags = new HashSet<Tag>();

    // 单例实现
    private static TransactionManager INSTANCE;
    private TransactionManager() {}
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

    public Tag getTag(String tagStr) {
        Tag t = tagRegistry.get(tagStr);
        if(t==null){
            System.out.println("创建了新的Tag"+tagStr);
            return new Tag(tagStr);
            
        }
        else return t;

    }

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

    public Set<Tag> getTagsForTransaction(Transaction ta) {
        return ta.getTags();
    }

    public Set<Transaction> getTransactionByTag(Tag tag, Set<Transaction> tas) {
        if(tag==null)return null;
        Set<Transaction> result = new HashSet<>();
        for(Transaction ta: tas) {
            if(ta.getTags().contains(tag)) {
                result.add(ta);
            }
        }
        return result;
    }

    public Set<Transaction> getTransactionByTag(String tagStr, Set<Transaction> transactions) {
        return getTransactionByTag(getTag(tagStr), transactions);
    }

    // 根据tag对交易进行筛选，isUnion为真时tag之间取并集，为假时取交集
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
     * 根据预算设置计算相关交易总金额
     * @param budget 预算对象
     * @return 符合预算条件的交易总金额（单位：分）
     */
    public int calculateAmount(Budget budget) {
        return calculateAmount(budget.getUser().getTransactions(), budget.getTags(), budget.getStartDateTime(), budget.getEndDateTime());
    }

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

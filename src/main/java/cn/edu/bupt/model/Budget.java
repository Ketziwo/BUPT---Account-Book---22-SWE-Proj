package cn.edu.bupt.model;

import cn.edu.bupt.utils.DateUtils;
import java.util.HashSet;
import java.util.*;

/**
 * Budget class represents a budget plan with amount, tags, and time range.
 * 预算类表示具有金额、标签和时间范围的预算计划。
 */
public class Budget {
    private User user;                  // Budget owner / 预算所有者
    private int amount;                 // Budget amount in cents / 预算金额（单位：分）
    private Set<Tag> tags;              // Tags associated with this budget / 与此预算关联的标签
    private String startDateTime;       // Start date and time of the budget period / 预算周期的开始日期和时间
    private String endDateTime;         // End date and time of the budget period / 预算周期的结束日期和时间
    private String description;         // Budget description / 预算描述
    private static TransactionManager tm = TransactionManager.getInstance();

    /**
     * Creates a new budget with the specified parameters.
     * 创建具有指定参数的新预算。
     * 
     * @param user Budget owner / 预算所有者
     * @param amount Budget amount in cents / 预算金额（单位：分）
     * @param tags List of tag names / 标签名称列表
     * @param startDateTime Start date and time in "yyyy-MM-dd HH:mm:ss" format / 开始日期和时间，格式为"yyyy-MM-dd HH:mm:ss"
     * @param endDateTime End date and time in "yyyy-MM-dd HH:mm:ss" format / 结束日期和时间，格式为"yyyy-MM-dd HH:mm:ss"
     * @param description Budget description / 预算描述
     */

    public Budget(User user, int amount, List<String> tags, String startDateTime, String endDateTime, String description) {
        validateParams(user, amount, startDateTime, endDateTime);
        
        this.user = user;
        this.amount = amount;
        this.tags = new HashSet<Tag>();
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.description = description;
        // this.frequency = frequency;

        for(String t:tags) {
            this.tags.add(tm.getTag(t));
        }
        
        TransactionManager.getInstance().Budgets.add(this);
        user.getBudgets().add(this);
    }    
    
    private void validateParams(User user, int amount, String start, String end) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (!DateUtils.isValidDatetime(start) || !DateUtils.isValidDatetime(end)) {
            throw new IllegalArgumentException("Invalid date time format");
        }
        if (DateUtils.compareDateTimes(start, end) > 0) {
            throw new IllegalArgumentException("Start time cannot be later than end time");
        }
    }

    // Getter & Setter 方法
    public User getUser() { return user; }
    public void setUser(User user) { 
        if (user == null) throw new IllegalArgumentException("用户不能为空");
        this.user = user; 
    }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { 
        if (amount <= 0) throw new IllegalArgumentException("金额必须为正数");
        this.amount = amount; 
    }

    public Set<Tag> getTags() { return tags; }
    // public void setTags(Set<Tag> tags) { 
    //     this.tags = (tags != null) ? new HashSet<>(tags) : new HashSet<>(); 
    // }

    public String getStartDateTime() { return startDateTime; }
    public void setStartDateTime(String start) {
        if (!DateUtils.isValidDatetime(start)) throw new IllegalArgumentException("时间格式无效");
        if (DateUtils.compareDateTimes(start, this.endDateTime) > 0) {
            throw new IllegalArgumentException("开始时间不能晚于当前结束时间");
        }
        this.startDateTime = start;
    }

    public String getEndDateTime() { return endDateTime; }
    public void setEndDateTime(String end) {
        if (!DateUtils.isValidDatetime(end)) throw new IllegalArgumentException("时间格式无效");
        if (DateUtils.compareDateTimes(this.startDateTime, end) > 0) {
            throw new IllegalArgumentException("结束时间不能早于当前开始时间");
        }
        this.endDateTime = end;
    }

    public String getDescription() {return description;}
    public void setDescription(String d)  {description = d;}

    // public Frequency getFrequency() { return frequency; }
    // public void setFrequency(Frequency frequency) { this.frequency = frequency; }

    // 从管理器中移除预算
    public void remove() {
        TransactionManager.getInstance().Budgets.remove(this);
    }
}
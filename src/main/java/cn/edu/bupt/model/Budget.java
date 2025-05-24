package cn.edu.bupt.model;

import cn.edu.bupt.utils.DateUtils;
import java.util.HashSet;
import java.util.*;

/**
 * Budget class represents a budget plan with amount, tags, and time range.
 * This class enables users to set spending limits for specific categories (tags)
 * over defined time periods. Budgets are used for financial planning and
 * expense tracking within the account book system.
 * 
 * Each budget is associated with a user, has a monetary amount, a set of tags
 * for categorization, a time period defined by start and end dates, and an
 * optional description.
 * 
 * @author BUPT Account Book Team
 * @version 1.0
 */
public class Budget {    /**
     * The user who owns this budget.
     */
    private User user;                  // Budget owner / 预算所有者
    
    /**
     * The budget limit amount in cents (100 cents = 1 currency unit).
     */
    private int amount;                 // Budget amount in cents / 预算金额（单位：分）
    
    /**
     * Tags associated with this budget for categorization.
     */
    private Set<Tag> tags;              // Tags associated with this budget / 与此预算关联的标签
    
    /**
     * Start date and time of the budget period in "yyyy-MM-dd HH:mm:ss" format.
     */
    private String startDateTime;       // Start date and time of the budget period / 预算周期的开始日期和时间
    
    /**
     * End date and time of the budget period in "yyyy-MM-dd HH:mm:ss" format.
     */
    private String endDateTime;         // End date and time of the budget period / 预算周期的结束日期和时间
    
    /**
     * Optional description for this budget.
     */
    private String description;         // Budget description / 预算描述
    
    /**
     * Reference to the TransactionManager singleton.
     */
    private static TransactionManager tm = TransactionManager.getInstance();    /**
     * Creates a new budget with the specified parameters.
     * The budget is automatically registered with the TransactionManager and associated with the user.
     * 
     * @param user Budget owner / 预算所有者
     * @param amount Budget amount in cents / 预算金额（单位：分）
     * @param tags List of tag names / 标签名称列表
     * @param startDateTime Start date and time in "yyyy-MM-dd HH:mm:ss" format / 开始日期和时间，格式为"yyyy-MM-dd HH:mm:ss"
     * @param endDateTime End date and time in "yyyy-MM-dd HH:mm:ss" format / 结束日期和时间，格式为"yyyy-MM-dd HH:mm:ss"
     * @param description Budget description / 预算描述
     * @throws IllegalArgumentException If any parameters fail validation
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
    
    /**
     * Validates the budget parameters to ensure they meet requirements.
     * 
     * @param user The user to validate (must not be null)
     * @param amount The budget amount (must be positive)
     * @param start The start date-time (must be in valid format)
     * @param end The end date-time (must be in valid format and after start time)
     * @throws IllegalArgumentException If any validation fails
     */
    private void validateParams(User user, int amount, String start, String end) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (!DateUtils.isValidDatetime(start) || !DateUtils.isValidDatetime(end)) {
            throw new IllegalArgumentException("Invalid date time format");
        }
        if (DateUtils.compareDateTimes(start, end) > 0) {
            throw new IllegalArgumentException("Start time cannot be later than end time");
        }
    }    // Getter & Setter methods
    
    /**
     * Gets the user who owns this budget.
     * 
     * @return The budget owner
     */
    public User getUser() { return user; }
    
    /**
     * Sets the user who owns this budget.
     * 
     * @param user The new budget owner (must not be null)
     * @throws IllegalArgumentException If the user is null
     */
    public void setUser(User user) { 
        if (user == null) throw new IllegalArgumentException("用户不能为空");
        this.user = user; 
    }

    /**
     * Gets the budget amount in cents.
     * 
     * @return The budget amount
     */
    public int getAmount() { return amount; }
    
    /**
     * Sets the budget amount in cents.
     * 
     * @param amount The new budget amount (must be positive)
     * @throws IllegalArgumentException If the amount is not positive
     */
    public void setAmount(int amount) { 
        if (amount <= 0) throw new IllegalArgumentException("金额必须为正数");
        this.amount = amount; 
    }    /**
     * Gets the set of tags associated with this budget.
     * 
     * @return The set of tags
     */
    public Set<Tag> getTags() { return tags; }
    // public void setTags(Set<Tag> tags) { 
    //     this.tags = (tags != null) ? new HashSet<>(tags) : new HashSet<>(); 
    // }

    /**
     * Gets the start date-time of the budget period.
     * 
     * @return The start date-time in "yyyy-MM-dd HH:mm:ss" format
     */
    public String getStartDateTime() { return startDateTime; }
    
    /**
     * Sets the start date-time of the budget period.
     * 
     * @param start The new start date-time (must be in valid format and not after end date)
     * @throws IllegalArgumentException If the date format is invalid or after the end date
     */
    public void setStartDateTime(String start) {
        if (!DateUtils.isValidDatetime(start)) throw new IllegalArgumentException("时间格式无效");
        if (DateUtils.compareDateTimes(start, this.endDateTime) > 0) {
            throw new IllegalArgumentException("开始时间不能晚于当前结束时间");
        }
        this.startDateTime = start;
    }    /**
     * Gets the end date-time of the budget period.
     * 
     * @return The end date-time in "yyyy-MM-dd HH:mm:ss" format
     */
    public String getEndDateTime() { return endDateTime; }
    
    /**
     * Sets the end date-time of the budget period.
     * 
     * @param end The new end date-time (must be in valid format and not before start date)
     * @throws IllegalArgumentException If the date format is invalid or before the start date
     */
    public void setEndDateTime(String end) {
        if (!DateUtils.isValidDatetime(end)) throw new IllegalArgumentException("时间格式无效");
        if (DateUtils.compareDateTimes(this.startDateTime, end) > 0) {
            throw new IllegalArgumentException("结束时间不能早于当前开始时间");
        }
        this.endDateTime = end;
    }    /**
     * Gets the description of this budget.
     * 
     * @return The budget description
     */
    public String getDescription() {return description;}
    
    /**
     * Sets the description of this budget.
     * 
     * @param d The new budget description
     */
    public void setDescription(String d)  {description = d;}

    // public Frequency getFrequency() { return frequency; }
    // public void setFrequency(Frequency frequency) { this.frequency = frequency; }

    /**
     * Removes this budget from the system.
     * This will delete the budget from the TransactionManager's registry.
     */
    public void remove() {
        TransactionManager.getInstance().Budgets.remove(this);
    }
}
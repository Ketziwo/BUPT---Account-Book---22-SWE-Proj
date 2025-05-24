package cn.edu.bupt.model;

import cn.edu.bupt.utils.DateUtils;
import java.util.HashSet;
import java.util.*;

public class Budget {
    private User user;
    private int amount; // 单位：分
    private Set<Tag> tags;
    private String startDateTime;
    private String endDateTime;
    private String description;
    private static TransactionManager tm = TransactionManager.getInstance();
    // private Frequency frequency;

    // public enum Frequency {
    //     ONCE,   // 单次
    //     WEEKLY, // 每周循环
    //     MONTHLY // 每月循环
    // }

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
        if (user == null) throw new IllegalArgumentException("用户不能为空");
        if (amount <= 0) throw new IllegalArgumentException("金额必须为正数");
        if (!DateUtils.isValidDatetime(start) || !DateUtils.isValidDatetime(end)) {
            throw new IllegalArgumentException("时间格式无效");
        }
        if (DateUtils.compareDateTimes(start, end) > 0) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
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
package cn.edu.bupt.model;

import cn.edu.bupt.utils.DateUtils;
import java.util.*;
import java.util.regex.*;

public class Transaction {
    /**
     * 运行程序时的时间和序号，用于生成交易ID
     */
    private static String lastDate = DateUtils.getDate();
    private static int num = 1;
    private static TransactionManager TM = TransactionManager.getInstance();

    /**
     * 类中的各种基本元素
     */
    private final String transaction_id;    // 交易ID 年月日-序号（如20230901-1）
    private int amount;                     // 交易金额 单位为分

    private String datetime;                // 交易时间 ISO-8601格式 yyyy-mm-dd hh:mm:ss
    private String created_at;              // 交易创建时间
    private String modified_at;             // 最后修改时间
    
    private String description;             // 交易详情描述（自由文本）

    /**
     * 交易对象构造函数，交易ID自动构造为输入日期+序号，或可以自定义，交易ID只在创建时可定义
     * 
     * 提供四种构造函数，全部自定义；全部自定义+tags；自定义交易细节+tags+默认ID和时间；全部默认
     */
    public Transaction(String tid, int amount, String datetime, String created_at, String modified_at, String description) {
        final String tidPattern = "^\\d{8}-\\d+$";
        final String datetimePattern = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01]) ([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$";

        /*
         * 初始化主键：检测ID是否符合格式，如果符合格式则试图更新num值，如果不符合则自动生成新ID
         */
        if(Pattern.matches(tidPattern, tid)) {
            if(tid.startsWith(lastDate)) {
                try{
                    int temp = Integer.parseInt(tid.substring(9)) +1;
                    if(temp > num)num = temp;
                }catch(Exception e){}
            }
            this.transaction_id = tid;
        }
        else {
            this.transaction_id = lastDate + '-' + num++;
        }

        /*
         * 初始化金额
         */
        this.amount = amount;

        /*
         * 初始化交易时间，生成时间并检测时间是否符合格式
         */
        if(Pattern.matches(datetimePattern, datetime)) {
            this.datetime = datetime;
        }
        else {
            this.datetime = "";
        }
        /*
         * 初始化创建时间，修改时间
         */
        if (Pattern.matches(datetimePattern, created_at) && Pattern.matches(datetimePattern, modified_at)) {
            this.created_at = created_at;
            this.modified_at = modified_at;
        }
        else {
            this.created_at = DateUtils.getDatetime();
            this.modified_at = DateUtils.getDatetime();
        }

        /*
         * 初始化用户自定义文本
         */
        this.description = description.replace(',','|');
        
        TM.Transactions.add(this);
    }
    
    public Transaction(String tid, int amount, String datetime, String created_at, String modified_at, String description, ArrayList<String> tags) {
        this(tid, amount, datetime, created_at, modified_at, description);
        for(String str: tags) {
            TM.addTagToTA(this, str);
        }
    }
    public Transaction(int amount, String datetime, String description, ArrayList<String> tags) {
        this("", amount, datetime, "", "", description, tags);
    }
    public Transaction() {
        this("", 0, "", "", "", "");
    }


    /**
     * 从注册表中删除本交易条目
     */
    public void remove() {
        TM.Transactions.remove(this);
    }

    /**
     * get交易ID
     */
    public String getTransaction_id() {return transaction_id;}

    /**
     * set交易时间
     */
    public void setDatetime(String t) {
        String datetimePattern = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01]) ([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$";
        if(Pattern.matches(datetimePattern, t)) {
            this.datetime = t;
        }
        fresh();
    }
    public String getDatetime() {return datetime;}

    /**
     * @param a 交易金额 单位为分
     */
    public void setAmount(int a) {
        amount = a;
        fresh();
    }
    public int getAmount() {return amount;}

    /**
     * @param d 交易用户自定义描述
     */
    public void setDescription(String d) {
        description = d;
        fresh();
    }
    public String getDescription() {return description;}

    public String getCreateTime() {return created_at;}
    public String getModifiedTime() {return modified_at;}

    public void fresh() {this.modified_at = DateUtils.getDatetime();}


    // 调用 交易管理器的函数
    public void addTag(Tag tag) { TM.addTagToTA(this, tag); }
    public void addTag(String str) { TM.addTagToTA(this, str); }
    public void removeTag(Tag tag) { TM.removeTagFromTA(this, tag); }
    public void removeTag(String str) { TM.removeTagFromTA(this, str); }
    public Set<Tag> getTags() { return TM.getTagsForTransaction(this); }
}

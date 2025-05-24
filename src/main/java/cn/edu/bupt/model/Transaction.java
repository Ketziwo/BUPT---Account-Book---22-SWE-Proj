package cn.edu.bupt.model;

import cn.edu.bupt.utils.DateUtils;
import java.util.*;
import java.util.regex.*;

public class Transaction {
    /**
     * Date and sequence number at runtime, used for generating transaction ID.
     * 运行程序时的时间和序号，用于生成交易ID。
     */
    private static String lastDate = DateUtils.getDate();
    private static int num = 1;
    private static TransactionManager TM = TransactionManager.getInstance();

    /**
     * Basic elements of the class.
     * 类中的各种基本元素。
     */

    private final String transaction_id;    // Transaction ID YYYYMMDD-sequence (e.g., 20230901-1) / 交易ID 年月日-序号（如20230901-1）
    private User user;                      // Transaction user / 交易用户
    private int amount;                     // Transaction amount in cents / 交易金额 单位为分

    private String datetime;                // Transaction time in ISO-8601 format yyyy-mm-dd hh:mm:ss / 交易时间 ISO-8601格式 yyyy-mm-dd hh:mm:ss
    private String created_at;              // Transaction creation time / 交易创建时间
    private String modified_at;             // Last modification time / 最后修改时间
    
    private String description;             // Transaction detail description (free text) / 交易详情描述（自由文本）

    private Set<Tag> Tags = new HashSet<Tag>();

    /**
     * Transaction object constructor. The transaction ID is automatically constructed as input date + sequence number, or can be customized.
     * Transaction ID can only be defined at creation time.
     * 
     * Four constructors are provided: all customized; all customized + tags; custom transaction details + tags + default ID and time; all default.
     * 
     * 交易对象构造函数，交易ID自动构造为输入日期+序号，或可以自定义，交易ID只在创建时可定义。
     * 
     * 提供四种构造函数，全部自定义；全部自定义+tags；自定义交易细节+tags+默认ID和时间；全部默认。
     */
    public Transaction(String tid, User user, int amount, String datetime, String created_at, String modified_at, String description) {
        final String tidPattern = "^\\d{8}-\\d+$";
        final String datetimePattern = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01]) ([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$";        
        
        /*
         * Initialize primary key: Check if ID matches format, if it matches, try to update num value, if not, automatically generate a new ID.
         * 初始化主键：检测ID是否符合格式，如果符合格式则试图更新num值，如果不符合则自动生成新ID。
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
         * Initialize user.
         * 初始化用户。
         */
        this.user = user;
        user.getTransactions().add(this);        
        
        /*
         * Initialize amount.
         * 初始化金额。
         */
        this.amount = amount;        
        
        /*
         * Initialize transaction time, generate time and check if time matches format.
         * 初始化交易时间，生成时间并检测时间是否符合格式。
         */
        if(Pattern.matches(datetimePattern, datetime)) {
            this.datetime = datetime;
        }
        else {
            this.datetime = "";
        }        /*
         * Initialize creation time, modification time.
         * 初始化创建时间，修改时间。
         */
        if (Pattern.matches(datetimePattern, created_at) && Pattern.matches(datetimePattern, modified_at)) {
            this.created_at = created_at;
            this.modified_at = modified_at;
        }
        else {
            this.created_at = DateUtils.getDatetime();
            this.modified_at = DateUtils.getDatetime();
        }        /*
         * Initialize user-defined text.
         * 初始化用户自定义文本。
         */
        this.description = description.replace(',','|');
        
        TM.Transactions.add(this);
    }
    
    public Transaction(String tid, User user, int amount, String datetime, String created_at, String modified_at, String description, ArrayList<String> tags) {
        this(tid, user, amount, datetime, created_at, modified_at, description);
        for(String str: tags) {
            TM.addTagToTA(this, str);
        }
    }
    public Transaction(User user, int amount, String datetime, String description, ArrayList<String> tags) {
        this("", user, amount, datetime, "", "", description, tags);
    }
    public Transaction() {
        this("", TM.currentUser, 0, "", "", "", "");
    }
    /**
     * Remove this transaction entry from the registry.
     * 从注册表中删除本交易条目。
     */
    public void remove() {
        TM.Transactions.remove(this);
    }    
    
    /**
     * Get transaction ID.
     * 获取交易ID。
     */
    public String getTransaction_id() {return transaction_id;}    
    
    /**
     * Set user.
     * 设置用户。
     */
    public void setUser(User u) {
        this.user.getTransactions().remove(this);
        this.user = u;
        u.getTransactions().add(this);
        refresh();
    }
    public User getUser() {return this.user;}    
    
    /**
     * Set transaction time.
     * 设置交易时间。
     */
    public void setDatetime(String t) {
        String datetimePattern = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01]) ([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$";
        if(Pattern.matches(datetimePattern, t)) {
            this.datetime = t;
        }
        refresh();
    }
    public String getDatetime() {return datetime;}    
    
    /**
     * @param a Transaction amount in cents
     *        交易金额 单位为分
     */
    public void setAmount(int a) {
        amount = a;
        refresh();
    }
    public int getAmount() {return amount;}    
    
    /**
     * @param d User-defined transaction description
     *        交易用户自定义描述
     */
    public void setDescription(String d) {
        description = d;
        refresh();
    }
    public String getDescription() {return description;}

    public String getCreateTime() {return created_at;}
    public String getModifiedTime() {return modified_at;}

    public void refresh() {this.modified_at = DateUtils.getDatetime();}


    // 调用 交易管理器的函数
    public void addTag(Tag tag) { TM.addTagToTA(this, tag); }
    public void addTag(String str) { TM.addTagToTA(this, str); }
    public void removeTag(Tag tag) { TM.removeTagFromTA(this, tag); }
    public void removeTag(String str) { TM.removeTagFromTA(this, str); }
    public Set<Tag> getTags() { return this.Tags; }
}

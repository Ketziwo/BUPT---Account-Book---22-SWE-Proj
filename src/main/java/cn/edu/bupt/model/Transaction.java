package cn.edu.bupt.model;

import cn.edu.bupt.utils.DateUtils;
import java.util.*;
import java.util.regex.*;

/**
 * Represents a financial transaction in the account book system.
 * This class manages transaction data including amount, date, user information,
 * description, and associated tags. It provides methods for creating, modifying,
 * and querying transaction details.
 * 
 * Each transaction has a unique ID generated using the date and a sequence number.
 * Transactions are automatically registered with the TransactionManager when created.
 * 
 * @author BUPT Account Book Team
 * @version 1.0
 */
public class Transaction {    
    /**
     * Date and sequence number at runtime, used for generating transaction ID.
     * 运行程序时的时间和序号，用于生成交易ID。
     */
    private static String lastDate = DateUtils.getDate();
    
    /**
     * Counter for transaction sequence within the same date.
     */
    private static int num = 1;
    
    /**
     * Reference to the TransactionManager singleton instance.
     */
    private static TransactionManager TM = TransactionManager.getInstance();    /**
     * Basic elements of the class.
     * 类中的各种基本元素。
     */

    /**
     * Unique identifier for the transaction in format YYYYMMDD-sequence.
     * This ID is immutable after creation.
     */
    private final String transaction_id;    // Transaction ID YYYYMMDD-sequence (e.g., 20230901-1) / 交易ID 年月日-序号（如20230901-1）
    
    /**
     * User who made the transaction.
     */
    private User user;                      // Transaction user / 交易用户
    
    /**
     * Transaction amount in cents (100 cents = 1 currency unit).
     */
    private int amount;                     // Transaction amount in cents / 交易金额 单位为分

    /**
     * Transaction time in ISO-8601 format (yyyy-MM-dd HH:mm:ss).
     */
    private String datetime;                // Transaction time in ISO-8601 format yyyy-mm-dd hh:mm:ss / 交易时间 ISO-8601格式 yyyy-mm-dd hh:mm:ss
    
    /**
     * Time when the transaction record was created.
     */
    private String created_at;              // Transaction creation time / 交易创建时间
    
    /**
     * Time when the transaction record was last modified.
     */
    private String modified_at;             // Last modification time / 最后修改时间
    
    /**
     * Descriptive text about the transaction.
     */
    private String description;             // Transaction detail description (free text) / 交易详情描述（自由文本）

    /**
     * Collection of tags associated with this transaction.
     */
    private Set<Tag> Tags = new HashSet<Tag>();    /**
     * Full constructor for creating a transaction with all details specified.
     * The transaction ID is validated or auto-generated if invalid.
     * 
     * @param tid           Transaction ID in format YYYYMMDD-sequence, or empty string for auto-generation
     * @param user          User associated with this transaction
     * @param amount        Transaction amount in cents
     * @param datetime      Transaction time in ISO-8601 format (yyyy-MM-dd HH:mm:ss)
     * @param created_at    Creation time of this record
     * @param modified_at   Last modification time of this record
     * @param description   Descriptive text about the transaction
     * 
     * @throws IllegalArgumentException If parameters fail validation
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
    
    /**
     * Constructor for creating a transaction with tags.
     * 
     * @param tid           Transaction ID in format YYYYMMDD-sequence, or empty string for auto-generation
     * @param user          User associated with this transaction
     * @param amount        Transaction amount in cents
     * @param datetime      Transaction time in ISO-8601 format (yyyy-MM-dd HH:mm:ss)
     * @param created_at    Creation time of this record
     * @param modified_at   Last modification time of this record
     * @param description   Descriptive text about the transaction
     * @param tags          List of tag names to associate with this transaction
     */
    public Transaction(String tid, User user, int amount, String datetime, String created_at, String modified_at, String description, ArrayList<String> tags) {
        this(tid, user, amount, datetime, created_at, modified_at, description);
        for(String str: tags) {
            TM.addTagToTA(this, str);
        }
    }
    /**
     * Simplified constructor with auto-generated ID and timestamps.
     * 
     * @param user          User associated with this transaction
     * @param amount        Transaction amount in cents
     * @param datetime      Transaction time in ISO-8601 format (yyyy-MM-dd HH:mm:ss)
     * @param description   Descriptive text about the transaction
     * @param tags          List of tag names to associate with this transaction
     */
    public Transaction(User user, int amount, String datetime, String description, ArrayList<String> tags) {
        this("", user, amount, datetime, "", "", description, tags);
    }
    /**
     * Default constructor that creates an empty transaction for the current user.
     */
    public Transaction() {
        this("", TM.currentUser, 0, "", "", "", "");
    }    /**
     * Removes this transaction from the system.
     * This will delete the transaction from the TransactionManager's registry.
     */
    public void remove() {
        TM.Transactions.remove(this);
    }    
      /**
     * Gets the unique identifier of this transaction.
     * 
     * @return The transaction ID in format YYYYMMDD-sequence
     */
    public String getTransaction_id() {return transaction_id;}
      /**
     * Updates the user associated with this transaction.
     * This method also updates the user's transaction collection and refreshes the modification timestamp.
     * 
     * @param u The new user to associate with this transaction
     */
    public void setUser(User u) {
        this.user.getTransactions().remove(this);
        this.user = u;
        u.getTransactions().add(this);
        refresh();
    }
    /**
     * Gets the user associated with this transaction.
     * 
     * @return The user who made this transaction
     */
    public User getUser() {return this.user;}    
      /**
     * Updates the date and time of this transaction.
     * The format must match ISO-8601 (yyyy-MM-dd HH:mm:ss).
     * 
     * @param t The new date and time in ISO-8601 format
     */
    public void setDatetime(String t) {
        String datetimePattern = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01]) ([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$";
        if(Pattern.matches(datetimePattern, t)) {
            this.datetime = t;
        }
        refresh();
    }
    /**
     * Gets the date and time of this transaction.
     * 
     * @return The transaction date and time in ISO-8601 format
     */
    public String getDatetime() {return datetime;}    
      /**
     * Updates the amount of this transaction.
     * 
     * @param a The new amount in cents (100 cents = 1 currency unit)
     */
    public void setAmount(int a) {
        amount = a;
        refresh();
    }
    /**
     * Gets the amount of this transaction.
     * 
     * @return The transaction amount in cents
     */
    public int getAmount() {return amount;}    
      /**
     * Updates the description of this transaction.
     * 
     * @param d The new description text
     */
    public void setDescription(String d) {
        description = d;
        refresh();
    }    /**
     * Gets the description of this transaction.
     * 
     * @return The transaction description text
     */
    public String getDescription() {return description;}

    /**
     * Gets the creation time of this transaction record.
     * 
     * @return The creation timestamp in ISO-8601 format
     */
    public String getCreateTime() {return created_at;}
    
    /**
     * Gets the last modification time of this transaction record.
     * 
     * @return The last modification timestamp in ISO-8601 format
     */
    public String getModifiedTime() {return modified_at;}

    /**
     * Updates the modification timestamp to the current time.
     * Called internally whenever a transaction property is modified.
     */
    public void refresh() {this.modified_at = DateUtils.getDatetime();}
    // Tag management methods

    /**
     * Adds a tag object to this transaction.
     * 
     * @param tag The tag object to add
     */
    public void addTag(Tag tag) { TM.addTagToTA(this, tag); }
    
    /**
     * Adds a tag by name to this transaction.
     * If the tag doesn't exist, it will be created.
     * 
     * @param str The tag name to add
     */
    public void addTag(String str) { TM.addTagToTA(this, str); }
    
    /**
     * Removes a tag object from this transaction.
     * 
     * @param tag The tag object to remove
     */
    public void removeTag(Tag tag) { TM.removeTagFromTA(this, tag); }
    
    /**
     * Removes a tag by name from this transaction.
     * 
     * @param str The tag name to remove
     */
    public void removeTag(String str) { TM.removeTagFromTA(this, str); }
    
    /**
     * Gets all tags associated with this transaction.
     * 
     * @return A set of Tag objects
     */
    public Set<Tag> getTags() { return this.Tags; }
}

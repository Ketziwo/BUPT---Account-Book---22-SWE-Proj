package cn.edu.bupt.model;

import cn.edu.bupt.utils.DateUtils;
import java.util.ArrayList;
import java.util.regex.*;

public class Transaction {

    /**
     * 这个动态数组储存所有交易
     */
    public static ArrayList<Transaction> allTransactions = new ArrayList<>();


    /**
     * 运行程序时的时间和序号，用于生成交易ID
     */
    private static String lastDate = DateUtils.getDate();
    private static int num = 1;

    /**
     * 类中的各种基本元素
     */
    private String transaction_id;  // 交易ID 年月日-序号（如20230901-1）
    private String datetime;        // 交易时间 ISO-8601格式 yyyy-mm-dd hh:mm:ss
    private int amount;             // 交易金额 单位为分
    private Currency currency;      // 交易货币，默认CNY，3字母货币代码
    private TransactionType type;   // 收入或支出类型
    private Category category;      // 分类：餐饮、娱乐等
    private Source source;          // 交易渠道（微信支付/现金/银行卡等）
    private String description;     // 交易详情描述（自由文本）
    private ArrayList<Tag> tags= new ArrayList<Tag>();    
                                    // 用户自定义标签
    private String created_at;      // 交易创建时间
    private String modified_at;     // 最后修改时间


    /**
     * 交易对象构造函数，交易ID自动构造为输入日期+序号，或可以自定义，交易ID只在创建时可定义
     * 
     * 提供三种构造函数，全部自定义/ 自定义交易ID/ 全部默认
     */
    public Transaction(String tid, String datetime, int amount, Currency currency, TransactionType type, Category category, Source source, String description, ArrayList<Tag> tags, String created_at, String modified_at) {
        String tidPattern = "^\\d{8}-\\d+$";
        String datetimePattern = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01]) ([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$";

        /*
         * 检测ID是否符合格式，如果符合格式则试图更新num值，如果不符合则自动生成新ID
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
         * 检测交易时间是否符合格式
         */
        if(Pattern.matches(datetimePattern, datetime)) {
            this.datetime = datetime;
        }
        else {
            this.datetime = null;
        }

        this.amount = amount;
        this.currency = currency==null?Currency.CNY:currency;
        this.type = type==null?TransactionType.EXPENSE:type;
        this.category = category==null?Category.FOODS:category;
        this.source = source==null?Source.USER:source;
        this.description = description;
        this.tags = tags==null?new ArrayList<Tag>():tags;

        /*
         * 检测时间是否符合格式
         */
        if (Pattern.matches(datetimePattern, created_at) && Pattern.matches(datetimePattern, modified_at)) {
            this.created_at = created_at;
            this.modified_at = modified_at;
        }
        else {
            this.created_at = DateUtils.getDatetime();
            this.modified_at = DateUtils.getDatetime();
        }

        allTransactions.add(this);
    }
    
    public Transaction(String tid, String created_at, String modified_at) {
        this(tid,"",0,Currency.CNY,TransactionType.EXPENSE,Category.DFT,Source.USER,"",null,created_at,modified_at);
    }
    public Transaction(String tid) {
        this(tid, "", "");
    }
    public Transaction() {
        this("");
    }


    /**
     * 从注册表中删除本交易条目
     */
    public void remove() {
        for(int i=0; i<allTransactions.size(); ++i) {
            if(this == allTransactions.get(i)) {
                allTransactions.remove(i);
            }
        }
    }

    /**
     * 从注册表当中寻找该ID的元素
     */
    public static Transaction findTransactionByID(String ID) {
        for(int i=0; i<allTransactions.size()-1; ++i) {
            if(ID == allTransactions.get(i).getTransaction_id()) {
                return allTransactions.get(i);
            }
        }
        return null;
    }

    /**
     * 
     * @return get交易ID
     */
    public String getTransaction_id() {return transaction_id;}

    public String getDatetime() {return datetime;}

    /**
     * @param a 交易金额 单位为分
     */
    public void setAmount(int a) {amount = a;}
    public int getAmount() {return amount;}

    /**
     * @param c 交易货币，默认CNY
     */
    public void setCurrency(Currency c) {currency = c;}
    public Currency getCurrency() {return currency;}
    
    /**
     * @param t 交易类型
     */
    public void setType(TransactionType t) {type = t;}
    public TransactionType getType() {return type;}

    /**
     * @param c 分类
     */
    public void setCategory(Category c) {category = c;}
    public Category getCategory() {return category;}

    /**
     * @param s 交易来源
     */
    public void setSource(Source s) {source = s;}
    public Source getSource() {return source;}

    /**
     * @param d 交易用户自定义描述
     */
    public void setDescription(String d) {description = d;}
    public String getDescription() {return description;}

    public void addTag(String n) {
        Tag t = Tag.findTag(n);
        if(t == null) {
            t = new Tag(n); 
        }
        t.addToTag(this);
        tags.add(t);
    }
    public int removeTag(String n) {
        Tag t = Tag.findTag(n);
        if(t == null) {
            return 0;
        }
        for(int i=0; i<tags.size()-1; ++i) {
            if(tags.get(i) == t) {
                tags.remove(i);
                t.removeFromTag(this);
                return 1;
            }
        }
        return 0;
    }
    public ArrayList<Tag> getTags() {
        return tags;
    }

    public String getCreateTime() {
        return created_at;
    }

    public String getModifiedTime() {
        return modified_at;
    }
}

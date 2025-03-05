package cn.edu.bupt.model;

import cn.edu.bupt.utils.DateUtils;

/*
 * 交易货币3位代码
 */
enum Currency {
    CNY,USD;
}

/*
 * 收入或支出类型
 */
enum TransactionType {
    INCOME,EXPENSE;
}

public class Transaction {
    private static String lastDate = DateUtils.getDate();
    private static int num = 0;

    private String transaction_id;  // 交易ID 年月日-序号（如20230901-1）
    private String datetime;        // 交易时间 ISO-8601格式 yyyy-mm-dd hh:mm:ss
    private int amount;             // 交易金额 单位为分
    private Currency currency;      // 交易货币，默认CNY，3字母货币代码
    private TransactionType type;   // 收入或支出类型
    private String category;        // 分类：餐饮、娱乐等
    private String source;          // 交易渠道（微信支付/现金/银行卡等）
    private String description;     // 交易详情描述（自由文本）
    private String tags;            // 用户自定义标签
    private String created_at;      // 交易创建时间
    private String modified_at;     // 最后修改时间


    /**
     * 交易对象构造函数，交易ID自动构造为输入日期+序号，或可以自定义，交易ID只在创建时可定义
     * 
     * @param tid 交易ID
     * @param create_time 交易创建时间
     * @param modified_time 最后修改时间
     */
    public Transaction(String tid, String create_at, String modified_at) {
        this.transaction_id = tid;
        this.created_at = create_at;
        this.modified_at = modified_at;
    }
    public Transaction() {
        this.transaction_id = lastDate + '-' + num++;
        this.created_at = DateUtils.getDatetime();
        this.modified_at = created_at;
    }
    public String getTransaction_id() {return transaction_id;}

    /**
     * @param a 交易金额 单位为分
     */
    public void setAmount(int a) {amount = a;}
    public int getAmount() {return amount;}

    /**
     * @param c 交易货币，默认CNY
     */
    public void setCurrency(Currency c) {currency = c;}
    public Currency getCurrency(){return currency;}
    
    /**
     * @param t 交易类型
     */
    public void setType(TransactionType t) {type = t;}
    public TransactionType getType(){return type;}

    public 

}

package model;

import java.math.BigDecimal;

enum Currency {
    CNY,USD;
}

enum TransactionType {
    INCOME,EXPENSE;
}

public class Transaction {
    private String transaction_id;  // 交易ID 年月日-序号（如20230901-001）
    private String datetime;        // 交易时间 ISO-8601格式 yyyy-mm-dd hh:mm:ss
    private BigDecimal amount;      // 交易金额
    private Currency currency;      // 交易货币，默认CNY，3字母货币代码
    private TransactionType type;   // 收入或支出类型
    private String category;        // 分类：餐饮、娱乐等
    private String source;          // 交易渠道（微信支付/现金/银行卡等）
    private String description;     // 交易详情描述（自由文本）
    private String tags;            // 用户自定义标签
    private String created_at;      // 交易创建时间
    private String modified_at;     // 最后修改时间
}

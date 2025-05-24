package cn.edu.bupt.model;

import java.util.HashSet;
import java.util.Set;

/**
 * User class represents a user of the account book system.
 * 用户类表示账本系统的用户。
 */
public class User {
    private final String name;              // Username / 用户名
    private String pwd;                     // Password / 密码
    private String TransactionPATH;         // Path to transaction CSV file / 交易CSV文件路径
    private String BudgetPATH;              // Path to budget CSV file / 预算CSV文件路径
    private Set<Transaction> Transactions = new HashSet<Transaction>(); // User's transactions / 用户的交易记录
    private Set<Budget> Budgets = new HashSet<Budget>();               // User's budgets / 用户的预算

    /**
     * Default user with predefined credentials.
     * 预定义凭据的默认用户。
     */
    public static User defaultUser = new User("default", "88888888");

    /**
     * Creates a new user with the specified username and password.
     * 创建具有指定用户名和密码的新用户。
     * 
     * @param name Username / 用户名
     * @param pwd Password / 密码
     */
    public User(String name, String pwd) {
        this.name = name;
        this.pwd = pwd;
        this.TransactionPATH = "data/Transaction_" + name + ".csv";
        this.BudgetPATH = "data/Budget_" + name + ".csv";
        TransactionManager.getInstance().Users.add(this);
    }
    
    /**
     * Gets the set of transactions associated with this user.
     * 获取与此用户关联的交易集合。
     * 
     * @return Set of transactions / 交易集合
     */
    public Set<Transaction> getTransactions() {return Transactions;}
    
    /**
     * Gets the set of budgets associated with this user.
     * 获取与此用户关联的预算集合。
     * 
     * @return Set of budgets / 预算集合
     */
    public Set<Budget> getBudgets() {return Budgets;}
    
    /**
     * Gets the username.
     * 获取用户名。
     * 
     * @return Username / 用户名
     */
    public String getname() {return name;}
    
    /**
     * Gets the path to the transaction CSV file.
     * 获取交易CSV文件的路径。
     * 
     * @return Path to transaction CSV / 交易CSV文件路径
     */
    public String getTransactionPATH() {return TransactionPATH;}
    
    /**
     * Gets the path to the budget CSV file.
     * 获取预算CSV文件的路径。
     * 
     * @return Path to budget CSV / 预算CSV文件路径
     */
    public String getBudgetPATH() {return BudgetPATH;}
    
    /**
     * Gets the password.
     * 获取密码。
     * 
     * @return Password / 密码
     */
    public String getPwd() {return pwd;}
}

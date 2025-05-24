package cn.edu.bupt.model;

import java.util.HashSet;
import java.util.Set;

public class User {
    private final String name;
    private String pwd;
    private String TransactionPATH;
    private String BudgetPATH;
    private Set<Transaction> Transactions = new HashSet<Transaction>();
    private Set<Budget> Budgets = new HashSet<Budget>();

    public static User defaultUser = new User("default", "88888888");

    public User(String name, String pwd) {
        this.name = name;
        this.pwd = pwd;
        this.TransactionPATH = "data/Transaction_" + name + ".csv";
        this.BudgetPATH = "data/Budget_" + name + ".csv";
        TransactionManager.getInstance().Users.add(this);
    }
    
    public Set<Transaction> getTransactions() {return Transactions;}
    public Set<Budget> getBudgets() {return Budgets;}
    public String getname() {return name;}
    public String getTransactionPATH() {return TransactionPATH;}
    public String getBudgetPATH() {return BudgetPATH;}
    public String getPwd() {return pwd;}
}

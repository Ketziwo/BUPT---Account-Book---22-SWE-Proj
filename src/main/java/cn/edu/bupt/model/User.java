package cn.edu.bupt.model;

import java.util.HashSet;
import java.util.Set;

public class User {
    private final String name;
    private String pwd;
    private String path;
    private Set<Transaction> Transactions = new HashSet<Transaction>();

    public static User defaultUser = new User("default", "88888888");

    public User(String name, String pwd) {
        this.name = name;
        this.pwd = pwd;
        this.path = "data/Transaction_" + name + ".csv";
        TransactionManager.getInstance().Users.add(this);
    }
    
    public Set<Transaction> getTransactions() {return Transactions;}
    public String getname() {return name;}
    public String getpath() {return path;}
    public String getPwd() {return pwd;}
}

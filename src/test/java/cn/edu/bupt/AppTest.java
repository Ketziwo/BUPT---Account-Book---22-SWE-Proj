package cn.edu.bupt;

import cn.edu.bupt.dao.CsvTransactionDao;
import cn.edu.bupt.utils.*;
import cn.edu.bupt.model.*;
import cn.edu.bupt.service.*; // 引入 TransactionService 类
import cn.edu.bupt.view.*;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/*
 * maven自动生成的
 */
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import javax.swing.*;

/**
 * 用于单元测试的类
 */
public class AppTest {

    public static Object lock = new Object();

    @Test
    public void shouldAnswerWithTrue() throws InterruptedException, InvocationTargetException {

        // 从csv中读取
        CsvTransactionDao.readAllCSV();
        TransactionManager.getInstance().currentUser = User.defaultUser;
        

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(lock);
        });

        // 等待主窗口关闭
        synchronized(lock) {
            try {
                lock.wait(); // 阻塞直到被唤醒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

//    // 交易管理器 TM
    //    TransactionManager TM = TransactionManager.getInstance();

    //    // 现在所有交易存储在TM.Transactions当中
    //    // Transaction类型为 Set<Transaction> 可用方法如下：
    //    // 读取Transactions的大小
    //    int size = TM.Transactions.size();
    //    System.out.println(size);

    //    // 遍历Transaction中的元素：使用for each的循环方法
    //    // 并打印其描述和金额（有些金额是转账，微信默认没有描述）
    //    for(Transaction t : TM.Transactions) {
    //        System.out.println("描述："+t.getDescription()+"||金额："+t.getAmount()+"分");
    //    }

    //    System.out.println("\n\n");

    //    // 新建一个 Transaction 对象a，初始化金额50元（5000分），新建对象将自动存入TM.Transactions集合
    //    Transaction a = new Transaction();
    //    a.setAmount(5000);
    //    a.setDescription("我是A");

    //    // 验证a是否在集合当中
    //    System.out.println("a是否在集合当中：" + TM.Transactions.contains(a));

    //    // 给a添加两个tag（tag目前默认存在"收入"和"支出"两个）
    //    // 再给a移除一个tag
    //    a.addTag("支出");
    //    a.addTag("饮食");
    //    a.addTag("住宿");
    //    a.removeTag("住宿");

    //    // 打印a含有的所有tag名字：
    //    Set<Tag> aTags = a.getTags();
    //    System.out.println("a元素包含的tag:");
    //    for(Tag t:aTags) {
    //        System.out.println(t.getName());
    //    }

    //    // 新建交易对象b
    //    Transaction b = new Transaction();
    //    b.setAmount(2000);
    //    b.setDescription("我是b");
    //    b.addTag("饮食");

    //    // 寻找所有该tag的交易对象
    //    Set<Transaction> sets = TM.getTransactionByTag("饮食", TM.Transactions);
    //    System.out.println("所有包含‘饮食’tag的交易描述：");
    //    for(Transaction t: sets) {
    //        System.out.println(t.getDescription());
    //    }

    //    // 添加一个手动输入的交易
    //    TransactionService transactionService = new TransactionService();
    //    transactionService.addTransactionManually();
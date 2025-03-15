package cn.edu.bupt;

import cn.edu.bupt.dao.CsvTransactionDao;
import cn.edu.bupt.utils.WeChatParser;
import cn.edu.bupt.model.*;

import java.util.ArrayList;

/*
 * maven自动生成的
 */
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * 用于单元测试的类
 */
public class AppTest {
    @Test
    public void shouldAnswerWithTrue() {  

        // TransactionManager TM = TransactionManager.getInstance();

        // CsvTransactionDao.readTransactionsFromCSV();
        WeChatParser.readWECHATfile("data/微信支付账单(20250203-20250303)——【解压密码可在微信支付公众号查看】.csv");
        // for(Tag t:TM.Tags) {
        //     System.out.println(t.getName());
        // }
        CsvTransactionDao.writeTransactionsToCSV();

        

        // Transaction a = new Transaction();
        // a.setAmount(100);
        // Transaction b = new Transaction();
        // b.setAmount(200);
        // Transaction c = new Transaction();
        // c.setAmount(300);
        // Transaction d = new Transaction();
        // d.setAmount(400);


        // Tag wechat = new Tag("WECHAT");
        // Tag user = new Tag("USER");

        // TM.addTagToTA(a, "USER");
        // TM.addTagToTA(b, "user");
        // TM.addTagToTA(c, user);
        // TM.addTagToTA(a, wechat);
        // for(Transaction t:TM.getTransactionByTag("user")) {
        //     System.out.println(t.getAmount());
        // }
        // for(Tag t: TM.getTagsForTransaction(a)) {
        //     System.out.println(t.getName());
        // }

    }
}

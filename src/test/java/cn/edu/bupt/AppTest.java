package cn.edu.bupt;

import cn.edu.bupt.dao.CsvTransactionDao;
import cn.edu.bupt.model.Transaction;

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
        System.out.println("Hello");

        Transaction x = new Transaction("111", "111", "111");

        System.out.println(x.getCreateTime());

        // ArrayList<String> a = CsvTransactionDao.readCsv();
        // System.out.println(a.get(0));
    }
}

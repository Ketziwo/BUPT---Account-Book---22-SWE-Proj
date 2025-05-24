package cn.edu.bupt.utils;

import java.io.File;

import cn.edu.bupt.dao.*;
import cn.edu.bupt.model.User;
import cn.edu.bupt.model.TransactionManager;

public final class AIAdvisor {
    public static void getBudgetAdvise(User user)  {
        String result = DeepSeekClient.getAnswer(DeepSeekClient.BudgetAdvisorAIprompt + CsvTransactionDao.readCSVtoString(new File(user.getBudgetPATH())), 8192);
        System.out.println(result);
        CsvTransactionDao.readBudgetsFromCSV(CsvTransactionDao.readCSV(result), TransactionManager.getInstance().currentUser);
    }
}

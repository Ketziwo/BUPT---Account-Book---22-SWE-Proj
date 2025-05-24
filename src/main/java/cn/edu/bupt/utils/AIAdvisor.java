package cn.edu.bupt.utils;

import java.io.File;

import cn.edu.bupt.dao.*;
import cn.edu.bupt.model.User;
import cn.edu.bupt.model.TransactionManager;

/**
 * AI Advisor utility class for providing intelligent budget recommendations.
 * This class uses DeepSeek AI to analyze transaction data and generate budget suggestions.
 */
public final class AIAdvisor {
    /**
     * Gets budget advice for a user by sending transaction data to the AI service.
     * 
     * @param user The user for whom to generate budget advice
     */
    public static void getBudgetAdvise(User user)  {
        String result = DeepSeekClient.getAnswer(DeepSeekClient.BudgetAdvisorAIprompt + CsvTransactionDao.readCSVtoString(new File(user.getBudgetPATH())), 8192);
        System.out.println(result);
        CsvTransactionDao.readBudgetsFromCSV(CsvTransactionDao.readCSV(result), TransactionManager.getInstance().currentUser);
    }
}

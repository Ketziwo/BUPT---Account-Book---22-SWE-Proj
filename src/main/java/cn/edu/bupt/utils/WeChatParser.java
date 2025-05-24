package cn.edu.bupt.utils;

import java.io.File;
import java.util.*;

import cn.edu.bupt.dao.*;
import cn.edu.bupt.model.*;

/**
 * Utility class for parsing WeChat payment CSV export files.
 * This class provides methods to read and process WeChat transaction records,
 * converting them into Transaction objects in the account book system.
 * 
 * The class supports both direct CSV parsing and AI-assisted parsing for
 * more complex or irregular export formats.
 * 
 * @author BUPT Account Book Team
 * @version 1.0
 */
public final class WeChatParser {    
    /**
     * Parses a WeChat payment CSV export file and creates Transaction objects.
     * The method expects a specific CSV format from WeChat payment exports,
     * with transaction data starting from line 17.
     * 
     * Transactions are automatically categorized as income or expense based on
     * the information in the CSV file, and tagged with "WECHAT" for tracking.
     * 
     * @param file The WeChat payment CSV export file to parse
     */
    public static void readWECHATfile(File file) {
        List<String[]> lines = CsvTransactionDao.readCSV(file);

        for(int i=17; i<lines.size(); ++i) {
            String[] line = lines.get(i);

            // 交易时间,交易类型,交易对方,商品,收/支,金额(元),支付方式,当前状态,交易单号,商户单号,备注
            // 2025-03-03 20:37:32,群收款,dynandu,"/",支出,¥2.58,零钱,支付成功,100004950125030300077141738419180280	,10000495012025030302202580549066	,"/"
            ArrayList<String> tags = new ArrayList<>();
            tags.add("WECHAT");

            int amount = 0;
            try{
                amount = Integer.parseInt(line[5].replaceAll("[^\\d]", ""));
            }catch(Exception e) {
                e.printStackTrace();
            }

            Transaction ta = new Transaction(TransactionManager.getInstance().currentUser, amount, line[0], line[3], tags); 
            
            if(line[4].equals("\"支出\"")) {TransactionTypeUtils.setIOcome(ta, Tag.EXPENSE);}
            else if (line[4].equals("\"收入\"")) {TransactionTypeUtils.setIOcome(ta, Tag.INCOME);}
            else {TransactionTypeUtils.setIOcome(ta, Tag.UNKNOWN);}
        }
    }    
    /**
     * Overloaded method to parse a WeChat payment CSV export file by path.
     * 
     * @param path The file path to the WeChat payment CSV export file
     */
    public static void readWECHATfile(String path) {
        readWECHATfile(new File(path));
    }

    /**
     * Parses a WeChat payment CSV export file using AI assistance.
     * This method uses the DeepSeekClient to process potentially complex
     * or irregular formats that the standard parser might not handle correctly.
     * 
     * @param path The file path to the WeChat payment CSV export file
     */
    public static void readWECHATfileByAI(String path) {
        String result = DeepSeekClient.getAnswer(DeepSeekClient.WechatTransactionsReaderAIprompt + CsvTransactionDao.readCSVtoString(new File(path)), 8192);
        // System.out.println(result);
        CsvTransactionDao.readTransactionsFromCSV(CsvTransactionDao.readCSV(result), TransactionManager.getInstance().currentUser);
    }
}

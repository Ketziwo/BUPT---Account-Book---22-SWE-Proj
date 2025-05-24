package cn.edu.bupt.utils;

import java.io.File;
import java.util.*;

import cn.edu.bupt.dao.*;
import cn.edu.bupt.model.*;

public final class WeChatParser {

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

    public static void readWECHATfile(String path) {
        readWECHATfile(new File(path));
    }

    public static void readWECHATfileByAI(String path) {
        String result = DeepSeekClient.getAnswer(DeepSeekClient.WechatTransactionsReaderAIprompt + CsvTransactionDao.readCSVtoString(new File(path)), 8192);
        // System.out.println(result);
        CsvTransactionDao.readTransactionsFromCSV(CsvTransactionDao.readCSV(result), TransactionManager.getInstance().currentUser);
    }
}

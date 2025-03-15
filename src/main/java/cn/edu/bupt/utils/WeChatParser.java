package cn.edu.bupt.utils;

import java.io.File;
import java.util.ArrayList;

import cn.edu.bupt.dao.CsvTransactionDao;
import cn.edu.bupt.model.Transaction;

public final class WeChatParser {

    public static void readWECHATfile(File file) {
        ArrayList<String> lines = CsvTransactionDao.readCSV(file);

        for(int i=17; i<lines.size(); ++i) {
            String line = lines.get(i);
			int size = line.length();
			int left=0;
			int right=0;

            ArrayList<String> tags = new ArrayList<>();
            tags.add("WECHAT");

            // 交易时间,交易类型,交易对方,商品,收/支,金额(元),支付方式,当前状态,交易单号,商户单号,备注
            // 2025-03-03 20:37:32,群收款,dynandu,"/",支出,¥2.58,零钱,支付成功,100004950125030300077141738419180280	,10000495012025030302202580549066	,"/"
			// 读取交易时间
			while(right<size && line.charAt(right) != ',') {right++;}
			String datetime = line.substring(left, right);
			right++;
			left = right;

            // 读取交易类型
            while(right<size && line.charAt(right) != ',') {right++;}
			// 无操作
			right++;
			left = right;

            // 读取交易对方
            while(right<size && line.charAt(right) != ',') {right++;}
			// 无操作
			right++;
			left = right;

            // 读取交易商品
            while(right<size && line.charAt(right) != ',') {right++;}
            String description = line.substring(left, right);
			right++;
			left = right;

            // 读取交易收支类型
            while(right<size && line.charAt(right) != ',') {right++;}
			switch(line.substring(left, right)) {
                case "支出": tags.add("支出");break;
                case "收入": tags.add("收入");break;
            }
			right++;
			left = right;

            // 读取交易金额
            while(right<size && line.charAt(right) != ',') {right++;}
            int amount = 0;
            try{
                String str = line.substring(left+1, right-3) + line.substring(right-2, right);
                amount = Integer.parseInt(str);
            }catch(Exception e) {} 
			right++;
			left = right;

            new Transaction(amount, datetime, description, tags);
        }
    }

    public static void readWECHATfile(String path) {
        readWECHATfile(new File(path));
    }
}

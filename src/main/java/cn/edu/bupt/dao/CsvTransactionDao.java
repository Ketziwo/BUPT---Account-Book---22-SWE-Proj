package cn.edu.bupt.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

import cn.edu.bupt.model.*;

public final class CsvTransactionDao {

	static private File file = new File("data/Transactions.csv");
     /**
     * BufferedReader 读取
     */
    public static ArrayList<String> readCSV() {
        try
		{
			ArrayList<String> data = new ArrayList<>();//list of lists to store data
			FileReader filereader = new FileReader(file);
			BufferedReader bufferedreader = new BufferedReader(filereader);
			
			//Reading until we run out of lines
			String line;

			while((line = bufferedreader.readLine()) != null) {
				data.add(line);
			}

			bufferedreader.close();

			return data;

		}
		catch(Exception e){
			e.printStackTrace();
		}
        return null;
    }

	public static void readTransactionsFromCSV() {
		ArrayList<String> csv = readCSV();
		if(csv == null)return;

		//transaction_id,datetime,amount,currency,transaction_type,category,source,description,tags,created_at,modified_at

		for(int i=1; i<csv.size(); ++i) {
			String line = csv.get(i);
			int size = line.length();
			int left=0;
			int right=0;

			// 读取交易ID
			while(right<size && line.charAt(right) != ',') {right++;}
			String tid = line.substring(left, right);
			right++;
			left = right;

			// 读取交易时间
			while(right<size && line.charAt(right) != ',') {right++;}
			String datetime = line.substring(left, right);
			right++;
			left = right;

			// 读取交易金额
			while(right<size && line.charAt(right) != ',') {right++;}
			int amount = 0;
			try{
				amount = Integer.parseInt(line.substring(left, right));
			}catch(Exception e){}
			right++;
			left = right;

			// 读取币种
			while(right<size && line.charAt(right) != ',') {right++;}
			Currency currency;
			switch(line.substring(left, right)) {
				case "CNY": currency = Currency.CNY;break; 
				case "USD": currency = Currency.USD;break;
				default:	currency = Currency.CNY;break;
			}
			right++;
			left = right;

			// 读取交易类型
			while(right<size && line.charAt(right) != ',') {right++;}
			TransactionType type;
			switch(line.substring(left, right)) {
				case "EXPENSE": type = TransactionType.EXPENSE;break; 
				case "INCOME": 	type = TransactionType.INCOME;break;
				default: 		type = TransactionType.EXPENSE;break;
			}
			right++;
			left = right;

			// 读取分类
			while(right<size && line.charAt(right) != ',') {right++;}
			Category category;
			switch(line.substring(left, right)) {
				case "DFT": 	category = Category.DFT;break;
				case "FOODS": 	category = Category.FOODS;break; 
				default: 		category = Category.DFT;break;
			}
			right++;
			left = right;

			// 读取交易来源
			while(right<size && line.charAt(right) != ',') {right++;}
			Source source;
			switch(line.substring(left, right)) {
				case "USER": 	source = Source.USER;break; 
				case "WECHAT":	source = Source.WECHAT;break;
				default: 		source = Source.USER;break;
			}
			right++;
			left = right;

			// 读取描述
			while(right<size && line.charAt(right) != ',') {right++;}
			String description = line.substring(left, right);
			right++;
			left = right;

			// 读取tag
			while(right<size && line.charAt(right) != ',') {right++;}
			String tags = line.substring(left, right);
			right++;
			left = right;

			// 读取创建时间
			while(right<size && line.charAt(right) != ',') {right++;}
			String create_at = line.substring(left, right);
			right++;
			left = right;

			// 读取修改时间
			while(right<size && line.charAt(right) != ',') {right++;}
			String modified_at = line.substring(left, right);

			new Transaction(tid, datetime, amount, currency, type, category, source, description, null, create_at, modified_at);
		}
	}

	public static void writeTransactionsToCSV() {

		try {
			
			FileWriter filewriter = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(filewriter);

			bw.write("transaction_id,datetime,amount,currency,transaction_type,category,source,description,tags,created_at,modified_at\n");

			String line = "";

			for(int i=0; i<Transaction.allTransactions.size(); ++i) {
				Transaction t = Transaction.allTransactions.get(i);
				
				line = t.getTransaction_id() + ',';
				line += t.getDatetime() + ',';
				line += t.getAmount() + ",";
				
				// 读取币种
				switch(t.getCurrency()) {
					case CNY: 	line+="CNY,";break; 
					case USD: 	line+="USD,";break;
					default:	line+="undefined currency!,";break;
				}

				// 读取交易类型
				switch(t.getType()) {
					case EXPENSE: 	line+="EXPENSE,";break; 
					case INCOME: 	line+="INCOME,";break;
					default: 		line+="undifined type!,";break;
				}

				// 读取分类
				switch(t.getCategory()) {
					case DFT:	line+= "DFT,";break;
					case FOODS:	line+= "FOODS,";break; 
					default: 	line+="undifined category,";break;
				}

				// 读取交易来源
				switch(t.getSource()) {
					case USER: 		line+="USER,";break; 
					case WECHAT:	line+="WECHAT,";break;
					default: 		line+="undifined source,";break;
				}

				line += t.getDescription() + ',';
				line += ',';
				line += t.getCreateTime() + ',';
				line += t.getModifiedTime() + '\n';

				bw.write(line);
			}
            
            bw.close();
			return;
        } catch(Exception e){
			e.printStackTrace();
		}
	}
}
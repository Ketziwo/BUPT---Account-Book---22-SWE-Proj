package cn.edu.bupt.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale.Category;

import javax.xml.transform.Source;

import cn.edu.bupt.model.*;

public final class CsvTransactionDao {

	static private File file = new File("data/Transactions.csv");
     /**
     * BufferedReader 读取
     */
    public static ArrayList<String> readCSV(File file) {
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
	public static ArrayList<String> readCSV(){return readCSV(file);} 

	public static void readTransactionsFromCSV() {
		ArrayList<String> csv = readCSV();
		if(csv == null)return;

		// transaction_id,amount,datetime,created_at,modified_at,description,tags

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

			// 读取交易金额
			while(right<size && line.charAt(right) != ',') {right++;}
			int amount = 0;
			try{
				amount = Integer.parseInt(line.substring(left, right));
			}catch(Exception e){}
			right++;
			left = right;

			// 读取交易时间
			while(right<size && line.charAt(right) != ',') {right++;}
			String datetime = line.substring(left, right);
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
			right++;
			left = right;

			// 读取描述
			while(right<size && line.charAt(right) != ',') {right++;}
			String description = line.substring(left, right);
			right++;
			left = right;

			// 读取tag
			ArrayList<String> tags = new ArrayList<>();
			while(right<size) {
				while(right<size && line.charAt(right) != '|') {right++;}
				tags.add(line.substring(left, right));
				right++;
				left = right;
			}

			new Transaction(tid, amount, datetime, create_at, modified_at, description);
		}
	}

	public static void writeTransactionsToCSV() {

		TransactionManager TM = TransactionManager.getInstance();

		try {
			
			FileWriter filewriter = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(filewriter);

			bw.write("transaction_id,amount,datetime,created_at,modified_at,description,tags\n");

			String line = "";

			for(Transaction t: TM.Transactions) {
				
				line = t.getTransaction_id() + ',';
				line += t.getAmount() + ",";
				line += t.getDatetime() + ',';
				line += t.getCreateTime() + ',';
				line += t.getModifiedTime() + ',';
				line += t.getDescription() + ',';

				for(Tag tag:TM.getTagsForTransaction(t)) {
					line += tag.getName() + '|';
				}

				line += '\n';
	
				bw.write(line);
			}
            
            bw.close();
			return;
        } catch(Exception e){
			e.printStackTrace();
		}
	}
}
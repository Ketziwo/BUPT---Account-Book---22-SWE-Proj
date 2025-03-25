package cn.edu.bupt.dao;

import cn.edu.bupt.model.Tag;
import cn.edu.bupt.model.Transaction;
import cn.edu.bupt.model.TransactionManager;

import java.io.*;
import java.util.ArrayList;

public final class CsvTransactionDao {
	private static File file = new File("data/Transactions.csv");

	public CsvTransactionDao() {
	}

	public static ArrayList<String> readCSV(File file) {
		try {
			ArrayList<String> data = new ArrayList<>();
			FileReader filereader = new FileReader(file);
			BufferedReader bufferedreader = new BufferedReader(filereader);

			String line;
			while ((line = bufferedreader.readLine()) != null) {
				data.add(line);
			}

			bufferedreader.close();
			return data;
		} catch (Exception var5) {
			var5.printStackTrace();
			return null;
		}
	}

	public static ArrayList<String> readCSV() {
		return readCSV(file);
	}

	public static void readTransactionsFromCSV() {
		ArrayList<String> csv = readCSV();
		if (csv != null) {
			TransactionManager TM = TransactionManager.getInstance();
			TM.Transactions.clear();
			for (int i = 1; i < csv.size(); ++i) { // Skip the header
				String line = csv.get(i);
				String[] parts = line.split(",");
				if (parts.length >= 7) {
					String tid = parts[0];
					int amount = Integer.parseInt(parts[1]);
					String datetime = parts[2];
					String create_at = parts[3];
					String modified_at = parts[4];
					String description = parts[5];
					String[] tagParts = parts[6].split("\\|");
					ArrayList<String> tags = new ArrayList<>();
					for (String tag : tagParts) {
						tags.add(tag);
					}
					Transaction transaction = new Transaction(tid, amount, datetime, create_at, modified_at, description, tags);
					TM.Transactions.add(transaction);
				}
			}
		}
	}

	public static void writeTransactionsToCSV() {
		TransactionManager TM = TransactionManager.getInstance();

		try {
			FileWriter filewriter = new FileWriter(file); // Overwrite the file
			BufferedWriter bw = new BufferedWriter(filewriter);
			bw.write("transaction_id,amount,datetime,created_at,modified_at,description,tags\n");
			for (Transaction t : TM.Transactions) {
				String line = t.getTransaction_id() + "," +
						t.getAmount() + "," +
						t.getDatetime() + "," +
						t.getCreateTime() + "," +
						t.getModifiedTime() + "," +
						t.getDescription() + "," +
						String.join("|", t.getTags().stream().map(Tag::getName).toArray(String[]::new)) + "\n";
				bw.write(line);
			}
			bw.close();
		} catch (Exception var8) {
			var8.printStackTrace();
		}
	}

	public static void appendTransactionToCSV(Transaction transaction) {
		try {
			FileWriter filewriter = new FileWriter(file, true); // Append mode
			BufferedWriter bw = new BufferedWriter(filewriter);
			String line = transaction.getTransaction_id() + "," +
					transaction.getAmount() + "," +
					transaction.getDatetime() + "," +
					transaction.getCreateTime() + "," +
					transaction.getModifiedTime() + "," +
					transaction.getDescription() + "," +
					String.join("|", transaction.getTags().stream().map(Tag::getName).toArray(String[]::new)) + "\n";
			bw.write(line);
			bw.close();
		} catch (Exception var8) {
			var8.printStackTrace();
		}
	}
}
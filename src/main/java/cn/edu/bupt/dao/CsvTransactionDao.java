package cn.edu.bupt.dao;

import cn.edu.bupt.model.*;

import java.io.*;
import java.util.*;

/**
 * Data Access Object for CSV file operations in the account book system.
 * This class provides methods to read from and write to CSV files for
 * storing transactions, budgets, and user data.
 * 
 * The class handles all persistence operations including loading data at
 * application startup and saving data when changes are made.
 * 
 * @author BUPT Account Book Team
 * @version 1.0
 */
public final class CsvTransactionDao {
	// private static File file = new File("data/Transactions.csv");

	public CsvTransactionDao() {
	}
	/**
	 * Reads all CSV files for the application.
	 * This method loads all users from the allUsers.csv file and then
	 * reads their respective transaction and budget files.
	 * It should be called at application startup to initialize the data.
	 */
	public static void readAllCSV() {
		File allUsers = new File("data/allUsers.csv");
		Set<User> users = readUsersFromCSV(allUsers);
		for(User user: users){
			// System.out.println(user.getname());
			File Transactionfile = new File(user.getTransactionPATH());
			readTransactionsFromCSV(Transactionfile, user);

			File Budgetfile = new File(user.getBudgetPATH());
			readBudgetsFromCSV(Budgetfile, user);
		}
	}
	/**
	 * Updates all CSV files with the current data in memory.
	 * This method writes all users to the allUsers.csv file and then
	 * updates their respective transaction and budget files.
	 * It should be called whenever data changes need to be persisted.
	 */
	public static void updateAllCSV() {
		File allUsers = new File("data/allUsers.csv");
		try{
			FileWriter filewriter = new FileWriter(allUsers); // Overwrite the file
			BufferedWriter userbw = new BufferedWriter(filewriter);

			String userLine = "";
			for(User user:TransactionManager.getInstance().Users) {
				userLine = userLine.concat(user.getname()+","+user.getPwd()+"\n");

				File Transactionfile = new File(user.getTransactionPATH());
				File Budgetfile = new File(user.getBudgetPATH());
				try{
					Transactionfile.createNewFile();
					Budgetfile.createNewFile();
				} catch(Exception e){}
				writeTransactionsToCSV(Transactionfile, user.getTransactions());
				writeBudgetsToCSV(Budgetfile, user.getBudgets());
			}
			userbw.write(userLine);
			userbw.close();
		} catch(Exception e){}
	}
	/**
	 * Reads a CSV file and returns its contents as a single string.
	 * This is useful for processing the file content with AI services.
	 * 
	 * @param file The CSV file to read
	 * @return A string containing the entire file content, or null if an error occurs
	 */
	public static String readCSVtoString(File file) {
		try {
			FileReader filereader = new FileReader(file);
			BufferedReader bufferedreader = new BufferedReader(filereader);

			String line, result = "";
			while ((line = bufferedreader.readLine()) != null) {
				result += line;
			}

			bufferedreader.close();
			return result;
		} catch(Exception e) {return null;}
	}
	/**
	 * Reads a CSV file and parses it into an array of string arrays.
	 * Each inner array represents a row in the CSV file, with elements
	 * corresponding to comma-separated values.
	 * 
	 * The method uses a regex pattern to handle commas within quoted fields correctly.
	 * 
	 * @param file The CSV file to parse
	 * @return An ArrayList of string arrays representing the CSV data
	 */
	public static ArrayList<String[]> readCSV(File file) {
		try {
			ArrayList<String[]> data = new ArrayList<>();
			FileReader filereader = new FileReader(file);
			BufferedReader bufferedreader = new BufferedReader(filereader);

			String line;
			while ((line = bufferedreader.readLine()) != null) {
				String[] items = line.trim().split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1);
				data.add(items);
			}

			bufferedreader.close();
			return data;
		} catch (Exception var5) {
			var5.printStackTrace();
			return null;
		}
	}
	/**
	 * Parses a CSV string into an array of string arrays.
	 * This is particularly useful when processing CSV data from an external source
	 * or from AI-generated content.
	 * 
	 * The method handles quoted fields and trims whitespace from each field.
	 * 
	 * @param csvString The CSV content as a string
	 * @return An ArrayList of string arrays representing the CSV data
	 */
	public static ArrayList<String[]> readCSV(String csvString) {
        ArrayList<String[]> result = new ArrayList<>();
        
        if (csvString == null || csvString.isEmpty()) {
            return result;
        }
        
        // 按行分割字符串
        String[] lines = csvString.split("\n");
        
        // 处理每一行
        for (String line : lines) {
            // 跳过空行
            if (line.trim().isEmpty()) {
                continue;
            }
            
            // 按逗号分割行，保留引号内的内容
            String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            
            // 去除每个字段的首尾空格和可能的引号
            for (int i = 0; i < fields.length; i++) {
                fields[i] = fields[i].trim();
                if (fields[i].startsWith("\"") && fields[i].endsWith("\"")) {
                    fields[i] = fields[i].substring(1, fields[i].length() - 1);
                }
            }
            
            result.add(fields);
        }
        
        return result;
    }
	/**
	 * Reads user data from a CSV file and creates User objects.
	 * The expected CSV format is username,password on each line.
	 * 
	 * The method handles the special case of the "default" user.
	 * All users are added to the TransactionManager's user registry.
	 * 
	 * @param file The CSV file containing user data
	 * @return A Set of User objects created from the file
	 */
	public static Set<User> readUsersFromCSV(File file) {
		Set<User> users = new HashSet<>();
		List<String[]> csv = readCSV(file); // 假设该方法正确读取文件所有行
		
		for (String[] line : csv) {
			if (!line[0].isEmpty()) {
				User u;
				if(line[0].equals("default")) {
					u = User.defaultUser;
				}
				else {
					u = new User(line[0], line[1]);
				}
				users.add(u); // 自动去重（依赖User的equals/hashCode）
				TransactionManager.getInstance().Users.add(u);
			}
		}
		
		return users;
	}
	/**
	 * Parses transaction data from a CSV structure and creates Transaction objects.
	 * The method expects a specific CSV format with transaction details including
	 * ID, amount, dates, description, and tags.
	 * 
	 * The first row (header) is skipped during processing.
	 * 
	 * @param csv A List of string arrays containing transaction data
	 * @param user The user to associate with the transactions
	 */
	public static void readTransactionsFromCSV(List<String[]> csv, User user) {
		csv.remove(0);
		for (String[] parts:csv) { // Skip the header
			// String line = csv.get(i);
			// String[] parts = line.split(",");
			if (parts.length >= 7) {
				String tid = parts[0];
				int amount = 0;
				amount = Integer.parseInt(parts[1]);
					
				String datetime = parts[2];
				String create_at = parts[3];
				String modified_at = parts[4];
				String description = parts[5];
				String[] tagParts = parts[6].split("\\|");
				ArrayList<String> tags = new ArrayList<>();
				for (String tag : tagParts) {
					tags.add(tag);
				}
				Transaction transaction = new Transaction(tid, user, amount, datetime, create_at, modified_at, description, tags);
				TransactionManager.getInstance().Transactions.add(transaction);
			}
		}
	}

	public static void readTransactionsFromCSV(File file, User user) {
		List<String[]> csv = readCSV(file);
		if (csv != null) {
			readTransactionsFromCSV(csv, user);
		}
	}

	public static void readBudgetsFromCSV(List<String[]> csv, User user) {
		csv.remove(0); // 跳过表头
		for (String[] parts : csv) {
			try {
				if (parts.length < 4) continue;

				// 解析数值型字段
				int amount = Integer.parseInt(parts[0].trim());
				
				// 解析时间字段
				String start = parts[1].trim();
				String end = parts[2].trim();

				String description = parts[3].trim();
				
				// 解析标签集合
				ArrayList<String> tags = new ArrayList<>();
				for (String tagName : parts[4].split("\\|")) {
					tags.add(tagName); // 假设Tag构造函数支持名称创建
				}

				// 自动加入TransactionManager（通过构造函数）
				new Budget(user, amount, tags, start, end, description);
				
			} catch (Exception e) {
				System.err.println("解析预算数据失败: " + Arrays.toString(parts));
			}
		}
	}

	public static void readBudgetsFromCSV(File file, User user) {
		List<String[]> csv = readCSV(file);
		if (csv == null || csv.isEmpty()) return;

		readBudgetsFromCSV(csv, user);
	}

	public static void writeTransactionsToCSV(File file, Set<Transaction> tas) {
		try {
			FileWriter filewriter = new FileWriter(file); // Overwrite the file
			BufferedWriter bw = new BufferedWriter(filewriter);
			bw.write("transaction_id,amount,datetime,created_at,modified_at,description,tags\n");
			for (Transaction t : tas) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeBudgetsToCSV(File file, Set<Budget> bgs) {
		try {
			FileWriter filewriter = new FileWriter(file); // Overwrite the file
			BufferedWriter bw = new BufferedWriter(filewriter);
			bw.write("amount,starttime,endtime,description,tags\n");
			for (Budget b : bgs) {
				String line = b.getAmount() + "," +
						b.getStartDateTime() + "," +
						b.getEndDateTime() + "," +
						b.getDescription() + "," +
						String.join("|", b.getTags().stream().map(Tag::getName).toArray(String[]::new)) + "\n";
				bw.write(line);
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
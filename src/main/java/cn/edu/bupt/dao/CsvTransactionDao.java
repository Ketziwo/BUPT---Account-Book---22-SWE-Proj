package cn.edu.bupt.dao;

import cn.edu.bupt.model.*;

import java.io.*;
import java.security.spec.ECFieldF2m;
import java.util.*;

public final class CsvTransactionDao {
	// private static File file = new File("data/Transactions.csv");

	public CsvTransactionDao() {
	}

	public static void readAllCSV() {
		File allUsers = new File("data/allUsers.csv");
		Set<User> users = readUsersFromCSV(allUsers);
		for(User user: users){
			// System.out.println(user.getname());
			File tafile = new File(user.getpath());
			readTransactionsFromCSV(tafile, user);
		}
	}

	public static void updateAllCSV() {
		File allUsers = new File("data/allUsers.csv");
		try{
			FileWriter filewriter = new FileWriter(allUsers); // Overwrite the file
			BufferedWriter userbw = new BufferedWriter(filewriter);

			String userLine = "";
			for(User user:TransactionManager.getInstance().Users) {
				userLine = userLine.concat(user.getname()+","+user.getPwd()+"\n");

				File userfile = new File("data/Transaction_"+user.getname()+".csv");
				try{
					userfile.createNewFile();
				} catch(Exception e){}
				writeTransactionsToCSV(userfile, user.getTransactions());
			}
			userbw.write(userLine);
			userbw.close();
		} catch(Exception e){}
	}

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

	public static void readTransactionsFromCSV(File file, User user) {
		List<String[]> csv = readCSV(file);
		if (csv != null) {
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
}
package cn.edu.bupt.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.ArrayList;

import cn.edu.bupt.model.Transaction;

public final class CsvTransactionDao {
     /**
     * BufferedReader 读取
     */
    public static ArrayList<String> readCsv() {
        try
		{
			ArrayList<String> data = new ArrayList<>();//list of lists to store data
			String path = "data/Transactions.csv";//file path
			File file = new File(path);
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

}
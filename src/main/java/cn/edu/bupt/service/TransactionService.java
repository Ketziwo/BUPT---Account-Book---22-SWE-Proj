package cn.edu.bupt.service;

import cn.edu.bupt.dao.CsvTransactionDao;
import cn.edu.bupt.model.*;
import cn.edu.bupt.utils.DateUtils;

import java.util.ArrayList;
import java.util.Scanner;

public class TransactionService {
    private static String lastDate = DateUtils.getDate();
    private static int num = 0;
    private final Scanner scanner = new Scanner(System.in);

    public TransactionService() {
        // 读取 CSV 以确定最新交易编号
        ArrayList<String> transactions = CsvTransactionDao.readCSV();
        if (transactions != null && transactions.size() > 1) {
            String lastTransaction = transactions.get(transactions.size() - 1);
            String[] parts = lastTransaction.split(",");
            if (parts.length > 0) {
                String[] idParts = parts[0].split("-");
                if (idParts.length == 2 && idParts[0].equals(lastDate)) {
                    try {
                        num = Integer.parseInt(idParts[1]) + 1;
                    } catch (NumberFormatException e) {
                        System.err.println("CSV 文件中的交易编号格式错误: " + parts[0]);
                    }
                }
            }
        }
    }

    // 手动输入交易
    public void addTransactionManually() {
        System.out.println("请输入交易信息：");

        // 生成唯一交易 ID
        String transactionId = lastDate + "-" + (num++);

        // 交易金额
        int amount = getIntInput("交易金额 (单位: 分)：");

        // 交易时间
        String datetime = DateUtils.getDatetime();

        // 交易描述
        System.out.print("交易描述：");
        String description = scanner.nextLine().trim();

        // 交易标签
        System.out.print("交易标签 (用逗号分隔)：");
        String[] tagInputs = scanner.nextLine().split(",");
        ArrayList<String> tags = new ArrayList<>();
        for (String tag : tagInputs) {
            if (!tag.trim().isEmpty()) {
                tags.add(tag.trim());
            }
        }

        // 创建交易对象
        Transaction transaction = new Transaction(transactionId, amount, datetime, datetime, datetime, description, tags);

        // **添加到 TransactionManager**
        TransactionManager.getInstance().Transactions.add(transaction);

        // **保存到 CSV**
        CsvTransactionDao.writeTransactionsToCSV();

        System.out.println("交易添加成功！");
    }

    // 获取整数输入
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("输入无效，请输入整数！");
            }
        }
    }

//    // 0收入1 支出
////    public void aaa(amount, time, description, 支出还是收入？,如果是支出属于什么类型，个人输入还是微信导入,ArrayList<String> s)
//    if(1){a.addtag("支出")}
//    if(10){a。addtag("旅游")}
//    if(0){a.addtag(“WECHAT”)}USER
//    for(){
//        a.addtag(s.get(i))
//    }

}

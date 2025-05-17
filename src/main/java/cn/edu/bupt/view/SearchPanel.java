package cn.edu.bupt.view;

import cn.edu.bupt.model.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class SearchPanel extends JPanel {
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private JLabel balanceLabel;
    private JPanel listPanel;

    Set<Transaction> Tset;

    public SearchPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 顶部按钮区域
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton timeFilterBtn = new JButton("全部");
        JButton tagFilterBtn = new JButton("筛选");
        JButton refreshBtn = new JButton("刷新");
        topPanel.add(timeFilterBtn);
        topPanel.add(tagFilterBtn);
        topPanel.add(refreshBtn);

        // 统计信息区域
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 0));
        JLabel t1 = new JLabel("收入");
        t1.setSize(150, 30);
        statsPanel.add(t1);
        JLabel t2 = new JLabel("支出");
        t2.setSize(150, 30);
        statsPanel.add(t2);
        JLabel t3 = new JLabel("结余");
        t3.setSize(150, 30);
        statsPanel.add(t3);

        incomeLabel = new JLabel("0.00 ");
        incomeLabel.setSize(150, 70);
        expenseLabel = new JLabel("0.00 ");
        expenseLabel.setSize(150, 70);
        balanceLabel = new JLabel("0.00 ");
        balanceLabel.setSize(150, 70);
        statsPanel.add(incomeLabel);
        statsPanel.add(expenseLabel);
        statsPanel.add(balanceLabel);

        listPanel = new JPanel();
        refresh(TransactionManager.getInstance().currentUser.getTransactions());

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setSize(500, 100);
        searchPanel.add(statsPanel, BorderLayout.NORTH);
        searchPanel.add(listPanel, BorderLayout.CENTER);

        JScrollPane searchPane = new JScrollPane(searchPanel);

        // // 表格区域
        // tableModel = new TransactionTableModel();
        // JTable table = new JTable(tableModel);
        // table.setAutoCreateRowSorter(false);
        // JScrollPane scrollPane = new JScrollPane(table);

        // 整体布局
        add(topPanel, BorderLayout.NORTH);
        add(searchPane, BorderLayout.CENTER);
        // add(scrollPane, BorderLayout.SOUTH);

        // updateData();
    }

    public void refresh(Set<Transaction> Tset){
        this.Tset = Tset;
        refresh();
    }

    public void refresh() {
        List<Transaction> Tlist = new ArrayList<>(Tset);
        Tlist.sort((t1, t2) -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        return sdf.parse(t2.getDatetime()).compareTo(sdf.parse(t1.getDatetime()));
                    } catch (ParseException e) {
                        return 0;
                    }
                });

        listPanel.removeAll();
        listPanel.repaint();

        int expense = 0;
        int income = 0;

        TransactionManager TM = TransactionManager.getInstance();
        listPanel.setLayout(new GridLayout(Tlist.size(),1));
        for(Transaction t: Tlist) {
            JButton jb = new JButton(t.getDescription());
            jb.setPreferredSize(new Dimension(400, 50));
            listPanel.add(jb);
            if(t.getTags().contains(TM.tagRegistry.get("支出"))) {
                expense += t.getAmount();
            }else if (t.getTags().contains(TM.tagRegistry.get("收入"))) {
                income += t.getAmount();
            }
        }
        listPanel.revalidate();
        listPanel.repaint();

        incomeLabel.setText(income+"");
        expenseLabel.setText(expense+"");
        balanceLabel.setText((income-expense)+"");
    }

    // public void updateData() {
    //     // 获取排序后的交易记录
    //     List<Transaction> sortedTransactions = new ArrayList<>(TransactionManager.getInstance().Transactions);
    //     sortedTransactions.sort((t1, t2) -> {
    //         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //         try {
    //             return sdf.parse(t2.getDatetime()).compareTo(sdf.parse(t1.getDatetime()));
    //         } catch (ParseException e) {
    //             return 0;
    //         }
    //     });

    //     // 更新表格数据
    //     tableModel.setTransactions(sortedTransactions);

    //     // 计算统计数据
    //     double income = 0;
    //     double expense = 0;
    //     for (Transaction t : TransactionManager.getInstance().Transactions) {
    //         double amount = t.getAmount() / 100.0;
    //         if (amount > 0) {
    //             income += amount;
    //         } else {
    //             expense += amount;
    //         }
    //     }

    //     // 更新统计显示
    //     NumberFormat nf = NumberFormat.getNumberInstance();
    //     incomeLabel.setText("收入：" + nf.format(income) + " 元");
    //     expenseLabel.setText("支出：" + nf.format(expense) + " 元");
    //     balanceLabel.setText("结余：" + nf.format(income + expense) + " 元");
    // }

    // // 自定义表格模型
    // private static class TransactionTableModel extends AbstractTableModel {
    //     private final String[] COLUMNS = {"时间", "描述", "金额（元）"};
    //     private List<Transaction> transactions = new ArrayList<>();

    //     public void setTransactions(List<Transaction> transactions) {
    //         this.transactions = transactions;
    //         fireTableDataChanged();
    //     }

    //     @Override
    //     public int getRowCount() {
    //         return transactions.size();
    //     }

    //     @Override
    //     public int getColumnCount() {
    //         return COLUMNS.length;
    //     }

    //     @Override
    //     public String getColumnName(int column) {
    //         return COLUMNS[column];
    //     }

    //     @Override
    //     public Object getValueAt(int rowIndex, int columnIndex) {
    //         Transaction t = transactions.get(rowIndex);
    //         switch (columnIndex) {
    //             case 0: return t.getDatetime();
    //             case 1: return t.getDescription();
    //             case 2: return t.getAmount() / 100.0;
    //             default: return null;
    //         }
    //     }

    //     @Override
    //     public Class<?> getColumnClass(int columnIndex) {
    //         return columnIndex == 2 ? Double.class : String.class;
    //     }
    // }
}

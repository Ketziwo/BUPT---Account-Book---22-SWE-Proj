// ManagerPanel.java
package cn.edu.bupt.view;

import cn.edu.bupt.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ManagerPanel extends JPanel {
    private static final String[] COLUMN_NAMES = {
        "ID", "Amount", "Datetime", "Created At", "Description", "Tags"
    };
    
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
    private final JTable table = new JTable(tableModel);
    private final List<Transaction> dataSnapshot = new ArrayList<>();

    public ManagerPanel() {
        initUI();
        resetTable();

        //启动table监听
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // 初始化界面方法
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        // 添加按钮
        JButton addButton = new JButton("添加/修改");
        addButton.addActionListener(e -> showTransactionDialog());
        toolBar.add(addButton);
        
        // 刷新按钮
        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> resetTable());
        toolBar.add(refreshButton);

        // 刷新按钮
        JButton FilterButton = new JButton("筛选");
        FilterButton.addActionListener(e -> showFilterDialog());
        toolBar.add(FilterButton);

        add(scrollPane, BorderLayout.CENTER);
        add(toolBar, BorderLayout.SOUTH);
    }

    // 表格重置方法
    private void resetTable() {
        // 刷新数据
        dataSnapshot.clear();
        dataSnapshot.addAll(TransactionManager.getInstance().currentUser.getTransactions());

        // 重载表格数据
        tableModel.setRowCount(0);
        dataSnapshot.forEach(transaction -> 
            tableModel.addRow(createTableRow(transaction))
        );
    }

    // 创建表格行数据
    private Object[] createTableRow(Transaction t) {
        DecimalFormat df = new DecimalFormat("#.00");
        String amount = df.format(new BigDecimal(t.getAmount()).divide(new BigDecimal(100))).toString();

        TransactionManager tm = TransactionManager.getInstance();

        List<String> tagNames = new ArrayList<>();
        for (Tag tag : t.getTags()) {
            if(tag == Tag.EXPENSE || tag == Tag.INCOME)continue;
            tagNames.add(tag.getName());
        }
        String DIYtags = String.join("|", tagNames);

        return new Object[]{
            t.getTransaction_id(),
            amount,
            t.getDatetime(),
            t.getCreateTime(),
            t.getDescription(),
            DIYtags
        };
    }

    // 筛选对话框相关方法
    private void showFilterDialog() {
        FilterDialog dialog = new FilterDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            true,
            this::applyFilters
        );
        dialog.setVisible(true);
    }

    private void applyFilters(FilterDialog.FilterCriteria criteria) {
        tableModel.setRowCount(0);
        dataSnapshot.stream()
            .filter(t -> matchesDescription(t, criteria.description))
            .filter(t -> withinDateRange(t, criteria.startDate, criteria.endDate))
            .filter(t -> withinAmountRange(t, criteria.minAmount, criteria.maxAmount))
            .forEach(t -> tableModel.addRow(createTableRow(t)));
    }

    // 筛选条件判断方法
    private boolean matchesDescription(Transaction t, String keyword) {
        return keyword.isEmpty() || 
            t.getDescription().toLowerCase().contains(keyword.toLowerCase());
    }

    private boolean withinDateRange(Transaction t, String start, String end) {
        String date = t.getDatetime();
        return (start.isEmpty() || date.compareTo(start) >= 0) &&
               (end.isEmpty() || date.compareTo(end) <= 0);
    }

    private boolean withinAmountRange(Transaction t, Double min, Double max) {
        double amount = t.getAmount() / 100.0;
        boolean minValid = (min == null) || (amount >= min);
        boolean maxValid = (max == null) || (amount <= max);
        return minValid && maxValid;
    }

    // 编辑功能相关方法
    private void showTransactionDialog() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            TransactionDialog dialog = new TransactionDialog((Frame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
        }
        else {
            String transactionId = (String) tableModel.getValueAt(selectedRow, 0);
            Transaction transaction = findTransactionById(transactionId);
            if (transaction == null)return;
            TransactionDialog dialog = new TransactionDialog((Frame) SwingUtilities.getWindowAncestor(this), transaction);
            dialog.setVisible(true);
        }
        resetTable();
    }

    private Transaction findTransactionById(String id) {
        return dataSnapshot.stream()
            .filter(t -> t.getTransaction_id().equals(id))
            .findFirst()
            .orElse(null);
    }

    private void handleUpdateResult(Transaction updated, int selectedRow) {
        if (updated != null) {
            updateDataSource(updated);
            updateTableRow(updated, selectedRow);
        }
    }

    private void updateDataSource(Transaction updated) {
        TransactionManager.getInstance().currentUser.getTransactions()
            .stream()
            .filter(t -> t.getTransaction_id().equals(updated.getTransaction_id()))
            .findFirst()
            .ifPresent(t -> {
                t.setAmount(updated.getAmount());
                t.setDatetime(updated.getDatetime());
                t.setDescription(updated.getDescription());
            });
    }

    private void updateTableRow(Transaction updated, int row) {
        tableModel.setValueAt(updated.getAmount() / 100.0, row, 1);
        tableModel.setValueAt(updated.getDatetime(), row, 2);
        tableModel.setValueAt(updated.getModifiedTime(), row, 4);
        tableModel.setValueAt(updated.getDescription(), row, 5);
    }

}
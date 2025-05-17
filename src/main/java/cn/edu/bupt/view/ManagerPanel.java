// ManagerPanel.java
package cn.edu.bupt.view;

import cn.edu.bupt.model.Transaction;
import cn.edu.bupt.model.TransactionManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ManagerPanel extends JPanel {
    private static final String[] COLUMN_NAMES = {
        "Transaction ID", "Amount", "Datetime", 
        "Created At", "Modified At", "Description", "Tags"
    };
    
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
    private final JTable table = new JTable(tableModel);
    private final List<Transaction> dataSnapshot = new ArrayList<>();

    public ManagerPanel() {
        initUI();
        initData();
        initEventListeners();
    }

    // 初始化界面方法
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        actionPanel.add(createActionButton("刷新", this::resetTable));
        actionPanel.add(createActionButton("修改", this::editSelectedRow));
        actionPanel.add(createActionButton("筛选", this::showFilterDialog));

        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    // 数据初始化方法
    private void initData() {
        refreshDataSnapshot();
        reloadTableData();
    }

    // 事件监听初始化
    private void initEventListeners() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // 按钮创建方法
    private JButton createActionButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> action.run());
        return button;
    }

    // 数据刷新方法
    private void refreshDataSnapshot() {
        dataSnapshot.clear();
        dataSnapshot.addAll(
            TransactionManager.getInstance().currentUser.getTransactions()
        );
    }

    // 表格数据重载
    private void reloadTableData() {
        tableModel.setRowCount(0);
        dataSnapshot.forEach(transaction -> 
            tableModel.addRow(createTableRow(transaction))
        );
    }

    // 创建表格行数据
    private Object[] createTableRow(Transaction t) {
        return new Object[]{
            t.getTransaction_id(),
            t.getAmount() / 100.0, // 分转元
            t.getDatetime(),
            t.getCreateTime(),
            t.getModifiedTime(),
            t.getDescription(),
            String.join("|", t.getTags().stream()
                .map(tag -> tag.getName())
                .toArray(String[]::new))
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

    // 表格重置方法
    private void resetTable() {
        refreshDataSnapshot();
        reloadTableData();
    }

    // 编辑功能相关方法
    private void editSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("请先选择要修改的记录");
            return;
        }

        String transactionId = (String) tableModel.getValueAt(selectedRow, 0);
        Transaction transaction = findTransactionById(transactionId);
        
        if (transaction != null) {
            new EditDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                transaction,
                updated -> handleUpdateResult(updated, selectedRow)
            ).setVisible(true);
        }
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

    // 工具方法
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "操作提示",
            JOptionPane.WARNING_MESSAGE
        );
    }
}
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

/**
 * Transaction management panel for displaying and managing transactions
 * 交易管理面板，用于显示和管理交易记录
 */
public class ManagerPanel extends JPanel {
    private static final String[] COLUMN_NAMES = {
        "ID", "Amount", "Datetime", "Created At", "Description", "Tags"
    };
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
    private final JTable table = new JTable(tableModel);
    private final List<Transaction> dataSnapshot = new ArrayList<>();

    /**
     * Constructor for the transaction manager panel
     * 交易管理面板的构造函数
     */
    public ManagerPanel() {
        initUI();
        resetTable();

        // Set up table listener / 启动table监听
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }    
    
    /**
     * Initialize UI components
     * 初始化界面组件
     */
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        // Add button / 添加按钮
        JButton addButton = new JButton("Add/Edit");
        addButton.addActionListener(e -> showTransactionDialog());
        toolBar.add(addButton);
        
        // Refresh button / 刷新按钮
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> resetTable());
        toolBar.add(refreshButton);

        // Filter button / 筛选按钮
        JButton FilterButton = new JButton("Filter");
        FilterButton.addActionListener(e -> showFilterDialog());
        toolBar.add(FilterButton);

        add(scrollPane, BorderLayout.CENTER);
        add(toolBar, BorderLayout.SOUTH);
    }    
    
    /**
     * Reset table with all transactions
     * 重置表格，显示所有交易
     */
    private void resetTable() {
        // Refresh data / 刷新数据
        dataSnapshot.clear();
        dataSnapshot.addAll(TransactionManager.getInstance().currentUser.getTransactions());

        // Reload table data / 重载表格数据
        tableModel.setRowCount(0);
        dataSnapshot.forEach(transaction -> 
            tableModel.addRow(createTableRow(transaction))
        );
    }    
    
    /**
     * Create table row data from a transaction
     * 从交易记录创建表格行数据
     * 
     * @param t Transaction to display / 要显示的交易记录
     * @return Array of cell values / 单元格值数组
     */
    private Object[] createTableRow(Transaction t) {
        DecimalFormat df = new DecimalFormat("#.00");
        String amount = df.format(new BigDecimal(t.getAmount()).divide(new BigDecimal(100))).toString();

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
    
    /**
     * Show filter dialog to filter transactions
     * 显示筛选对话框以筛选交易记录
     */
    private void showFilterDialog() {
        FilterDialog dialog = new FilterDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            true,
            this::applyFilters
        );
        dialog.setVisible(true);
    }    
    
    /**
     * Apply filter criteria to the transaction table
     * 将筛选条件应用到交易表格
     * 
     * @param criteria Filter criteria / 筛选条件
     */
    private void applyFilters(FilterDialog.FilterCriteria criteria) {
        tableModel.setRowCount(0);
        dataSnapshot.stream()
            .filter(t -> matchesDescription(t, criteria.description))
            .filter(t -> withinDateRange(t, criteria.startDate, criteria.endDate))
            .filter(t -> withinAmountRange(t, criteria.minAmount, criteria.maxAmount))
            .forEach(t -> tableModel.addRow(createTableRow(t)));
    }    
    
    // Filter condition methods / 筛选条件判断方法
    
    /**
     * Check if transaction description contains keyword
     * 检查交易描述是否包含关键词
     * 
     * @param t Transaction to check / 要检查的交易
     * @param keyword Keyword to search for / 要搜索的关键词
     * @return True if matches / 如果匹配则为true
     */
    private boolean matchesDescription(Transaction t, String keyword) {
        return keyword.isEmpty() || 
            t.getDescription().toLowerCase().contains(keyword.toLowerCase());
    }

    /**
     * Check if transaction date is within range
     * 检查交易日期是否在范围内
     * 
     * @param t Transaction to check / 要检查的交易
     * @param start Start date / 开始日期
     * @param end End date / 结束日期
     * @return True if within range / 如果在范围内则为true
     */
    private boolean withinDateRange(Transaction t, String start, String end) {
        String date = t.getDatetime();
        return (start.isEmpty() || date.compareTo(start) >= 0) &&
               (end.isEmpty() || date.compareTo(end) <= 0);
    }    
    
    /**
     * Check if transaction amount is within range
     * 检查交易金额是否在范围内
     * 
     * @param t Transaction to check / 要检查的交易
     * @param min Minimum amount / 最小金额
     * @param max Maximum amount / 最大金额
     * @return True if within range / 如果在范围内则为true
     */
    private boolean withinAmountRange(Transaction t, Double min, Double max) {
        double amount = t.getAmount() / 100.0;
        boolean minValid = (min == null) || (amount >= min);
        boolean maxValid = (max == null) || (amount <= max);
        return minValid && maxValid;
    }    
    
    /**
     * Show transaction dialog to add or edit a transaction
     * 显示交易对话框以添加或编辑交易
     */
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
    
    /**
     * Find transaction by ID
     * 通过ID查找交易
     * 
     * @param id Transaction ID / 交易ID
     * @return Transaction object or null if not found / 交易对象，如果未找到则为null
     */
    private Transaction findTransactionById(String id) {
        return dataSnapshot.stream()
            .filter(t -> t.getTransaction_id().equals(id))
            .findFirst()
            .orElse(null);
    }    
    
    /**
     * Handle update result from transaction dialog
     * 处理交易对话框的更新结果
     * 
     * @param updated Updated transaction / 更新后的交易
     * @param selectedRow Selected row in table / 表格中选中的行
     */
    private void handleUpdateResult(Transaction updated, int selectedRow) {
        if (updated != null) {
            updateDataSource(updated);
            updateTableRow(updated, selectedRow);
        }
    }

    /**
     * Update transaction in data source
     * 更新数据源中的交易
     * 
     * @param updated Updated transaction / 更新后的交易
     */
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

    /**
     * Update transaction in table row
     * 更新表格行中的交易
     * 
     * @param updated Updated transaction / 更新后的交易
     * @param row Row index / 行索引
     */
    private void updateTableRow(Transaction updated, int row) {
        tableModel.setValueAt(updated.getAmount() / 100.0, row, 1);
        tableModel.setValueAt(updated.getDatetime(), row, 2);
        tableModel.setValueAt(updated.getModifiedTime(), row, 4);
        tableModel.setValueAt(updated.getDescription(), row, 5);
    }

}
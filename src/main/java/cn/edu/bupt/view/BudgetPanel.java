package cn.edu.bupt.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import cn.edu.bupt.model.*;

public class BudgetPanel extends JPanel {
    private JTable budgetTable;
    private BudgetTableModel tableModel;
    private JButton refreshButton;
    private JLabel statusLabel;

    public BudgetPanel() {
        setLayout(new BorderLayout(5, 5));
        initializeComponents();
        loadData();
    }

    private void initializeComponents() {
        // 工具栏面板
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        toolBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // 添加新按钮
        JButton addButton = new JButton("添加/修改预算");
        addButton.addActionListener(e -> showAddDialog());
        toolBar.add(addButton);
        
        // 刷新按钮
        refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refresh());
        
        // 状态标签
        statusLabel = new JLabel("就绪");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        toolBar.add(refreshButton);
        toolBar.add(statusLabel);
        add(toolBar, BorderLayout.NORTH);

        // 表格初始化（与原始代码相同）
        tableModel = new BudgetTableModel();
        budgetTable = new JTable(tableModel) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 6 ? Double.class : super.getColumnClass(column);
            }
        };
        budgetTable.setRowHeight(30);
        budgetTable.setAutoCreateRowSorter(true);
        
        TableColumnModel columnModel = budgetTable.getColumnModel();
        columnModel.getColumn(6).setCellRenderer(new ProgressBarRenderer());

        JScrollPane scrollPane = new JScrollPane(budgetTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("预算列表"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void showAddDialog() {
        int selectedRow = budgetTable.getSelectedRow();
        if(selectedRow != -1) {
            Budget selectedBudget = tableModel.budgetList.get(selectedRow);
            BudgetDialog dialog = new BudgetDialog((Frame) SwingUtilities.getWindowAncestor(this), selectedBudget);
            dialog.setVisible(true);
        } else {
            BudgetDialog dialog = new BudgetDialog((Frame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
        }
        refresh();
    }

    private void loadData() {
        TransactionManager tm = TransactionManager.getInstance();
        try {
            if (tm.currentUser != null) {
                // 创建新集合确保数据更新
                Set<Budget> budgets = new HashSet<>(tm.currentUser.getBudgets()); 
                tableModel.setBudgets(budgets);
                statusLabel.setText("加载完成 - 共 " + budgets.size() + " 条预算");
            } else {
                statusLabel.setText("未登录用户");
            }
        } catch (Exception ex) {
            showError("数据加载失败: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void refresh() {
        refreshButton.setEnabled(false);
        statusLabel.setText("正在刷新数据...");
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                loadData();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    budgetTable.updateUI();
                    statusLabel.setText("刷新完成 - " + new Date());
                } catch (Exception ex) {
                    showError("刷新失败: " + ex.getMessage());
                } finally {
                    refreshButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", 
            JOptionPane.ERROR_MESSAGE);
    }

    


    // 自定义表格模型
    private class BudgetTableModel extends AbstractTableModel {
        private List<Budget> budgetList = new ArrayList<>(); 
        private List<Integer> usedAmounts = new ArrayList<>();
        private final String[] columnNames = {
            "描述", "开始时间", "结束时间", 
            "预算金额", "已用金额", 
            "剩余金额", "进度"
        };

        public void setBudgets(Set<Budget> budgets) {
            budgetList.clear();
            usedAmounts.clear();
            
            TransactionManager tm = TransactionManager.getInstance();
            for (Budget budget : budgets) {
                budgetList.add(budget);
                usedAmounts.add(tm.calculateAmount(budget));
            }
            
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return budgetList.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= budgetList.size()) return null;
            
            Budget budget = budgetList.get(rowIndex);
            int used = usedAmounts.get(rowIndex);
            int remaining = Math.max(budget.getAmount() - used, 0);
            
            switch (columnIndex) {
                case 0: return budget.getDescription();
                case 1: return formatDateTime(budget.getStartDateTime());
                case 2: return formatDateTime(budget.getEndDateTime());
                case 3: return budget.getAmount()/100;
                case 4: return used/100;
                case 5: return remaining/100;
                case 6: return calculateProgress(budget.getAmount(), used);
                default: return null;
            }
        }

        private String formatDateTime(String datetime) {
            return (datetime != null && datetime.length() > 10) ? 
                   datetime.substring(0, 10) : datetime;
        }

        private double calculateProgress(int total, int used) {
            if (total == 0) return 0.0;
            return Math.min((double) used / total, 1.0);
        }
    }

    // 进度条渲染器
    private static class ProgressBarRenderer extends JProgressBar implements TableCellRenderer {
        public ProgressBarRenderer() {
            super(0, 100);
            setStringPainted(true);
            setBorderPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            
            double progress = (value instanceof Double) ? 
                (Double) value * 100 : 0.0;
            int progressValue = (int) Math.round(progress);

            // 设置进度条值
            setValue(progressValue >= 0 ? progressValue : 0);
            
            // 设置颜色和文本
            if (progress > 100) {
                setForeground(new Color(200, 0, 0)); // 深红
                setString(String.format("超额 %.1f%%", progress));
            } else if (progress > 80) {
                setForeground(new Color(255, 153, 0)); // 橙色
                setString(progressValue + "%");
            } else {
                setForeground(new Color(0, 153, 0)); // 深绿
                setString(progressValue + "%");
            }

            // 设置背景色
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }

            return this;
        }
    }
}
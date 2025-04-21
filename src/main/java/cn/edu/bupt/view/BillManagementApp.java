package cn.edu.bupt.view;

import cn.edu.bupt.dao.CsvTransactionDao;
import cn.edu.bupt.model.Transaction;
import cn.edu.bupt.model.TransactionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BillManagementApp {
    private JPanel mainPanel = new JPanel(new BorderLayout());
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField queryField;
    private JTextField startDateField;
    private JTextField endDateField;
    private List<Transaction> originalData;

    public BillManagementApp() {
        this.mainPanel.setPreferredSize(new Dimension(900, 500));
        JPanel searchPanel = new JPanel(new FlowLayout());
        this.queryField = new JTextField(15);
        this.startDateField = new JTextField(10);
        this.endDateField = new JTextField(10);
        JButton queryButton = new JButton("Query");
        JButton resetButton = new JButton("Reset");
        searchPanel.add(new JLabel("Bill Classification:"));
        searchPanel.add(this.queryField);
        searchPanel.add(new JLabel("Start Date:"));
        searchPanel.add(this.startDateField);
        searchPanel.add(new JLabel("End Date:"));
        searchPanel.add(this.endDateField);
        searchPanel.add(queryButton);
        searchPanel.add(resetButton);
        this.mainPanel.add(searchPanel, BorderLayout.NORTH);
        String[] columnNames = new String[]{"Transaction ID", "Amount", "Datetime", "Created At", "Modified At", "Description", "Tags"};
        this.tableModel = new DefaultTableModel(columnNames, 0);
        this.table = new JTable(this.tableModel);
        JScrollPane scrollPane = new JScrollPane(this.table);
        this.mainPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton deleteButton = new JButton("Delete Selected");
        bottomPanel.add(deleteButton);
        this.mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        this.originalData = new ArrayList<>();
        this.loadData();
        queryButton.addActionListener(e -> this.filterTable());
        resetButton.addActionListener(e -> this.resetTable());
        deleteButton.addActionListener(e -> this.deleteSelectedRow());
    }

    public JPanel getMainPanel() {
        return this.mainPanel;
    }

    private void loadData() {
        this.tableModel.setRowCount(0);
        CsvTransactionDao.readTransactionsFromCSV();
        TransactionManager TM = TransactionManager.getInstance();

        for (Transaction transaction : TM.Transactions) {
            Object[] row = new Object[]{
                    transaction.getTransaction_id(),
                    transaction.getAmount(),
                    transaction.getDatetime(),
                    transaction.getCreateTime(),
                    transaction.getModifiedTime(),
                    transaction.getDescription(),
                    String.join("|", transaction.getTags().stream().map(tag -> tag.getName()).toArray(String[]::new))
            };
            this.tableModel.addRow(row);
            this.originalData.add(transaction);
        }
    }

    private void filterTable() {
        String queryText = this.queryField.getText().trim().toLowerCase();
        String startDate = this.startDateField.getText().trim();
        String endDate = this.endDateField.getText().trim();
        this.tableModel.setRowCount(0);

        for (Transaction transaction : this.originalData) {
            String category = transaction.getDescription().toLowerCase();
            String date = transaction.getDatetime();
            boolean categoryMatch = queryText.isEmpty() || category.contains(queryText);
            boolean dateMatch = (startDate.isEmpty() || date.compareTo(startDate) >= 0) && (endDate.isEmpty() || date.compareTo(endDate) <= 0);
            if (categoryMatch && dateMatch) {
                Object[] row = new Object[]{
                        transaction.getTransaction_id(),
                        transaction.getAmount(),
                        transaction.getDatetime(),
                        transaction.getCreateTime(),
                        transaction.getModifiedTime(),
                        transaction.getDescription(),
                        String.join("|", transaction.getTags().stream().map(tag -> tag.getName()).toArray(String[]::new))
                };
                this.tableModel.addRow(row);
            }
        }
    }

    private void resetTable() {
        this.queryField.setText("");
        this.startDateField.setText("");
        this.endDateField.setText("");
        this.tableModel.setRowCount(0);

        for (Transaction transaction : this.originalData) {
            Object[] row = new Object[]{
                    transaction.getTransaction_id(),
                    transaction.getAmount(),
                    transaction.getDatetime(),
                    transaction.getCreateTime(),
                    transaction.getModifiedTime(),
                    transaction.getDescription(),
                    String.join("|", transaction.getTags().stream().map(tag -> tag.getName()).toArray(String[]::new))
            };
            this.tableModel.addRow(row);
        }
    }

    private void deleteSelectedRow() {
        int selectedRow = this.table.getSelectedRow();
        if (selectedRow != -1) {
            String transactionId = (String) this.tableModel.getValueAt(selectedRow, 0);
            TransactionManager TM = TransactionManager.getInstance();
            Transaction transactionToRemove = null;

            for (Transaction transaction : TM.Transactions) {
                if (transaction.getTransaction_id().equals(transactionId)) {
                    transactionToRemove = transaction;
                    break;
                }
            }

            if (transactionToRemove != null) {
                TM.Transactions.remove(transactionToRemove);
                CsvTransactionDao.writeTransactionsToCSV();
                this.tableModel.removeRow(selectedRow);
                this.originalData.remove(transactionToRemove);
            }
        } else {
            JOptionPane.showMessageDialog(this.mainPanel, "Please select a row to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
 80 changes: 80 additions & 0 deletions80  
src/main/java/cn/edu/bupt/view/FinanceDashboard.java
Original file line number	Diff line number	Diff line change
@@ -0,0 +1,80 @@
package cn.edu.bupt.view;

import javax.swing.*;
import java.awt.*;

public class FinanceDashboard extends JFrame {
    private JPanel mainPanel;

    public FinanceDashboard() {
        this.setTitle("Finance Dashboard");
        this.setSize(1200, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        topPanel.add(this.createLabel("用户余额: ￥10100", JLabel.CENTER));
        topPanel.add(this.createLabel("用户支出: ￥500", JLabel.CENTER));
        topPanel.add(this.createLabel("账户收入: ￥10600", JLabel.CENTER));
        this.add(topPanel, BorderLayout.NORTH);
        JPanel leftPanel = new JPanel(new BorderLayout());
        JPanel navigationPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        navigationPanel.add(this.createHomeButton("系统主页", JLabel.CENTER));
        String[] options = new String[]{"账单管理", "记账日记", "储蓄计划"};
        final JComboBox<String> dropdown = new JComboBox<>(options);
        dropdown.addActionListener(e -> {
            String selectedOption = (String) dropdown.getSelectedItem();
            FinanceDashboard.this.displayPage(selectedOption);
        });
        navigationPanel.add(dropdown);
        leftPanel.add(navigationPanel, BorderLayout.NORTH);
        this.add(leftPanel, BorderLayout.WEST);
        this.mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        this.displayHomePage();
        this.add(this.mainPanel, BorderLayout.CENTER);
    }

    private JButton createHomeButton(String text, int alignment) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(alignment);
        button.addActionListener(e -> FinanceDashboard.this.displayHomePage());
        return button;
    }

    private JLabel createLabel(String text, int alignment) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(alignment);
        return label;
    }

    private void displayHomePage() {
        this.mainPanel.removeAll();
        this.mainPanel.revalidate();
        this.mainPanel.repaint();
    }

    private void displayPage(String option) {
        this.mainPanel.removeAll();
        switch (option) {
            case "账单管理":
                this.mainPanel.setLayout(new BorderLayout());
                this.mainPanel.add((new BillManagementApp()).getMainPanel(), BorderLayout.CENTER);
                break;
            case "记账日记":
                this.mainPanel.add(new FinanceManagerPanel());
                break;
            case "储蓄计划":
                this.mainPanel.add(new JLabel("储蓄计划页面（未实现）", JLabel.CENTER));
                break;
        }

        this.mainPanel.revalidate();
        this.mainPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FinanceDashboard frame = new FinanceDashboard();
            frame.setVisible(true);
        });
    }
}
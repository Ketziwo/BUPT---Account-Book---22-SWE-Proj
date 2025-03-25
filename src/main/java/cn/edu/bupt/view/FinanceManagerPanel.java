package cn.edu.bupt.view;

import cn.edu.bupt.dao.CsvTransactionDao;
import cn.edu.bupt.model.Transaction;
import cn.edu.bupt.model.TransactionManager;
import cn.edu.bupt.utils.DateUtils;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FinanceManagerPanel extends JPanel {
    public FinanceManagerPanel() {
        this.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("Finance Manager", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        this.add(titleLabel, BorderLayout.NORTH);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("收入", this.createTransactionPanel("收入"));
        tabbedPane.addTab("支出", this.createTransactionPanel("支出"));
        this.add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createTransactionPanel(String type) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBackground(Color.WHITE);
        JLabel amountLabel = this.createStyledLabel("Amount:");
        JTextField amountField = this.createStyledTextField();
        JLabel datetimeLabel = this.createStyledLabel("Datetime:");
        JTextField datetimeField = this.createStyledTextField();
        datetimeField.setText((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
        JLabel descriptionLabel = this.createStyledLabel("Description:");
        JTextField descriptionField = this.createStyledTextField();
        JButton saveButton = this.createStyledButton("Save");
        JTextArea resultArea = this.createResultArea();
        saveButton.addActionListener(e -> {
            String amountStr = amountField.getText().trim();
            String datetime = datetimeField.getText().trim();
            String description = descriptionField.getText().trim();
            if (!amountStr.isEmpty() && !datetime.isEmpty() && !description.isEmpty()) {
                try {
                    int amount = Integer.parseInt(amountStr);
                    ArrayList<String> tags = new ArrayList<>();
                    tags.add(type);
                    String transactionId = DateUtils.getDate() + "-" + TransactionManager.getInstance().Transactions.size();
                    Transaction transaction = new Transaction(transactionId, amount, datetime, DateUtils.getDatetime(), DateUtils.getDatetime(), description, tags);
                    TransactionManager.getInstance().Transactions.add(transaction);
                    CsvTransactionDao.writeTransactionsToCSV();
                    resultArea.setText(type + " saved!\nTransaction ID: " + transactionId + "\nAmount: " + amount + "\nDatetime: " + datetime + "\nDescription: " + description);
                } catch (NumberFormatException ex) {
                    resultArea.setText("Please enter a valid amount!");
                }
            } else {
                resultArea.setText("Amount, datetime, and description cannot be empty!");
            }
        });
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(datetimeLabel);
        panel.add(datetimeField);
        panel.add(descriptionLabel);
        panel.add(descriptionField);
        panel.add(new JLabel());
        panel.add(saveButton);
        JPanel container = new JPanel(new BorderLayout());
        container.add(panel, BorderLayout.NORTH);
        container.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        return container;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.BLACK);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setBackground(Color.LIGHT_GRAY);
        textField.setForeground(Color.BLACK);
        return textField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        return button;
    }

    private JTextArea createResultArea() {
        JTextArea resultArea = new JTextArea();
        resultArea.setBackground(Color.LIGHT_GRAY);
        resultArea.setForeground(Color.BLACK);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        return resultArea;
    }
}
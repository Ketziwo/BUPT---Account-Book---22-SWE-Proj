// EditDialog.java
package cn.edu.bupt.view;

import cn.edu.bupt.model.Transaction;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.Consumer;

public class EditDialog extends JDialog {
    private JTextField datetimeField;
    private JTextField amountField;
    private JTextField descField;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EditDialog(Frame owner, Transaction original, Consumer<Transaction> callback) {
        super(owner, "编辑交易记录", true);
        initComponents(original, callback);
        configureDialog(owner);
    }

    private void initComponents(Transaction original, Consumer<Transaction> callback) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 使用GridBagLayout实现更灵活的布局
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 第一列标签
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("交易ID:"), gbc);
        
        gbc.gridy++;
        formPanel.add(new JLabel("创建时间:"), gbc);
        
        gbc.gridy++;
        formPanel.add(new JLabel("最后修改时间:"), gbc);
        
        gbc.gridy++;
        formPanel.add(new JLabel("金额（元）:"), gbc);
        
        gbc.gridy++;
        formPanel.add(new JLabel("交易时间:"), gbc);
        
        gbc.gridy++;
        formPanel.add(new JLabel("描述:"), gbc);

        // 第二列内容
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        formPanel.add(new JLabel(original.getTransaction_id()), gbc);
        
        gbc.gridy++;
        formPanel.add(new JLabel(original.getCreateTime()), gbc);
        
        gbc.gridy++;
        formPanel.add(new JLabel(original.getModifiedTime()), gbc);
        
        gbc.gridy++;
        amountField = new JTextField(10);
        amountField.setText(String.format("%.2f", original.getAmount() / 100.0));
        formPanel.add(amountField, gbc);
        
        gbc.gridy++;
        datetimeField = new JTextField(15);
        datetimeField.setToolTipText("格式: 2023-01-01 12:00:00");
        datetimeField.setText(original.getDatetime());
        formPanel.add(datetimeField, gbc);
        
        gbc.gridy++;
        descField = new JTextField(20);
        descField.setText(original.getDescription());
        formPanel.add(descField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(original, callback), BorderLayout.SOUTH);
        getContentPane().add(mainPanel);
    }

    private JPanel createButtonPanel(Transaction original, Consumer<Transaction> callback) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        
        JButton btnSave = new JButton("保存");
        btnSave.addActionListener(e -> {
            if (validateInput()) {
                updateTransaction(original);
                callback.accept(original);
                dispose();
            }
        });
        
        JButton btnCancel = new JButton("取消");
        btnCancel.addActionListener(e -> dispose());
        
        panel.add(btnSave);
        panel.add(btnCancel);
        
        return panel;
    }

    private void configureDialog(Frame owner) {
        setSize(500, 350);
        setLocationRelativeTo(owner);
        setResizable(true);
    }

    private boolean validateInput() {
        try {
            validateAmount();
            validateDateTime();
            validateDescription();
            return true;
        } catch (IllegalArgumentException ex) {
            showErrorDialog(ex.getMessage());
            return false;
        }
    }

    private void validateAmount() {
        try {
            double yuan = Double.parseDouble(amountField.getText());
            if (yuan <= 0) {
                throw new IllegalArgumentException("金额必须大于0");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("金额格式不正确");
        }
    }

    private void validateDateTime() {
        String input = datetimeField.getText().trim();
        try {
            LocalDateTime.parse(input, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("日期时间格式应为: yyyy-MM-dd HH:mm:ss");
        }
    }

    private void validateDescription() {
        if (descField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("描述不能为空");
        }
    }

    private void updateTransaction(Transaction original) {
        original.setAmount((int)(Double.parseDouble(amountField.getText()) * 100));
        original.setDatetime(datetimeField.getText().trim());
        original.setDescription(descField.getText().trim());
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "输入错误",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
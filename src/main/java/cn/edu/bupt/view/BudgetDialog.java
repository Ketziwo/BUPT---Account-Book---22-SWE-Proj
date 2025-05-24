package cn.edu.bupt.view;

import cn.edu.bupt.model.*;
import cn.edu.bupt.utils.DateUtils;
import cn.edu.bupt.utils.TransactionTypeUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class BudgetDialog extends JDialog {
    private boolean confirmed = false;
    private boolean isCreating = true;
    private Budget budget;
    private final JTextField descriptionField = new JTextField(20);
    private final JSpinner startDateSpinner = DateUtils.createDatetimeSpinner();
    private final JSpinner endDateSpinner = DateUtils.createDatetimeSpinner();
    private final JTextField amountField = new JTextField(10);

    private final JTextField customTagField = new JTextField(20);

    private final TransactionManager TM = TransactionManager.getInstance();

    public BudgetDialog(Frame parent, Budget b) {
        super(parent, "修改预算", true);
        this.budget = b;
        this.isCreating = false;
        initializeUI();
        setSize(400, 250);
        setLocationRelativeTo(parent);
    }

    public BudgetDialog(Frame parent) {
        super(parent, "添加预算", true);
        initializeUI();
        setSize(400, 250);
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if(!isCreating) {
            descriptionField.setText(budget.getDescription());
            amountField.setText(String.format("%.2f", budget.getAmount() / 100.0));
            DateUtils.setJSpinner(startDateSpinner, budget.getStartDateTime());
            DateUtils.setJSpinner(endDateSpinner, budget.getEndDateTime());

            List<String> tagNames = new ArrayList<>();
            for (Tag tag : budget.getTags()) {
                tagNames.add(tag.getName());
            }
            customTagField.setText(String.join("|", tagNames));
        }

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 10));
        inputPanel.add(new JLabel("描述:"));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("开始日期:"));
        inputPanel.add(startDateSpinner);
        inputPanel.add(new JLabel("结束日期:"));
        inputPanel.add(endDateSpinner);
        inputPanel.add(new JLabel("预算金额:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("自定义Tag:"));
        inputPanel.add(customTagField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("确认");
        confirmButton.addActionListener(e -> onConfirm());
        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> onCancel());

        if(!isCreating) {
            JButton deleteButton = new JButton("删除");
            deleteButton.addActionListener(e -> onDelete());
            buttonPanel.add(deleteButton);
        }
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void createBudgetFromInput() {
        // 验证描述
        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            throw new IllegalArgumentException("描述不能为空");
        }

        // 获取并验证日期
        String start = DateUtils.getDatetime((Date) startDateSpinner.getValue());
        String end = DateUtils.getDatetime((Date) endDateSpinner.getValue());

        // 验证金额
        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            throw new IllegalArgumentException("金额不能为空");
        }
        
        int amount;
        try {
            amount = (int)Math.round(Double.parseDouble(amountText) * 100);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("金额必须为整数");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("金额必须大于0");
        }

        User currentUser = TransactionManager.getInstance().currentUser;
        // ArrayList<String> tags = new ArrayList<>();
        String[] tagNames = customTagField.getText().split("\\|");
        ArrayList<String> DIYtags = new ArrayList<>();
        Set<Tag> DIYtagsSet = new HashSet<>();

        for (String tagName : tagNames) {
            DIYtags.add(tagName);
            DIYtagsSet.add(TM.getTag(tagName));
            System.out.println(tagName);
        }

        if(isCreating) {
            budget = new Budget(currentUser, amount, DIYtags, start, end, description);
        }
        else {
            budget.setAmount(amount);
            budget.setStartDateTime(start);
            budget.setEndDateTime(end);
            budget.setDescription(description);
            budget.getTags().clear();
            budget.getTags().addAll(DIYtagsSet);
        }
    }

    private void onConfirm() {
        try {
            createBudgetFromInput();
            confirmed = true;
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), 
                "输入错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        confirmed = false;
        dispose();
    }

    private void onDelete() {
        int result = JOptionPane.showConfirmDialog(this, 
            "确定要删除这个预算吗？", "确认删除", 
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            TransactionManager.getInstance().currentUser.getBudgets().remove(this.budget);
            dispose();
        }
    }
}
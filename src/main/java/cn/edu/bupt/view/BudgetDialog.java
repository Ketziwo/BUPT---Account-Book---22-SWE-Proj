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

/**
 * Dialog for creating and editing budget entries
 * 用于创建和编辑预算项目的对话框
 */
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

    /**
     * Constructor for editing an existing budget
     * 用于编辑现有预算的构造函数
     */
    public BudgetDialog(Frame parent, Budget b) {
        super(parent, "Modify Budget", true);
        this.budget = b;
        this.isCreating = false;
        initializeUI();
        setSize(400, 250);
        setLocationRelativeTo(parent);
    }

    /**
     * Constructor for creating a new budget
     * 用于创建新预算的构造函数
     */
    public BudgetDialog(Frame parent) {
        super(parent, "Add Budget", true);
        initializeUI();
        setSize(400, 250);
        setLocationRelativeTo(parent);
    }

    /**
     * Initializes the UI components for the dialog
     * 初始化对话框的UI组件
     */
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
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("Start Date:"));
        inputPanel.add(startDateSpinner);
        inputPanel.add(new JLabel("End Date:"));
        inputPanel.add(endDateSpinner);
        inputPanel.add(new JLabel("Budget Amount:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Custom Tags:"));
        inputPanel.add(customTagField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(e -> onConfirm());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> onCancel());

        if(!isCreating) {
            JButton deleteButton = new JButton("Delete");
            deleteButton.addActionListener(e -> onDelete());
            buttonPanel.add(deleteButton);
        }
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    /**
     * Creates or updates a Budget object from input fields
     * 从输入字段创建或更新预算对象
     * @throws IllegalArgumentException if input validation fails
     */
    private void createBudgetFromInput() {
        // Validate description / 验证描述
        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        // Get and validate dates / 获取并验证日期
        String start = DateUtils.getDatetime((Date) startDateSpinner.getValue());
        String end = DateUtils.getDatetime((Date) endDateSpinner.getValue());

        // Validate amount / 验证金额
        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            throw new IllegalArgumentException("Amount cannot be empty");
        }
        
        int amount;
        try {
            amount = (int)Math.round(Double.parseDouble(amountText) * 100);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Amount must be a number");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
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

    /**
     * Handles the confirm button action
     * 处理确认按钮操作
     */
    private void onConfirm() {
        try {
            createBudgetFromInput();
            confirmed = true;
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), 
                "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the cancel button action
     * 处理取消按钮操作
     */
    private void onCancel() {
        confirmed = false;
        dispose();
    }

    /**
     * Handles the delete button action
     * 处理删除按钮操作
     */
    private void onDelete() {
        int result = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this budget?", "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            TransactionManager.getInstance().currentUser.getBudgets().remove(this.budget);
            dispose();
        }
    }
}
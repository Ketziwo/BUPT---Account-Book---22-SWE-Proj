package cn.edu.bupt.view;

import cn.edu.bupt.model.*;
import cn.edu.bupt.utils.DateUtils;
import cn.edu.bupt.utils.TransactionTypeUtils;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Dialog for creating and editing transaction entries
 * 用于创建和编辑交易记录的对话框
 */
public class TransactionDialog extends JDialog {
    private final JTextField amountField = new JTextField(10);;
    private final JTextField descField = new JTextField(20);
    private final JTextField customTagField = new JTextField(20);;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> typeComboBox;
    private final JSpinner DatetimeSpinner = DateUtils.createDatetimeSpinner();

    private boolean isCreating = true;
    private boolean confirmed = false;
    private Transaction transaction;

    private final TransactionManager TM = TransactionManager.getInstance();

    /**
     * Constructor for creating a new transaction
     * 用于创建新交易的构造函数
     * 
     * @param parent The parent frame / 父窗口
     */
    public TransactionDialog(Frame parent) {
        super(parent, "Add New Transaction", true);
        initializeUI();
        setSize(400, 300);
        setLocationRelativeTo(parent);
    }

    /**
     * Constructor for editing an existing transaction
     * 用于编辑现有交易的构造函数
     * 
     * @param parent The parent frame / 父窗口
     * @param ta The transaction to edit / 要编辑的交易
     */
    public TransactionDialog(Frame parent, Transaction ta) {
        super(parent, "Edit Transaction", true);
        this.isCreating = false;
        this.transaction = ta;
        initializeUI();
        setSize(400, 400);
        setLocationRelativeTo(parent);
    }

    /**
     * Initializes the UI components
     * 初始化UI组件
     */
    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        typeComboBox = new JComboBox<>(new String[]{"Income", "Expense"});
        categoryComboBox = new JComboBox<>();
        refreshCategoryComboBox(); // Initialize categories / 初始化分类
        typeComboBox.addActionListener(e -> refreshCategoryComboBox()); // Dynamic response to type change / 动态响应类型切换

        if(!isCreating) {
            JPanel topPanel = new JPanel(new GridLayout(4, 1, 1, 0));
            topPanel.add(new JLabel("Transaction ID: " + transaction.getTransaction_id()));
            topPanel.add(new JLabel("Creation Time: " + transaction.getCreateTime()));
            topPanel.add(new JLabel("Last Modified: " + transaction.getModifiedTime()));
            mainPanel.add(topPanel, BorderLayout.NORTH);

            amountField.setText(String.format("%.2f", transaction.getAmount() / 100.0));
            descField.setText(transaction.getDescription());
            DateUtils.setJSpinner(DatetimeSpinner, transaction.getDatetime());


            List<String> tagNames = new ArrayList<>();
            for (Tag tag : transaction.getTags()) {
                if (TM.expenseTags.contains(tag) || TM.incomeTags.contains(tag)){
                    categoryComboBox.setSelectedItem(Tag.LABEL_MAP.get(tag));
                }
                else if (tag==Tag.EXPENSE){
                    typeComboBox.setSelectedItem("Expense");
                }
                else if (tag==Tag.INCOME){
                    typeComboBox.setSelectedItem("Income");
                }
                else {
                    tagNames.add(tag.getName());
                }
            }
            customTagField.setText(String.join("|", tagNames));

        }

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 10));

        inputPanel.add(new JLabel("Type:"));
        inputPanel.add(typeComboBox);
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descField);
        inputPanel.add(new JLabel("Transaction Time:"));
        inputPanel.add(DatetimeSpinner);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryComboBox);
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
        getContentPane().add(mainPanel);
    }

    /**
     * Creates or updates a Transaction object from input fields
     * 从输入字段创建或更新交易对象
     * 
     * @throws IllegalArgumentException if input validation fails / 如果输入验证失败
     */
    private void createTransactionFromInput() {
        // Validate description / 验证描述
        String description = descField.getText().trim();
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        // Get and validate date / 获取并验证日期
        String time = DateUtils.getDatetime((Date) DatetimeSpinner.getValue());

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

        ArrayList<String> tags = new ArrayList<>();

        

        // Read income/expense type / 读取收支
        Tag expenseOrIncome;
        if("Income".equals((String)typeComboBox.getSelectedItem())) expenseOrIncome = Tag.INCOME;
        else if ("Expense".equals((String)typeComboBox.getSelectedItem())) expenseOrIncome = Tag.EXPENSE;
        else throw new IllegalArgumentException("Invalid type selection");
        
        
        // Read category / 读取分类
        String categoryString = ((String)categoryComboBox.getSelectedItem());
        Tag cateTag = Tag.getDefaultTag(categoryString);
        if(cateTag==Tag.UNKNOWN) {
            throw new IllegalArgumentException("Invalid category selection");
        }

        String[] tagNames = customTagField.getText().split("\\|");
        HashSet<Tag> DIYtags = new HashSet<>();

        for (String tagName : tagNames) {
            DIYtags.add(TM.getTag(tagName));
        }

        if(isCreating) {
            transaction = new Transaction();
        }
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setDatetime(time);
        transaction.getTags().clear();
        TransactionTypeUtils.setIOcome(transaction,expenseOrIncome);
        TransactionTypeUtils.setCategory(transaction, cateTag);
        transaction.getTags().addAll(DIYtags);
    }

    /**
     * Handles the confirm button action
     * 处理确认按钮操作
     */
    private void onConfirm() {
        try {
            createTransactionFromInput();
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
            "Are you sure you want to delete this transaction?", "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            transaction.getUser().getTransactions().remove(transaction);
            dispose();
        }
    }

    /**
     * Refreshes the category combo box based on selected transaction type
     * 根据选定的交易类型刷新类别组合框
     */
    private void refreshCategoryComboBox() {
        String selectedType = (String) typeComboBox.getSelectedItem();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

        Set<Tag> tags = "Income".equals(selectedType) ? TM.incomeTags : TM.expenseTags;
        for (Tag tag : tags) {
            model.addElement(Tag.LABEL_MAP.get(tag));
        }

        // Set model to combo box / 设置模型到组合框
        categoryComboBox.setModel(model);      
    }
}

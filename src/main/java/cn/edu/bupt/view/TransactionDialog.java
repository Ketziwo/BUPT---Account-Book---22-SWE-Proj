package cn.edu.bupt.view;

import cn.edu.bupt.model.*;
import cn.edu.bupt.utils.DateUtils;
import cn.edu.bupt.utils.TransactionTypeUtils;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

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

    public TransactionDialog(Frame parent) {
        super(parent, "添加新交易", true);
        initializeUI();
        setSize(400, 300);
        setLocationRelativeTo(parent);
    }

    public TransactionDialog(Frame parent, Transaction ta) {
        super(parent, "修改新交易", true);
        this.isCreating = false;
        this.transaction = ta;
        initializeUI();
        setSize(400, 400);
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        typeComboBox = new JComboBox<>(new String[]{"收入", "支出"});
        categoryComboBox = new JComboBox<>();
        refreshCategoryComboBox(); // 初始化分类
        typeComboBox.addActionListener(e -> refreshCategoryComboBox()); // 动态响应类型切换

        if(!isCreating) {
            JPanel topPanel = new JPanel(new GridLayout(4, 1, 1, 0));
            topPanel.add(new JLabel("交易ID: " + transaction.getTransaction_id()));
            topPanel.add(new JLabel("创建时间: " + transaction.getCreateTime()));
            topPanel.add(new JLabel("最后修改时间: " + transaction.getModifiedTime()));
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
                    typeComboBox.setSelectedItem("支出");
                }
                else if (tag==Tag.INCOME){
                    typeComboBox.setSelectedItem("收入");
                }
                else {
                    tagNames.add(tag.getName());
                }
            }
            customTagField.setText(String.join("|", tagNames));

        }

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 10));

        inputPanel.add(new JLabel("收支类型:"));
        inputPanel.add(typeComboBox);
        inputPanel.add(new JLabel("金额:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("描述:"));
        inputPanel.add(descField);
        inputPanel.add(new JLabel("交易时间:"));
        inputPanel.add(DatetimeSpinner);
        inputPanel.add(new JLabel("分类:"));
        inputPanel.add(categoryComboBox);
        inputPanel.add(new JLabel("自定义标签:"));
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
        getContentPane().add(mainPanel);
    }

    private void createTransactionFromInput() {
        // 验证描述
        String description = descField.getText().trim();
        if (description.isEmpty()) {
            throw new IllegalArgumentException("描述不能为空");
        }

        // 获取并验证日期
        String time = DateUtils.getDatetime((Date) DatetimeSpinner.getValue());

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

        ArrayList<String> tags = new ArrayList<>();

        

        // 读取收支
        Tag expenseOrIncome;
        if("收入".equals((String)typeComboBox.getSelectedItem())) expenseOrIncome = Tag.INCOME;
        else if ("支出".equals((String)typeComboBox.getSelectedItem())) expenseOrIncome = Tag.EXPENSE;
        else throw new IllegalArgumentException("收支选择无效");
        
        
        // 读取分类
        String categoryString = ((String)categoryComboBox.getSelectedItem());
        Tag cateTag = Tag.getDefaultTag(categoryString);
        if(cateTag==Tag.UNKNOWN) {
            throw new IllegalArgumentException("分类选择无效");
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

    private void onConfirm() {
        try {
            createTransactionFromInput();
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
            transaction.getUser().getTransactions().remove(transaction);
            dispose();
        }
    }

    private void refreshCategoryComboBox() {
        String selectedType = (String) typeComboBox.getSelectedItem();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

        Set<Tag> tags = "收入".equals(selectedType) ? TM.incomeTags : TM.expenseTags;
        for (Tag tag : tags) {
            model.addElement(Tag.LABEL_MAP.get(tag));
        }

        // 设置模型到组合框
        categoryComboBox.setModel(model);      
    }
}

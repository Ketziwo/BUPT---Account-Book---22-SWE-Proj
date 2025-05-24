package cn.edu.bupt.view;

import javax.swing.*;

import cn.edu.bupt.utils.DateUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import java.util.Date;

/**
 * Dialog for filtering transactions based on various criteria
 * 基于各种条件筛选交易的对话框
 */
public class FilterDialog extends JDialog {
    private final JTextField descriptionField = new JTextField(15);
    private final JSpinner startDatetimeSpinner = DateUtils.createDatetimeSpinner();
    private final JSpinner endDatetimeSpinner = DateUtils.createDatetimeSpinner();
    // private final JTextField startDateField = new JTextField(8);
    // private final JTextField endDateField = new JTextField(8);
    private final JTextField minAmountField = new JTextField(8);
    private final JTextField maxAmountField = new JTextField(8);

    /**
     * Constructor for the filter dialog
     * 筛选对话框的构造函数
     * 
     * @param owner The parent frame / 父窗口
     * @param modal Whether the dialog should be modal / 对话框是否为模态
     * @param callback Callback function to receive filter criteria / 接收筛选条件的回调函数
     */
    public FilterDialog(Frame owner, boolean modal, Consumer<FilterCriteria> callback) {
        super(owner, "Filter Criteria", modal);
        initUI(callback);
        setSize(400, 250);
        setLocationRelativeTo(owner);
    }

    /**
     * Initializes the UI components
     * 初始化UI组件
     * 
     * @param callback Callback function to receive filter criteria / 接收筛选条件的回调函数
     */
    private void initUI(Consumer<FilterCriteria> callback) {
        JPanel mainPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(new JLabel("Description contains:"));
        mainPanel.add(descriptionField);
        
        mainPanel.add(new JLabel("Start date (YYYY-MM-DD):"));
        mainPanel.add(startDatetimeSpinner);
        
        mainPanel.add(new JLabel("End date (YYYY-MM-DD):"));
        mainPanel.add(endDatetimeSpinner);
        
        mainPanel.add(new JLabel("Minimum amount:"));
        mainPanel.add(minAmountField);
        
        mainPanel.add(new JLabel("Maximum amount:"));
        mainPanel.add(maxAmountField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(e -> {
            callback.accept(getFilterCriteria());
            dispose();
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates a FilterCriteria object from user input
     * 根据用户输入创建筛选条件对象
     * 
     * @return FilterCriteria object with user-specified values / 包含用户指定值的筛选条件对象
     */
    private FilterCriteria getFilterCriteria() {
        return new FilterCriteria(
            descriptionField.getText().trim(),
            DateUtils.getDatetime((Date) startDatetimeSpinner.getValue()),
            DateUtils.getDatetime((Date) endDatetimeSpinner.getValue()),
            parseDouble(minAmountField.getText().trim()),
            parseDouble(maxAmountField.getText().trim())
        );
    }

    /**
     * Parses a string to a Double, returning null for empty or invalid input
     * 将字符串解析为Double，对于空或无效输入返回null
     * 
     * @param text Text to parse / 要解析的文本
     * @return Parsed Double value or null / 解析后的Double值或null
     */
    private Double parseDouble(String text) {
        try {
            return text.isEmpty() ? null : Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Inner class to hold filter criteria
     * 保存筛选条件的内部类
     */
    public static class FilterCriteria {
        public final String description;
        public final String startDate;
        public final String endDate;
        public final Double minAmount;
        public final Double maxAmount;

        /**
         * Constructor for filter criteria
         * 筛选条件的构造函数
         */
        public FilterCriteria(String description, String startDate, 
                            String endDate, Double minAmount, Double maxAmount) {
            this.description = description;
            this.startDate = startDate;
            this.endDate = endDate;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
        }
    }
}
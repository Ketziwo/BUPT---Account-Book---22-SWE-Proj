package cn.edu.bupt.view;

import javax.swing.*;

import cn.edu.bupt.utils.DateUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import java.util.Date;

public class FilterDialog extends JDialog {
    private final JTextField descriptionField = new JTextField(15);
    private final JSpinner startDatetimeSpinner = DateUtils.createDatetimeSpinner();
    private final JSpinner endDatetimeSpinner = DateUtils.createDatetimeSpinner();
    // private final JTextField startDateField = new JTextField(8);
    // private final JTextField endDateField = new JTextField(8);
    private final JTextField minAmountField = new JTextField(8);
    private final JTextField maxAmountField = new JTextField(8);

    public FilterDialog(Frame owner, boolean modal, Consumer<FilterCriteria> callback) {
        super(owner, "筛选条件", modal);
        initUI(callback);
        setSize(400, 250);
        setLocationRelativeTo(owner);
    }

    private void initUI(Consumer<FilterCriteria> callback) {
        JPanel mainPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(new JLabel("描述包含:"));
        mainPanel.add(descriptionField);
        
        mainPanel.add(new JLabel("开始日期 (YYYY-MM-DD):"));
        mainPanel.add(startDatetimeSpinner);
        
        mainPanel.add(new JLabel("结束日期 (YYYY-MM-DD):"));
        mainPanel.add(endDatetimeSpinner);
        
        mainPanel.add(new JLabel("最小金额:"));
        mainPanel.add(minAmountField);
        
        mainPanel.add(new JLabel("最大金额:"));
        mainPanel.add(maxAmountField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("确定");
        confirmButton.addActionListener(e -> {
            callback.accept(getFilterCriteria());
            dispose();
        });
        
        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private FilterCriteria getFilterCriteria() {
        return new FilterCriteria(
            descriptionField.getText().trim(),
            DateUtils.getDatetime((Date) startDatetimeSpinner.getValue()),
            DateUtils.getDatetime((Date) endDatetimeSpinner.getValue()),
            parseDouble(minAmountField.getText().trim()),
            parseDouble(maxAmountField.getText().trim())
        );
    }

    private Double parseDouble(String text) {
        try {
            return text.isEmpty() ? null : Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static class FilterCriteria {
        public final String description;
        public final String startDate;
        public final String endDate;
        public final Double minAmount;
        public final Double maxAmount;

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
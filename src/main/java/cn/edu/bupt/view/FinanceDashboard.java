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
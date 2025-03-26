import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AccountingApp extends JFrame {

    // 数据存储
    private Map<String, Double> incomeData = new HashMap<>();
    private Map<String, Double> expenseData = new HashMap<>();

    public AccountingApp() {
        // 设置窗口标题
        setTitle("记账软件");
        setSize(1200, 800); // 调整窗口大小以容纳更多图表
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 设置窗口背景颜色
        getContentPane().setBackground(new Color(240, 240, 240));

        // 创建输入区域
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);

        // 初始化图表显示区域
        refreshCharts(); // 初始化时加载图表
    }

    // 创建输入面板
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 增加边距
        panel.setBackground(new Color(220, 220, 220));

        // 收入输入
        JPanel incomePanel = new JPanel();
        incomePanel.setBackground(new Color(220, 220, 220));
        incomePanel.add(new JLabel("收入类别:"));
        JTextField incomeCategoryField = new JTextField(10);
        incomePanel.add(incomeCategoryField);
        incomePanel.add(new JLabel("金额:"));
        JTextField incomeAmountField = new JTextField(10);
        incomePanel.add(incomeAmountField);
        JButton addIncomeButton = new JButton("添加收入");
        addIncomeButton.setBackground(new Color(100, 150, 200));
        addIncomeButton.setForeground(Color.WHITE);
        incomePanel.add(addIncomeButton);

        // 收入删除按钮
        JButton deleteIncomeButton = new JButton("删除收入");
        deleteIncomeButton.setBackground(new Color(200, 100, 100));
        deleteIncomeButton.setForeground(Color.WHITE);
        incomePanel.add(deleteIncomeButton);

        // 支出输入
        JPanel expensePanel = new JPanel();
        expensePanel.setBackground(new Color(220, 220, 220));
        expensePanel.add(new JLabel("支出类别:"));
        JTextField expenseCategoryField = new JTextField(10);
        expensePanel.add(expenseCategoryField);
        expensePanel.add(new JLabel("金额:"));
        JTextField expenseAmountField = new JTextField(10);
        expensePanel.add(expenseAmountField);
        JButton addExpenseButton = new JButton("添加支出");
        addExpenseButton.setBackground(new Color(200, 100, 150));
        addExpenseButton.setForeground(Color.WHITE);
        expensePanel.add(addExpenseButton);

        // 支出删除按钮
        JButton deleteExpenseButton = new JButton("删除支出");
        deleteExpenseButton.setBackground(new Color(200, 100, 100));
        deleteExpenseButton.setForeground(Color.WHITE);
        expensePanel.add(deleteExpenseButton);

        panel.add(incomePanel);
        panel.add(expensePanel);

        // 添加收入按钮事件
        addIncomeButton.addActionListener(e -> {
            String category = incomeCategoryField.getText();
            String amountText = incomeAmountField.getText();
            try {
                double amount = Double.parseDouble(amountText);
                incomeData.put(category, incomeData.getOrDefault(category, 0.0) + amount);
                refreshCharts();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "请输入有效的金额", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 删除收入按钮事件
        deleteIncomeButton.addActionListener(e -> {
            String category = incomeCategoryField.getText();
            if (incomeData.containsKey(category)) {
                incomeData.remove(category);
                refreshCharts();
            } else {
                JOptionPane.showMessageDialog(this, "未找到该收入类别", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 添加支出按钮事件
        addExpenseButton.addActionListener(e -> {
            String category = expenseCategoryField.getText();
            String amountText = expenseAmountField.getText();
            try {
                double amount = Double.parseDouble(amountText);
                expenseData.put(category, expenseData.getOrDefault(category, 0.0) + amount);
                refreshCharts();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "请输入有效的金额", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 删除支出按钮事件
        deleteExpenseButton.addActionListener(e -> {
            String category = expenseCategoryField.getText();
            if (expenseData.containsKey(category)) {
                expenseData.remove(category);
                refreshCharts();
            } else {
                JOptionPane.showMessageDialog(this, "未找到该支出类别", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // 创建饼状图面板
    private JPanel createPieChartPanel(String title, Map<String, Double> data) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                title, // 图表标题
                dataset, // 数据集
                true, // 是否显示图例
                true, // 是否显示工具提示
                false // 是否生成 URL
        );

        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setCircular(true); // 设置为圆形
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setBackgroundPaint(new Color(240, 240, 240)); // 设置背景颜色

        return new ChartPanel(pieChart);
    }

    // 创建柱状图面板
    private JPanel createBarChartPanel(String type, Map<String, Double> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // 添加数据
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), type, entry.getKey());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                type + "详情", // 图表标题
                "类别", // X 轴标签
                "金额", // Y 轴标签
                dataset // 数据集
        );

        CategoryPlot plot = (CategoryPlot) barChart.getPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setItemMargin(0.1); // 设置柱状图柱间距
        plot.setBackgroundPaint(new Color(240, 240, 240)); // 设置背景颜色

        return new ChartPanel(barChart);
    }

    // 刷新图表
    private void refreshCharts() {
        // 移除旧的图表
        if (getContentPane().getComponentCount() > 1) {
            getContentPane().remove(1);
        }

        // 创建新的图表面板
        JPanel chartPanel = new JPanel(new GridLayout(2, 2)); // 2行2列的布局
        chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 增加边距

        // 收入饼状图
        chartPanel.add(createPieChartPanel("收入分布", incomeData));

        // 支出饼状图
        chartPanel.add(createPieChartPanel("支出分布", expenseData));

        // 收入柱状图
        chartPanel.add(createBarChartPanel("收入", incomeData));

        // 支出柱状图
        chartPanel.add(createBarChartPanel("支出", expenseData));

        // 添加到窗口
        add(chartPanel, BorderLayout.CENTER);

        // 重绘窗口
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AccountingApp app = new AccountingApp();
            app.setVisible(true);
        });
    }
}
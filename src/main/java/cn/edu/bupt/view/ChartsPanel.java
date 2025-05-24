package cn.edu.bupt.view;

import cn.edu.bupt.model.*;
import org.jfree.chart.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;
import org.jfree.data.general.*;
import javax.swing.*;
import java.awt.*;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Charts panel for displaying transaction statistics in various chart formats
 * 图表面板，用于以各种图表格式显示交易统计信息
 */

public class ChartsPanel extends JPanel {
    private final TransactionManager tm = TransactionManager.getInstance();
    private JComboBox<Integer> yearComboBox;
    private JComboBox<String> monthComboBox;
    private JPanel chartsPanel;

    /**
     * Constructor for ChartsPanel
     * ChartsPanel的构造函数
     */

    public ChartsPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Initialize filter panel / 初始化筛选面板
        JPanel filterPanel = createFilterPanel();
        add(filterPanel, BorderLayout.NORTH);        // Main chart area with scrollbar / 主图表区域（带滚动条）
        chartsPanel = new JPanel();
        chartsPanel.setLayout(new BoxLayout(chartsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(chartsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        refreshCharts();
    }

    /**
     * Create filter panel with year and month selection
     * 创建带有年份和月份选择的过滤面板
     * 
     * @return JPanel Filter panel / 过滤面板
     */
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        yearComboBox = new JComboBox<>();
        monthComboBox = new JComboBox<>(new String[]{"All Year", "Jan", "Feb", "Mar", "Apr", "May", "Jun","Jul", "Aug", "Sep", "Oct", "Nov", "Dec"});
        JButton refreshBtn = new JButton("Refresh");
        
        initYearComboBox();
        panel.add(new JLabel("Year:"));
        panel.add(yearComboBox);
        panel.add(new JLabel("Month:"));
        panel.add(monthComboBox);
        panel.add(refreshBtn);
        
        refreshBtn.addActionListener(e -> refreshCharts());
        return panel;
    }
    
    /**
     * Initialize year combo box with available years from transactions
     * 使用交易记录中的可用年份初始化年份下拉框
     */
    // Initialize yearComboBox, correcting the Collections.max call / 在 initYearComboBox 方法中，修正 Collections.max 的调用
    private void initYearComboBox() {
        yearComboBox.removeAllItems();
        Set<Integer> existingYears = new TreeSet<>();
        for (Transaction ta : tm.currentUser.getTransactions()) {
            try {
                int year = Integer.parseInt(ta.getDatetime().substring(0, 4));
                existingYears.add(year);
            } catch (Exception ignored) {}
        }
        
        int currentYear = 2025;
        int minYear = existingYears.isEmpty() ? currentYear : Collections.min(existingYears);
        for (int y = minYear; y <= currentYear; y++) {
            yearComboBox.addItem(y);
        }
        // 使用单参数的 Collections.max，并确保 existingYears 非空
        if (!existingYears.isEmpty()) {
            yearComboBox.setSelectedItem(Collections.max(existingYears));
        } 
        else {
            yearComboBox.addItem(currentYear);
            yearComboBox.setSelectedItem(currentYear);
        }
    }

    /**
     * Refresh all charts based on selected year and month
     * 根据选定的年份和月份刷新所有图表
     */
    private void refreshCharts() {        
        
        chartsPanel.removeAll();
        
        // Get filter parameters / 获取筛选参数
        int selectedYear = (int) yearComboBox.getSelectedItem();
        int monthIdx = monthComboBox.getSelectedIndex(); // 0=All Year,1=Jan,...12=Dec
        boolean isFullYear = (monthIdx == 0);
        
        // Generate date range parameters / 生成时间范围参数
        String startDateTime, endDateTime;
        if (isFullYear) {
            startDateTime = String.format("%04d-01-01 00:00:00", selectedYear);
            endDateTime = String.format("%04d-01-01 00:00:00", selectedYear + 1);
        } 
        else {
            startDateTime = String.format("%04d-%02d-01 00:00:00", selectedYear, monthIdx);
            endDateTime = (monthIdx == 12) ? 
                String.format("%04d-01-01 00:00:00", selectedYear + 1) : 
                String.format("%04d-%02d-01 00:00:00", selectedYear, monthIdx + 1);
        }        
        
        // Create chart container / 创建图表容器
        JPanel chartRow = new JPanel(new GridLayout(1, 2, 10, 0));
        chartRow.setMaximumSize(new Dimension(550, 220));

        // Generate pie chart datasets / 生成饼图数据集
        DefaultPieDataset incomeDataset = createPieDataset(true, startDateTime, endDateTime);
        DefaultPieDataset expenseDataset = createPieDataset(false, startDateTime, endDateTime);        // Create pie chart panels / 创建饼图面板
        ChartPanel incomePie = createPieChartPanel(
            incomeDataset,
            "Income Breakdown",
            new Color[]{new Color(102, 205, 170), new Color(60, 179, 113), new Color(34, 139, 34)}
        );
        ChartPanel expensePie = createPieChartPanel(
            expenseDataset,
            "Expense Breakdown",
            new Color[]{new Color(255, 160, 122), new Color(219, 112, 147), new Color(199, 21, 133)}
        );
        chartRow.add(incomePie);
        chartRow.add(expensePie);        
        
        // Generate bar chart / 生成柱状图
        CategoryDataset barDataset = createBarDataset(selectedYear, monthIdx, startDateTime, endDateTime);
        JPanel barChartPanel = createBarChartPanel(barDataset, selectedYear, monthIdx);

        // Assemble components / 组装组件
        chartsPanel.add(chartRow);
        chartsPanel.add(Box.createVerticalStrut(10));
        chartsPanel.add(barChartPanel);
        
        // No data prompt / 无数据提示
        if (incomeDataset.getKeys().size() == 1 && "No Data".equals(incomeDataset.getKeys().get(0)) 
            && expenseDataset.getKeys().size() == 1 && "No Data".equals(expenseDataset.getKeys().get(0))) {
            chartsPanel.add(new JLabel("<html><h3 style='color:gray'>No transaction data in selected period</h3></html>"));
        }

        chartsPanel.revalidate();
        chartsPanel.repaint();
    }    
    
    /**
     * Create pie chart dataset based on transaction data
     * 根据交易数据创建饼图数据集
     * 
     * @param isIncome Whether to show income data / 是否显示收入数据
     * @param start Start date/time / 开始日期/时间
     * @param end End date/time / 结束日期/时间
     * @return Pie chart dataset / 饼图数据集
     */
    private DefaultPieDataset createPieDataset(boolean isIncome, String start, String end) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Set<Tag> tags = isIncome ? tm.incomeTags : tm.expenseTags;

        // Iterate through all child tags and calculate amounts / 遍历所有子标签并统计金额
        tags.forEach(tag -> {
            int amountCent = tm.calculateAmount(
                tm.currentUser.getTransactions(),
                Collections.singleton(tag),
                start, end
            );
              // Clean tag name / 清理标签名称
            String name = tag.getName()
                .replaceAll("^__(INCOME|EXPENSE)_?", "")
                .replace("__", "")
                .replace("_", " ")
                .trim();
            
            if (amountCent > 0) {
                dataset.setValue(name, amountCent / 100.0);
            }        
        });

        // Handle empty data case / 处理空数据情况
        if (dataset.getKeys().isEmpty()) {
            dataset.setValue("No Data", 0);
        }
        return dataset;
    }    
    
    /**
     * Create bar chart dataset for comparing income and expenses
     * 创建用于比较收入和支出的柱状图数据集
     * 
     * @param year Selected year / 选定年份
     * @param monthIdx Selected month index / 选定月份索引
     * @param start Start date/time / 开始日期/时间
     * @param end End date/time / 结束日期/时间
     * @return Bar chart dataset / 柱状图数据集
     */
    private CategoryDataset createBarDataset(int year, int monthIdx, String start, String end) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] months = new DateFormatSymbols().getShortMonths();

        if (monthIdx == 0) { // Full year mode / 全年模式
            for (int m = 0; m < 12; m++) {
                // Generate monthly time range / 生成当月时间范围
                String monthStart = String.format("%04d-%02d-01 00:00:00", year, m+1);
                String monthEnd = (m == 11) ? 
                    String.format("%04d-01-01 00:00:00", year+1) : 
                    String.format("%04d-%02d-01 00:00:00", year, m+2);

                // Calculate monthly income and expenses / 统计当月收支
                double income = tm.calculateAmount(
                    tm.currentUser.getTransactions(),
                    tm.incomeTags,
                    monthStart, monthEnd
                ) / 100.0;
                
                double expense = tm.calculateAmount(
                    tm.currentUser.getTransactions(),
                    tm.expenseTags, 
                    monthStart, monthEnd
                ) / 100.0;

                dataset.addValue(income, "Income", months[m]);
                dataset.addValue(expense, "Expense", months[m]);
            }        
        } 
        else { // Single month mode / 单月模式
            String monthName = new DateFormatSymbols().getMonths()[monthIdx-1];
            
            double income = tm.calculateAmount(
                tm.currentUser.getTransactions(),
                tm.incomeTags,
                start, end
            ) / 100.0;
            
            double expense = tm.calculateAmount(
                tm.currentUser.getTransactions(),
                tm.expenseTags,
                start, end
            ) / 100.0;

            dataset.addValue(income, "Income", monthName);
            dataset.addValue(expense, "Expense", monthName);
        }
        return dataset;
    }    
    
    /**
     * Create pie chart panel for displaying transaction breakdown
     * 创建用于显示交易明细的饼图面板
     * 
     * @param dataset Pie chart dataset / 饼图数据集
     * @param title Chart title / 图表标题
     * @param colors Color scheme for the chart / 图表的配色方案
     * @return ChartPanel object / 图表面板对象
     */
    private ChartPanel createPieChartPanel(PieDataset dataset, String title, Color[] colors) {
        JFreeChart chart = ChartFactory.createPieChart(
                null, // Hide main title / 隐藏主标题
                dataset,
                false, // Disable legend / 关闭图例
                true,
                false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
        plot.setSimpleLabels(false);
        plot.setBackgroundPaint(Color.WHITE);
          // Set colors / 设置颜色
        for (int i = 0; i < dataset.getKeys().size(); i++) {
            plot.setSectionPaint((Comparable<?>) dataset.getKeys().get(i), colors[i % colors.length]);
        }
        
        // Set fonts / 设置字体
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        
        ChartPanel panel = new ChartPanel(chart);
        panel.setMaximumDrawWidth(250);
        panel.setPreferredSize(new Dimension(250, 200));
        return panel;
    }

    /**
     * Create bar chart panel for displaying income/expense comparison
     * 创建用于显示收入/支出比较的柱状图面板
     * 
     * @param dataset Bar chart dataset / 柱状图数据集
     * @param year Selected year / 选定年份
     * @param month Selected month / 选定月份
     * @return JPanel containing the chart / 包含图表的面板
     */
    private JPanel createBarChartPanel(CategoryDataset dataset, int year, int month) {
        JFreeChart chart = ChartFactory.createBarChart(
                null, // Hide main title / 隐藏主标题
                null, // Hide X-axis label / 隐藏X轴标签
                "Amount (yuan)",
                dataset,
                PlotOrientation.VERTICAL,
                false, // Show legend / 显示图例
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
          // Set axis label angle / 设置坐标轴标签角度
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 9));
        
        // Set value axis font / 设置数值轴字体
        plot.getRangeAxis().setLabelFont(new Font("SansSerif", Font.PLAIN, 10));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(530, 220));
        
        JPanel container = new JPanel(new BorderLayout());
        container.add(chartPanel, BorderLayout.CENTER);
        return container;
    }    
    
    /**
     * Refresh all chart data and redraw charts
     * 刷新所有图表数据并重绘图表
     */
    public void refresh() {
        initYearComboBox();
        refreshCharts();
    }
}
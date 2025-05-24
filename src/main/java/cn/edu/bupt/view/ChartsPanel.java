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

public class ChartsPanel extends JPanel {
    private final TransactionManager tm = TransactionManager.getInstance();
    private JComboBox<Integer> yearComboBox;
    private JComboBox<String> monthComboBox;
    private JPanel chartsPanel;

    public ChartsPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 初始化筛选面板
        JPanel filterPanel = createFilterPanel();
        add(filterPanel, BorderLayout.NORTH);

        // 主图表区域（带滚动条）
        chartsPanel = new JPanel();
        chartsPanel.setLayout(new BoxLayout(chartsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(chartsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        refreshCharts();
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        yearComboBox = new JComboBox<>();
        monthComboBox = new JComboBox<>(new String[]{"All Year", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"});
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

    // 在 initYearComboBox 方法中，修正 Collections.max 的调用
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
        } else {
            yearComboBox.addItem(currentYear);
            yearComboBox.setSelectedItem(currentYear);
        }
    }

    private void refreshCharts() {
        chartsPanel.removeAll();
        
        // 获取筛选参数
        int selectedYear = (int) yearComboBox.getSelectedItem();
        int monthIdx = monthComboBox.getSelectedIndex(); // 0=All Year,1=Jan,...12=Dec
        boolean isFullYear = (monthIdx == 0);
        
        // 生成时间范围参数
        String startDateTime, endDateTime;
        if (isFullYear) {
            startDateTime = String.format("%04d-01-01 00:00:00", selectedYear);
            endDateTime = String.format("%04d-01-01 00:00:00", selectedYear + 1);
        } else {
            startDateTime = String.format("%04d-%02d-01 00:00:00", selectedYear, monthIdx);
            endDateTime = (monthIdx == 12) ? 
                String.format("%04d-01-01 00:00:00", selectedYear + 1) : 
                String.format("%04d-%02d-01 00:00:00", selectedYear, monthIdx + 1);
        }

        // 创建图表容器
        JPanel chartRow = new JPanel(new GridLayout(1, 2, 10, 0));
        chartRow.setMaximumSize(new Dimension(550, 220));

        // 生成饼图数据集
        DefaultPieDataset incomeDataset = createPieDataset(true, startDateTime, endDateTime);
        DefaultPieDataset expenseDataset = createPieDataset(false, startDateTime, endDateTime);

        // 创建饼图面板
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

        // 生成柱状图
        CategoryDataset barDataset = createBarDataset(selectedYear, monthIdx, startDateTime, endDateTime);
        JPanel barChartPanel = createBarChartPanel(barDataset, selectedYear, monthIdx);

        // 组装组件
        chartsPanel.add(chartRow);
        chartsPanel.add(Box.createVerticalStrut(10));
        chartsPanel.add(barChartPanel);
        
        // 无数据提示
        if (incomeDataset.getKeys().size() == 1 && "No Data".equals(incomeDataset.getKeys().get(0)) 
            && expenseDataset.getKeys().size() == 1 && "No Data".equals(expenseDataset.getKeys().get(0))) {
            chartsPanel.add(new JLabel("<html><h3 style='color:gray'>No transaction data in selected period</h3></html>"));
        }

        chartsPanel.revalidate();
        chartsPanel.repaint();
    }

    private DefaultPieDataset createPieDataset(boolean isIncome, String start, String end) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Set<Tag> tags = isIncome ? tm.incomeTags : tm.expenseTags;

        // 遍历所有子标签并统计金额
        tags.forEach(tag -> {
            int amountCent = tm.calculateAmount(
                tm.currentUser.getTransactions(),
                Collections.singleton(tag),
                start, end
            );
            
            // 清理标签名称
            String name = tag.getName()
                .replaceAll("^__(INCOME|EXPENSE)_?", "")
                .replace("__", "")
                .replace("_", " ")
                .trim();
            
            if (amountCent > 0) {
                dataset.setValue(name, amountCent / 100.0);
            }
        });

        // 处理空数据情况
        if (dataset.getKeys().isEmpty()) {
            dataset.setValue("No Data", 0);
        }
        return dataset;
    }

    private CategoryDataset createBarDataset(int year, int monthIdx, String start, String end) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] months = new DateFormatSymbols().getShortMonths();

        if (monthIdx == 0) { // 全年模式
            for (int m = 0; m < 12; m++) {
                // 生成当月时间范围
                String monthStart = String.format("%04d-%02d-01 00:00:00", year, m+1);
                String monthEnd = (m == 11) ? 
                    String.format("%04d-01-01 00:00:00", year+1) : 
                    String.format("%04d-%02d-01 00:00:00", year, m+2);

                // 统计当月收支
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
        } else { // 单月模式
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

    private ChartPanel createPieChartPanel(PieDataset dataset, String title, Color[] colors) {
        JFreeChart chart = ChartFactory.createPieChart(
                null, // 隐藏主标题
                dataset,
                false, // 关闭图例
                true,
                false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
        plot.setSimpleLabels(false);
        plot.setBackgroundPaint(Color.WHITE);
        
        // 设置颜色
        for (int i = 0; i < dataset.getKeys().size(); i++) {
            plot.setSectionPaint((Comparable<?>) dataset.getKeys().get(i), colors[i % colors.length]);
        }
        
        // 设置字体
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        
        ChartPanel panel = new ChartPanel(chart);
        panel.setMaximumDrawWidth(250);
        panel.setPreferredSize(new Dimension(250, 200));
        return panel;
    }

    private JPanel createBarChartPanel(CategoryDataset dataset, int year, int month) {
        JFreeChart chart = ChartFactory.createBarChart(
                null, // 隐藏主标题
                null, // 隐藏X轴标签
                "Amount (yuan)",
                dataset,
                PlotOrientation.VERTICAL,
                false, // 显示图例
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        // 设置坐标轴标签角度
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 9));
        
        // 设置数值轴字体
        plot.getRangeAxis().setLabelFont(new Font("SansSerif", Font.PLAIN, 10));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(530, 220));
        
        JPanel container = new JPanel(new BorderLayout());
        container.add(chartPanel, BorderLayout.CENTER);
        return container;
    }

    public void refresh() {
        initYearComboBox();
        refreshCharts();
    }
}
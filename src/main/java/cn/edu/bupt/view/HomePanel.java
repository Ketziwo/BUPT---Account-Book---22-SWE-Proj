// package cn.edu.bupt.view;

// import javax.swing.*;
// import java.awt.*;
// import java.time.YearMonth;
// import java.util.HashMap;

// public class HomePanel extends JPanel {
//     private JComboBox<String> ledgerComboBox;
//     private YearMonth currentMonth;
//     private JPanel chartPanel;
//     private JPanel calendarPanel;

//     public HomePanel() {
//         setLayout(new BorderLayout(20, 20));
//         currentMonth = YearMonth.now();
        
//         // 1. 顶部控制栏
//         JPanel topBar = createTopBar();
//         add(topBar, BorderLayout.NORTH);

//         // 2. 主体内容区（左右分割）
//         JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//         mainSplitPane.setDividerLocation(600); // 设置左右分割比例
        
//         // 左侧统计区
//         JPanel leftPanel = buildLeftStatisticsArea();
//         mainSplitPane.setLeftComponent(leftPanel);
        
//         // 右侧日历区
//         calendarPanel = buildCalendarPanel();
//         mainSplitPane.setRightComponent(calendarPanel);
        
//         add(mainSplitPane, BorderLayout.CENTER);
//     }

//     // 创建顶部控制栏（网页9的布局嵌套实践）
//     private JPanel createTopBar() {
//         JPanel panel = new JPanel(new BorderLayout(10, 0));
//         panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

//         // 左侧账本选择
//         JPanel ledgerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//         ledgerComboBox = new JComboBox<>(new String[]{"全部账本", "个人账本", "家庭账本"});
//         ledgerComboBox.setSelectedIndex(0);
//         ledgerPanel.add(new JLabel("选择账本："));
//         ledgerPanel.add(ledgerComboBox);

//         // 右侧新增交易按钮（网页11的事件绑定模式）
//         JButton newButton = new JButton("新增交易");
//         newButton.addActionListener(e -> new JPanel().setVisible(true));

//         panel.add(ledgerPanel, BorderLayout.WEST);
//         panel.add(newButton, BorderLayout.EAST);
//         return panel;
//     }

//     // 构建左侧统计区（网页9的网格布局应用）
//     private JPanel buildLeftStatisticsArea() {
//         JPanel panel = new JPanel(new BorderLayout(0, 15));
        
//         // 上部三个统计卡片（网页10的面板嵌套技巧）
//         JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
//         statsPanel.add(createStatCard("本月支出", "¥ 3,200"));
//         statsPanel.add(createStatCard("本月收入", "¥ 8,500"));
//         statsPanel.add(createStatCard("当前结余", "¥ 5,300"));
        
//         // 中间直方图（网页3的数据可视化思路）
//         chartPanel = new JPanel();
//         chartPanel.setBorder(BorderFactory.createTitledBorder("每日收支趋势"));
//         // 此处可集成JFreeChart等图表库
        
//         // 下部饼图区域（网页7的组件分层概念）
//         JPanel piePanel = new JPanel(new GridLayout(1, 2, 15, 0));
//         piePanel.add(createPieChartPanel("收入占比"));
//         piePanel.add(createPieChartPanel("支出分类"));

//         panel.add(statsPanel, BorderLayout.NORTH);
//         panel.add(chartPanel, BorderLayout.CENTER);
//         panel.add(piePanel, BorderLayout.SOUTH);
//         return panel;
//     }

//     // 创建统计卡片（网页9的样式设计）
//     private JPanel createStatCard(String title, String value) {
//         JPanel card = new JPanel(new BorderLayout());
//         card.setBorder(BorderFactory.createCompoundBorder(
//             BorderFactory.createLineBorder(Color.LIGHT_GRAY),
//             BorderFactory.createEmptyBorder(10, 15, 10, 15)
//         ));
//         card.add(new JLabel(title, SwingConstants.CENTER), BorderLayout.NORTH);
//         JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
//         valueLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
//         card.add(valueLabel, BorderLayout.CENTER);
//         return card;
//     }

//     // 创建日历面板（网页6的JLayeredPane分层思想）
//     private JPanel buildCalendarPanel() {
//         JPanel panel = new JPanel(new BorderLayout());
//         panel.setBorder(BorderFactory.createTitledBorder("月度日历视图"));
        
//         // 日历头部的月份切换（网页10的事件处理模式）
//         JPanel header = new JPanel(new BorderLayout());
//         JLabel monthLabel = new JLabel(currentMonth.toString(), SwingConstants.CENTER);
//         JButton prevBtn = new JButton("←");
//         prevBtn.addActionListener(e -> changeMonth(-1));
//         JButton nextBtn = new JButton("→");
//         nextBtn.addActionListener(e -> changeMonth(1));
        
//         header.add(prevBtn, BorderLayout.WEST);
//         header.add(monthLabel, BorderLayout.CENTER);
//         header.add(nextBtn, BorderLayout.EAST);
        
//         // 日历主体（网页9的网格布局实践）
//         JPanel grid = new JPanel(new GridLayout(0, 7, 2, 2));
//         String[] days = {"日", "一", "二", "三", "四", "五", "六"};
//         for (String day : days) {
//             grid.add(new JLabel(day, SwingConstants.CENTER));
//         }
//         // 此处添加日期单元格（示例数据）
//         for (int i = 1; i <= 30; i++) {
//             JButton dayBtn = new JButton(i + "\n¥ " + (i*100));
//             dayBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
//             grid.add(dayBtn);
//         }
        
//         panel.add(header, BorderLayout.NORTH);
//         panel.add(grid, BorderLayout.CENTER);
//         return panel;
//     }

//     // 月份切换逻辑（网页4的时间处理思路）
//     private void changeMonth(int delta) {
//         currentMonth = currentMonth.plusMonths(delta);
//         remove(calendarPanel);
//         calendarPanel = buildCalendarPanel();
//         revalidate();
//         repaint();
//     }

//     // 饼图占位面板（网页3的图表占位方案）
//     private JPanel createPieChartPanel(String title) {
//         JPanel panel = new JPanel() {
//             @Override
//             protected void paintComponent(Graphics g) {
//                 super.paintComponent(g);
//                 // 此处可绘制饼图
//                 g.setColor(Color.BLUE);
//                 g.fillArc(10, 10, 100, 100, 0, 120);
//                 g.setColor(Color.RED);
//                 g.fillArc(10, 10, 100, 100, 120, 240);
//             }
//         };
//         panel.setBorder(BorderFactory.createTitledBorder(title));
//         return panel;
//     }
// }

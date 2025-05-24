package cn.edu.bupt.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import cn.edu.bupt.dao.*;
import cn.edu.bupt.model.TransactionManager;

public class MainFrame extends JFrame{
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    public LeftPanel leftPanel;

    public MainFrame(Object lock) {
        setTitle("智能账单管理");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示[4](@ref)
        setResizable(false); // 允许窗口调整大小
        setVisible(true);

        
        // 1. 使用分层布局
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new BorderLayout());
        
        // 2. 初始化左侧导航栏（固定宽度150px）
        leftPanel = new LeftPanel(this);
        leftPanel.setPreferredSize(new Dimension(150, getHeight()));
        layeredPane.add(leftPanel, BorderLayout.WEST);
        
        // 3. 初始化右侧内容区（动态宽度）
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setPreferredSize(new Dimension(getWidth() - 150, getHeight()));
        
        // 4. 预加载所有功能面板
        mainContentPanel.add(new BudgetPanel(), "budget");
        mainContentPanel.add(new ChartsPanel(), "charts");
        mainContentPanel.add(new ManagerPanel(), "find");
        mainContentPanel.add(new SettingsPanel(this), "user");
        
        layeredPane.add(mainContentPanel, BorderLayout.CENTER);
        
        setContentPane(layeredPane);

        // 软件退出逻辑
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("正在执行关闭前的重要操作...");

                System.out.println("正在保存数据...");
                CsvTransactionDao.updateAllCSV();

                dispose();
                // 第三步：激活锁，唤醒并关闭主程序
                synchronized(lock) {
                    lock.notify();
                }
            }
        });
    }
    
    // 切换视图方法
    public void switchView(String viewName) {
        cardLayout.show(mainContentPanel, viewName);
        mainContentPanel.revalidate(); // 动态布局刷新[9](@ref)
        mainContentPanel.repaint();
    }
    public void refresh() {
        leftPanel.refresh();
        // managerPanel.refresh(TransactionManager.getInstance().currentUser.getTransactions());
    }
}

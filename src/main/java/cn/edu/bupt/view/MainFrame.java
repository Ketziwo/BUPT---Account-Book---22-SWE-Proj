package cn.edu.bupt.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import cn.edu.bupt.dao.*;
import cn.edu.bupt.model.TransactionManager;

/**
 * Main application window with layered layout
 * 主应用窗口，使用分层布局
 */
public class MainFrame extends JFrame{
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    public LeftPanel leftPanel;    /**
     * Main frame constructor
     * 主窗口构造函数
     * 
     * @param lock Object used for synchronization / 用于同步的对象
     */
    public MainFrame(Object lock) {
        setTitle("Smart Account Book");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen / 居中显示
        setResizable(false); // Disable window resizing / 禁止窗口调整大小
        setVisible(true);        
        // 1. Use layered pane / 使用分层布局
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new BorderLayout());
        
        // 2. Initialize left navigation bar (fixed width 150px) / 初始化左侧导航栏（固定宽度150px）
        leftPanel = new LeftPanel(this);
        leftPanel.setPreferredSize(new Dimension(150, getHeight()));
        layeredPane.add(leftPanel, BorderLayout.WEST);
        
        // 3. Initialize right content area (dynamic width) / 初始化右侧内容区（动态宽度）
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setPreferredSize(new Dimension(getWidth() - 150, getHeight()));
        
        // 4. Preload all function panels / 预加载所有功能面板
        mainContentPanel.add(new BudgetPanel(), "budget");
        mainContentPanel.add(new ChartsPanel(), "charts");
        mainContentPanel.add(new ManagerPanel(), "find");
        mainContentPanel.add(new SettingsPanel(this), "user");
        
        layeredPane.add(mainContentPanel, BorderLayout.CENTER);
          setContentPane(layeredPane);

        // Application exit logic / 软件退出逻辑
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Performing important operations before closing...");

                System.out.println("Saving data...");
                CsvTransactionDao.updateAllCSV();

                dispose();
                // Activate lock, wake up and close main program / 激活锁，唤醒并关闭主程序
                synchronized(lock) {
                    lock.notify();
                }
            }
        });    
    }
    
    /**
     * Switch between different views in the main content area
     * 切换主内容区中的不同视图
     * 
     * @param viewName Name of the view to display / 要显示的视图名称
     */
    public void switchView(String viewName) {
        cardLayout.show(mainContentPanel, viewName);
        mainContentPanel.revalidate(); // Dynamic layout refresh / 动态布局刷新
        mainContentPanel.repaint();
    }
    
    /**
     * Refresh all panels in the main frame
     * 刷新主窗口中的所有面板
     */
    public void refresh() {
        leftPanel.refresh();
        // managerPanel.refresh(TransactionManager.getInstance().currentUser.getTransactions());
    }
}

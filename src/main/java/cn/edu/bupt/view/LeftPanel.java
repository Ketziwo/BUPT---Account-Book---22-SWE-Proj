package cn.edu.bupt.view;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;

import cn.edu.bupt.model.*;

/**
 * Left navigation panel with user info and navigation buttons
 * 左侧导航面板，包含用户信息和导航按钮
 */
public class LeftPanel extends JPanel {
    private JLabel JLusericon;
    private JLabel JLusername;    
    
    /**
     * Left panel constructor
     * 左侧面板构造函数
     * 
     * @param parent Parent main frame / 父主窗口
     */
    public LeftPanel(MainFrame parent) {
        setLayout(new GridLayout(6, 1, 0, 15)); // 6 rows with 15px spacing / 6行按钮，间距15px
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // User avatar and name display / 用户名头像显示
        JLusericon = new JLabel();
        ImageIcon userIcon = new ImageIcon("resources/Users/default.png");
        userIcon = new ImageIcon(userIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH));
        JLusericon.setIcon(userIcon);
        JLusericon.setHorizontalAlignment(SwingConstants.CENTER);
        add(JLusericon);        
        JLusername = new JLabel(TransactionManager.getInstance().currentUser.getname());
        JLusername.setHorizontalAlignment(SwingConstants.CENTER);
        add(JLusername);

        // Navigation button configuration / 导航按钮配置
        String[] buttons = {
            "budget", "charts", "find", "user"
        };
        
        // Dynamically generate navigation buttons / 动态生成导航按钮
        for (String btnText : buttons) {
            JButton button = createStyledButton(btnText);
            button.addActionListener(e -> handleNavigation(parent, btnText));
            add(button);
        }    
    }

    /**
     * Create a styled button with icon
     * 创建带图标的按钮
     * 
     * @param text Button text / 按钮文本
     * @return Styled JButton / 样式化的按钮
     */
    private JButton createStyledButton(String text) {

        ImageIcon raw = new ImageIcon("resources/LeftPanel/" + text + ".png");        
        ImageIcon icon = new ImageIcon(raw.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));

        JButton button = new JButton(text, icon);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(Color.WHITE);
        button.setSize(130,20);
        button.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 20));
        return button;
    }    
    
    /**
     * Handle navigation button click events
     * 处理导航按钮点击事件
     * 
     * @param parent Parent main frame / 父主窗口
     * @param viewName View name to switch to / 要切换的视图名称
     */
    private void handleNavigation(MainFrame parent, String viewName) {
        parent.switchView(viewName);
        System.out.println("Switching to panel: " + viewName); // Debug log / 调试日志
    }    
    
    /**
     * Refresh user avatar and username display
     * 刷新用户头像和用户名显示
     */
    public void refresh() {
        ImageIcon userIcon = new ImageIcon("resources/Users/" + TransactionManager.getInstance().currentUser.getname() + ".png");
        userIcon = new ImageIcon(userIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH));
        JLusericon.setIcon(userIcon);
        JLusername.setText(TransactionManager.getInstance().currentUser.getname());
    }
}
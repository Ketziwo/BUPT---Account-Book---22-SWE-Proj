package cn.edu.bupt.view;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;

import cn.edu.bupt.model.*;

public class LeftPanel extends JPanel {
    private JLabel JLusericon;
    private JLabel JLusername;

    public LeftPanel(MainFrame parent) {
        setLayout(new GridLayout(6, 1, 0, 15)); // 5行按钮，间距15px[5](@ref)
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // 用户名头像显示
        JLusericon = new JLabel();
        ImageIcon userIcon = new ImageIcon("resources/Users/default.png");
        userIcon = new ImageIcon(userIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH));
        JLusericon.setIcon(userIcon);
        JLusericon.setHorizontalAlignment(SwingConstants.CENTER);
        add(JLusericon);

        JLusername = new JLabel(TransactionManager.getInstance().currentUser.getname());
        JLusername.setHorizontalAlignment(SwingConstants.CENTER);
        add(JLusername);

        // 导航按钮配置
        String[] buttons = {
            "budget", "charts", "find", "user"
        };
        
        // 动态生成导航按钮
        for (String btnText : buttons) {
            JButton button = createStyledButton(btnText);
            button.addActionListener(e -> handleNavigation(parent, btnText));
            add(button);
        }
    }

    // 创建带图标的按钮
    private JButton createStyledButton(String text) {

        ImageIcon raw = new ImageIcon("resources/LeftPanel/" + text + ".png");
        ImageIcon icon = new ImageIcon(raw.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));

        JButton button = new JButton(text, icon);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(Color.WHITE);
        button.setSize(130,20);
        button.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 20));
        return button;
    }

    // 处理导航事件
    private void handleNavigation(MainFrame parent, String viewName) {
        parent.switchView(viewName);
        System.out.println("切换到面板: " + viewName); // 调试日志
    }

    public void refresh() {
        ImageIcon userIcon = new ImageIcon("resources/Users/" + TransactionManager.getInstance().currentUser.getname() + ".png");
        userIcon = new ImageIcon(userIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH));
        JLusericon.setIcon(userIcon);
        JLusername.setText(TransactionManager.getInstance().currentUser.getname());
    }
}
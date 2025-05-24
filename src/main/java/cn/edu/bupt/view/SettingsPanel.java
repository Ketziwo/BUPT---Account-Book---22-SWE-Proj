package cn.edu.bupt.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.edu.bupt.model.*;
import cn.edu.bupt.utils.*;

/**
 * Settings panel for user settings and data import
 * 设置面板，用于用户设置和数据导入
 */
public class SettingsPanel extends JPanel {

    /**
     * Settings panel constructor
     * 设置面板构造函数
     * 
     * @param parent Parent main frame / 父主窗口
     */
    public SettingsPanel(MainFrame parent) {        
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create buttons and center them / 创建按钮并设置居中
        JButton switchAccountButton = createStyledButton("Switch Account");
        JButton importWeChatButton = createStyledButton("Import WeChat CSV");
        JButton importWeChatAIButton = createStyledButton("Import WeChat CSV (AI)");
        JButton BudgetAIAdvisorButton = createStyledButton("AI Budget Advisor");


        // Add spacing between buttons / 添加按钮间的间距
        add(Box.createVerticalGlue());
        add(switchAccountButton);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(importWeChatButton);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(importWeChatAIButton);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(BudgetAIAdvisorButton);
        add(Box.createVerticalGlue());        // Switch account button event handler / 切换账号按钮事件处理
        switchAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
                JTextField usernameField = new JTextField(15);
                JPasswordField passwordField = new JPasswordField(15);
                
                inputPanel.add(new JLabel("Username:"));
                inputPanel.add(usernameField);
                inputPanel.add(new JLabel("Password:"));
                inputPanel.add(passwordField);

                int result = JOptionPane.showConfirmDialog(
                        SettingsPanel.this,
                        inputPanel,
                        "Switch Account",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

                if (result == JOptionPane.OK_OPTION) {
                    String inputName = usernameField.getText().trim();
                    String inputPwd = new String(passwordField.getPassword()).trim();                    
                    if (inputName.isEmpty() || inputPwd.isEmpty()) {
                        showErrorDialog("Username and password cannot be empty");
                        return;
                    }

                    boolean isValid = false;
                    for (User user : TransactionManager.getInstance().Users) {
                        if (user.getname().equals(inputName) && user.getPwd().equals(inputPwd)) {
                            TransactionManager.getInstance().currentUser = user;
                            isValid = true;
                            break;
                        }
                    }

                    if (isValid) {                        JOptionPane.showMessageDialog(
                                SettingsPanel.this,
                                "Switch successful! Current user: " + TransactionManager.getInstance().currentUser.getname(),
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        parent.refresh();
                    } else {
                        showErrorDialog("Invalid username or password");
                    }
                }
            }
        });

        // Import WeChat button event handler / 导入微信按钮事件处理
        importWeChatButton.addActionListener(e -> WeChatParser.readWECHATfile("data/微信支付账单(20250501-20250510).csv"));
        importWeChatAIButton.addActionListener(e -> WeChatParser.readWECHATfileByAI("data/微信支付账单(20250501-20250510).csv"));
        BudgetAIAdvisorButton.addActionListener(e -> AIAdvisor.getBudgetAdvise(TransactionManager.getInstance().currentUser));
    }    
    
    /**
     * Creates a styled button with consistent appearance
     * 创建具有一致外观的样式化按钮
     * 
     * @param text Button text / 按钮文本
     * @return Styled JButton / 样式化的JButton
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(180, 40));
        button.setMaximumSize(new Dimension(200, 45));
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        return button;
    }

    /**
     * Shows an error dialog with the specified message
     * 显示带有指定消息的错误对话框
     * 
     * @param message Error message to display / 要显示的错误消息
     */
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Operation Failed",
                JOptionPane.ERROR_MESSAGE
        );
    }
}

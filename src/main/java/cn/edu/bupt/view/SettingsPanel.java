package cn.edu.bupt.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.edu.bupt.model.*;
import cn.edu.bupt.utils.*;

public class SettingsPanel extends JPanel {

    public SettingsPanel(MainFrame parent) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 创建按钮并设置居中
        JButton switchAccountButton = createStyledButton("切换账号");
        JButton importWeChatButton = createStyledButton("导入微信CSV");

        // 添加按钮间的间距
        add(Box.createVerticalGlue());
        add(switchAccountButton);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(importWeChatButton);
        add(Box.createVerticalGlue());

        // 切换账号按钮事件处理
        switchAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
                JTextField usernameField = new JTextField(15);
                JPasswordField passwordField = new JPasswordField(15);
                
                inputPanel.add(new JLabel("账号:"));
                inputPanel.add(usernameField);
                inputPanel.add(new JLabel("密码:"));
                inputPanel.add(passwordField);

                int result = JOptionPane.showConfirmDialog(
                        SettingsPanel.this,
                        inputPanel,
                        "账号切换",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

                if (result == JOptionPane.OK_OPTION) {
                    String inputName = usernameField.getText().trim();
                    String inputPwd = new String(passwordField.getPassword()).trim();

                    if (inputName.isEmpty() || inputPwd.isEmpty()) {
                        showErrorDialog("账号和密码不能为空");
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

                    if (isValid) {
                        JOptionPane.showMessageDialog(
                                SettingsPanel.this,
                                "切换成功！当前用户：" + TransactionManager.getInstance().currentUser.getname(),
                                "成功",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        parent.refresh();
                    } else {
                        showErrorDialog("账号或密码错误");
                    }
                }
            }
        });

        // 导入微信按钮事件处理
        importWeChatButton.addActionListener(e -> WeChatParser.readWECHATfile("data/微信支付账单(20250203-20250303)——【解压密码可在微信支付公众号查看】.csv"));
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(180, 40));
        button.setMaximumSize(new Dimension(200, 45));
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        return button;
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "操作失败",
                JOptionPane.ERROR_MESSAGE
        );
    }
}

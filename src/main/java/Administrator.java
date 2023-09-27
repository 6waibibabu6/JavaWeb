import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Administrator {
    private JFrame frame;
    private JButton passwordManagementButton;
    private JButton userManagementButton;
    private JButton goodsManagementButton; // 新增商品管理按钮

    public Administrator() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("管理员界面");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400); // 调整窗口大小
        frame.setLayout(new GridLayout(3, 1, 0, 10)); // 调整GridLayout的行数和行之间的间距

        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        passwordManagementButton = new JButton("密码管理");
        passwordPanel.add(passwordManagementButton);
        frame.add(passwordPanel);

        JPanel userPanel = new JPanel();
        userPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        userManagementButton = new JButton("用户管理");
        userPanel.add(userManagementButton);
        frame.add(userPanel);

        JPanel goodsPanel = new JPanel(); // 新增商品管理按钮面板
        goodsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        goodsManagementButton = new JButton("商品管理");
        goodsPanel.add(goodsManagementButton);
        frame.add(goodsPanel);

        // 为密码管理按钮添加 ActionListener
        passwordManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 调用 AdministratorController 中的方法处理密码管理逻辑
                AdministratorController.handlePasswordManagement();
            }
        });

        // 为用户管理按钮添加 ActionListener
        userManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 调用 AdministratorController 中的方法处理用户管理逻辑
                AdministratorController.handleUserManagement();
            }
        });

        // 为商品管理按钮添加 ActionListener
        goodsManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 调用 AdministratorController 中的方法处理商品管理逻辑
                AdministratorController.handleGoodsManagement();
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new Administrator();
    }
}


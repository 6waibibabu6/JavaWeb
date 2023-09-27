import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class AdministratorController {
    private JFrame frame;
    private JButton passwordManagementButton;

    private JButton goodsManagementButton;

    public AdministratorController() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("管理员界面");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);

        passwordManagementButton = new JButton("密码管理");
        panel.add(passwordManagementButton);

        goodsManagementButton=new JButton("商品管理");
        panel.add(goodsManagementButton);

        passwordManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePasswordManagement();
            }
        });
        goodsManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleGoodsManagement();
            }
        });


        frame.setVisible(true);
    }

    public static void handlePasswordManagement() {
        // 获取所有需要重置密码的用户ID
        ArrayList<String> resetUserIds = DBUtils.getUsersToResetPassword();

        if (resetUserIds.isEmpty()) {
            JOptionPane.showMessageDialog(null, "当前没有需要重置密码的用户。");
        } else {
            // 创建一个 JList，用于显示需要重置密码的用户ID
            JList<String> userList = new JList<>(resetUserIds.toArray(new String[0]));

            // 创建一个确认按钮
            JButton confirmButton = new JButton("确认");

            // 创建一个包含 JList 和确认按钮的面板
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(new JScrollPane(userList));
            panel.add(confirmButton);

            // 添加确认按钮的点击事件监听器
            confirmButton.addActionListener(e -> {
                // 获取选中的用户ID
                String selectedUserId = userList.getSelectedValue();

                // 重置密码并移除 reset 标记
                boolean success = DBUtils.resetPassword(selectedUserId);

                if (success) {
                    JOptionPane.showMessageDialog(null, "用户密码已重置为默认密码 00000。");
                } else {
                    JOptionPane.showMessageDialog(null, "密码重置失败。");
                }
            });

            // 显示对话框，让管理员选择需要重置密码的用户
            int option = JOptionPane.showOptionDialog(null, panel, "重置密码",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                    new Object[]{}, null);
        }
    }

    public static void handleUserManagement() {
        String[] options = {"查询单个用户", "查询所有用户"};
        int choice = JOptionPane.showOptionDialog(null, "请选择查询方式", "用户管理", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);

        switch (choice) {
            case 0:
                handleSingleUserQuery();
                break;
            case 1:
                handleAllUsersQuery();
                break;
            default:
                break;
        }
    }

    public static void handleSingleUserQuery() {
        String input = JOptionPane.showInputDialog(null, "请输入用户的id或username", "查询单个用户", JOptionPane.PLAIN_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            User user = DBUtils.getUserByIdOrUsername(input);
            if (user != null) {
                String message = "ID: " + user.getId() + "\nUsername: " + user.getName() + "\nPassword: " + user.getPassword() +
                        "\nPhone: " + user.getPhone() + "\nEmail: " + user.getEmail() + "\nLevel: " + user.getLevel() +
                        "\nPurchase Price: " + user.getPurchasePrice();
                JOptionPane.showMessageDialog(null, message, "查询结果", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "未找到该用户", "查询结果", JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    public static void handleAllUsersQuery() {
        ArrayList<User> allUsers = DBUtils.getAllUsers();
        if (!allUsers.isEmpty()) {
            StringBuilder message = new StringBuilder();
            for (User user : allUsers) {
                message.append("ID: ").append(user.getId()).append("\nUsername: ").append(user.getName()).append("\nPassword: ").append(user.getPassword())
                        .append("\nPhone: ").append(user.getPhone()).append("\nEmail: ").append(user.getEmail()).append("\nLevel: ").append(user.getLevel())
                        .append("\nPurchase Price: ").append(user.getPurchasePrice()).append("\n\n");
            }
            JOptionPane.showMessageDialog(null, message, "查询结果", JOptionPane.PLAIN_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "当前没有用户信息", "查询结果", JOptionPane.PLAIN_MESSAGE);
        }
    }

    public  static void handleGoodsManagement(){
        String[] goodsOptions = {"增加商品", "修改商品", "显示商品", "删除商品"};
        int goodsChoice = JOptionPane.showOptionDialog(null, "请选择商品管理操作", "商品管理", JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, goodsOptions, null);

        switch (goodsChoice) {
            case 0:
                handleAddGoods();
                break;
            case 1:
                handleModifyGoods();
                break;
            case 2:
                handleShowGoods();
                break;
            case 3:
                handleDeleteGoods();
                break;
            default:
                break;
        }

        }

    private static void handleAddGoods() {
        // 添加商品逻辑
        JTextField nameField = new JTextField(20);
        JTextField dateField = new JTextField(20);
        JTextField manufacturerField = new JTextField(20);
        JTextField speciesField = new JTextField(20);
        JTextField amountField = new JTextField(20);
        JTextField purchasePriceField = new JTextField(20);
        JTextField priceField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(8, 2));
        panel.add(new JLabel("名称(name):"));
        panel.add(nameField);
        panel.add(new JLabel("生产日期(date):"));
        panel.add(dateField);
        panel.add(new JLabel("生产厂家(manufacturer):"));
        panel.add(manufacturerField);
        panel.add(new JLabel("商品种类(category):"));
        panel.add(speciesField);
        panel.add(new JLabel("数量(amount):"));
        panel.add(amountField);
        panel.add(new JLabel("进货价(purchaseprice):"));
        panel.add(purchasePriceField);
        panel.add(new JLabel("零售价(price):"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(null, panel, "增加商品",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String date = dateField.getText();
            String manufacturer = manufacturerField.getText();
            String species = speciesField.getText();
            String amount = amountField.getText();
            String purchasePrice = purchasePriceField.getText();
            String price = priceField.getText();

            // 将商品信息插入数据库中
            insertGoodsIntoDatabase(name, date, manufacturer, species, amount, purchasePrice, price);
        }
    }

    private static void insertGoodsIntoDatabase(String name, String date, String manufacturer, String species,
                                         String amount, String purchasePrice, String price) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/user_test", "root", "Ylh203217");
             Statement statement = connection.createStatement()) {
            String insertQuery = "INSERT INTO goods (id, name, date, manufacturer, species, amount, purchaseprice, price) " +
                    "VALUES ('" + generateRandomId() + "', '" + name + "', '" + date + "', '" + manufacturer + "', '" +
                    species + "', '" + amount + "', '" + purchasePrice + "', '" + price + "')";
            statement.executeUpdate(insertQuery);

            JOptionPane.showMessageDialog(null, "商品已成功添加。");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "添加商品时出现错误。");
        }
    }

    private static String generateRandomId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }



    private static void handleModifyGoods() {
        JTextField idField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("商品ID或名称:"));
        panel.add(idField);

        int result = JOptionPane.showConfirmDialog(null, panel, "修改商品",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String idOrName = idField.getText();
            // 获取商品信息并显示在界面上，供用户修改

            try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/user_test", "root", "Ylh203217");
                 Statement statement = connection.createStatement()) {
                String selectQuery = "SELECT * FROM goods WHERE id = '" + idOrName + "' OR name = '" + idOrName + "'";
                ResultSet resultSet = statement.executeQuery(selectQuery);

                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String date = resultSet.getString("date");
                    String manufacturer = resultSet.getString("manufacturer");
                    String species = resultSet.getString("species");
                    String amount = resultSet.getString("amount");
                    String purchasePrice = resultSet.getString("purchaseprice");
                    String price = resultSet.getString("price");

                    JTextField nameField = new JTextField(name, 20);
                    JTextField dateField = new JTextField(date, 20);
                    JTextField manufacturerField = new JTextField(manufacturer, 20);
                    JTextField speciesFiledField = new JTextField(species, 20);
                    JTextField amountField = new JTextField(amount, 20);
                    JTextField purchasePriceField = new JTextField(purchasePrice, 20);
                    JTextField priceField = new JTextField(price, 20);

                    JPanel modifyPanel = new JPanel(new GridLayout(8, 2));
                    modifyPanel.add(new JLabel("名称(name):"));
                    modifyPanel.add(nameField);
                    modifyPanel.add(new JLabel("生产日期(date):"));
                    modifyPanel.add(dateField);
                    modifyPanel.add(new JLabel("生产厂家(manufacturer):"));
                    modifyPanel.add(manufacturerField);
                    modifyPanel.add(new JLabel("商品种类(species):"));
                    modifyPanel.add(speciesFiledField);
                    modifyPanel.add(new JLabel("数量(amount):"));
                    modifyPanel.add(amountField);
                    modifyPanel.add(new JLabel("进货价(purchaseprice):"));
                    modifyPanel.add(purchasePriceField);
                    modifyPanel.add(new JLabel("零售价(price):"));
                    modifyPanel.add(priceField);

                    int modifyResult = JOptionPane.showConfirmDialog(null, modifyPanel, "修改商品信息",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    if (modifyResult == JOptionPane.OK_OPTION) {
                        String newName = nameField.getText();
                        String newDate = dateField.getText();
                        String newManufacturer = manufacturerField.getText();
                        String newSpecies = speciesFiledField.getText();
                        String newAmount = amountField.getText();
                        String newPurchasePrice = purchasePriceField.getText();
                        String newPrice = priceField.getText();

                        String updateQuery = "UPDATE goods SET name = '" + newName + "', date = '" + newDate +
                                "', manufacturer = '" + newManufacturer + "', category = '" + newSpecies +
                                "', amount = '" + newAmount + "', purchaseprice = '" + newPurchasePrice +
                                "', price = '" + newPrice + "' WHERE id = '" + resultSet.getString("id") + "'";
                        statement.executeUpdate(updateQuery);

                        JOptionPane.showMessageDialog(null, "商品信息已成功修改。");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "找不到对应的商品。");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "修改商品信息时出现错误。");
            }
        }
    }

    private static void handleShowGoods() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/user_test", "root", "Ylh203217");
             Statement statement = connection.createStatement()) {

            String selectQuery = "SELECT * FROM goods";
            ResultSet resultSet = statement.executeQuery(selectQuery);

            StringBuilder goodsInfo = new StringBuilder("商品信息：\n");

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String date = resultSet.getString("date");
                String manufacturer = resultSet.getString("manufacturer");
                String species= resultSet.getString("species");
                String amount = resultSet.getString("amount");
                String purchasePrice = resultSet.getString("purchaseprice");
                String price = resultSet.getString("price");

                goodsInfo.append("ID: ").append(id)
                        .append(", 名称: ").append(name)
                        .append(", 生产日期: ").append(date)
                        .append(", 生产厂家: ").append(manufacturer)
                        .append(", 商品种类: ").append(species)
                        .append(", 数量: ").append(amount)
                        .append(", 进货价: ").append(purchasePrice)
                        .append(", 零售价: ").append(price)
                        .append("\n");
            }

            if (goodsInfo.length() > 0) {
                JOptionPane.showMessageDialog(null, goodsInfo.toString(), "商品信息", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "没有商品信息。");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "获取商品信息时出现错误。");
        }
    }

    private static void handleDeleteGoods() {
        JTextField idField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("商品ID:"));
        panel.add(idField);

        int result = JOptionPane.showConfirmDialog(null, panel, "删除商品",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String id = idField.getText();

            try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/user_test", "root", "Ylh203217");
                 Statement statement = connection.createStatement()) {

                String selectQuery = "SELECT * FROM goods WHERE id = '" + id + "'";
                ResultSet resultSet = statement.executeQuery(selectQuery);

                if (resultSet.next()) {
                    int confirmResult = JOptionPane.showConfirmDialog(null, "确定要删除该商品吗？\n注意：删除后信息将无法恢复。",
                            "确认删除", JOptionPane.YES_NO_OPTION);

                    if (confirmResult == JOptionPane.YES_OPTION) {
                        String deleteQuery = "DELETE FROM goods WHERE id = '" + id + "'";
                        statement.executeUpdate(deleteQuery);
                        JOptionPane.showMessageDialog(null, "商品已成功删除。");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "找不到对应的商品。");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "删除商品时出现错误。");
            }
        }
    }

    public static void main(String[] args) {
        new Administrator();
    }
}

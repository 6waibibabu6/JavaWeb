import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Shopping {
    private JFrame frame;
    private JTable table;
    private JScrollPane scrollPane;
    private JButton userInfoButton; // 添加查看个人信息的按钮
    private JLabel cartInfoLabel; // 购物车信息标签
    private final static String driver = "com.mysql.cj.jdbc.Driver";
    private final static String username = "root";
    private final static String password = "Ylh203217";
    private final static String url = "jdbc:mysql://127.0.0.1/user_test";
    private final static String select = "SELECT id, name, price FROM goods";
    private static Connection connection;//连接对象
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private User currentUser;//User对象
    private  final ShoppingController shoppingController;//控制类对象
    double total = 0.0;//总价的计算要在增加和删除中进行

    // 控制刷新的静态变量
    public static boolean status=false;

    //连接数据库
    static {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
            connection = null;
        }
    }

    public Shopping(User user) {
        currentUser = user;
        shoppingController = new ShoppingController(currentUser, this, connection);
        connectToDatabase();
        initialize();
        displayGoods();
    }


    private void connectToDatabase() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
            connection = null;
        }
    }



    private void initialize() {
        frame = new JFrame("购物系统");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // 创建顶部面板
        JLabel titleLabel = new JLabel("购物系统");
        userInfoButton = new JButton("个人信息");

        // 设置个人信息按钮的首选大小
        Dimension buttonSize = new Dimension(100, 30);
        userInfoButton.setPreferredSize(buttonSize);

        topPanel.add(titleLabel);
        topPanel.add(userInfoButton);
        frame.add(topPanel, BorderLayout.NORTH);

        table = new JTable();
        scrollPane = new JScrollPane(table);
        frame.add(scrollPane);

        // 添加查看个人信息的按钮的动作监听器
        userInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UserInfo(currentUser);
            }
        });

        frame.setVisible(true);
    }




    private void displayGoods() {
        try {
            preparedStatement = connection.prepareStatement(select);
            resultSet = preparedStatement.executeQuery();
            // Get the column names from the ResultSet
            int columnCount = resultSet.getMetaData().getColumnCount();
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = resultSet.getMetaData().getColumnName(i);
            }

            // Create a DefaultTableModel with column names
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            // Add rows to the DefaultTableModel from the ResultSet
            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                model.addRow(rowData);
            }

            // 添加到购物车的按钮
            JButton addToCartButton = new JButton("添加到购物车");
            addToCartButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0) {
                        String productId = (String) table.getValueAt(selectedRow, 0);
                        String productName = (String) table.getValueAt(selectedRow, 1);
                        BigDecimal productPrice = (BigDecimal) table.getValueAt(selectedRow, 2);
                        double price = productPrice.doubleValue();
                        // 创建选购的商品实例
                        Goods goods = new Goods(productId, productName, price);
                        System.out.println(productId);
                        shoppingController.addToCart(goods);//启动
                    }
                }
            });

            JButton viewCartButton = new JButton("查看购物车");
            viewCartButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showShoppingCart(); // 显示购物车信息
                }
            });
            Dimension buttonSize = new Dimension(100, 30);
            addToCartButton.setPreferredSize(buttonSize);
            viewCartButton.setPreferredSize(buttonSize);
            frame.add(addToCartButton, BorderLayout.EAST);
            frame.add(viewCartButton, BorderLayout.SOUTH);

            // Set the DefaultTableModel to the table
            table.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the resources
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private void showShoppingCart() {
        try {
            // 打开数据库连接
           total=0;// 每次更新购物车时需要重新更新金额
            connection = DriverManager.getConnection(url, username, password);
            String selectQuery = "SELECT g.name, g.price, c.goods_amount, c.goods_id FROM goods g JOIN shopping_cart c ON g.id = c.goods_id WHERE c.user_id = ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setString(1, currentUser.getId());

            ResultSet resultSet = selectStatement.executeQuery();

            // 创建 DefaultTableModel 用于购物车表格
            DefaultTableModel model = new DefaultTableModel(new String[]{"商品ID","商品名称", "商品价格", "商品数量"}, 0);

            // 向 DefaultTableModel 添加行数据
            while (resultSet.next()) {
                String productName = resultSet.getString("name");
                double productPrice = resultSet.getDouble("price");
                int productAmount = resultSet.getInt("goods_amount");
                String productId = resultSet.getString("goods_id");
                total += productPrice * productAmount;
                Object[] rowData = new Object[]{productId,productName, productPrice, productAmount};
                model.addRow(rowData);
            }

            // 添加总价行到 DefaultTableModel
            Object[] totalRow = new Object[]{"总价", total, "/", "/","/"};
            model.addRow(totalRow);

            // JTable 并使用 DefaultTableModel
            JTable table = new JTable(model);

            // 调整列宽度
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            table.getTableHeader().setReorderingAllowed(false);
            table.getColumnModel().getColumn(0).setPreferredWidth(200);
            table.getColumnModel().getColumn(1).setPreferredWidth(100);
            table.getColumnModel().getColumn(2).setPreferredWidth(100);
            table.getColumnModel().getColumn(2).setPreferredWidth(100);
            // 创建 JScrollPane 并将 JTable 添加到其中
            JScrollPane scrollPane = new JScrollPane(table);

            // 创建新的 JFrame 并将 JScrollPane 添加到其中
            JFrame frame = new JFrame("购物车");

            // 创建支付按钮
            JButton paymentButton = new JButton("支付");
            paymentButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Pay pay = new Pay(currentUser.getId(), total);
                    pay.displayPaymentPage();
                    // 支付完成才进行刷新
                    if(status==true) showShoppingCart();
                }
            });

            // 创建 "移除" 按钮
            JButton removeButton = new JButton("移除");
            removeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String inputRow = JOptionPane.showInputDialog(frame, "请输入要移除的商品在列表中的行数(从1开始):");
                    if (inputRow != null) {
                        try {
                            int rowIndex = Integer.parseInt(inputRow) - 1; // 转换为0-based索引
                            if (rowIndex >= 0 && rowIndex < model.getRowCount()) {
                                String productId = (String) table.getValueAt(rowIndex, 0);
                                String inputAmount = JOptionPane.showInputDialog(frame, "请输入要移除的数量:");
                                if (inputAmount != null) {
                                    try {
                                        int removeAmount = Integer.parseInt(inputAmount);
                                        if (removeAmount > 0) {
                                            Goods product = new Goods(productId, "", 0.0); // 创建一个虚拟商品对象
                                            int productAmount=(int)table.getValueAt(rowIndex,3);
                                            product.setAmount(productAmount);
                                            currentUser.addToCart(product);
                                            //System.out.println(productId+" "+productAmount)
                                            shoppingController.removeFromCart(product, removeAmount); // 从购物车中删除商品
                                            showShoppingCart(); // 重新加载购物车界面
                                        } else {
                                            JOptionPane.showMessageDialog(frame, "请输入有效的数量");
                                        }
                                    } catch (NumberFormatException ex) {
                                        JOptionPane.showMessageDialog(frame, "请输入有效的数量");
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(frame, "请输入有效的商品行数");
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(frame, "请输入有效的商品行数");
                        }
                    }
                }
            });

            JButton  historyButton=new JButton("购物历史");
            historyButton.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        JFrame historyFrame = new JFrame("购物历史");
                        historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 关闭子窗口时不会退出应用程序
                        historyFrame.setSize(800, 600);
                        DefaultTableModel tableModel = new DefaultTableModel();
                        tableModel.addColumn("商品名称");
                        tableModel.addColumn("商品价格");
                        tableModel.addColumn("商品数量");
                        tableModel.addColumn("支付时间");
                        JTable table = new JTable(tableModel);
                        JScrollPane scrollPane = new JScrollPane(table);
                        historyFrame.add(scrollPane);



                        connection = DriverManager.getConnection(url, username, password);
                        String sql = "SELECT sh.user_id,sh.goods_amount,sh.date, g.name, g.price " +
                                "FROM shopping_history sh " +
                                "INNER JOIN goods g ON sh.goods_id = g.id " +
                                "WHERE sh.user_id = ?";
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                        String queryID=currentUser.getId();
                        preparedStatement.setString(1, queryID);
                        ResultSet resultSet = preparedStatement.executeQuery();


                        tableModel.setRowCount(0);// 清空数据
                        while (resultSet.next()) {
                            String productName = resultSet.getString("name");
                            String productPrice = resultSet.getString("price");
                            Double productAmount=resultSet.getDouble("goods_amount");
                            String paymentTime = resultSet.getString("date");
                            //添加到表格
                            tableModel.addRow(new Object[]{productName, productPrice, productAmount, paymentTime});
                        }
                        resultSet.close();
                        preparedStatement.close();
                        connection.close();
                        historyFrame.setVisible(true);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                }
            });


            // 按钮添加到底部
            JPanel buttonPanel =new JPanel(new FlowLayout(FlowLayout.LEFT));

            buttonPanel.add(removeButton);
            buttonPanel.add(paymentButton);
            buttonPanel.add(historyButton);
            frame.getContentPane().add(buttonPanel,BorderLayout.SOUTH);
            frame.getContentPane().add(scrollPane);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // 关闭 ResultSet 和 PreparedStatement
            resultSet.close();
            selectStatement.close();

            // 关闭数据库连接
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    }

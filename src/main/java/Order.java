import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Random;
import java.util.UUID;

public class Order {
    private final static String driver = "com.mysql.cj.jdbc.Driver";
    private final static String url = "jdbc:mysql://127.0.0.1/user_test";
    private final static String username = "root";
    private final static String password = "Ylh203217";
    private static Connection connection;

    private String userId;
    private String orderId;
    private double price;


    Order(String userId,double price){
        this.userId=userId;
        this.price=price;
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

    public void displayOrderInfo() {
            OrderController orderController=new OrderController();
            orderId= UUID.randomUUID().toString().substring(0, 8);
            orderController.createOrder(orderId,userId,price);
            JFrame frame = new JFrame("订单信息");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(300, 200);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel(new GridLayout(3, 2));

            JLabel userIdLabel = new JLabel("用户ID:");
            JLabel orderIdLabel = new JLabel("订单号:");
            JLabel priceLabel = new JLabel("价格:");

            JLabel userIdValue = new JLabel(userId);
            JLabel orderIdValue = new JLabel(orderId);
            JLabel priceValue = new JLabel(Double.toString(price));

            panel.add(userIdLabel);
            panel.add(userIdValue);
            panel.add(orderIdLabel);
            panel.add(orderIdValue);
            panel.add(priceLabel);
            panel.add(priceValue);
            frame.getContentPane().add(panel);
            frame.setVisible(true);
    }
}

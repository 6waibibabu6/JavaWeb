import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderController {
    private  static Connection connection;
    private final static String driver = "com.mysql.cj.jdbc.Driver";
    private final static String username = "root";
    private final static String password = "Ylh203217";
    private final static String url = "jdbc:mysql://127.0.0.1/user_test";

    static
    {
        try
        {
            Class.forName(driver);
            connection = DriverManager.getConnection(url,username,password);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void createOrder(String orderID, String userId, double totalPrice) {
        try {
            // 1. 检查订单中的商品数量是否超过库存
            if (!checkOrderQuantity(userId)) {
                System.out.println("某类商品数量超过库存，请修改订单");
                return;
            }

            // 2. 更新goods表中的商品数量
            updateGoodsQuantity(userId);

            // 3. 插入订单信息
            String insertQuery = "INSERT INTO orders (order_id, user_id, price) VALUES (?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setString(1, orderID);
            insertStatement.setString(2, userId);
            insertStatement.setDouble(3, totalPrice);
            insertStatement.executeUpdate();
            insertStatement.close();

            // 4. 移除购物车中的商品信息并插入到shopping_history表
            moveCartToHistory(userId);

            // 5. 更新user表中的purchase字段
            updateUserPurchase(userId, totalPrice);



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 1. 检查订单中的商品数量是否超过库存
    private boolean checkOrderQuantity(String userId) {
        // 根据订单中的商品数量与goods表中的库存进行比较，如果有一个商品数量超过库存，则返回false
        try {
            String selectQuery = "SELECT c.goods_id, c.goods_amount, g.amount FROM shopping_cart c " +
                    "INNER JOIN goods g ON c.goods_id = g.id WHERE c.user_id = ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setString(1, userId);
            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                int cartAmount = resultSet.getInt("goods_amount");
                int goodsQuantity = resultSet.getInt("amount");
                if (cartAmount > goodsQuantity) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. 更新goods表中的商品数量
    private void updateGoodsQuantity(String userId) {
        // 根据订单中的商品数量更新goods表中的库存
        try {
            String updateQuery = "UPDATE goods g " +
                    "INNER JOIN shopping_cart c ON g.id = c.goods_id " +
                    "SET g.amount = g.amount - c.goods_amount " +
                    "WHERE c.user_id = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setString(1, userId);
            updateStatement.executeUpdate();
            updateStatement.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 4. 移除购物车中的商品信息并插入到shopping_history表
    private void moveCartToHistory(String userId) {
        // 获取购物车中的商品信息
        List<Goods> cartItems = new ArrayList<>();

        try {
            String selectQuery = "SELECT * FROM shopping_cart WHERE user_id = ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setString(1, userId);
            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                String goodsId = resultSet.getString("goods_id");
                int goodsAmount = resultSet.getInt("goods_amount");
                Goods goods=new Goods(goodsId,"",0);
                goods.setAmount(goodsAmount);
                cartItems.add(goods);
            }
            resultSet.close();
            selectStatement.close();

            //获取时间
            java.util.Date currentDate = new java.util.Date();
            Timestamp currentTimestamp = new Timestamp(currentDate.getTime());

            // 将购物车中的商品信息插入到shopping_history表
            String insertQuery = "INSERT INTO shopping_history (user_id, goods_id, goods_amount,date) VALUES (?, ?, ?,?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

            for (Goods cartItem : cartItems) {
                insertStatement.setString(1, userId);
                insertStatement.setString(2, cartItem.getId());
                insertStatement.setInt(3, cartItem.getAmount());
                insertStatement.setTimestamp(4,currentTimestamp);
                insertStatement.executeUpdate();
            }
            insertStatement.close();

            // 清空购物车
            String deleteQuery = "DELETE FROM shopping_cart WHERE user_id = ?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
            deleteStatement.setString(1, userId);
            deleteStatement.executeUpdate();
            deleteStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 5. 更新user表中的purchase字段
    private void updateUserPurchase(String userId, double totalPrice) {
        try {
            // 获取当前用户的purchase字段值
            String selectQuery = "SELECT purchaseprice FROM user WHERE id = ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setString(1, userId);
            ResultSet resultSet = selectStatement.executeQuery();

            double currentPurchase = 0.0;
            if (resultSet.next()) {
                currentPurchase = resultSet.getDouble("purchaseprice");
            }
            resultSet.close();
            selectStatement.close();

            // 更新purchase字段
            double newPurchase = currentPurchase + totalPrice;
            String updateQuery = "UPDATE user SET purchaseprice = ? WHERE id = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setDouble(1, newPurchase);
            updateStatement.setString(2, userId);
            updateStatement.executeUpdate();
            updateStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

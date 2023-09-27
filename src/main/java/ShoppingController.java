import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
public class ShoppingController  {
   //需要获得当前User以及Shopping
    private Shopping shopping;//Shopping的对象
    private User currentUser;
    private static Connection connection;//连接数据库
    private final static String driver = "com.mysql.cj.jdbc.Driver";
    private final static String username = "root";
    private final static String password = "Ylh203217";
    private final static String url = "jdbc:mysql://127.0.0.1/user_test";




 public ShoppingController(User user, Shopping shopping, Connection connection) {
  currentUser = user;
  this.shopping = shopping;
  this.connection = connection;
 }

 public void addToCart(Goods product) {
  currentUser.addToCart(product);
  updateCartInDatabase(); // 数据库中的购物车信息
 }


 public void updateCartInDatabase() {
  try {
   connection = DriverManager.getConnection(url,username,password);

   // 查询用户当前购物车中的商品信息
   String selectQuery = "SELECT * FROM shopping_cart WHERE user_id = ? AND goods_id = ?";
   PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
   selectStatement.setString(1, currentUser.getId());

   for (Goods product : currentUser.getShoppingCart()) {
    selectStatement.setString(2, product.getId());
    ResultSet resultSet = selectStatement.executeQuery();

    if (resultSet.next()) {
     // 商品已存在于购物车中，更新数量
     int amount = resultSet.getInt("goods_amount");
     String updateQuery = "UPDATE shopping_cart SET goods_amount = ? WHERE user_id = ? AND goods_id = ?";
     PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
     updateStatement.setInt(1, amount + 1);
     updateStatement.setString(2, currentUser.getId());
     updateStatement.setString(3, product.getId());
     updateStatement.executeUpdate();
     updateStatement.close();
    } else {
     // 商品不存在于购物车中，创建新记录
     String insertQuery = "INSERT INTO shopping_cart (user_id, goods_id, goods_amount) VALUES (?, ?, 1)";
     PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
     insertStatement.setString(1, currentUser.getId());
     insertStatement.setString(2, product.getId());
     insertStatement.executeUpdate();
     insertStatement.close();
    }
   }
   // 关闭语句
   selectStatement.close();
   // 关闭数据库连接
   connection.close();
  } catch (SQLException e) {
   e.printStackTrace();
  }
 }

 public void removeFromCart(Goods product, int amountToRemove) {
  currentUser.removeFromCart(product, amountToRemove);
 modifyCartInDatabase();
 }
 public void modifyCartInDatabase() {
  try {
   connection = DriverManager.getConnection(url, username, password);
   String deleteQuery = "DELETE FROM shopping_cart WHERE user_id = ? AND goods_id = ?";
   String updateQuery = "UPDATE shopping_cart SET goods_amount = ? WHERE user_id = ? AND goods_id = ?";

   for (Goods product : currentUser.getShoppingCart()) {// i guess currentUser didn't initialize  ?
    String productId = product.getId();
    int productAmount = product.getAmount();

    if (productAmount <= 0) {
     // 如果商品数量小于等于零，从购物车中删除
     PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
     deleteStatement.setString(1, currentUser.getId());
     deleteStatement.setString(2, productId);
     deleteStatement.executeUpdate();
     deleteStatement.close();
    } else {
     // 更新购物车中商品的数量
     PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
     updateStatement.setInt(1, productAmount);
     updateStatement.setString(2, currentUser.getId());
     updateStatement.setString(3, productId);
     updateStatement.executeUpdate();
     updateStatement.close();
    }
   }

   // 关闭数据库连接
   connection.close();
  } catch (SQLException e) {
   e.printStackTrace();
  }
 }

}

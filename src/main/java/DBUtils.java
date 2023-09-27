
import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.ArrayList;

    public class DBUtils
    {
        private final static String driver = "com.mysql.cj.jdbc.Driver";
        private final static String username = "root";
        private final static String password = "Ylh203217";
        private final static String url = "jdbc:mysql://127.0.0.1/user_test";
        private final static String insert = "insert into user(id,username,password) values(?,?,?)";
        private final static String update = "update user set username = ?,password = ? where id = ?";
        private final static String delete = "delete from user where id = ?";
        private final static String select = "select * from user where username = ?";

        private static Connection connection;

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
                connection = null;
            }
        }

        public static boolean exists(User user) {
            try {
                PreparedStatement exist = connection.prepareStatement(select);
                exist.setString(1, user.getName());
                ResultSet existResult = exist.executeQuery();
                return existResult.next();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        public static boolean add(User user) {
            try {
                PreparedStatement add = connection.prepareStatement("INSERT INTO user (id, username, password, registertime) VALUES (?, ?, ?, ?)");
                user.setId(UUID.randomUUID().toString().substring(0, 8));
                add.setString(1, user.getId());
                add.setString(2, user.getName());
                add.setString(3, user.getPassword());

                // 获取当前时间并设置为注册时间
                LocalDateTime currentTime = LocalDateTime.now();
                Timestamp timestamp = Timestamp.valueOf(currentTime);
                add.setTimestamp(4, timestamp);

                return add.executeUpdate() == 1;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }


        public static boolean authenticate(User user) {
            try {
                PreparedStatement authenticate = connection.prepareStatement(select);
                authenticate.setString(1, user.getName());
                ResultSet authResult = authenticate.executeQuery();
                if (authResult.next()) {
                    String password = authResult.getString("password");
                    return password.equals(user.getPassword());
                }
                return false;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }


        public static boolean modify(User user) {
            try {
                PreparedStatement modify = connection.prepareStatement("UPDATE user SET username = ?, password = ?, phone = ?, email = ? WHERE id = ?");
                modify.setString(1, user.getName());
                modify.setString(2, user.getPassword());
                modify.setString(3, user.getPhone()); // 设置电话字段
                modify.setString(4, user.getEmail()); // 设置电子邮件字段
                modify.setString(5, user.getId());
                return modify.executeUpdate() == 1;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }


        public static boolean delete(User user)
        {
            if(exists(user))
            {
                try
                {
                    PreparedStatement del = connection.prepareStatement(delete);
                    del.setString(1, user.getId());
                    return del.executeUpdate() == 1;
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
            return false;
        }

        public static User getByName(String name)
        {
            try
            {
                PreparedStatement exist = connection.prepareStatement(select);
                exist.setString(1, name);
                ResultSet existResult = exist.executeQuery();
                if(existResult.next())
                {
                    User user = new User();
                    user.setId(existResult.getString("id"));
                    user.setName(existResult.getString("username"));
                    user.setPassword(existResult.getString("password"));
                    return user;
                }
                return null;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        //进行密码重置的请求
        public static boolean requestPasswordReset(String username) {
            try {
                String updateQuery = "UPDATE user SET reset = true WHERE username = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setString(1, username);
                int affectedRows = updateStatement.executeUpdate();
                updateStatement.close();

                return affectedRows > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        // 获取需要重置密码的账户
        public static ArrayList<String> getUsersToResetPassword() {
            ArrayList<String> resetUserIds = new ArrayList<>();

            String query = "SELECT id FROM user WHERE reset = true";
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    String userId = resultSet.getString("id");
                    resetUserIds.add(userId);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return resetUserIds;
        }

        //进行密码重置的操作
        public static boolean resetPassword(String userId) {
            try {
                // 设置自动提交为false，开启事务
                connection.setAutoCommit(false);

                // 首先将密码重置为默认密码 00000
                String updateQuery = "UPDATE user SET password = ? WHERE id = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    updateStatement.setString(1, "00000");
                    updateStatement.setString(2, userId);
                    updateStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    connection.rollback(); // 发生异常时回滚事务
                    return false;
                }

                // 然后移除 reset 标记
                String resetQuery = "UPDATE user SET reset = false WHERE id = ?";
                try (PreparedStatement resetStatement = connection.prepareStatement(resetQuery)) {
                    resetStatement.setString(1, userId);
                    resetStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    connection.rollback(); // 发生异常时回滚事务
                    return false;
                }

                // 提交事务
                connection.commit();

                // 设置自动提交为true
                connection.setAutoCommit(true);

                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        public static ArrayList<User> getAllUsers() {
            ArrayList<User> allUsers = new ArrayList<>();

            String query = "SELECT * FROM user";
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String name = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    String phone = resultSet.getString("phone");
                    String email = resultSet.getString("email");
                    String level = resultSet.getString("level");
                    String purchasePrice = resultSet.getString("purchaseprice");

                    User user = new User();
                    user.setId(id);
                    user.setName(name);
                    user.setPassword(password);
                    user.setPhone(phone);
                    user.setEmail(email);
                    user.setLevel(level);
                    user.setPurchasePrice(purchasePrice);

                    allUsers.add(user);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return allUsers;
        }


        public static User getUserByIdOrUsername(String idOrUsername) {
            String query = "SELECT * FROM user WHERE id = ? OR username = ?";
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                // 判断输入的idOrUsername是否是ID（UUID）格式，如果是则根据ID查询，否则根据用户名查询
                UUID uuid = null;
                try {
                    uuid = UUID.fromString(idOrUsername);
                } catch (IllegalArgumentException ignored) {
                }

                preparedStatement.setString(1, uuid != null ? idOrUsername : "");
                preparedStatement.setString(2, uuid == null ? idOrUsername : "");

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String id = resultSet.getString("id");
                        String name = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        String phone = resultSet.getString("phone");
                        String email = resultSet.getString("email");
                        String level = resultSet.getString("level");
                        String purchasePrice = resultSet.getString("purchaseprice");

                        User user =new User();
                        user.setId(id);
                        user.setName(name);
                        user.setPassword(password);
                        user.setPhone(phone);
                        user.setEmail(email);
                        user.setLevel(level);
                        user.setPurchasePrice(purchasePrice);

                        return user;
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }


    }

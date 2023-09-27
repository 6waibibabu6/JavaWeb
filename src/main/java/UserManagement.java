import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Enumeration;
import java.awt.Container;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class UserManagement
{
    private JFrame mainFrame = new JFrame("登录");
    private Container container = mainFrame.getContentPane();
    private JLabel titleLabel = new JLabel("登录/注册", JLabel.CENTER);

    private JPanel inputField = new JPanel();
    private JLabel usernameLabel = new JLabel("用户名:", JLabel.CENTER);
    private JTextField username = new JTextField();
    private JLabel passwordLabel = new JLabel("密码:", JLabel.CENTER);
    private JPasswordField password = new JPasswordField();

    private JPanel buttonField = new JPanel();
    private JButton login = new JButton("登录");
    private JButton register=new JButton("注册");
    private JButton cancel = new JButton("取消");

    private JPanel resetField = new JPanel();

    private JLabel resetLabel = new JLabel("密码重置:", JLabel.CENTER);

    private JButton resetPasswordButton = new JButton("密码重置");

    private User currentUser;
    //记录登录失败的次数
    private  int  failedLoginAttempts=0;
    //限制最大的登录次数
    private  int   MAX_LOGIN_ATTEMPTS=5;

    public UserManagement()
    {
        init();
        setFont(new Font("微软雅黑",Font.PLAIN,14));
        addEvent();
    }

    private void init() {
        container.setLayout(new GridLayout(4, 1, 0, 10));
        container.add(titleLabel);

        inputField.setLayout(new GridLayout(2, 2, 5, 5));
        inputField.add(usernameLabel);
        inputField.add(username);
        inputField.add(passwordLabel);
        inputField.add(password);
        container.add(inputField);

        JPanel buttonField = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonField.add(login);
        buttonField.add(register);
        buttonField.add(resetPasswordButton);
        buttonField.add(cancel);
        container.add(buttonField);

        resetField.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        resetField.add(resetLabel);
        resetField.add(resetPasswordButton);
        container.add(resetField);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 230);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private void setFont(Font font)
    {
        FontUIResource fontRes = new FontUIResource(font);
        for(Enumeration<Object> keys = UIManager.getDefaults().keys();keys.hasMoreElements();)
        {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if(value instanceof FontUIResource)
                UIManager.put(key, fontRes);
        }
    }

private void addEvent() {
    //登录逻辑的判定
    login.addActionListener(e -> {
        String username = this.username.getText();
        String password = new String(this.password.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "用户名和密码不能为空");
        }
        else {
            User user = new User();
            user.setName(username);
            user.setPassword(password);

            if (DBUtils.authenticate(user)) {
                // 登录成功
                if(username.equals("admin")&&password.equals("ynuadmin")){
                    JOptionPane.showMessageDialog(mainFrame,"管理员登录成功");
                    new Administrator();
                }
                else {
                    JOptionPane.showMessageDialog(mainFrame, "登录成功");
                    // new UserInfo(DBUtils.getByName(user.getName()));//跳转到购物界面
                    currentUser = DBUtils.getByName(username);
                    user.setId(currentUser.getId());//需要给出User对象Id！！！！,后续用于购物车的处理
                    new Shopping(user);
                }
                mainFrame.dispose();
            }
            else {
                // 登录次数的控制
                handleFailedLogin();
            }
        }
    });

    // 注册按钮的逻辑判断部分
    register.addActionListener(e -> {
        String username = this.username.getText();
        String password = new String(this.password.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "用户名和密码不能为空");
        } else if (username.length() < 5) {
            JOptionPane.showMessageDialog(mainFrame, "用户名长度不能小于5个字符");
        } else if (password.length() < 8) {
            JOptionPane.showMessageDialog(mainFrame, "密码长度不能小于8个字符");
        } else if (!isValidPassword(password)) {
            JOptionPane.showMessageDialog(mainFrame, "密码必须由大小写字母、数字和标点组成");
        } else {
            User user = new User();
            user.setName(username);
            user.setPassword(password);

            if (DBUtils.exists(user)) {
                // 用户名已存在
                JOptionPane.showMessageDialog(mainFrame, "用户名已存在");
            } else {
                // 注册成功
                if (DBUtils.add(user)) {
                    JOptionPane.showMessageDialog(mainFrame, "注册成功");
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "注册失败");
                }
            }
        }
    });



    cancel.addActionListener(e -> {
        mainFrame.dispose();
    });
    //重置密码逻辑
    resetPasswordButton.addActionListener(e-> {
            String enteredUsername = JOptionPane.showInputDialog(mainFrame, "请输入账号：");
            if (enteredUsername != null) {
                // Check if the entered username exists in the database
                User user = new User();
                user.setName(enteredUsername);
                if (DBUtils.exists(user)) {
                    // Update the reset column for the entered username
                    if (DBUtils.requestPasswordReset(enteredUsername)) {
                        JOptionPane.showMessageDialog(mainFrame, "密码重置请求已发送，请等待管理员确认。");
                    } else {
                        JOptionPane.showMessageDialog(mainFrame, "密码重置请求失败。");
                    }
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "输入的账号不存在。");
                }
            }
        }
    );

}
    private boolean isValidPassword(String password) {
        // 密码必须包含大小写字母、数字和标点符号
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(pattern);
    }

    private void handleFailedLogin() {
        failedLoginAttempts++;

        if (failedLoginAttempts >= MAX_LOGIN_ATTEMPTS) {
            int lockDuration = (int) Math.pow(2, failedLoginAttempts - MAX_LOGIN_ATTEMPTS + 1) * 5;
            JOptionPane.showMessageDialog(mainFrame, "登录失败次数过多，账户已锁定 " + lockDuration + " 秒");

            // 休眠锁定时间
            try {
                TimeUnit.SECONDS.sleep(lockDuration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 重置登录失败次数
            failedLoginAttempts = 0;
        } else {
            JOptionPane.showMessageDialog(mainFrame, "用户名或密码错误");
        }
    }

    public static void main(String[] args)
    {
        new UserManagement();
    }
}
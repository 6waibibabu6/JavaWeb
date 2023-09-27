import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;
import javax.swing.plaf.FontUIResource;

public class UserInfo {
    private JFrame mainFrame = new JFrame("用户信息");
    private Container container = mainFrame.getContentPane();
    private JLabel titleLabel = new JLabel("用户信息", JLabel.CENTER);

    private JPanel inputField = new JPanel();
    private JLabel idLabel = new JLabel("Id", JLabel.CENTER);
    private JTextField id = new JTextField();
    private JLabel usernameLabel = new JLabel("Username", JLabel.CENTER);
    private JTextField username = new JTextField();
    private JLabel passwordLabel = new JLabel("Password", JLabel.CENTER);
    private JPasswordField password = new JPasswordField();
    private JLabel phoneLabel = new JLabel("Phone", JLabel.CENTER);
    private JTextField phone = new JTextField();
    private JLabel emailLabel = new JLabel("Email", JLabel.CENTER);
    private JTextField email = new JTextField();

    private JLabel  levelLabel=new JLabel("Level",JLabel.CENTER);

    private JTextField level=new JTextField();
    private JPanel buttonField = new JPanel();
    private JButton update = new JButton("更新");
    private User user;

    public UserInfo(User user) {
        if (user == null)
            mainFrame.dispose();
        this.user = user;
        init();
        setFont(new Font("微软雅黑", Font.PLAIN, 14));
        addEvent();
    }

    private void init() {
        container.setLayout(new BorderLayout(10, 10));
        container.add(titleLabel, BorderLayout.NORTH);

        inputField.setLayout(new GridLayout(6, 2, 10, 10));
        inputField.add(idLabel);
        id.setText(user.getId());
        id.setEditable(false);
        inputField.add(id);

        inputField.add(levelLabel);
        double price=Double.parseDouble(user.getPurchasePrice());
        int levelValue=(int)(price/100);
        String FinalLevel=Integer.toString(levelValue);
        level.setText(FinalLevel);
        level.setEditable(false);
        inputField.add(level);

        inputField.add(usernameLabel);
        username.setText(user.getName());
        inputField.add(username);

        inputField.add(passwordLabel);
        password.setText(user.getPassword());
        inputField.add(password);

        inputField.add(phoneLabel);
        phone.setText(user.getPhone());
        inputField.add(phone);
        inputField.add(emailLabel);
        email.setText(user.getEmail());
        inputField.add(email);

        container.add(inputField, BorderLayout.CENTER);

        buttonField.setLayout(new FlowLayout());
        buttonField.add(update);
        container.add(buttonField, BorderLayout.SOUTH);

        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setSize(300, 320);
    }

    private void setFont(Font font) {
        FontUIResource fontRes = new FontUIResource(font);
        for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource)
                UIManager.put(key, fontRes);
        }
    }

    private void addEvent() {
        update.addActionListener(
                e -> {
                    user.setName(username.getText());
                    user.setPassword(new String(password.getPassword()));
                    user.setPhone(phone.getText());
                    user.setEmail(email.getText());
                    JOptionPane.showConfirmDialog(null, "更新" + (DBUtils.modify(user) ? "成功" : "失败"), "确认", JOptionPane.CLOSED_OPTION);
                }
        );
    }
}

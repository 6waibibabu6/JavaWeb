import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Pay {
    private String userId;
    private double totalPrice;

    private JDialog paymentDialog; // 使用 JDialog

    public Pay(String userId, double totalPrice) {
        this.userId = userId;
        this.totalPrice = totalPrice;
    }

    public void displayPaymentPage() {
        paymentDialog = new JDialog();
        paymentDialog.setTitle("支付");
        paymentDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        paymentDialog.setSize(300, 200);
        paymentDialog.setLocationRelativeTo(null);

        JLabel paymentLabel = new JLabel("请选择支付方式:");
        JLabel totalPriceLabel = new JLabel("需要支付的总价是: " + totalPrice);
        JButton unionPayButton = new JButton("银联支付");
        JButton wechatPayButton = new JButton("微信支付");

        unionPayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PayController payController = new PayController(userId, totalPrice);
                boolean paymentSuccess = payController.processPayment("银联支付");
                if (paymentSuccess) {
                    JOptionPane.showMessageDialog(null, "支付成功");
                } else {
                    JOptionPane.showMessageDialog(null, "支付失败");
                }
                paymentDialog.dispose(); // 关闭对话框
                // 可以进行购物车的刷新
                Shopping.status=true;
            }
        });

        wechatPayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PayController payController = new PayController(userId, totalPrice);
                boolean paymentSuccess = payController.processPayment("微信支付");
                if (paymentSuccess) {
                    JOptionPane.showMessageDialog(null, "支付成功");
                    paymentDialog.dispose(); // 关闭对话框
                } else {
                    JOptionPane.showMessageDialog(null, "支付失败");
                }
            }
        });

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.add(paymentLabel);
        panel.add(totalPriceLabel);
        panel.add(unionPayButton);
        panel.add(wechatPayButton);

        paymentDialog.getContentPane().add(panel);
        paymentDialog.setVisible(true); // 显示对话框
    }
}

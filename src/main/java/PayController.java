import java.util.Random;

public  class PayController{
    private String userId;
    private double totalPrice;

    PayController(String userId,double totalPrice){
        this.userId=userId;
        this.totalPrice=totalPrice;
    }
    public boolean processPayment(String paymentMethod) {
        // 执行支付逻辑，根据支付方式进行处理
        // 在这里你可以根据实际情况进行相应的支付操作，比如调用支付接口、更新订单状态等
        // 创建 OrderController 实例
        Order order=new Order(userId,totalPrice);
        order.displayOrderInfo();
        return true;
    }


}
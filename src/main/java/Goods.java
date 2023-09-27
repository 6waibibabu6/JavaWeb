public class Goods {
   private String id;
   private String name;
   private double price;
   private int amount; // 用于购物车的计算

   // 构造方法
   public Goods(String id, String name, double price) {
      this.id = id;
      this.name = name;
      this.price = price;
      this.amount = 0; // 初始化为0
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public double getPrice() {
      return price;
   }

   public int getAmount() {
      return amount;
   }

   public void setAmount(int amount) {
      this.amount = amount;
   }
}

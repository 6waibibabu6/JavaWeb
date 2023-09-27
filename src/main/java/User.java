import java.util.ArrayList;
import java.util.List;
public class User
{
    private String name;
    private String password;
    private String id;

    private String phone;

    private String  email;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setPurchasePrice(String purchaseprice) {
        this.purchaseprice = purchaseprice;
    }

    public String getPurchasePrice() {
        return purchaseprice;
    }

    private String  level;

    private String purchaseprice;
    private List<Goods> shoppingCart;//购物车字段

    //初始化购物车
    public User(){
        shoppingCart=new ArrayList<>();
    }
    //购物车的功能
    public void addToCart(Goods product) {
        shoppingCart.add(product);
    }


    private Goods getCartItemById(String productId) {
        for (Goods item : shoppingCart) {
            if (item.getId().equals(productId)) {
                return item;
            }
        }
        return null;// 会返回为空
    }

    public void removeFromCart(Goods product, int amountToRemove) {
        Goods cartItem = getCartItemById(product.getId());
        if (cartItem != null) {
            int currentAmount = cartItem.getAmount();
                cartItem.setAmount(currentAmount - amountToRemove);
        }
    }


    public  List<Goods> getShoppingCart(){return shoppingCart;}//获取购物车的方法
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setPhone(String phone) {this.phone=phone;}

    public void  setEmail(String email){this.email=email;}
    public void setId(String id)
    {
        this.id = id;
    }
    public String getPassword()
    {
        return password;
    }

    public String getId()
    {
        return id;
    }

    public String getPhone(){return phone;}

    public  String getEmail(){return email;}


}
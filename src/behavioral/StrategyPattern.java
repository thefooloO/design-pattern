package behavioral;

/**
 * 策略模式
 * 定义一族算法类, 将每个算法分别封装起来, 让它们可以相互替换
 * 该模式解耦了策略的定义、创建和使用
 */

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 策略的定义
 */
interface Strategy {
    void algorithmInterface();
}

class ConcreteStrategyA implements Strategy {

    @Override
    public void algorithmInterface() {
        // ...
    }
}

class ConcreteStrategyB implements Strategy {

    @Override
    public void algorithmInterface() {
        // ...
    }
}


/**
 * 策略的创建
 * 策略模式会包含一组策略, 使用时一般会通过类型来判断创建哪个策略来使用
 */
class StrategyFactory {
    private static final Map<String, Strategy> strategies = new HashMap<>();

    static {
        strategies.put("A", new ConcreteStrategyA());
        strategies.put("B", new ConcreteStrategyB());
    }


    public static Strategy getStrategy(String type) throws Exception {
        if(type == null || type.isEmpty()) {
            throw new Exception("type should not be empty");
        }
        return strategies.get(type);
    }
}


/**
 * 策略的使用
 * 客户端代码一般是在运行时根据配置、用户输入、计算结果等因素动态决定使用哪种策略
 */
class Application {
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.load(new FileInputStream("config.properties"));
        Strategy strategy = StrategyFactory.getStrategy(props.getProperty("type"));
        // ...
    }
}


/**
 * 例：如何利用策略模式避免分支判断？
 */
class Order {

    int type;

    public int getType() {
        return type;
    }
}

class OrderService1 {
    double discount(Order order){
        double discount = 0.0;
        if(order.getType() == 1) {
            // ...
        }
        else if(order.getType() == 2) {
            // ...
        }
        else if(order.getType() == 3) {
            // ...
        }
        return discount;
    }
}

interface DiscountStrategy {
    double calDiscount(Order order);
}

class NormalDiscountStrategy implements DiscountStrategy {

    @Override
    public double calDiscount(Order order) {
        return 0;
    }
}

class GrouponDiscountStrategy implements DiscountStrategy {

    @Override
    public double calDiscount(Order order) {
        return 0;
    }
}

class PromotionDiscountStrategy implements DiscountStrategy {

    @Override
    public double calDiscount(Order order) {
        return 0;
    }
}

class DiscountStrategyFactory {
    private static final Map<Integer, DiscountStrategy> strategies = new HashMap<>();

    static {
        strategies.put(1,new NormalDiscountStrategy());
        strategies.put(2,new GrouponDiscountStrategy());
        strategies.put(3,new PromotionDiscountStrategy());
    }

    public static DiscountStrategy getDiscountStrategy(Integer type) {
        return strategies.get(type);
    }
}

class OrderService2 {
    double discount(Order order) {
        DiscountStrategy discountStrategy = DiscountStrategyFactory.getDiscountStrategy(order.getType());
        return discountStrategy.calDiscount(order);
    }
}
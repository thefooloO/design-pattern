package structural;

/**
 * 代理模式
 * 在不改变原始类(被代理类)代码的情况下, 通过引入代理类来给原始类附加功能
 *
 * 应用场景：
 *  1. 业务系统的非功能性需求开发(监控、日志、统计等)
 *  2. RPC
 */

/**
 * example: 用于收集接口请求数据的性能计数器的使用
 */
class RequestInfo {

    String name;
    long responseTime;
    long startTimestamp;
    long endTimestamp;

    public RequestInfo(String name, long responseTime, long startTimestamp, long endTimestamp) {
        this.name = name;
        this.responseTime = responseTime;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }
}


class MetricsCollector {
    void recordRequest(RequestInfo requestInfo){}
}


class UserController1 {
    private MetricsCollector metricsCollector;

    public void login() {

        long startTimestamp = System.currentTimeMillis();
        // ...
        long endTimestamp = System.currentTimeMillis();
        long responseTime = endTimestamp - startTimestamp;
        metricsCollector.recordRequest(new RequestInfo("login", responseTime, startTimestamp, endTimestamp));
        // ...
    }

    public void register() {

        long startTimestamp = System.currentTimeMillis();
        // ...
        long endTimestamp = System.currentTimeMillis();
        long responseTime = endTimestamp - startTimestamp;
        metricsCollector.recordRequest(new RequestInfo("register", responseTime, startTimestamp, endTimestamp));
        // ...
    }

}

/**
 * 上述代码有两个问题：
 *  1. 性能计数器框架代码侵入到业务代码中
 *  2. 收集数据的代码跟业务代码无关, 本就不应该放到一个类中
 *  为了解耦框架代码和业务代码, 代理模式就派上用场了
 */
interface IUserController {
    void login();
    void register();
}

class UserController2 implements IUserController {

    @Override
    public void login() {}

    @Override
    public void register() {}
}


class UserControllerProxy1 implements IUserController {

    private MetricsCollector metricsCollector;
    public UserController2 userController2;

    public UserControllerProxy1(MetricsCollector metricsCollector, UserController2 userController2) {
        this.metricsCollector = metricsCollector;
        this.userController2 = userController2;
    }

    @Override
    public void login() {
        long startTimestamp = System.currentTimeMillis();
        userController2.login();
        long endTimestamp = System.currentTimeMillis();
        long responseTime = endTimestamp - startTimestamp;
        metricsCollector.recordRequest(new RequestInfo("login", responseTime, startTimestamp, endTimestamp));
    }

    @Override
    public void register() {
        long startTimestamp = System.currentTimeMillis();
        userController2.register();
        long endTimestamp = System.currentTimeMillis();
        long responseTime = endTimestamp - startTimestamp;
        metricsCollector.recordRequest(new RequestInfo("register", responseTime, startTimestamp, endTimestamp));
    }

    public static void main(String[] args) {
        IUserController userController2 = new UserControllerProxy1(new MetricsCollector(), new UserController2());
    }
}


/**
 * 如果原始类没有定义接口, 我们一般采用继承的方式来扩展它
 */

class UserControllerProxy2 extends UserController2 {

    private MetricsCollector metricsCollector;

    public UserControllerProxy2() {
        metricsCollector = new MetricsCollector();
    }

    @Override
    public void login() {
        long startTimestamp = System.currentTimeMillis();
        super.login();
        long endTimestamp = System.currentTimeMillis();
        long responseTime = endTimestamp - startTimestamp;
        metricsCollector.recordRequest(new RequestInfo("login", responseTime, startTimestamp, endTimestamp));
    }

    @Override
    public void register() {
        long startTimestamp = System.currentTimeMillis();
        super.register();
        long endTimestamp = System.currentTimeMillis();
        long responseTime = endTimestamp - startTimestamp;
        metricsCollector.recordRequest(new RequestInfo("register", responseTime, startTimestamp, endTimestamp));
    }

    public static void main(String[] args) {
        UserController2 userController2 = new UserControllerProxy2();
    }
}
package creation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 单例模式
 * 一个类只允许创建一个对象实例
 * 构造方法私有
 */


/**
 *   example1  处理资源访问冲突
 */
class Logger1 {
    private FileWriter writer;

    public Logger1() throws IOException {
        writer = new FileWriter(new File("log.txt"), true);
    }

    public void log(String message) throws IOException {
        writer.write(message);
    }

}

class UserController {
    private Logger1 logger = new Logger1();

    UserController() throws IOException {}

    public void login(String username, String password) throws IOException {
        logger.log(username + " login!");
    }

}

class OrderController {
    private Logger1 logger = new Logger1();

    OrderController() throws IOException {}

    public void create(String order) throws IOException {
        logger.log("Create an order: " + order);
    }

}

/**
 *  多线程环境下, UserController、OrderController中的两个Logger对象同时写日志可能存在日志互相覆盖的情况
 *  因此我们可以将Logger设计成一个单例类, 所有线程共享一个Logger对象
 *  实现如下：
 */
class Logger2 {

    private FileWriter writer;
    private static final Logger2 instance = new Logger2();

    private Logger2() {
        try {
            writer = new FileWriter(new File("log.txt"), true);
        } catch (IOException e) {}
    }

    public static Logger2 getInstance() {
        return instance;
    }

    public void log(String message) throws IOException {
        writer.write(message);
    }
}

class UserController2 {
    public void login(String username, String password) throws IOException {
        Logger2.getInstance().log(username + " login!");
    }
}


/**
 * example2 表示全局唯一类
 * 唯一递增ID生成器(如果有两个对象就可能生成重复ID)
 */

/**
 * 饿汉式(线程安全)
 */
class IdGenerator1 {
    private AtomicLong id = new AtomicLong(0);
    private static final IdGenerator1 instance = new IdGenerator1();
    private IdGenerator1(){}

    public static IdGenerator1 getInstance() {
        return instance;
    }

    public long getId() {
        return id.incrementAndGet();
    }
}


/**
 * 懒汉式(延迟加载、并发度低)
 */
class IdGenerator2 {
    private AtomicLong id = new AtomicLong(0);
    private static IdGenerator2 instance;
    private IdGenerator2(){}

    public static synchronized IdGenerator2 getInstance() {
        if(instance == null)
            instance = new IdGenerator2();
        return instance;
    }

    public long getId() {
        return id.incrementAndGet();
    }
}


/**
 * 双重检索式
 */
class IdGenerator3 {
    private AtomicLong id = new AtomicLong(0);
    private static volatile IdGenerator3 instance;    // volatile禁止指令重排序(避免返回未完成初始化的实例)
    private IdGenerator3(){}

    public static IdGenerator3 getInstance() {
        if(instance == null) {
            synchronized (IdGenerator3.class) {
                if(instance == null) {
                    instance = new IdGenerator3();
                }
            }
        }
        return instance;
    }

    public long getId() {
        return id.incrementAndGet();
    }

}


/**
 * 枚举
 */
enum IdGenerator4 {
    INSTANCE;
    private AtomicLong id = new AtomicLong(0);

    public long getId() {
        return id.incrementAndGet();
    }
}
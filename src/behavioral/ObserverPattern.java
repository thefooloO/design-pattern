package behavioral;

/**
 * 观察者模式
 * 在对象之间定义一个一对多的依赖, 当一个对象状态改变的时候, 所有依赖的对象都会自动收到通知
 * 被依赖的对象叫做被观察者(Observable), 依赖的对象叫做观察者(Observer)
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;

/**
 * 经典实现方式
 */
class Message{}

interface Subject {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(Message message);
}

interface Observer {
    void update(Message message);
}

class ConcreteSubject implements Subject {

    List<Observer> observers = new ArrayList<>();

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Message message) {
        for(Observer observer : observers) {
            observer.update(message);
        }
    }
}


class ConcreteObserver1 implements Observer {

    @Override
    public void update(Message message) {
        System.out.println("ConcreteObserver1 is notified");
    }
}

class ConcreteObserver2 implements Observer {

    @Override
    public void update(Message message) {
        System.out.println("ConcreteObserver2 is notified");
    }
}

class Demo {
    public static void main(String[] args) {
        ConcreteSubject subject = new ConcreteSubject();
        subject.registerObserver(new ConcreteObserver1());
        subject.registerObserver(new ConcreteObserver2());
        subject.notifyObservers(new Message());
    }
}


/**
 * EventBus
 */

// 该注解用于标明观察者中的哪个函数可以接收消息
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Subscribe {}

// 该类用来表示@Subscribe注解的方法, 主要用在ObserverRegistry观察者注册表中
class ObserverAction {

    private Object target;
    private Method method;

    public ObserverAction(Object target, Method method) {
        this.target = target;
        this.method = method;
        this.method.setAccessible(true);
    }

    public void execute(Object event) {
        try {
            method.invoke(target,event);
        } catch (Exception e){}
    }
}

// Observer注册表
class ObserverRegistry {

    private ConcurrentMap<Class<?>, CopyOnWriteArraySet<ObserverAction>> registry = new ConcurrentHashMap<>();


    public void register(Object observer) {
        Map<Class<?>, Collection<ObserverAction>> observerActions = findAllObserverActions(observer);
        for(Map.Entry<Class<?>,Collection<ObserverAction>> entry : observerActions.entrySet()) {

            Class<?> eventType = entry.getKey();
            CopyOnWriteArraySet<ObserverAction> registeredEventActions = registry.get(eventType);

            if (registeredEventActions == null) {
                registry.putIfAbsent(eventType, new CopyOnWriteArraySet<>());
                registeredEventActions = registry.get(eventType);
            }

            registeredEventActions.addAll(entry.getValue());
        }
    }


    private Map<Class<?>, Collection<ObserverAction>> findAllObserverActions(Object observer) {
        // <EventType, ObserverActions>
        Map<Class<?>, Collection<ObserverAction>> observerActions = new HashMap<>();
        Class<?> clazz = observer.getClass();

        for(Method method : getAnnotatedMethods(clazz)) {

            // 事件类型
            Class<?> eventType = method.getParameterTypes()[0];

            if(!observerActions.containsKey(eventType)) {
                observerActions.put(eventType, new ArrayList<>());
            }
            observerActions.get(eventType).add(new ObserverAction(observer, method));
        }

        return observerActions;
    }

    // 找到@Subscribe注解的方法
    private List<Method> getAnnotatedMethods(Class<?> clazz) {
        List<Method> annotatedMethods = new ArrayList<>();
        for(Method method : clazz.getDeclaredMethods()) {
            if(method.isAnnotationPresent(Subscribe.class)) {
                annotatedMethods.add(method);
            }
        }
        return annotatedMethods;
    }


    public List<ObserverAction> getMatchedObserverActions(Object event) {
        List<ObserverAction> matchedObservers = new ArrayList<>();
        Class<?> postedEventType = event.getClass();
        for (Map.Entry<Class<?>, CopyOnWriteArraySet<ObserverAction>> entry : registry.entrySet()) {
            Class<?> eventType = entry.getKey();
            Collection<ObserverAction> eventActions = entry.getValue();
            // postedEventType是否与eventType相同或是其父类
            if(postedEventType.isAssignableFrom(eventType)) {
                matchedObservers.addAll(eventActions);
            }
        }
        return matchedObservers;
    }

}

class EventBus {
    private Executor executor;
    private ObserverRegistry registry = new ObserverRegistry();

    public EventBus() {
        this(Executors.newSingleThreadExecutor());
    }

    protected EventBus(Executor executor) {
        this.executor = executor;
    }

    public void register(Object object) {
        registry.register(object);
    }

    public void post(Object event) {
        List<ObserverAction> observerActions = registry.getMatchedObserverActions(event);
        for(ObserverAction observerAction : observerActions) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    observerAction.execute(event);
                }
            });
        }
    }
}

class AsyncEventBus extends EventBus {
    public AsyncEventBus(Executor executor) {
        super(executor);
    }
}
package behavioral;

/**
 * 责任链模式
 * 将请求的发送和接收解耦, 让多个接收对象都有机会处理这个请求
 * 将这些接收对象串成一条链, 并沿着这条链传递这个请求
 */

abstract class Handler1 {
    protected Handler1 successor = null; // 链中的下一个接收对象

    public void setSuccessor(Handler1 successor) {
        this.successor = successor;
    }

    public abstract void handle();
}

class HandlerA1 extends Handler1 {

    @Override
    public void handle() {
        // ...
        System.out.println("HandlerA1");
        if(successor != null) {
            successor.handle();
        }
    }
}

class HandlerB1 extends Handler1 {

    @Override
    public void handle() {
        // ...
        System.out.println("HandlerB1");
        if(successor != null) {
            successor.handle();
        }
    }
}


class HandlerChain1 {
    Handler1 head = null;
    Handler1 tail = null;

    void addHandler(Handler1 handler) {
        handler.setSuccessor(null);

        if(head == null) {
            head = handler;
            tail = handler;
            return;
        }

        tail.setSuccessor(handler);
        tail = handler;
    }

    void handle() {
        if(head != null) {
            head.handle();
        }
    }

    public static void main(String[] args) {
        HandlerChain1 chain = new HandlerChain1();
        chain.addHandler(new HandlerA1());
        chain.addHandler(new HandlerB1());
        chain.handle();
    }
}


/**
 * 上述代码中, 处理器类的handle()函数不仅包含自己的业务逻辑, 还包含对下一个处理器的调用
 * 针对这个问题, 我们利用模板模式对代码进行重构
 */

abstract class Handler2 {
    protected Handler2 successor = null;
    public void setSuccessor(Handler2 successor) {
        this.successor = successor;
    }

    public final void handle() {
        doHandle();
        if(successor != null) {
            successor.handle();
        }
    }

    protected abstract void doHandle();
}


class HandlerA2 extends Handler2 {

    @Override
    protected void doHandle() {
        // ...
        System.out.println("HandlerA2");
    }
}

class HandlerB2 extends Handler2 {

    @Override
    protected void doHandle() {
        System.out.println("HandlerB2");
    }
}


class HandlerChain2 {
    Handler2 head = null;
    Handler2 tail = null;

    void addHandler(Handler2 handler) {
        handler.setSuccessor(null);

        if(head == null) {
            head = handler;
            tail = handler;
            return;
        }

        tail.setSuccessor(handler);
        tail = handler;
    }

    void handle() {
        if(head != null) {
            head.handle();
        }
    }

    public static void main(String[] args) {
        HandlerChain2 chain = new HandlerChain2();
        chain.addHandler(new HandlerA2());
        chain.addHandler(new HandlerB2());
        chain.handle();
    }
}
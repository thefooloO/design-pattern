package behavioral;

/**
 * 模板模式
 * 在一个方法中定义一个算法骨架, 并将某些步骤推迟到子类中实现
 * 让子类在不改变算法整体结构的情况下, 重新定义算法中的某些步骤
 */

/**
 * 示例：
 */
abstract class AbstractClass {
    public final void templateMethod() {
        // ...
        method1();
        // ...
        method2();
        // ...
    }

    protected abstract void method1();
    protected abstract void method2();
}

class ConcreteClass1 extends AbstractClass {

    @Override
    protected void method1() {
        // ...
    }

    @Override
    protected void method2() {
        // ...
    }
}

class ConcreteClass2 extends AbstractClass {

    @Override
    protected void method1() {
        // ...
    }

    @Override
    protected void method2() {
        // ...
    }
}

class test {
    public static void main(String[] args) {
        AbstractClass demo = new ConcreteClass1();
        demo.templateMethod();
    }
}
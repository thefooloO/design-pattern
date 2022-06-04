package structural;

import java.util.ArrayList;
import java.util.List;

/**
 * 适配器模式
 * 该模式用来将不兼容的接口转换为可兼容的接口
 * 该模式有两种实现方式：类适配器(通过继承实现)、对象适配器(通过组合实现)
 * Adaptor将Adaptee转化成一组符合ITarget接口定义的类
 */

interface ITarget {
    void f1();
    void f2();
    void fc();
}

class Adaptee {
    void fa(){}
    void fb(){}
    void fc(){}
}

/**
 * 类适配器
 */
class Adaptor1 extends Adaptee implements ITarget {
    public void f1(){
        super.fa();
    }

    public void f2(){
        // 重新实现
    }

    public void fc() {
        super.fc();
    }
}

/**
 * 对象适配器
 */
class Adaptor2 implements ITarget {
    private Adaptee adaptee;

    public Adaptor2(Adaptee adaptee) {
        this.adaptee = adaptee;
    }

    public void f1() {
        adaptee.fa(); // 委托给adaptee
    }

    public void f2(){
        // 重新实现
    }

    public void fc() {
        adaptee.fc();
    }
}


/**
 * 使用场景：
 */

/**
 * example1：封装有缺陷的接口设计
 */
class CD {
    static void staticFunction1(){}
    void uglyNamingFunction2(){}
    void lowPerformanceFunction3(){}
}

interface ITargetCD {
    void function1();
    void function2();
    void function3();
}

class CDAdaptor extends CD implements ITargetCD {

    @Override
    public void function1() {
        super.staticFunction1();
    }

    @Override
    public void function2() {
        super.uglyNamingFunction2();
    }

    @Override
    public void function3() {
        // 重新实现
    }
}


/**
 * example2：统一多个类的接口设计
 * 假设我们引入了多款第三方敏感词过滤系统依次对用户输入的内容进行过滤,
 * 但是每个系统提供的过滤接口都不同, 我们就没法复用一套逻辑来调用各个系统,
 * 这时我们可以使用适配器模式, 将所有系统的接口适配为统一的接口定义
 */
class ASensitiveWordsFilter {
    public void filterSexyWords(String text){}
    public void filterPoliticalWords(String text){}
}

class BSensitiveWordsFilter {
    public void filter(String text){}
}


// 使用适配器模式前
class RiskManagement1 {
    ASensitiveWordsFilter aFilter = new ASensitiveWordsFilter();
    BSensitiveWordsFilter bFilter = new BSensitiveWordsFilter();

    void filterSensitiveWords(String text) {
        aFilter.filterSexyWords(text);
        aFilter.filterPoliticalWords(text);
        bFilter.filter(text);
    }
}


// 使用适配器模式进行改造
interface ISensitiveWordsFilter {  // 统一接口定义
    void filter(String text);
}

class ASensitiveWordsFilterAdaptor implements ISensitiveWordsFilter {

    ASensitiveWordsFilter aFilter;

    @Override
    public void filter(String text) {
        aFilter.filterSexyWords(text);
        aFilter.filterPoliticalWords(text);
    }
}

class RiskManagement2 {
    List<ISensitiveWordsFilter> filters = new ArrayList<>();

    void addSensitiveWordsFilter(ISensitiveWordsFilter filter) {
        filters.add(filter);
    }

    void filterSensitiveWords(String text) {
        for(ISensitiveWordsFilter filter : filters) {
            filter.filter(text);
        }
    }
}
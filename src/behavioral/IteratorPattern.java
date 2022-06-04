package behavioral;

import java.util.*;

/**
 * 迭代器模式(游标模式)
 * 将集合对象的遍历操作从集合类中拆分出来, 放到迭代器类中, 让两者的职责更单一
 */

interface MyIterator<E> {
    boolean hasNext();
    void next();
    E currentItem();
}

class MyArrayIterator1<E> implements MyIterator<E> {

    private int cursor;
    private ArrayList<E> arrayList;

    public MyArrayIterator1(ArrayList<E> arrayList) {
        this.cursor = 0;
        this.arrayList = arrayList;
    }

    @Override
    public boolean hasNext() {
        return cursor != arrayList.size();
    }

    @Override
    public void next() {
        cursor++;
    }

    @Override
    public E currentItem() {
        if(cursor >= arrayList.size()) {
            throw new NoSuchElementException();
        }
        return arrayList.get(cursor);
    }


    public static void main(String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("A");
        strings.add("B");
        strings.add("C");

        MyIterator<String> iterator = new MyArrayIterator1<>(strings);
        while(iterator.hasNext()) {
            System.out.println(iterator.currentItem());
            iterator.next();
        }
    }
}


/**
 * 我们可以在容器中定义一个iterator()方法, 来创建对应的迭代器
 */
class MyArrayList1<E> {

    private ArrayList<E> arrayList;

    public MyArrayList1(ArrayList<E> arrayList) {
        this.arrayList = arrayList;
    }

    public void add(E e) {
        arrayList.add(e);
    }

    public MyIterator<E> iterator() {
        return new MyArrayIterator1<>(arrayList);
    }


    public static void main(String[] args) {

        MyArrayList1<String> strings = new MyArrayList1<>(new ArrayList<>());
        strings.add("C");
        strings.add("B");
        strings.add("A");

        MyIterator iterator = strings.iterator();
        while(iterator.hasNext()) {
            System.out.println(iterator.currentItem());
            iterator.next();
        }
    }
}


/**
 * 在通过迭代器遍历集合元素的同时, 增加或者删除集合中的元素, 可能导致某个元素被重复遍历或遍历不到
 * 这种行为称为未决行为, 一般通过增删元素之后让遍历报错来避免未决行为
 */
class MyArrayList2<E> {

    private int modCount;             // 记录集合被修改的次数
    private ArrayList<E> arrayList;

    public MyArrayList2(ArrayList<E> arrayList) {
        this.arrayList = arrayList;
        this.modCount = 0;
    }

    public void add(E e) {
        arrayList.add(e);
        modCount++;
    }

    public void remove(E e) {
        arrayList.remove(e);
        modCount++;
    }

    public int getModCount() {
        return modCount;
    }

    public int size() {
        return arrayList.size();
    }

    public E get(Integer cursor) {
        return arrayList.get(cursor);
    }
}


class MyArrayIterator2<E> implements MyIterator<E> {

    private int cursor;
    private MyArrayList2<E> arrayList2;
    private int expectedModCount;

    public MyArrayIterator2(MyArrayList2 arrayList2) {
        this.arrayList2 = arrayList2;
        this.expectedModCount = arrayList2.getModCount();   // 创建迭代器时记录modCount值
        this.cursor = 0;
    }

    @Override
    public boolean hasNext() {
        checkForConcurrentModification();
        return cursor < arrayList2.size();
    }

    @Override
    public void next() {
        checkForConcurrentModification();
        cursor++;
    }

    @Override
    public E currentItem() {
        checkForConcurrentModification();
        return arrayList2.get(cursor);
    }

    private void checkForConcurrentModification(){
        if(arrayList2.getModCount() != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    public static void main(String[] args) {
        MyArrayList2<String> strings = new MyArrayList2<>(new ArrayList<>());
        strings.add("A");
        strings.add("B");
        strings.add("C");

        MyArrayIterator2 iterator2 = new MyArrayIterator2(strings);
        iterator2.next();
        strings.remove("A");
        iterator2.next();           // ConcurrentModificationException异常
    }

}
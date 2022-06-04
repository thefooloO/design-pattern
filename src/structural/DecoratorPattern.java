package structural;


/**
 * 装饰器模式
 * 典型应用：Java I/O
 * 动态地将责任附加到对象上, 比继承更有弹性
 */

import java.io.*;

/**
 *  InputStream:  输入字节流
 *  OutputStream: 输出字节流
 *  Reader:       输入字符流
 *  Writer:       输出字符流
 */
class IODemo {
    public static void main(String[] args) throws IOException {
        InputStream in = new FileInputStream("test.txt");
        InputStream bin = new BufferedInputStream(in);
        byte[] data = new byte[128];
        while(bin.read(data) != -1) {
            // ...
        }
    }
}

/**
 * 例：LineNumberInputStream(BufferedInputStream(FileInputStream))
 *  FileInputStream: 被装饰的组件, 提供了最基本的字节读取功能
 *  BufferedInputStream: 一个具体的装饰器, 它加入了两种行为：利用缓冲输入来改进性能、用readline()方法来增强接口
 *  LineNumberInputStream: 一个具体的装饰器, 它加上了计算行数的能力
 */

/**
 * 自定义Java I/O装饰器
 */
class LowerCaseInputStream extends FilterInputStream {
    public LowerCaseInputStream(InputStream in) {
        super(in);
    }

    public int read() throws IOException {
        int c = super.read();
        return (c == -1 ? c : Character.toLowerCase((char)c));
    }

    public int read(byte[] b, int offset, int len) throws IOException {
        int result = super.read(b, offset, len);
        for(int i = offset; i < offset + result; i++) {
            b[i] = (byte) Character.toLowerCase((char)b[i]);
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        int c;
        InputStream in = new LowerCaseInputStream(new BufferedInputStream(new FileInputStream("test.txt")));
        while((c = in.read()) >= 0) {
            System.out.println((char)c);
        }
        in.close();
    }
}
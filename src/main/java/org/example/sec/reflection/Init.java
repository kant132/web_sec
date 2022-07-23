package org.example.sec.reflection;

import org.junit.Test;

import java.io.IOException;

public class Init {
    //只会触发静态初始化块
    @Test
    public void test1() throws IOException, ClassNotFoundException {
        Class.forName("org.example.sec.reflection.TrainPrint");
    }

    @Test
    public void test2() throws IOException, ClassNotFoundException {
        TrainPrint test = new TrainPrint();
    }
    //父类静态初始块->子类静态初始块
    @Test
    public void test3() throws IOException, ClassNotFoundException {
        Class.forName("org.example.sec.reflection.SubTrainPrint");
    }
    //父类静态初始块->子类静态初始块->父类初始块->父类构造函数->子类初始块->子类构造函数
    @Test
    public void test4() throws IOException, ClassNotFoundException {
        TrainPrint test = new SubTrainPrint();
    }
}

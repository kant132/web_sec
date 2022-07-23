package org.example.sec.reflection;

public class TrainPrint {
    //初始块
    {
        System.out.printf("Empty block initial %s\n", this.getClass());
    }

    //静态初始块
    static {
        System.out.printf("Static initial %s\n", TrainPrint.class);
    }

    //构造函数
    public TrainPrint() {
        System.out.printf("Initial %s\n", this.getClass());
    }
}
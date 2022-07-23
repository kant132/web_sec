package org.example.sec.reflection;

public class SubTrainPrint extends TrainPrint{

    //初始块
    {
        System.out.printf("Sub Empty block initial %s\n", this.getClass());
    }

    //静态初始块
    static {
        System.out.printf("Sub Static initial %s\n", TrainPrint.class);
    }

    //构造函数
    public SubTrainPrint() {
        System.out.printf("Sub Initial %s\n", this.getClass());
    }
}

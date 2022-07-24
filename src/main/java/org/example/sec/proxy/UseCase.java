package org.example.sec.proxy;

import java.lang.reflect.Proxy;

public class UseCase {
    public static void main(String[] args) {
        PersonEat personEat = new PersonEat();
        //代理类仍然有personEat的接口
        Eat eat = (Eat) Proxy.newProxyInstance(personEat.getClass().getClassLoader(),
                personEat.getClass().getInterfaces(), (proxy, method, args1) -> {
                    System.out.println("wash");
                    Object obj = method.invoke(personEat, args1);
                    System.out.println("clean");
                    return obj;
                });
        eat.eat();
    }
}

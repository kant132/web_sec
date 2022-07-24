package org.example.sec.proxy;

public class PersonEat implements Eat{
    @Override
    public void eat() {
        System.out.println("person eat");
    }
}

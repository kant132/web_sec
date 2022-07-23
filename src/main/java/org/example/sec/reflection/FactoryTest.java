package org.example.sec.reflection;

public class FactoryTest {
    public static void main(String[] a){  
        Fruit f=Factory.getInstance("Orange");
        f.eat();

        f=FactoryByReflection.getInstance("org.example.sec.developer.Apple");
        f.eat();
    }  

}



package org.example.sec.reflection;

public class Factory{
    public static Fruit getInstance(String fruitName){
        Fruit f=null;
        if("Apple".equals(fruitName)){  
            f=new Apple();  
        }  
        if("Orange".equals(fruitName)){  
            f=new Orange();  
        }  
        return f;  
    }  
}  
package org.example.sec.reflection;

class FactoryByReflection {
    public static Fruit getInstance(String ClassName){
        Fruit f=null;
        try{
            f=(Fruit)Class.forName(ClassName).newInstance();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }
} 
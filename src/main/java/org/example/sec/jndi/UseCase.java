package org.example.sec.jndi;

import org.junit.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

public class UseCase {
    private InitialContext getJNDIContext() throws NamingException {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
        env.put(Context.PROVIDER_URL, "rmi://127.0.0.1:1199");
        return new InitialContext(env);
    }

    Person p = new Person("wq", "kant");

    public void rmiServer1()  {
        // 创建rmi注册中心
        ServiceImpl service = null;
        try {
            service = new ServiceImpl();
            Registry registry = LocateRegistry.createRegistry(1199);
            registry.bind("service", service); // 绑定远程对象HelloImp到RMI服务注册器
            System.out.println("server start");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
    }


    public void jndiServer1()  {
        // 创建rmi注册中心
        try {
            ServiceImpl service = new ServiceImpl();
            LocateRegistry.createRegistry(1199);
            Naming.bind("rmi://localhost:1199/service", service);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
    }


    public void jndiServer2() {
        // 创建rmi注册中心
        ServiceImpl service = null;
        try {
            service = new ServiceImpl();
            InitialContext ctx = getJNDIContext();
            LocateRegistry.createRegistry(1199);
            ctx.bind("service", service);
            ctx.close();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    //客户端只要持有hello接口就好了
    public void rmiClient1() {
        new Thread(()->rmiServer1()).start();
        try {
            Thread.sleep(1000);
            Registry registry = LocateRegistry.getRegistry("127.0.0.1",1199);
            Hello hello = (Hello) registry.lookup("service");
            System.out.println(hello.Hello(p));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    //客户端只要持有接口就好了
    public void jndiClient1() {
        new Thread(()->jndiServer1()).start();
        try {
            Thread.sleep(1000);
            Hello hello = (Hello)  Naming.lookup("rmi://localhost:1199/service");
            hello.Hello(p);
            System.out.println(hello.Hello(p));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    //client只需要持有接口或者父类就好了
    public void jndiClient2()  {
        new Thread(()->jndiServer2()).start();
        InitialContext ctx = null;
        try {
            Thread.sleep(1000);
            ctx = getJNDIContext();
            Hello hello = (Hello) ctx.lookup("service");
            hello.Hello(p);
            System.out.println(hello.Hello(p));
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}


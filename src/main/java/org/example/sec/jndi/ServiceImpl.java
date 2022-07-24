package org.example.sec.jndi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

//远程接口实现类必须继承UnicastRemoteObject类，，用于生成 Stub（存根）和 Skeleton（骨架）
public class ServiceImpl extends UnicastRemoteObject implements Hello {

    protected ServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String Hello(Person person) throws RemoteException {
        return "server say " + person.getNick()  + " hello";
    }
}

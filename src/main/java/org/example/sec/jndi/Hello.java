package org.example.sec.jndi;

import java.rmi.Remote;
import java.rmi.RemoteException;

//该接口仅为RMI标识接口，本身不代表使用任何方法，说明可以进行RMI java虚拟机调用。
public interface Hello extends Remote {
    String Hello(Person person) throws RemoteException;
}

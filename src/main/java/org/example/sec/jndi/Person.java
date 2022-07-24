package org.example.sec.jndi;

import java.io.Serializable;

//要被序列化传递
public class Person implements Serializable {
    private String name;
    private String nick;

    public Person(String name, String nick) {
        this.name = name;
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }
}

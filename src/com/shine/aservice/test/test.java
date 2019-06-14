package com.shine.aservice.test;

import kotlin.jvm.JvmStatic;
import kotlin.jvm.Throws;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class test {
    public static void main(String[] args) throws Exception {
        ClassLoader myLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                try {
                    String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";
                    InputStream is = getClass().getResourceAsStream(fileName);
                    if (is == null) {
                        return super.loadClass(name);
                    }
                    byte[] b = new byte[is.available()];
                    is.read(b);
                    return defineClass(name, b, 0, b.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException(name);
                }
            }
        };
        Object obj = myLoader.loadClass("com.shine.aservice.test.test").newInstance();

        System.out.println(obj.getClass());
        System.out.println(obj instanceof com.shine.aservice.test.test);
    }
}

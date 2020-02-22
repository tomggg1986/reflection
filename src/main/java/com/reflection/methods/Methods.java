package com.reflection.methods;

public class Methods {

    @Mark(name = "hello")
    public void printHello() {
        System.out.println("Hello to world");
    }

    @Mark(name = "ShowClass")
    public void showClass(String name) {
        System.out.println(this.getClass().getClassLoader());
    }

}

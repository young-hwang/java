package io.younghwang.java8;

public interface VariousInterface {
    static void printName() {
        System.out.println("young");
    }

    default void printAge() {
        System.out.println("38");
    }
}

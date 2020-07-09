package hw7.test;

import hw7.annotation.AfterSuite;
import hw7.annotation.BeforeSuite;
import hw7.annotation.Test;

public class Good {

    @BeforeSuite
    public void before() {
        System.out.println(getClass().getName() + " before");
    }

    @Test(priority = 2)
    public void method2() {
        System.out.println(getClass().getName() + " method2");
    }

    @Test(priority = 3)
    public void method3() {
        System.out.println(getClass().getName() + " method3");
    }

    @Test(priority = 1)
    public void method1() {
        System.out.println(getClass().getName() + " method1");
    }

    @AfterSuite
    public void after() {
        System.out.println(getClass().getName() + " after");
    }
}
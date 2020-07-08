package hw7;

import hw7.annotation.AfterSuite;
import hw7.annotation.BeforeSuite;
import hw7.annotation.Test;
import hw7.test.Bad;
import hw7.test.Good;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.TreeSet;

public class RunTest {
    public static void start(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
        start(Class.forName(className));
    }

    public static void start(Class c) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Method beforeSuit = findMethodByAnnotaion(BeforeSuite.class, c);
        Method afterSuit = findMethodByAnnotaion(AfterSuite.class, c);
        TreeSet<Method> testMethods = new TreeSet<Method>((Method m1, Method m2) -> {
            return m1.getAnnotation(Test.class).priority() - m2.getAnnotation(Test.class).priority();
        });
        for (Method m: c.getMethods()) {
            if (m.getAnnotation(Test.class) != null)
                testMethods.add(m);
        }
        Object o = c.getConstructors()[0].newInstance();
        if (beforeSuit != null)
            beforeSuit.invoke(o);
        for (Method m: testMethods) {
            m.invoke(o);
        }
        if (afterSuit != null)
            afterSuit.invoke(o);

        System.out.println("All tests for " + c.getName() + " passed");
    }

    private static Method findMethodByAnnotaion(Class annotation, Class c) {
        Method method = null;
        for (Method m: c.getMethods()) {
            if (m.getAnnotation(annotation) != null) {
                if (method == null)
                    method = m;
                else
                    throw new RuntimeException("Class " + c.getName() + " has more than 1 annotation " + annotation.getSimpleName());
            }
        }
        return method;
    }

    public static void main(String[] args) {
        try {
            start(Good.class);
            start(Bad.class);
        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

}
package hw1.task2;

import java.util.ArrayList;

public class Main {
    static <E> ArrayList<E> arrayToArrayList(E[] array) {
        ArrayList<E> arrayList = new ArrayList();
        for (E element: array) {
            arrayList.add(element);
        }
        return arrayList;
    }

    public static void main(String[] args) {
        String[] strings = {"asdas", "dffdf", "33r3r"};
        ArrayList<String> stringArrayList = arrayToArrayList(strings);
        System.out.println(stringArrayList);
    }

}

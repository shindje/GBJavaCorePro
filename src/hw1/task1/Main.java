package hw1.task1;

import java.util.Arrays;

public class Main {
    static <T> T[] swap(T[] arr, int idx1, int idx2) {
        if (idx1 < 0 || idx2 < 0 || idx1 >= arr.length || idx2 >= arr.length)
            throw new IllegalArgumentException("Индекс за пределами массива");

        T o = arr[idx1];
        arr[idx1] = arr[idx2];
        arr[idx2] = o;
        return arr;
    }

    public static void main(String[] args) {
        String[] strings = {"i0", "i1", "i2"};
        strings = swap(strings, 1, 2);
        System.out.println(Arrays.deepToString(strings));
    }
}

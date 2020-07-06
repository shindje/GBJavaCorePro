public class Task1 {
    public static int[] method1(int[] a) {
        int idx = -1;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == 4)
                idx = i;
        }
        if (idx == -1)
            throw new RuntimeException("no 4");
        int[] ret = new int[a.length - 1 - idx];
        System.arraycopy(a, idx + 1, ret, 0, ret.length);
        return ret;
    }
}

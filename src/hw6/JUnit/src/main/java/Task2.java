public class Task2 {
    public static boolean method2(int[] a) {
        boolean has1 = false;
        boolean has4 = false;
        boolean hasOthers = false;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == 1)
                has1 = true;
            else if (a[i] == 4)
                has4 = true;
            else
                hasOthers = true;
        }
        return has1 && has4 && !hasOthers;
    }
}

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class Task1Test {
    private int[] param;
    private int[] result;

    public Task1Test(int[] param, int[] result) {
        this.param = param;
        this.result = result;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {new int[] {4, 54, 2, 12}, new int[]{54, 2, 12}},
                {new int[] {4, 5, 2, 2, 4, 1, 3}, new int[]{1, 3}},
                {new int[] {3, 5, 4}, new int[]{}},
                {new int[] {4}, new int[]{}}
        });
    }

    @Test
    public void testEq() {
        Assert.assertArrayEquals(result, Task1.method1(param));
    }

    @Test(expected = RuntimeException.class)
    public void testException() {
        Task1.method1(new int[] {3, 5, 2});
    }

}

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class Task2Test {
    private int[] param;
    private boolean result;

    public Task2Test(int[] param, boolean result) {
        this.param = param;
        this.result = result;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {new int[] {4, 1, 1, 4}, true},
                {new int[] {1, 4}, true},
                {new int[] {1, 1, 1}, false},
                {new int[] {4, 4}, false},
                {new int[] {4, 5, 1, 4}, false},
                {new int[] {}, false}
        });
    }

    @Test
    public void testEq() {
        Assert.assertEquals(Task2.method2(param), result);
    }
}
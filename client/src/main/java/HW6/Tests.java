package HW6;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class Tests {
    private ArrayOperations ao;

    @Before
    public void beforeMethod() {
        ao = new ArrayOperations();
    }

    @Test
    public void testAfterFour1() {
        int[] arr = new int[]{0, 5, 6, 4, 7, 8, 4, 2, 1};
        int[] expectedResult = new int[] {2, 1};
        Assert.assertArrayEquals(expectedResult, ao.afterFour(arr));
    }

    @Test
    public void testAfterFour2() {
        int[] arr = new int[]{3, 5, 6, 4, 7, 8, 8, 2, 3};
        int[] expectedResult = new int[] {7, 8, 8, 2, 3};
        Assert.assertArrayEquals(expectedResult, ao.afterFour(arr));
    }

    @Test
    public void testAfterFour3() {
        int[] arr = new int[]{6, 4, 6, 1, 7, 8, 1, 2, 1};
        int[] expectedResult = new int[] {6, 1, 7, 8, 1, 2, 1};
        Assert.assertArrayEquals(expectedResult, ao.afterFour(arr));
    }

    @Test(expected = RuntimeException.class)
    public void testAfterFour4() {
        int[] arr = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        ao.afterFour(arr);
    }

    @Test
    public void testContainsTwoFour1() {
        int[] arr = new int[]{1, 1, 1, 4, 4, 4, 1, 1, 1};
        Assert.assertTrue(ao.containsTwoFour(arr));
    }

    @Test
    public void testContainsTwoFour2() {
        int[] arr = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1};
        Assert.assertFalse(ao.containsTwoFour(arr));
    }

    @Test
    public void testContainsTwoFour3() {
        int[] arr = new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4};
        Assert.assertFalse(ao.containsTwoFour(arr));
    }

    @Test
    public void testContainsTwoFour4() {
        int[] arr = new int[]{1, 5, 1, 2, 9, 4, 0, 1, 8};
        Assert.assertFalse(ao.containsTwoFour(arr));
    }
}

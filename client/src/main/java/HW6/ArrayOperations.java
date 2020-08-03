package HW6;

import java.util.Arrays;

public class ArrayOperations {
    public int[] afterFour(int[] arr) {
        int[] arrNew = null;
        boolean containsFour = false;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 4) {
                containsFour = true;
            }
        }
        if (containsFour) {
            for (int i = arr.length - 1; i >= 0; i--) {
                if (arr[i] == 4) {
                    arrNew = Arrays.copyOfRange(arr, i + 1, arr.length);
                    break;
                }
            }
            return arrNew;
        } else {
            throw new RuntimeException();
        }
    }

    public boolean containsTwoFour(int[] arr) {
        boolean containsOnes = true;
        boolean containsFours = true;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != 1) {
                containsOnes = false;
            }
            if (arr[i] != 4) {
                containsFours = false;
            }
        }

        if (containsFours || containsOnes) {
            return false;
        }

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != 4 && arr[i] != 1) {
                return false;
            }
        }
        return true;
    }
}

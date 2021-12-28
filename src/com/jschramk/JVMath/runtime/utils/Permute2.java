package com.jschramk.JVMath.runtime.utils;

import java.util.Arrays;

public class Permute2 {



    public static void main(String[] args) {

        nPr(3, 2);

    }

    private static void nPr(int n, int r) {

        int[] set = new int[n];

        for (int i = 0; i < set.length; i++) {
            set[i] = i;
        }

        nPrRecur(set, 0, new int[r], 0);

    }


    private static void nPrRecur(int[] set, int pos, int[] result, int resultPos) {

        if (resultPos == result.length) {
            System.out.println(Arrays.toString(result));
            return;
        }

        if (pos >= set.length) {
            return;
        }

        result[resultPos] = set[pos];

        nPrRecur(set, pos + 1, result, resultPos + 1);

        nPrRecur(set, pos + 1, result, resultPos);

    }

}

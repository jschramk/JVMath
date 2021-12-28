package com.jschramk.JVMath.runtime.utils;

import java.util.Arrays;

public class Permute {

    public static void main(String[] args) {

        Permute p = new Permute(3);

        while (p.hasNext()) {

            //System.out.println(Arrays.toString(p.getNext()));

            int[] perm = p.getNext();

            System.out.println(Arrays.toString(perm));

        }


    }

    int i = 0;
    int[] a, c, next;

    public Permute(int size) {
        a = new int[size];
        c = new int[size];

        for (int j = 0; j < a.length; j++) {
            a[j] = j;
        }

    }

    public boolean hasNext() {
        prepareNext();
        return next != null;
    }

    public int[] getNext() {
        return next;
    }

    public void prepareNext() {

        if (i == 0) {
            i++;
            next = a;
            return;
        }

        while (true) {

            if (i >= c.length) {
                next = null;
                return;
            }

            if (c[i] < i) {

                if (i % 2 == 0) {

                    int temp = a[0];

                    a[0] = a[i];

                    a[i] = temp;

                } else {

                    int temp = a[c[i]];

                    a[c[i]] = a[i];

                    a[i] = temp;

                }

                c[i]++;

                i = 1;

                next = a;

                return;

            } else {

                c[i] = 0;

                i++;

            }



        }

    }

}

package com.androidcorpo.lindapp.elipticurve;

public class KeyTransformationEncryption {

    public static int[] LeftShift(int[] binNber, int nbPos) {

        for (int i = 0; i < nbPos; i++) {
            int a = binNber[0];
            for (int j = 1; j < binNber.length; j++)
                binNber[j - 1] = binNber[j];
            binNber[binNber.length - 1] = a;
        }

        return binNber;
    }

    // select 48 out of original 64 bits. block is the initial 64 bits key

    public static int[][] keyTransf(int[] block) {

        int[][] keys = new int[16][48]; //table that will contain the 16 keys of 48 bits each
        int[] left = new int[28];
        int[] right = new int[28];
        int[] LeftAndright = new int[56];

        int[] CP1Table = {57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18,
                10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36,
                63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22,
                14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4};

        int[] CP2Table = {14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10,
                23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2,
                41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48,
                44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32};

        int[] tempCP1 = new int[56];
        int[] tempCP2 = new int[48];

        //first permutation CP1

        for (int i = 0; i < CP1Table.length; i++) {
            tempCP1[i] = block[CP1Table[i] - 1];
            //    System.out.print(tempCP1[i]);
        }

        LeftAndright = tempCP1;

        // production of the 16 keys

        for (int i = 0; i < 16; i++) {

            //split 56 bits into right and left

            for (int j = 0; j < 28; j++)
                left[j] = LeftAndright[j];
            for (int j = 28; j < 56; j++)
                right[j - 28] = LeftAndright[j];

            //left shift
            if (i == 0 || i == 1 || i == 8 || i == 15) {
                left = LeftShift(left, 1);
                right = LeftShift(right, 1);
            } else {
                left = LeftShift(left, 2);
                right = LeftShift(right, 2);
            }

            //reassemble Right and left

            String S = "";
            for (int j = 0; j < left.length; j++)
                S += left[j];

            for (int j = 0; j < right.length; j++) {
                S += right[j];
                //System.out.print(right[j]);
            }

            //Select 48 bits out of 56

            for (int k = 0; k < CP2Table.length; k++) {
                if (S.charAt(CP2Table[k] - 1) == '0')
                    tempCP2[k] = 0;
                else
                    tempCP2[k] = 1;
            }

            //save the ith key in keys table

            for (int k = 0; k < tempCP2.length; k++)
                keys[i][k] = tempCP2[k];

            //replace the original 56 block for the next round

            for (int k = 0; k < 56; k++) {
                if (S.charAt(k) == '0')
                    LeftAndright[k] = 0;
                else
                    LeftAndright[k] = 1;
            }

        }
        return keys;
    }

}

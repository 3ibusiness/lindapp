package com.androidcorpo.lindapp.elipticurve;

import java.math.BigInteger;

public class CoreAlgorithm {

    // key is the key entered by the user
    public static String crypt(String plainText, String key) {

        int[] EBox = {32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18,
                19, 20, 21, 20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1};

        int[] PBox = {16, 7, 10, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 20, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13,
                30, 6, 22, 11, 4, 25};

        int[] finalPerm = {40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30,
                37, 5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 42,
                10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25};

        int[][] SBox1 = {{14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
                {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
                {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
                {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}};

        int[][] SBox2 = {{15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
                {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
                {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
                {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}};

        int[][] SBox3 = {{10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
                {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
                {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
                {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}};

        int[][] SBox4 = {{7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
                {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
                {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
                {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}};

        int[][] SBox5 = {{2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
                {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
                {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
                {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}};

        int[][] SBox6 = {{12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
                {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
                {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
                {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}};

        int[][] SBox7 = {{4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
                {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
                {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
                {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}};

        int[][] SBox8 = {{13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
                {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
                {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
                {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}};

        int[] left = new int[32];
        int[] right = new int[32];
        int[][] keys = new int[16][48];
        String[] blocks;
        String finalEncryptText = "";
        int[] keyblock = new int[64];

        String temp = plainText = BinaryConversions.StringToBinary(plainText);
        temp = VerifPlainTex64Bits.controlPlainT(temp);
        blocks = new String[5000];

        String temp2 = key = BinaryConversions.StringToBinary(key);
        temp2 = VerifPlainTex64Bits.controlKey(temp2);

        // we obtain the key in binary

        for (int i = 0; i < temp2.length(); i++) {
            if (temp2.charAt(i) == '0')
                keyblock[i] = 0;
            else
                keyblock[i] = 1;
        }

        // we obtain all the 16 sub-keys

        keys = KeyTransformationEncryption.keyTransf(keyblock);

        // we obtain all the 64 bit blocks of the plaintext

        for (int i = 0; i < blocks.length; i++)
            blocks[i] = "";

        int N = 0, j = 0;
        while (j < temp.length()) {
            blocks[N] += temp.charAt(j);
            if ((j + 1) % 64 == 0)
                N++;
            j++;
        }

        // encrypt all blocks

        for (int k = 0; k < N; k++) {

            // initial permutation

            blocks[k] = InitialPlainTxtPermutEncryption.permut(blocks[k]);

            // dIvide each block into left and right

            for (int m = 0; m < 32; m++) {
                if (blocks[k].charAt(m) == '0')
                    left[m] = 0;
                else
                    left[m] = 1;
            }

            for (int m = 32; m < 64; m++) {
                if (blocks[k].charAt(m) == '0')
                    right[m - 32] = 0;
                else
                    right[m - 32] = 1;
            }

            // for each block we have 16 rounds

            for (int r = 0; r < 16; r++) {

                // exchanging left and right

                // expansion permutation of the right

                int[] ExpandRight = new int[48];

                for (int m = 0; m < 48; m++)
                    ExpandRight[m] = right[EBox[m] - 1];

                // XOR of the right with the partial key

                int[] XORTable = new int[48];
                for (int m = 0; m < 48; m++) {
                    if (ExpandRight[m] == 0 && keys[r][m] == 1 || ExpandRight[m] == 1 && keys[r][m] == 0)
                        XORTable[m] = 1;
                    else
                        XORTable[m] = 0;
                }

                // S-Box

                int[] SBoxResult = new int[8];
                int ro, co, one, two, three, four, five, six;

                one = XORTable[5];
                two = XORTable[0];
                three = XORTable[4];
                four = XORTable[3];
                five = XORTable[2];
                six = XORTable[1];
                ro = one + 2 * two;
                co = three + 2 * four + 4 * five + 8 * six;
                SBoxResult[0] = SBox1[ro][co];

                // System.out.println(BinaryConversions.DecToBinary( new
                // BigInteger(""+SBoxResult [0])));

                one = XORTable[11];
                two = XORTable[6];
                three = XORTable[10];
                four = XORTable[9];
                five = XORTable[8];
                six = XORTable[7];
                ro = one + 2 * two;
                co = three + 2 * four + 4 * five + 8 * six;
                SBoxResult[1] = SBox2[ro][co];

                one = XORTable[17];
                two = XORTable[12];
                three = XORTable[16];
                four = XORTable[15];
                five = XORTable[14];
                six = XORTable[13];
                ro = one + 2 * two;
                co = three + 2 * four + 4 * five + 8 * six;
                SBoxResult[2] = SBox3[ro][co];

                one = XORTable[23];
                two = XORTable[18];
                three = XORTable[22];
                four = XORTable[21];
                five = XORTable[20];
                six = XORTable[19];
                ro = one + 2 * two;
                co = three + 2 * four + 4 * five + 8 * six;
                SBoxResult[3] = SBox4[ro][co];

                one = XORTable[29];
                two = XORTable[24];
                three = XORTable[28];
                four = XORTable[27];
                five = XORTable[26];
                six = XORTable[25];
                ro = one + 2 * two;
                co = three + 2 * four + 4 * five + 8 * six;
                SBoxResult[4] = SBox5[ro][co];

                one = XORTable[35];
                two = XORTable[30];
                three = XORTable[34];
                four = XORTable[33];
                five = XORTable[32];
                six = XORTable[31];
                ro = one + 2 * two;
                co = three + 2 * four + 4 * five + 8 * six;
                SBoxResult[5] = SBox6[ro][co];

                one = XORTable[41];
                two = XORTable[36];
                three = XORTable[40];
                four = XORTable[39];
                five = XORTable[38];
                six = XORTable[37];
                ro = one + 2 * two;
                co = three + 2 * four + 4 * five + 8 * six;
                SBoxResult[6] = SBox7[ro][co];

                one = XORTable[47];
                two = XORTable[42];
                three = XORTable[46];
                four = XORTable[45];
                five = XORTable[44];
                six = XORTable[43];
                ro = one + 2 * two;
                co = three + 2 * four + 4 * five + 8 * six;
                SBoxResult[7] = SBox8[ro][co];

                // P-Box Permutation of the 32 bit output of the SBox prmutation

                String S = "";
                int[] S2 = new int[32];
                int[] ResultPPermut = new int[32];

                for (int m = 0; m < 8; m++) {
                    String t = "";
                    String a = BinaryConversions.DecToBinary(new BigInteger("" + SBoxResult[m]));
                    if (a.length() < 4) {
                        for (int i = 0; i < 4 - a.length(); i++)
                            t += "0";
                        a = t + a;
                    }
                    S += a;
                }

                for (int m = 0; m < S.length(); m++) {
                    if (S.charAt(m) == '0')
                        S2[m] = 0;
                    else
                        S2[m] = 1;
                }

                for (int m = 0; m < S2.length; m++) {
                    ResultPPermut[m] = S2[PBox[m] - 1];
                }

                int[] XORTable2 = new int[32];
                for (int m = 0; m < 32; m++) {
                    if (left[m] == 0 && ResultPPermut[m] == 1 || left[m] == 1 && ResultPPermut[m] == 0)
                        XORTable2[m] = 1;
                    else
                        XORTable2[m] = 0;

                }

                left = right;
                right = XORTable2;
            }

            // the 16 rounds are completed
            // concatenation of right and left and final permutation

            int[] leftAndRight = new int[64];
            int[] leftAndRightPermut = new int[64];
            for (int m = 0; m < 32; m++)
                leftAndRight[m] = right[m];
            for (int m = 32; m < 64; m++)
                leftAndRight[m] = left[m - 32];

            for (int m = 0; m < 64; m++)
                leftAndRightPermut[m] = leftAndRight[finalPerm[m] - 1];

            // concatenate the actual encrypted bloc to the final ciphertext

            for (int m = 0; m < 64; m++)
                finalEncryptText += leftAndRightPermut[m];

        }
        return finalEncryptText;
    }

    public static String decrypt(String plainText, String key){


        int [] EBox = { 32,  1,  2,  3,  4,  5,  4,  5,  6,  7,  8,  9,
                8,  9,  10,  11,  12,  13,  12,  13,  14,  15,  16,  17,
                16,  17,  18,  19,  20,  21,  20,  21,  22,  23,  24,  25,
                24,  25,  26,  27,  28,  29,  28,  29,  30,  31,  32,  1  } ;

        int [] PBox = { 16,  7,  10,  21,  29,  12,  28,  17,  1,  15,  23,  26,  5,  18,  31,  20,
                2,  8,  24,  14,  32,  27,  3,  9,  19,  13,  30,  6,  22,  11,  4,  25  };

        int [] finalPerm = { 40,  8,  48,  16,  56,  24,  64,  32,  39,  7,  47,  15,  55,  23,  63,  31,
                38,  6,  46,  14,  54,  22,  62,  30,  37,  5,  45,  13,  53,  21,  61,  29,
                36,  4,  44,  12,  52,  20,  60,  28,  35,  3,  43,  11,  51,  19,  59,  27,
                34,  2,  42,  10,  50,  18,  58,  26,  33,  1,  41,  9,  49,  17,  57,  25  };

        int [] [] SBox1 = { { 14,  4,  13,  1,  2,  15,  11,  8,  3,  10,  6,  12,  5,  9,  0,  7 },
                { 0,  15,  7,  4,  14,  2,  13,  1,  10,  6,  12,  11,  9,  5,  3,  8 },
                { 4,  1,  14,  8,  13,  6,  2,  11,  15,  12,  9,  7,  3,  10,  5,  0 },
                { 15,  12,  8,  2,  4,  9,  1,  7,  5,  11,  3,  14,  10,  0,  6,  13 }} ;


        int [] [] SBox2 = { { 15,  1,  8,  14,  6,  11,  3,  4,  9,  7,  2,  13,  12,  0,  5,  10 },
                { 3,  13,  4,  7,  15,  2,  8,  14,  12,  0,  1,  10,  6,  9,  11,  5 },
                { 0,  14,  7,  11,  10,  4,  13,  1,  5,  8,  12,  6,  9,  3,  2,  15 },
                {13,  8,  10,  1,  3,  15,  4,  2,  11,  6,  7,  12,  0,  5,  14,  9 }}  ;

        int [] [] SBox3 = { { 10,  0,  9,  14,  6,  3,  15,  5,  1,  13,  12,  7,  11,  4,  2,  8 },
                { 13,  7,  0,  9,  3,  4,  6,  10,  2,  8,  5,  14,  12,  11,  15,  1 },
                { 13,  6,  4,  9,  8,  15,  3,  0,  11,  1,  2,  12,  5,  10,  14,  7 },
                { 1,  10,  13,  0,  6,  9,  8,  7,  4,  15,  14,  3,  11,  5,  2,  12 }} ;

        int [] [] SBox4 = { { 7,  13,  14,  3,  0,  6,  9,  10,  1,  2,  8,  5,  11,  12,  4,  15 },
                { 13,  8,  11,  5,  6,  15,  0,  3,  4,  7,  2,  12,  1,  10,  14,  9 },
                { 10,  6,  9,  0,  12,  11,  7,  13,  15,  1,  3,  14,  5,  2,  8,  4 },
                { 3,  15,  0,  6,  10,  1,  13,  8,  9,  4,  5,  11,  12,  7,  2,  14 }} ;

        int [] [] SBox5 = { { 2,  12,  4,  1,  7,  10,  11,  6,  8,  5,  3,  15,  13,  0,  14,  9 },
                { 14,  11,  2,  12,  4,  7,  13,  1,  5,  0,  15,  10,  3,  9,  8,  6 },
                { 4,  2,  1,  11,  10,  13,  7,  8,  15,  9,  12,  5,  6,  3,  0,  14 },
                { 11,  8,  12,  7,  1,  14,  2,  13,  6,  15,  0,  9,  10,  4,  5,  3 }} ;

        int [] [] SBox6 = { { 12,  1,  10,  15,  9,  2,  6,  8,  0,  13,  3,  4,  14,  7,  5,  11 },
                { 10,  15,  4,  2,  7,  12,  9,  5,  6,  1,  13,  14,  0,  11,  3,  8 },
                { 9,  14,  15,  5,  2,  8,  12,  3,  7,  0,  4,  10,  1,  13,  11,  6 },
                { 4,  3,  2,  12,  9,  5,  15,  10,  11,  14,  1,  7,  6,  0,  8,  13 }} ;

        int [] [] SBox7 = { { 4,  11,  2,  14,  15,  0,  8,  13,  3,  12,  9,  7,  5,  10,  6,  1 },
                { 13,  0,  11,  7,  4,  9,  1,  10,  14,  3,  5,  12,  2,  15,  8,  6 },
                { 1,  4,  11,  13,  12,  3,  7,  14,  10,  15,  6,  8,  0,  5,  9,  2 },
                { 6,  11,  13,  8,  1,  4,  10,  7,  9,  5,  0,  15,  14,  2,  3,  12 }} ;

        int [] [] SBox8 = { { 13,  2,  8,  4,  6,  15,  11,  1,  10,  9,  3,  14,  5,  0,  12,  7 },
                { 1,  15,  13,  8,  10,  3,  7,  4,  12,  5,  6,  11,  0,  14,  9,  2 },
                { 7,  11,  4,  1,  9,  12,  14,  2,  0,  6,  10,  13,  15,  3,  5,  8 },
                { 2,  1,  14,  7,  4,  10,  8,  13,  15,  12,  9,  0,  3,  5,  6,  11 }} ;


        int[] left = new int[32];
        int[] right = new int[32];
        int[][] keys = new int [16][48];
        String[] blocks;
        String finalEncryptText = "" ;
        int[] keyblock = new int[64];

        String temp = plainText;//= BinaryConversions.StringToBinary(plainText);
        temp = VerifPlainTex64Bits.controlPlainT(temp);
        blocks = new String[5000];

        String temp2 = key = BinaryConversions.StringToBinary(key);
        temp2 = VerifPlainTex64Bits.controlKey(temp2);

        //we obtain the key in binary

        for(int i = 0; i < temp2.length() ; i++){
            if( temp2 . charAt(i) == '0' )
                keyblock[i] = 0;
            else
                keyblock[i] = 1;
        }


        //we obtain all the 16 sub-keys

        keys = KeyTransformationDecryption.keyTransf(keyblock);

        //we obtain all the 16 bit blocks of the plaintext

        for(int i = 0; i < blocks.length ; i++)
            blocks[i] = "";

        int N = 0, j = 0;
        while (j < temp.length()){
            blocks[N] += temp.charAt(j);
            if((j+1) % 64 == 0)
                N++;
            j++;
        }


        //encrypt all blocks

        for ( int k = 0 ; k < N ; k ++){

            //initial permutation

            blocks [ k ] = InitialPlainTxtPermutDecryption.permut( blocks [ k ] ) ;

            // dIvide each block into left and right

            for ( int m = 0 ; m < 32 ; m ++){
                if( blocks[k].charAt(m) == '0' )
                    left[m] = 0;
                else
                    left[m] = 1;
            }

            for ( int m = 32 ; m < 64 ; m ++){
                if( blocks[k].charAt(m) == '0' )
                    right[m - 32] = 0;
                else
                    right[m - 32] = 1;
            }

            //for each block we have 16 rounds

            for ( int r = 0 ; r < 16 ; r ++){

                // exchanging left and right

                // expansion permutation of the right

                int[]expandRight = new int[48];

                for (int m = 0; m < 48 ; m ++)
                    expandRight[m] = right [EBox[m] - 1];

                //XOR of the right with the partial key

                int [] XORTable = new int[48];
                for( int m = 0 ; m < 48 ; m ++ ){
                    if( expandRight[m] == 0 && keys [r][m] == 1 || expandRight[m] == 1 && keys [r][m] == 0 )
                        XORTable [m] = 1 ;
                    else
                        XORTable [m] = 0 ;
                }


                // S-Box

                int [] SBoxResult = new int[8];
                int ro , co , one , two , three , four , five , six ;


                one = XORTable [ 5 ];
                two = XORTable [ 0 ];
                three = XORTable [ 4 ];
                four = XORTable [ 3 ];
                five = XORTable [ 2 ];
                six = XORTable [ 1 ];
                ro = one + 2 * two;
                co = three + 2 * four + 4 * five + 8 * six ;
                SBoxResult [ 0 ] = SBox1 [ ro ] [co] ;

                //System.out.println(BinaryConversions.DecToBinary( new BigInteger(""+SBoxResult [0])));

                one = XORTable [ 11 ];
                two = XORTable [ 6 ];
                three = XORTable [ 10 ];
                four = XORTable [ 9 ];
                five = XORTable [ 8 ];
                six = XORTable [ 7 ];
                ro = one + 2 * two;
                co = three + 2 * four + 4 * five + 8 * six ;
                SBoxResult [ 1 ] = SBox2 [ ro ] [co] ;

                one = XORTable [ 17 ];
                two = XORTable [ 12 ];
                three = XORTable [ 16 ];
                four = XORTable [ 15 ];
                five = XORTable [ 14 ];
                six = XORTable [ 13 ];
                ro = one + 2 * two;
                co = three + 2 * four + 4 * five + 8 * six ;
                SBoxResult [ 2 ] = SBox3 [ ro ] [co] ;

                one = XORTable [ 23 ];
                two = XORTable [ 18 ];
                three = XORTable [ 22 ];
                four = XORTable [ 21 ];
                five = XORTable [ 20 ];
                six = XORTable [ 19 ];
                ro = one + 2 * two;
                co = three + 2 * four + 4 * five + 8 * six ;
                SBoxResult [ 3 ] = SBox4 [ ro ] [co] ;

                one = XORTable [ 29 ];
                two = XORTable [ 24 ];
                three = XORTable [ 28 ];
                four = XORTable [ 27 ];
                five = XORTable [ 26 ];
                six = XORTable [ 25 ];
                ro = one + 2 * two;
                co = three + 2 * four + 4 * five + 8 * six ;
                SBoxResult [ 4 ] = SBox5 [ ro ] [co] ;

                one = XORTable [ 35 ];
                two = XORTable [ 30 ];
                three = XORTable [ 34 ];
                four = XORTable [ 33 ];
                five = XORTable [ 32 ];
                six = XORTable [ 31 ];
                ro = one + 2 * two;
                co = three + 2 * four + 4 * five + 8 * six ;
                SBoxResult [ 5 ] = SBox6 [ ro ] [co] ;

                one = XORTable [ 41 ];
                two = XORTable [ 36 ];
                three = XORTable [ 40 ];
                four = XORTable [ 39 ];
                five = XORTable [ 38 ];
                six = XORTable [ 37 ];
                ro = one + 2 * two;
                co = three + 2 * four + 4 * five + 8 * six ;
                SBoxResult [ 6 ] = SBox7 [ ro ] [co] ;

                one = XORTable [ 47 ];
                two = XORTable [ 42 ];
                three = XORTable [ 46 ];
                four = XORTable [ 45 ];
                five = XORTable [ 44 ];
                six = XORTable [ 43 ];
                ro = one + 2 * two;
                co = three + 2 * four + 4 * five + 8 * six ;
                SBoxResult [ 7 ] = SBox8 [ ro ] [co] ;

                // P-Permutation of the 32 bit output of the SBox prmutation

                String S = "";
                int [] S2 = new int[ 32 ];
                int [] resultPPermut = new int[ 32 ];

                for( int m = 0 ; m < 8 ; m ++ ){
                    String t = "";
                    String a = BinaryConversions.DecToBinary( new BigInteger(""+SBoxResult [m]));
                    if( a.length () < 4 ){
                        for( int i = 0 ; i < 4 - a . length() ; i++ )
                            t += "0" ;
                        a = t + a ;
                    }
                    S += a ;
                }




                for (int m = 0; m < S.length() ; m ++){
                    if( S.charAt(m) == '0' )
                        S2 [m] = 0;
                    else
                        S2 [m] = 1;
                }

                for (int m = 0; m < S2.length ; m ++){
                    resultPPermut [m] = S2 [PBox[m] - 1];
                }

                // XOR the PPermut result with the left half

                int [] XORTable2 = new int[32];
                for( int m = 0 ; m < 32 ; m ++ ){
                    if( left[m] == 0 && resultPPermut [m] == 1 || left [m] == 1 && resultPPermut [m] == 0 )
                        XORTable2 [m] = 1 ;
                    else
                        XORTable2 [m] = 0 ;

                }


                left = right ;
                right = XORTable2 ;

            }

            // the 16 rounds are completed
            // concatenation of right and left and final permutation

            int [] leftAndRight =  new int[64];
            int [] leftAndRightPermut = new int[ 64 ];
            for( int m = 0 ; m < 32 ; m ++ )
                leftAndRight [ m ] = right [ m ] ;
            for( int m = 32 ; m < 64 ; m ++ )
                leftAndRight [ m ] = left [ m - 32] ;

            for (int m = 0; m < 64 ; m ++)
                leftAndRightPermut [m] = leftAndRight [finalPerm[m] - 1];
            for (int m = 0; m < 64 ; m ++)
                finalEncryptText += leftAndRightPermut[m];

        }

        int [] finalEncryptTextInteger = new int [finalEncryptText.length()];
        for ( int m=0 ; m < finalEncryptText.length() ; m++){
            if( finalEncryptText.charAt(m) == '0' )
                finalEncryptTextInteger [ m ] = 0;
            else
                finalEncryptTextInteger [ m ] = 1;
        }

        boolean b = false ;
        String decryptedText = "" ;
        int [] t = new int [8] ;
        int r = 0;
        for ( int m = 0 ; m < finalEncryptTextInteger.length ; m ++ ){
            t [ r ++ ] = finalEncryptTextInteger [ m ] ;
            if ( t [ r - 1 ] == 1 )
                b = true ;
            if ( r == 8 ) {
                if ( b == true )
                    decryptedText += (char)BinaryConversions.BinaryToDec( t ).intValue();
                r = 0 ;
            }
        }
        return decryptedText ;
    }

}
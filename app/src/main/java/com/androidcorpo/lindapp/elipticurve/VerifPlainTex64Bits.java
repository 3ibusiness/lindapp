package com.androidcorpo.lindapp.elipticurve;

public class VerifPlainTex64Bits {

    // Verify that a block of bits is a multiple of 64

    public static String controlPlainT(String bitsBlock) {
        String t = "";
        if (bitsBlock.length() < 64) {
            for (int i = 0; i < 64 - bitsBlock.length(); i++)
                t += "0";
            bitsBlock = t + bitsBlock;
            return bitsBlock;
        }

        if (bitsBlock.length() % 64 != 0) {
            while (bitsBlock.length() % 64 != 0)
                bitsBlock = "0" + bitsBlock;
        }

        return bitsBlock;
    }

    public static String controlKey(String bitsBlock) {
        String t = "";
        if (bitsBlock.length() < 64) {
            for (int i = 0; i < 64 - bitsBlock.length(); i++)
                t += "0";
            bitsBlock = t + bitsBlock;
        }
        return bitsBlock;
    }
}

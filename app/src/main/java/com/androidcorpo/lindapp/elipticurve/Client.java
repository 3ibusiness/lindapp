package com.androidcorpo.lindapp.elipticurve;

public class Client {

    String key = "67720986";
    String plaintext = "";
    String cypherText = CoreAlgorithm.crypt(plaintext, key);

    String cypherTextHex = BinaryConversions.binToHex(cypherText);
    String bin = BinaryConversions.hexToBin(cypherTextHex);

}

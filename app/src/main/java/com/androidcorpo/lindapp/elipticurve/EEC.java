package com.androidcorpo.lindapp.elipticurve;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EEC {

    // Génération de la paire de clés Privée + Publique
    public static KeyPair keyGeneration() {

        try {//Initialisation des paramètres
            ECNamedCurveParameterSpec params = ECNamedCurveTable.getParameterSpec("brainpoolp256r1");
            KeyPairGenerator generateur = KeyPairGenerator.getInstance("ECDH", new org.bouncycastle.jce.provider.BouncyCastleProvider());
            //Generations des deux clés
            generateur.initialize(params);
            KeyPair pairCles = generateur.genKeyPair();

            return pairCles;

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        }

    }

    //Génération des clés secrètes
    public static SecretKey secretKey(PrivateKey d, PublicKey q) {
        try {

            KeyAgreement accordCles = KeyAgreement.getInstance("ECDH", new org.bouncycastle.jce.provider.BouncyCastleProvider());
            accordCles.init(d);
            accordCles.doPhase(q, true);

            return accordCles.generateSecret("AES");

        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    // Fonction de chiffrement
    public static String crypt(SecretKey cleSecrete, String texteAchifrer, byte[] iv) {
        try {
            IvParameterSpec seed = new IvParameterSpec(iv);
            Cipher chiffreur = Cipher.getInstance("AES/GCM/NoPadding", new org.bouncycastle.jce.provider.BouncyCastleProvider());
            byte[] texteAchifrerBytes = texteAchifrer.getBytes(StandardCharsets.UTF_8);
            byte[] textChiffre;

            chiffreur.init(Cipher.ENCRYPT_MODE, cleSecrete, seed);
            textChiffre = new byte[chiffreur.getOutputSize(texteAchifrerBytes.length)];
            int tailleChiffrement = chiffreur.update(texteAchifrerBytes, 0, texteAchifrerBytes.length, textChiffre, 0);


            tailleChiffrement += chiffreur.doFinal(textChiffre, tailleChiffrement);

            return bytesToHex(textChiffre);


        } catch (NoSuchAlgorithmException
                | NoSuchPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException
                | ShortBufferException
                | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(SecretKey cleSecrete, String textChiffre, byte[] iv) {
        try {
            Key cleDechiffrement = new SecretKeySpec(cleSecrete.getEncoded(), cleSecrete.getAlgorithm());

            IvParameterSpec seed = new IvParameterSpec(iv);
            Cipher chiffreur = Cipher.getInstance("AES/GCM/NoPadding", new org.bouncycastle.jce.provider.BouncyCastleProvider());
            byte[] textChiffreBytes = hexToBytes(textChiffre);
            byte[] textClair;

            chiffreur.init(Cipher.DECRYPT_MODE, cleSecrete, seed);
            textClair = new byte[chiffreur.getOutputSize(textChiffreBytes.length)];
            int tailleDeChiffrement = chiffreur.update(textChiffreBytes, 0, textChiffreBytes.length, textClair, 0);

            tailleDeChiffrement += chiffreur.doFinal(textClair, tailleDeChiffrement);

            return new String(textClair, StandardCharsets.UTF_8);

        } catch (NoSuchAlgorithmException
                | NoSuchPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException
                | IllegalBlockSizeException | BadPaddingException
                | ShortBufferException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
    Fonctions helper de conversion de bytes en hexadécimal
    */
    private static String bytesToHex(byte[] data, int length) {
        String digits = "0123456789ABCDEF";
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i != length; i++) {
            int v = data[i] & 0xff;

            buffer.append(digits.charAt(v >> 4));
            buffer.append(digits.charAt(v & 0xf));
        }

        return buffer.toString();
    }

    public static String bytesToHex(byte[] data) {
        return bytesToHex(data, data.length);
    }

    public static byte[] hexToBytes(String string) {
        int length = string.length();
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(string.charAt(i), 16) << 4) + Character
                    .digit(string.charAt(i + 1), 16));
        }
        return data;
    }

    public static boolean isHexNumber (String hexString) {
        return  hexString.matches("[0-9A-F]+");
    }
}

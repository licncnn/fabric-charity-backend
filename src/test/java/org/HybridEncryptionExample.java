package org;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class HybridEncryptionExample {

    public static void main(String[] args) {
        try {
            KeyPair eccKeyPair = generateECCKeyPair();
            PublicKey eccPublicKey = eccKeyPair.getPublic();
            PrivateKey eccPrivateKey = eccKeyPair.getPrivate();

            String message = "这是一条要加密的信息";
            SecretKey aesKey = generateAESKey();

            byte[] encryptedMessage = encryptAES(aesKey, message);
            byte[] signature = sign(eccPrivateKey, encryptedMessage);

            //
            boolean signatureIsValid = verifySignature(eccPublicKey, encryptedMessage, signature);

            if (signatureIsValid) {
                String decryptedMessage = decryptAES(aesKey, encryptedMessage);
                System.out.println("解密后的信息: " + decryptedMessage);
            } else {
                System.err.println("签名验证失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static KeyPair generateECCKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
        kpg.initialize(ecSpec, new SecureRandom());
        return kpg.generateKeyPair();
    }

    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        return keyGen.generateKey();
    }

    public static byte[] encryptAES(SecretKey key, String message) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[cipher.getBlockSize()];
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
        return cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
    }

    public static String decryptAES(SecretKey key, byte[] encryptedMessage) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[cipher.getBlockSize()];
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
        byte[] decryptedBytes = cipher.doFinal(encryptedMessage);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static byte[] sign(PrivateKey privateKey, byte[] message) throws Exception {
        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initSign(privateKey);
        signature.update(message);
        return signature.sign();
    }

    public static boolean verifySignature(PublicKey publicKey, byte[] message, byte[] signatureBytes) throws Exception {
        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initVerify(publicKey);
        signature.update(message);
        return signature.verify(signatureBytes);
    }
}

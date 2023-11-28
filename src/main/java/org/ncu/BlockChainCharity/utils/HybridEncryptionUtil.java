package org.ncu.BlockChainCharity.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class HybridEncryptionUtil {

    public static KeyPair eccKeyPair;
    public static PublicKey eccPublicKey;
    public static PrivateKey eccPrivateKey;
    public static SecretKey aesKey ;



    static {
        try {
//            HybridEncryptionUtil hybridEncryptionUtil = new HybridEncryptionUtil();
            eccPublicKey = stringToPublicKey("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEG1iTPIeNvWvM55PsdNOZlvfwRiEJkgUzYvrAX8dKvoHQOL02KdmTBKX1nbufpATDevJ5uT5XdRcHmUhwGHIrmQ==");
            eccPrivateKey = stringToPrivateKey("MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCAXgZTNiWGLZkD091mjqL6mX8wy1HmI9kcuhL2ED3WwTA==");
            aesKey =  stringToSecretKey("5ltUHTts3fiXsalv/P9dVA==");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            KeyPair eccKeyPair = generateECCKeyPair();
            PublicKey eccPublicKey = eccKeyPair.getPublic();
            PrivateKey eccPrivateKey = eccKeyPair.getPrivate();

            String message = "这是一条要加密的信息1";

            byte[] encryptedMessage = encryptAES(aesKey, message);
            byte[] signature = sign(eccPrivateKey, encryptedMessage);




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


    public static String secretKeyToString(SecretKey secretKey) {
        byte[] secretKeyBytes = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(secretKeyBytes);
    }

    // 字符串转化为AES私钥
    public static SecretKey stringToSecretKey(String secretKeyString) {
        byte[] secretKeyBytes = Base64.getDecoder().decode(secretKeyString);
        return new SecretKeySpec(secretKeyBytes, "AES");
    }


    // Convert PrivateKey to String
    public static String privateKeyToString(PrivateKey privateKey) {
        byte[] privateKeyBytes = privateKey.getEncoded();
        return Base64.getEncoder().encodeToString(privateKeyBytes);
    }

    // Convert String to PrivateKey
    public static PrivateKey stringToPrivateKey(String privateKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(privateKeySpec);
    }


    // 公钥转化为字符串
    public static String publicKeyToString(PublicKey publicKey) {
        byte[] publicKeyBytes = publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(publicKeyBytes);
    }

    // 字符串转化为公钥
    public static PublicKey stringToPublicKey(String publicKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePublic(publicKeySpec);
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

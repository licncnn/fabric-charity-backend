package org;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

public class EncryptionPerformanceTest {

    private static byte[] generateRandomBytes(int size) {
        byte[] bytes = new byte[size];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return bytes;
    }

    private static long testEncryption(byte[] data, Cipher cipher) throws Exception {
        long startTime = System.nanoTime();
        byte[] encryptedData = cipher.doFinal(data);
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    private static long testDecryption(byte[] encryptedData, Cipher cipher) throws Exception {
        long startTime = System.nanoTime();
        byte[] decryptedData = cipher.doFinal(encryptedData);
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    private static void testAES(int dataSize) throws Exception {
        byte[] data = generateRandomBytes(dataSize);
        Security.addProvider(new BouncyCastleProvider());
        SecretKey key = new SecretKeySpec(generateRandomBytes(16), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        long encryptTime = 0;
        long decryptTime = 0;
        for (int i = 0; i < 100; i++) {
            long t1 = testEncryption(data, cipher);
            long t2 = testDecryption(cipher.doFinal(data), cipher);
            encryptTime += t1;
            decryptTime += t2;
        }
        System.out.printf("AES-%d: data size=%d bytes, avg encrypt time=%d ns, avg decrypt time=%d ns%n",
                key.getEncoded().length * 8, dataSize, encryptTime / 100, decryptTime / 100);
    }

    private static void testECC(int dataSize) throws Exception {
        byte[] data = generateRandomBytes(dataSize);
        Security.addProvider(new BouncyCastleProvider());
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECIES", "BC");
        keyGen.initialize(256);
        KeyPair keyPair = keyGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        Cipher cipher = Cipher.getInstance("ECIES", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        long encryptTime = 0;
        long decryptTime = 0;
        for (int i = 0; i < 100; i++) {
            long t1 = testEncryption(data, cipher);
            long t2 = testDecryption(cipher.doFinal(data), cipher);
            encryptTime += t1;
            decryptTime += t2;
        }
        int keySize = 0;
        if (publicKey instanceof java.security.interfaces.ECPublicKey) {
            keySize = ((java.security.interfaces.ECPublicKey) publicKey).getParams().getCurve().getField().getFieldSize();
        }
        System.out.printf("ECC-%d: data size=%d bytes, avg encrypt time=%d ns, avg decrypt time=%d ns%n",
                keySize, dataSize, encryptTime / 100, decryptTime / 100);
    }

    private static void testRSA(int dataSize) throws Exception {
        byte[] data = generateRandomBytes(dataSize);
        Security.addProvider(new BouncyCastleProvider());
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        long encryptTime = 0;
        long decryptTime = 0;
        for (int i = 0; i < 100; i++) {
            long t1 = testEncryption(data, cipher);
            long t2 = testDecryption(cipher.doFinal(cipher.doFinal(data)), cipher);
            encryptTime += t1;
            decryptTime += t2;
        }
        int keySize = 0;
        if (publicKey instanceof java.security.interfaces.RSAPublicKey) {
            keySize = ((java.security.interfaces.RSAPublicKey) publicKey).getModulus().bitLength();
        }
        System.out.printf("RSA-%d: data size=%d bytes, avg encrypt time=%d ns, avg decrypt time=%d ns%n",
                keySize, dataSize, encryptTime / 100, decryptTime / 100);
    }

    public static void main(String[] args) throws Exception {
        int[] dataSizes = {10, 50, 100, 150, 200};

        for (int size : dataSizes) {
            testAES(size);
            testECC(size);
            testRSA(size);
        }
    }
}
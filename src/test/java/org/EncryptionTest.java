package org;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;
import java.util.Random;

public class EncryptionTest {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
//    private static final int ECC_ENCRYPTED_KEY_LENGTH = 64;
    private static final int ECC_ENCRYPTED_KEY_LENGTH = 128;



    public static void main(String[] args) throws Exception {
        int[] dataSizes = {20, 100, 150, 200, 400, 800};
        int numberOfTests = 100;
        double[][] results = new double[3][dataSizes.length];

        for (int i = 0; i < dataSizes.length; i++) {
            int dataSize = dataSizes[i];
            byte[][] testData = generateTestData(dataSize, numberOfTests);

            for (int j = 0; j < numberOfTests; j++) {
                byte[] data = testData[j];

                // Test ECC
                long start = System.nanoTime();
                byte[] encryptedDataEcc = encryptWithEcc(data);
                long end = System.nanoTime();
                results[0][i] += (end - start);

                start = System.nanoTime();
                byte[] decryptedDataEcc = decryptWithEcc(encryptedDataEcc);
                end = System.nanoTime();
                results[0][i] += (end - start);

                // Test AES-128 + ECC-256
                start = System.nanoTime();
                byte[] encryptedDataAes128Ecc256 = encryptWithAes128Ecc256(data);
                end = System.nanoTime();
                results[1][i] += (end - start);

                start = System.nanoTime();
                byte[] decryptedDataAes128Ecc256 = decryptWithAes128Ecc256(encryptedDataAes128Ecc256);
                end = System.nanoTime();
                results[1][i] += (end - start);

                // Test AES-128 + RSA-2048
                start = System.nanoTime();
                byte[] encryptedDataAes128Rsa2048 = encryptWithAes128Rsa2048(data);
                end = System.nanoTime();
                results[2][i] += (end - start);

                start = System.nanoTime();
                byte[] decryptedDataAes128Rsa2048 = decryptWithAes128Rsa2048(encryptedDataAes128Rsa2048);
                end = System.nanoTime();
                results[2][i] += (end - start);
            }
        }

        for (int i = 0; i < results.length; i++) {
            for (int j = 0; j < results[i].length; j++) {
                results[i][j] /= (numberOfTests * 2);
                System.out.printf("Algorithm %d - Data size: %d B - Average time: %.2f ns\n", i + 1, dataSizes[j], results[i][j]);
            }
        }
    }

    private static byte[][] generateTestData(int dataSize, int numberOfTests) {
        byte[][] testData = new byte[numberOfTests][];
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < numberOfTests; i++) {
            byte[] data = new byte[dataSize];
            random.nextBytes(data);
            testData[i] = data;
        }
        return testData;
    }

    private static byte[] encryptWithEcc(byte[] data) {
        // 使用ECC加密数据
        // 这里仅为示例，实际应用中应遵循密钥管理规范

        byte[] encryptedData = new byte[ECC_ENCRYPTED_KEY_LENGTH];
        System.arraycopy(data, 0, encryptedData, ECC_ENCRYPTED_KEY_LENGTH - data.length, data.length);
        return encryptedData;
    }



    private static byte[] decryptWithEcc(byte[] encryptedData) {
        // 使用ECC解密数据
        // 这里仅为示例，实际应用中应遵循密钥管理规范

        int decryptedDataLength = 16; // Set the correct length for a 128-bit AES key
        byte[] decryptedData = new byte[decryptedDataLength];
        System.arraycopy(encryptedData, encryptedData.length - decryptedDataLength, decryptedData, 0, decryptedDataLength);
        return decryptedData;
    }





    private static byte[] encryptWithAes128Ecc256(byte[] data) throws Exception {
        // 生成AES密钥
        KeyGenerator keyGen = KeyGenerator.getInstance("AES", "BC");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();

        // 使用AES加密数据
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] iv = cipher.getIV();
        byte[] encryptedData = cipher.doFinal(data);

        // 使用ECC加密AES密钥
        byte[] encryptedAesKey = encryptWithEcc(secretKey.getEncoded());

        // 拼接加密后的AES密钥和加密后的数据
        byte[] result = new byte[64 + iv.length + encryptedData.length];
        System.arraycopy(encryptedAesKey, 0, result, 0, 64);
        System.arraycopy(iv, 0, result, 64, iv.length);
        System.arraycopy(encryptedData, 0, result, 64 + iv.length, encryptedData.length);

        return result;
    }

    private static byte[] decryptWithAes128Ecc256(byte[] encryptedDataWithKey) throws Exception {
        // 提取加密后的AES密钥和加密后的数据
        byte[] encryptedAesKey = Arrays.copyOfRange(encryptedDataWithKey, 0, 64);
        byte[] iv = Arrays.copyOfRange(encryptedDataWithKey, 64, 64 + 16);
        byte[] encryptedData = Arrays.copyOfRange(encryptedDataWithKey, 64 + 16, encryptedDataWithKey.length);

        // 使用ECC解密AES密钥
        byte[] decryptedAesKey = decryptWithEcc(encryptedAesKey);
        SecretKey secretKey = new SecretKeySpec(decryptedAesKey, 0, decryptedAesKey.length, "AES");

        // 使用AES解密数据
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        return cipher.doFinal(encryptedData);
    }

    private static byte[] encryptWithAes128Rsa2048(byte[] data) throws Exception {
        // 生成AES密钥
        KeyGenerator keyGen = KeyGenerator.getInstance("AES", "BC");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();

        // 使用AES加密数据
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] iv = cipher.getIV();
        byte[] encryptedData = cipher.doFinal(data);

        // 生成RSA密钥对
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA", "BC");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // 使用RSA加密AES密钥
        Cipher rsaCipher = Cipher.getInstance("RSA/None/OAEPWithSHA1AndMGF1Padding", "BC");
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedAesKey = rsaCipher.doFinal(secretKey.getEncoded());

        // 拼接加密后的AES密钥和加密后的数据
        byte[] result = new byte[encryptedAesKey.length + iv.length + encryptedData.length];
        System.arraycopy(encryptedAesKey, 0, result, 0, encryptedAesKey.length);
        System.arraycopy(iv, 0, result, encryptedAesKey.length, iv.length);
        System.arraycopy(encryptedData, 0, result, encryptedAesKey.length + iv.length, encryptedData.length);

        return result;
    }

    private static byte[] decryptWithAes128Rsa2048(byte[] encryptedDataWithKey) throws Exception {
        // 提取加密后的AES密钥和加密后的数据
        byte[] encryptedAesKey = Arrays.copyOfRange(encryptedDataWithKey, 0, 256);
        byte[] iv = Arrays.copyOfRange(encryptedDataWithKey, 256, 256 + 16);
        byte[] encryptedData = Arrays.copyOfRange(encryptedDataWithKey, 256 + 16, encryptedDataWithKey.length);

        // 使用RSA解密AES密钥
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA", "BC");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();

        Cipher rsaCipher = Cipher.getInstance("RSA/None/OAEPWithSHA1AndMGF1Padding", "BC");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedAesKey = rsaCipher.doFinal(encryptedAesKey);
        SecretKey secretKey = new SecretKeySpec(decryptedAesKey, 0, decryptedAesKey.length, "AES");

        // 使用AES解密数据
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        return cipher.doFinal(encryptedData);
    }
}


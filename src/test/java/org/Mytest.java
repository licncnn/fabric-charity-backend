package org;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.ByteBuffer;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;

public class Mytest {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final int ECC_ENCRYPTED_KEY_LENGTH = 128;

    public static void main(String[] args) throws Exception {
        int[] dataSizes = {20, 100, 150, 200, 400, 800};
        int numberOfTests = 100;
        double[][] results = new double[3][dataSizes.length];

        for (int i = 0; i < dataSizes.length; i++) {
            int dataSize = dataSizes[i];
            byte[][] testData = generateTestData(dataSize, numberOfTests);

            KeyPair eccKeyPair = generateEccKeyPair();

            for (int j = 0; j < numberOfTests; j++) {
                byte[] data = testData[j];

                long start = System.nanoTime();
                byte[] encryptedDataEcc = encryptWithEcc(data, eccKeyPair.getPublic());
                long end = System.nanoTime();
                results[0][i] += (end - start);

                start = System.nanoTime();
                byte[] decryptedDataEcc = decryptWithEcc(encryptedDataEcc, eccKeyPair.getPrivate());
                end = System.nanoTime();
                results[0][i] += (end - start);

                start = System.nanoTime();
                byte[] encryptedDataAes128Ecc256 = encryptWithAes128Ecc256(data);
                end = System.nanoTime();
                results[1][i] += (end - start);

                start = System.nanoTime();
                byte[] decryptedDataAes128Ecc256 = decryptWithAes128Ecc256(encryptedDataAes128Ecc256);
                end = System.nanoTime();
                results[1][i] += (end - start);

                start = System.nanoTime();
                byte[] encryptedDataAes128Rsa2048 = encryptWithAes128Rsa2048(data);
                end = System.nanoTime();
                results[2][i] += (end - start);

                start = System.nanoTime();
                byte[] decryptedDataAes128Rsa2048 = decryptWithAes128Rsa2048(encryptedDataAes128Rsa2048);
                end = System.nanoTime();
                results[2][i] += (end - start);

                if (!Arrays.equals(data, decryptedDataEcc) ||
                        !Arrays.equals(data, decryptedDataAes128Ecc256) ||
                        !Arrays.equals(data, decryptedDataAes128Rsa2048)) {
                    System.err.println("Decryption failed for data size " + dataSize + " at test " + j);
                }
            }
            results[0][i] /= (numberOfTests * 2.0);
            results[1][i] /= (numberOfTests * 2.0);
            results[2][i] /= (numberOfTests * 2.0);

            System.out.println("Data size: " + dataSize + " bytes");
            System.out.println("ECC: " + results[0][i] + " ns");
            System.out.println("AES-128 + ECC-256: " + results[1][i] + " ns");
            System.out.println("AES-128 + RSA-2048: " + results[2][i] + " ns");
            System.out.println();
        }
    }

    private static byte[][] generateTestData(int dataSize, int numberOfTests) {
        byte[][] testData = new byte[numberOfTests][];

        for (int i = 0; i < numberOfTests; i++) {
            testData[i] = new byte[dataSize];
            new SecureRandom().nextBytes(testData[i]);
        }

        return testData;
    }

    private static KeyPair generateEccKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
        ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("prime256v1");
        keyPairGenerator.initialize(ecGenParameterSpec, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    private static byte[] encryptWithEcc(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("ECIES", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    private static byte[] decryptWithEcc(byte[] encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("ECIES", "BC");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedData);
    }

    private static byte[] encryptWithAes128Ecc256(byte[] data) throws Exception {
        // Generate AES key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey aesKey = keyGen.generateKey();

        // Encrypt data with AES key
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivParameterSpec);
        byte[] encryptedData = cipher.doFinal(data);

        // Encrypt AES key with ECC public key
        KeyPair eccKeyPair = generateEccKeyPair();
        byte[] encryptedAesKey = encryptWithEcc(aesKey.getEncoded(), eccKeyPair.getPublic());

        // Concatenate encrypted data and encrypted AES key
        int encryptedDataLength = encryptedData.length;
        byte[] result = new byte[encryptedDataLength + encryptedAesKey.length + 4];
        ByteBuffer buffer = ByteBuffer.wrap(result);
        buffer.putInt(encryptedDataLength);
        buffer.put(encryptedData);
        buffer.put(encryptedAesKey);


        return result;
    }

    private static byte[] decryptWithAes128Ecc256(byte[] encryptedDataWithKey) throws Exception {
        // Separate encrypted data and encrypted AES key
        ByteBuffer buffer = ByteBuffer.wrap(encryptedDataWithKey);
        int encryptedDataLength = buffer.getInt();
        byte[] encryptedData = new byte[encryptedDataLength];
        buffer.get(encryptedData, 0, encryptedDataLength);
        byte[] encryptedAesKey = new byte[encryptedDataWithKey.length - encryptedDataLength - 4];
        buffer.get(encryptedAesKey, 0, encryptedAesKey.length);

        // Decrypt AES key with ECC private key
        KeyPair eccKeyPair = generateEccKeyPair();
        byte[] decryptedAesKeyBytes = decryptWithEcc(encryptedAesKey, eccKeyPair.getPrivate());
        SecretKeySpec decryptedAesKey = new SecretKeySpec(decryptedAesKeyBytes, "AES");

        // Decrypt data with decrypted AES key
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]);
        cipher.init(Cipher.DECRYPT_MODE, decryptedAesKey, ivParameterSpec);
        byte[] decryptedData = cipher.doFinal(encryptedData);
        return decryptedData;
    }

    private static byte[] encryptWithAes128Rsa2048(byte[] data) throws Exception {
        // Generate AES key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey aesKey = keyGen.generateKey();

        // Encrypt data with AES key
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivParameterSpec);
        byte[] encryptedData = cipher.doFinal(data);

        // Encrypt AES key with RSA public key
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair rsaKeyPair = keyPairGenerator.generateKeyPair();
        PublicKey rsaPublicKey = rsaKeyPair.getPublic();
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        byte[] encryptedAesKey = rsaCipher.doFinal(aesKey.getEncoded());

        // Concatenate encrypted data and encrypted AES key
        byte[] result = new byte[encryptedData.length + encryptedAesKey.length];
        System.arraycopy(encryptedData, 0, result, 0, encryptedData.length);
        System.arraycopy(encryptedAesKey, 0, result, encryptedData.length, encryptedAesKey.length);

        return result;
    }

    private static byte[] decryptWithAes128Rsa2048(byte[] encryptedDataWithKey) throws Exception {
        // Separate encrypted data and encrypted AES key
        int encryptedAesKeyLength = 256; // 2048 bits RSA key length
        byte[] encryptedData = Arrays.copyOfRange(encryptedDataWithKey, 0, encryptedDataWithKey.length - encryptedAesKeyLength);
        byte[] encryptedAesKey = Arrays.copyOfRange(encryptedDataWithKey, encryptedDataWithKey.length - encryptedAesKeyLength, encryptedDataWithKey.length);

        // Decrypt AES key with RSA private key
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair rsaKeyPair = keyPairGenerator.generateKeyPair();
        PrivateKey rsaPrivateKey = rsaKeyPair.getPrivate();
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        byte[] decryptedAesKeyBytes = rsaCipher.doFinal(encryptedAesKey);
        SecretKeySpec decryptedAesKey = new SecretKeySpec(decryptedAesKeyBytes, "AES");

        // Decrypt data with decrypted AES key
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]);
        cipher.init(Cipher.DECRYPT_MODE, decryptedAesKey, ivParameterSpec);
        byte[] decryptedData = cipher.doFinal(encryptedData);

        return decryptedData;
    }
}

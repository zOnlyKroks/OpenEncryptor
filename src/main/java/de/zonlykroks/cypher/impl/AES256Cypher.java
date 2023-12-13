package de.zonlykroks.cypher.impl;

import de.zonlykroks.cypher.SupportedCypher;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;


public class AES256Cypher implements SupportedCypher {

    private static final String CIPHER_SPEC = "AES/CBC/PKCS5Padding";
    private static final String KEYGEN_SPEC = "PBKDF2WithHmacSHA1";

    @Override
    public boolean isSymmectricCypher() {
        return false;
    }


    @Override
    public void encrypt(File work, char[] hashedPassword) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {
        byte[] clean = Files.readAllBytes(work.toPath());

        // Generating IV.
        int ivSize = 16;
        byte[] iv = new byte[ivSize];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(KEYGEN_SPEC);
        KeySpec spec = new PBEKeySpec(hashedPassword,iv,1000,256);
        SecretKey temp = secretKeyFactory.generateSecret(spec);
        SecretKeySpec secretKeySpec = new SecretKeySpec(temp.getEncoded(), "AES");

        // Encrypt.
        Cipher cipher = Cipher.getInstance(CIPHER_SPEC);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(clean);

        // Combine IV and encrypted part.
        byte[] encryptedIVAndText = new byte[ivSize + encrypted.length];
        System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize);
        System.arraycopy(encrypted, 0, encryptedIVAndText, ivSize, encrypted.length);

        Files.write(work.toPath(), encryptedIVAndText);
    }

    @Override
    public void decrypt(File work, char[] hashedPassword) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        byte[] encryptedIvTextBytes = Files.readAllBytes(work.toPath());

        int ivSize = 16;

        // Extract IV.
        byte[] iv = new byte[ivSize];
        System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Extract encrypted part.
        int encryptedSize = encryptedIvTextBytes.length - ivSize;
        byte[] encryptedBytes = new byte[encryptedSize];
        System.arraycopy(encryptedIvTextBytes, ivSize, encryptedBytes, 0, encryptedSize);

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(KEYGEN_SPEC);
        KeySpec spec = new PBEKeySpec(hashedPassword,iv,1000,256);
        SecretKey temp = secretKeyFactory.generateSecret(spec);
        SecretKeySpec secretKeySpec = new SecretKeySpec(temp.getEncoded(), "AES");

        // Decrypt.
        Cipher cipherDecrypt = Cipher.getInstance(CIPHER_SPEC);
        cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decrypted = cipherDecrypt.doFinal(encryptedBytes);

        Files.write(work.toPath(), decrypted);
    }

    @Override
    public String toString() {
        return CIPHER_SPEC + ":" + KEYGEN_SPEC;
    }
}

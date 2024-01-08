package de.zonlykroks.cypher.impl;

import de.zonlykroks.cypher.SupportedCypher;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class Blowfish implements SupportedCypher {

    private static final String CIPHER_SPEC = "Blowfish/CBC/PKCS5Padding";

    private static final String KEYGEN_SPEC = "PBKDF2WithHmacSHA1";

    @Override
    public boolean isSymmetricCypher() {
        return false;
    }
    @Override
    public void encrypt(File work, char[] hashedPassword) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {
        // Generate a secret key using PBKDF2
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEYGEN_SPEC);
        KeySpec keySpec = new PBEKeySpec(hashedPassword, new byte[16], 65536, 128);
        SecretKey secretKey = factory.generateSecret(keySpec);

        // Extract the raw key bytes
        byte[] keyBytes = secretKey.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "Blowfish");

        // Create Cipher instance
        Cipher cipher = Cipher.getInstance(CIPHER_SPEC);

        // Generate a random IV (Initialization Vector)
        SecureRandom random = new SecureRandom();
        byte[] ivBytes = new byte[cipher.getBlockSize()];
        random.nextBytes(ivBytes);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        // Initialize the Cipher for encryption
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);

        // Read the content from the file
        byte[] inputBytes = Files.readAllBytes(work.toPath());

        // Encrypt the content
        byte[] encryptedBytes = cipher.doFinal(inputBytes);

        // Write the IV followed by the encrypted content to the original file
        try (OutputStream out = new FileOutputStream(work)) {
            out.write(ivBytes);
            out.write(encryptedBytes);
        }
    }

    @Override
    public void decrypt(File work, char[] hashedPassword) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        // Generate a secret key using PBKDF2
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEYGEN_SPEC);
        KeySpec keySpec = new PBEKeySpec(hashedPassword, new byte[16], 65536, 128);
        SecretKey secretKey = factory.generateSecret(keySpec);

        // Extract the raw key bytes
        byte[] keyBytes = secretKey.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "Blowfish");

        // Create Cipher instance
        Cipher cipher = Cipher.getInstance(CIPHER_SPEC);

        // Read the IV from the encrypted file
        try (InputStream in = new FileInputStream(work)) {
            byte[] ivBytes = new byte[cipher.getBlockSize()];
            in.read(ivBytes)
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            // Initialize the Cipher for decryption
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

            // Read the encrypted content from the file
            byte[] encryptedBytes = Files.readAllBytes(work.toPath());

            // Decrypt the content (excluding the IV)
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes, cipher.getBlockSize(), encryptedBytes.length - cipher.getBlockSize());

            // Write the decrypted content back to the original file
            Files.write(work.toPath(), decryptedBytes, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    @Override
    public String toString() {
        return CIPHER_SPEC + ":" + KEYGEN_SPEC;
    }
}

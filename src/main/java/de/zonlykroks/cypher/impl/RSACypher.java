package de.zonlykroks.cypher.impl;

import de.zonlykroks.cypher.SupportedCypher;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class RSACypher implements SupportedCypher {
    @Override
    public boolean isSymmetricCypher() {
        return true;
    }

    @Override
    public void encrypt(File work, String key) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException,
            InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {
        // Remove the header and footer lines from the key string
        key = key.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").trim();

        // Remove all whitespace characters from the key string
        key = key.replaceAll("\\s", "");

        // Decode the Base64-encoded key string
        byte[] keyBytes = java.util.Base64.getDecoder().decode(key);

        // Generate the public key from the decoded bytes
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));

        // Encrypt the file content using the public key
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] encryptedData = encryptCipher.doFinal(Files.readAllBytes(work.toPath()));

        // Write the encrypted data back to the file
        Files.write(work.toPath(), encryptedData);
    }

    @Override
    public void decrypt(File work, String key) throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {

        // Remove the header and footer lines from the key string
        key = key.replaceAll("-----BEGIN .* PRIVATE KEY-----", "").replaceAll("-----END .* PRIVATE KEY-----", "").trim();

        // Remove all whitespace characters from the key string
        key = key.replaceAll("\\s", "");

        // Decode the Base64-encoded key string
        byte[] keyBytes = Base64.getDecoder().decode(key);

        // Generate the private key from the decoded bytes
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));

        // Initialize Cipher for decryption
        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);

        // Decrypt the file content using the private key
        byte[] decryptedData = decryptCipher.doFinal(Files.readAllBytes(work.toPath()));

        // Write the decrypted data back to the file
        Files.write(work.toPath(), decryptedData);
    }

    @Override
    public String toString() {
        return "RSA";
    }
}

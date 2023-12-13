package de.zonlykroks.cypher.impl;

import de.zonlykroks.cypher.SupportedCypher;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class SHA256 implements SupportedCypher {
    @Override
    public boolean isSymmectricCypher() {
        return false;
    }

    @Override
    public void encrypt(File work, char[] hashedPassword) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String fileAsString = String.join("", Files.readAllLines(work.toPath()));

        byte[] encodedHash = digest.digest(fileAsString.getBytes(StandardCharsets.UTF_8));

        String path = work.toPath().toString();
        int lastIndex = path.lastIndexOf(".");
        String pathWithoutEnding = path.substring(0, lastIndex) + ".hash";

        File file = new File(pathWithoutEnding);
        if(!file.exists()) file.createNewFile();

        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedHash) {
            hexString.append(String.format("%02x", b));
        }

        Files.writeString(file.toPath(), hexString);
    }

    @Override
    public String toString() {
        return "SHA-256";
    }
}

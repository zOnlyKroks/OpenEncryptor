package de.zonlykroks.cypher;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

public interface SupportedCypher{

    boolean isSymmetricCypher();

    default void encrypt(File work, char[] hashedPassword) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {
        throw new UnsupportedOperationException();
    }

    default void decrypt(File work, char[] hashedPassword) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        throw new UnsupportedOperationException();
    }

    default void encrypt(File work, String key) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException {
        throw new UnsupportedOperationException();
    }

    default void decrypt(File work, String key) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        throw new UnsupportedOperationException();
    }

    String toString();


}

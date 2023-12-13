package de.zonlykroks;

import de.zonlykroks.gui.file.FileSelectionModule;
import de.zonlykroks.util.PathUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import java.io.*;
import java.security.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

public class OpenEncryptor {

    private static int RSA_KEY_SIZE;

    public static void main(String[] args) throws Exception {
        loadConfig();

        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new BouncyCastlePQCProvider());

        Arrays.stream(args).anyMatch(s -> {
            try {

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                String key = dtf.format(now).replace(" ", "-").replace("/", "_").replace(":", "_");

                if (s.equalsIgnoreCase("-genRSAKey")) {
                    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                    kpg.initialize(RSA_KEY_SIZE);

                    System.out.println("Generating RSA Keypair with size " + RSA_KEY_SIZE);

                    KeyPair kp = kpg.generateKeyPair();
                    PrivateKey aPrivate = kp.getPrivate();
                    PublicKey aPublic = kp.getPublic();

                    try (FileOutputStream outPrivate = new FileOutputStream(PathUtils.getExecutionPath() + "/" + key + ".priv")) {
                        outPrivate.write(Base64.getEncoder().encode(aPrivate.getEncoded()));
                    }

                    try (FileOutputStream outPublic = new FileOutputStream(PathUtils.getExecutionPath() + "/" + key + ".pub")) {
                        outPublic.write(Base64.getEncoder().encode(aPublic.getEncoded()));
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });

        new FileSelectionModule();
    }

    private static void loadConfig() throws Exception {
        File configFile = new File(PathUtils.getExecutionPath() + "/config/openencryptor.properties");
        InputStream stream = new FileInputStream(configFile);

        Properties prop = new Properties();

        prop.load(stream);

        RSA_KEY_SIZE = Integer.parseInt((String) prop.getOrDefault("RSA_KEY_SIZE", 4096));
    }
}

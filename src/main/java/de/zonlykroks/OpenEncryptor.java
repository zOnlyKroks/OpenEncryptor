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
import java.util.logging.Logger;

public class OpenEncryptor {

    private static int RSA_KEY_SIZE;
    public static boolean CLEAN_PASSWORD_FIELD_UPON_COMPLETION;

    public static final Logger LOGGER = Logger.getLogger("OpenEncryptor");

    public static void main(String[] args) throws Exception {
        loadConfig();

        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new BouncyCastlePQCProvider());

        Arrays.stream(args).forEach(s -> {
            try {

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                String key = dtf.format(now).replace(" ", "-").replace("/", "_").replace(":", "_");

                if (s.equalsIgnoreCase("-genRSAKey")) {
                    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                    kpg.initialize(RSA_KEY_SIZE);

                    LOGGER.info("Generating RSA Keypair with size " + RSA_KEY_SIZE);

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
                LOGGER.severe(e.getMessage());
            }
        });

        new FileSelectionModule();
    }

    private static void loadConfig() throws Exception {
        File configFile = new File(PathUtils.getExecutionPath() + "/config/openencryptor.properties");
        InputStream stream = new FileInputStream(configFile);

        Properties prop = new Properties();

        prop.load(stream);

        RSA_KEY_SIZE = Integer.parseInt((String) prop.getOrDefault("RSA_KEY_SIZE", 4096));
        CLEAN_PASSWORD_FIELD_UPON_COMPLETION = Boolean.parseBoolean((String) prop.getOrDefault("CLEAN_PASSWORD_FIELD_UPON_COMPLETION", true));
        LOGGER.info("Successfully loaded config values! Changes during runtime to config will be ignored until the application is relaunched!");
    }
}

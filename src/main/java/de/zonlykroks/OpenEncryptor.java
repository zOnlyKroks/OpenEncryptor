package de.zonlykroks;

import de.zonlykroks.gui.file.FileSelectionModule;
import de.zonlykroks.gui.rsa.RSAKeyGenWindow;
import de.zonlykroks.util.PathUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import javax.swing.*;
import java.io.*;
import java.security.Security;
import java.util.Properties;
import java.util.logging.Logger;

public class OpenEncryptor {

    public static boolean CLEAN_PASSWORD_FIELD_UPON_COMPLETION;
    public static final Logger LOGGER = Logger.getLogger("OpenEncryptor");

    public static void main(String[] args) {
        try {
            loadConfig();
            Security.addProvider(new BouncyCastleProvider());
            Security.addProvider(new BouncyCastlePQCProvider());

            int c = JOptionPane.showOptionDialog(null,
                    "This app is for educational purposes only. The developer is not responsible for any misuse. By clicking \"OK,\" you agree to these terms. Click \"Cancel\" to exit. The app operates offline and does not send data to servers.",
                    "Disclaimer",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    new String[]{
                            "OK",
                            "Cancel"
                    },
                    "OK");

            if(c == 1) return;

            String[] options = {"File Selection Module", "RSA Key Generator"};
            int choice = JOptionPane.showOptionDialog(null,
                    "Select a window to open",
                    "Window Selection",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (choice == 0) {
                new FileSelectionModule();
            } else if (choice == 1) {
                new RSAKeyGenWindow();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadConfig() throws Exception {
        File configDir = new File(PathUtils.getExecutionPath() + "/config/");

        if(!configDir.exists()) configDir.mkdirs();

        File configFile = new File(PathUtils.getExecutionPath() + "/config/openencryptor.properties");

        if(!configFile.exists()) configFile.createNewFile();

        try (InputStream stream = new FileInputStream(configFile)) {
            Properties prop = new Properties();
            prop.load(stream);

            CLEAN_PASSWORD_FIELD_UPON_COMPLETION = Boolean.parseBoolean((String) prop.getOrDefault("CLEAN_PASSWORD_FIELD_UPON_COMPLETION", "true"));
            LOGGER.info("Successfully loaded config values! Changes during runtime to config will be ignored until the application is relaunched!");
        }
    }
}

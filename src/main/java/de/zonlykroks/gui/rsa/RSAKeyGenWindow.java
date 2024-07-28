package de.zonlykroks.gui.rsa;

import de.zonlykroks.OpenEncryptor;
import de.zonlykroks.util.PathUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class RSAKeyGenWindow {

    private final JFrame frame;
    private final JComboBox<Integer> keyLengthComboBox;

    public RSAKeyGenWindow() {
        frame = new JFrame("RSA Key Generator");
        frame.setSize(400, 150);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Key Length:"));

        Integer[] keyLengths = {1024, 2048, 3072, 4096};
        keyLengthComboBox = new JComboBox<>(keyLengths);
        inputPanel.add(keyLengthComboBox);

        JButton generateButton = new JButton("Generate");
        inputPanel.add(generateButton);

        frame.add(inputPanel, BorderLayout.NORTH);

        generateButton.addActionListener(_ -> {
            generateKeyPair();
            returnToMainMenu();
        });

        frame.setVisible(true);
    }

    private void generateKeyPair() {
        try {
            int keyLength = (int) keyLengthComboBox.getSelectedItem();

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(keyLength);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            String executionPath = PathUtils.getExecutionPath();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            saveKeyToFile(executionPath, "publicKey_" + timestamp + ".pem", keyPair.getPublic().getEncoded());
            saveKeyToFile(executionPath, "privateKey_" + timestamp + ".pem", keyPair.getPrivate().getEncoded());

            JOptionPane.showMessageDialog(frame, "Keys generated and saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NoSuchAlgorithmException | IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error generating key pair: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveKeyToFile(String path, String fileName, byte[] key) throws IOException {
        File file = new File(path + File.separator + fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("-----BEGIN KEY-----\n".getBytes());
            fos.write(Base64.getEncoder().encode(key));
            fos.write("\n-----END KEY-----\n".getBytes());
        }
    }

    private void returnToMainMenu() {
        frame.dispose();
        // Assuming main menu is in OpenEncryptor
        OpenEncryptor.main(null);
    }
}

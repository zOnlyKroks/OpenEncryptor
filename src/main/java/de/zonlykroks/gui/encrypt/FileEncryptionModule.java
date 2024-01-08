package de.zonlykroks.gui.encrypt;

import de.zonlykroks.OpenEncryptor;
import de.zonlykroks.cypher.SupportedCypher;
import de.zonlykroks.cypher.impl.*;
import de.zonlykroks.util.PathUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Random;

public class FileEncryptionModule extends JFrame {

    private final Button modeButton;
    @Nullable
    private File asymmetricKey = null;

    private boolean isModeEncrypt = true;

    public FileEncryptionModule(File file) {
        super("Lets get ciphering!");
        this.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        c.gridy = 0;

        DefaultListModel<SupportedCypher> cypherDefaultListModel = new DefaultListModel<>();

        cypherDefaultListModel.add(0, new AES256Cypher());
        cypherDefaultListModel.add(1, new DukeNukeEm());
        cypherDefaultListModel.add(2, new Blowfish());
        cypherDefaultListModel.add(3, new RSACypher());
        cypherDefaultListModel.add(4, new SHA256());

        final JList<?> supportedCypherJList = new JList<>(cypherDefaultListModel);
        this.add(supportedCypherJList,c);

        c.gridy = 1;//set the y location of the grid for the next component

        JButton publicKeyButton = new JButton();
        publicKeyButton.setText("Select Public Key (Optional, used by asymmetric algorithms)");
        this.add(publicKeyButton,c);

        c.gridy=3;

        JTextField selectedFileTextField = new JTextField();
        selectedFileTextField.setEditable(false);
        this.add(selectedFileTextField,c);

        c.gridy = 4;
        modeButton = new Button();
        modeButton.setLabel("Mode: Encrypt!");

        this.add(modeButton,c);

        c.gridy = 5;
        JPasswordField passwordTextField = new JPasswordField();
        passwordTextField.setEditable(true);
        this.add(passwordTextField,c);

        c.gridy = 6;
        Button finishButton = new Button();
        finishButton.setLabel("Finalize!");
        this.add(finishButton,c);

        modeButton.addActionListener(e -> {
            if(isModeEncrypt) {
                modeButton.setLabel("Mode: Decrypt!");
                isModeEncrypt = false;
            }else {
                modeButton.setLabel("Mode: Encrypt!");
                isModeEncrypt = true;
            }
        });

        finishButton.addActionListener(e -> {
            SupportedCypher cypher = (SupportedCypher) supportedCypherJList.getSelectedValue();

            if(cypher == null) {
                JOptionPane.showMessageDialog(this,"Please select a cypher!");
                return;
            }

            if(cypher.isSymmetricCypher() && asymmetricKey == null) {
                JOptionPane.showMessageDialog(this, "You have selected a symmetric key cypher, please choose a key to encrypt / decrypt the file");
                OpenEncryptor.LOGGER.severe("Key not chosen for symmetric cypher, aborting further actions!");
                return;
            }

            try {
                if(!cypher.isSymmetricCypher()) {
                    //AES etc
                    if(isModeEncrypt) {
                        cypher.encrypt(file, passwordTextField.getPassword());
                    }else {
                        cypher.decrypt(file,passwordTextField.getPassword());
                    }
                }else if(cypher.isSymmetricCypher()) {

                    if(asymmetricKey == null) return;

                    //RSA
                    if(isModeEncrypt) {
                        cypher.encrypt(file, Files.readString(asymmetricKey.toPath()));
                    }else {
                        cypher.decrypt(file, Files.readString(asymmetricKey.toPath()));
                    }
                }
            }catch (Exception exp) {
                JOptionPane.showMessageDialog(this, "Caught exception!: \n" + exp.fillInStackTrace());
                OpenEncryptor.LOGGER.severe(exp.getMessage());
            }finally {
                //This is all handled properly (hopefully), but why not go the extra mile.
                if(OpenEncryptor.CLEAN_PASSWORD_FIELD_UPON_COMPLETION) {
                    OpenEncryptor.LOGGER.info("Begin scrambling!");
                    passwordTextField.setText(genRandom32String());
                    System.gc();
                    OpenEncryptor.LOGGER.info("Finished scrambling!");
                }

                JOptionPane.showMessageDialog(this , "Finished without an exception");
            }
        });

        publicKeyButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setPreferredSize(new Dimension(500,500));
            fileChooser.setCurrentDirectory(new File(PathUtils.getExecutionPath()));

            int status = fileChooser.showOpenDialog(this);

            if (status == JFileChooser.APPROVE_OPTION) {
                asymmetricKey = fileChooser.getSelectedFile();
                OpenEncryptor.LOGGER.info("Symmetric key successfully loaded!");
            }
        });


        setSize(new Dimension(500,500));
        setLocationRelativeTo(null);

        selectedFileTextField.setText(file.getAbsolutePath());
        passwordTextField.setText(genRandom32String());

        JOptionPane.showMessageDialog(this, "Your password will be scrambled, after you forgot to input a cypher, an exception got caught or upon successful encryption. Keep this in mind. Stay safe! You can turn this off in the config!");
        OpenEncryptor.LOGGER.info("Warning acknowledged!");
    }

    public static String genRandom32String() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 32;
        Random random = new SecureRandom();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}

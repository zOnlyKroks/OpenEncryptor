package de.zonlykroks.gui.encrypt;

import de.zonlykroks.cypher.SupportedCypher;
import de.zonlykroks.cypher.impl.AES256Cypher;
import de.zonlykroks.cypher.impl.Blowfish;
import de.zonlykroks.cypher.impl.DukeNukeEm;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

public class FileEncryptionModule extends JFrame {

    private final Button modeButton, finishButton;

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

        final JList<?> supportedCypherJList = new JList<>(cypherDefaultListModel);
        this.add(supportedCypherJList,c);

        c.gridy = 1;//set the y location of the grid for the next component

        JTextField selectedFileTextField = new JTextField();
        selectedFileTextField.setEditable(false);
        this.add(selectedFileTextField,c);

        c.gridy = 2;
        modeButton = new Button();
        modeButton.setLabel("Mode: Encrypt!");

        this.add(modeButton,c);

        c.gridy = 3;
        JPasswordField passwordTextField = new JPasswordField();
        passwordTextField.setEditable(true);
        this.add(passwordTextField,c);

        c.gridy = 4;
        finishButton = new Button();
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

            try {
                if(isModeEncrypt) {
                    cypher.encrypt(file, passwordTextField.getPassword());
                }else {
                    cypher.decrypt(file,passwordTextField.getPassword());
                }
            }catch (Exception exp) {
                JOptionPane.showMessageDialog(this, "Caught exception!: \n" + exp);
            }finally {
                //This is all handled properly (hopefully), but why not go the extra mile.
                passwordTextField.setText(genRandom32String());
                System.gc();

                JOptionPane.showMessageDialog(this , "Finished without an exception");
            }
        });


        setSize(new Dimension(500,500));
        setLocationRelativeTo(null);

        selectedFileTextField.setText(file.getAbsolutePath());
        passwordTextField.setText(genRandom32String());

        JOptionPane.showMessageDialog(this, "Your password will be scrambled, after you forgot to input a cypher, an exception got caught or upon successful encryption. Keep this in mind. Stay safe!");
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

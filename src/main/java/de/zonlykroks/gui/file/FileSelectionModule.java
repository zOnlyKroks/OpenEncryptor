package de.zonlykroks.gui.file;

import de.zonlykroks.gui.encrypt.FileEncryptionModule;
import de.zonlykroks.util.PathUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileSelectionModule extends JFrame{

    public File toEncrypt;
    private final JFileChooser fileChooser;

    public FileSelectionModule() {
        super("Choose a file to encrypt!");

        fileChooser = new JFileChooser();
        fileChooser.setPreferredSize(new Dimension(500,500));
        fileChooser.setCurrentDirectory(new File(PathUtils.getExecutionPath()));

        int status = fileChooser.showOpenDialog(null);

        if (status == JFileChooser.APPROVE_OPTION) {
            toEncrypt = this.fileChooser.getSelectedFile();

            this.dispose();
            this.setVisible(false);

            FileEncryptionModule module = new FileEncryptionModule(toEncrypt);
            module.setVisible(true);
        }
    }
}
